package com.pragma.onclass_person_service.domain.usecase;

import com.pragma.onclass_person_service.domain.api.EnrollPersonServicePort;
import com.pragma.onclass_person_service.domain.enums.TechnicalMessage;
import com.pragma.onclass_person_service.domain.exceptions.BusinessException;
import com.pragma.onclass_person_service.domain.model.Person;
import com.pragma.onclass_person_service.domain.spi.BootcampGateway;
import com.pragma.onclass_person_service.domain.spi.BootcampPersonPersistencePort;
import com.pragma.onclass_person_service.domain.spi.PersonPersistencePort;
import com.pragma.onclass_person_service.infraestructure.adapters.bootcampwebclientadapter.dto.BootcampDetailsDTO;
import com.pragma.onclass_person_service.infraestructure.entrypoints.dto.PersonEnrollmentRequestDTO;
import com.pragma.onclass_person_service.infraestructure.entrypoints.mapper.PersonMapper;
import com.pragma.onclass_person_service.infraestructure.entrypoints.util.Constants;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class EnrollPersonUseCase implements EnrollPersonServicePort {

    private final PersonPersistencePort personPersistencePort;
    private final BootcampPersonPersistencePort bootcampPersonPersistencePort;
    private final BootcampGateway bootcampGateway;
    private final PersonMapper personMapper;

    @Override
    public Mono<Void> enrollPerson(PersonEnrollmentRequestDTO requestDTO, String messageId) {

        Person person = personMapper.personEnrollmentRequestDTOToPerson(requestDTO);

        return personPersistencePort.saveOrGetPerson(person)
                .flatMap(personId ->
                        bootcampPersonPersistencePort.findBootcampIdsByPersonId(personId)
                                .collectList()
                                .flatMap(activeBootcampIds -> {

                                    Long targetBootcampId = requestDTO.bootcampId();
                                    if(activeBootcampIds.size() >= 5){
                                        return Mono.error(new BusinessException(
                                                Constants.MESSAGE_ERROR_ENROLL, TechnicalMessage.MAX_BOOTCAMPS_EXCEEDED));
                                    }

                                    if(activeBootcampIds.contains(targetBootcampId)){
                                        return Mono.error(new BusinessException(
                                                Constants.MESSAGE_ERROR_ENROLL, TechnicalMessage.ALREADY_ENROLLED));
                                    }

                                    List<Long> allBootcampIds = new java.util.ArrayList<>(activeBootcampIds);
                                    allBootcampIds.add(targetBootcampId);

                                    return bootcampGateway.getBootcampsDetails(allBootcampIds, messageId)
                                            .collectList()
                                            .flatMap(allBootcamps -> {

                                                BootcampDetailsDTO targetBootcamp = allBootcamps.stream()
                                                        .filter(b -> b.getId().equals(targetBootcampId))
                                                        .findFirst()
                                                        .orElseThrow(() ->
                                                            new BusinessException(
                                                                    Constants.MESSAGE_ERROR_ENROLL, TechnicalMessage.BOOTCAMP_NOT_FOUND)
                                                        );

                                                List<BootcampDetailsDTO> activeBootcamps = allBootcamps.stream()
                                                        .filter(b -> !b.getId().equals(targetBootcampId))
                                                        .toList();

                                                if (!activeBootcamps.isEmpty()) {
                                                    boolean hasOverlap = activeBootcamps.stream()
                                                            .anyMatch(active -> datesOverlap(active, targetBootcamp));

                                                    if (hasOverlap) {
                                                        return Mono.error(new BusinessException(
                                                                Constants.MESSAGE_ERROR_ENROLL, TechnicalMessage.DATE_OVERLAP));
                                                    }
                                                }
                                                return bootcampPersonPersistencePort.saveEnrollment(personId, targetBootcampId);
                                            });
                                })
                );
    }

    private boolean datesOverlap(BootcampDetailsDTO existing, BootcampDetailsDTO target) {
        return target.getStartDate().isBefore(existing.getEndDate()) &&
                target.getEndDate().isAfter(existing.getStartDate());
    }
}
