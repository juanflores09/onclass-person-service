package com.pragma.onclass_person_service.domain.usecase;

import com.pragma.onclass_person_service.domain.api.EnrollPersonServicePort;
import com.pragma.onclass_person_service.domain.enums.TechnicalMessage;
import com.pragma.onclass_person_service.domain.exceptions.BusinessException;
import com.pragma.onclass_person_service.domain.model.Person;
import com.pragma.onclass_person_service.domain.spi.BootcampGateway;
import com.pragma.onclass_person_service.domain.spi.BootcampPersonPersistencePort;
import com.pragma.onclass_person_service.domain.spi.PersonPersistencePort;
import com.pragma.onclass_person_service.infraestructure.adapters.persistenceadapters.entity.Bootcamp;
import com.pragma.onclass_person_service.infraestructure.entrypoints.util.Constants;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class EnrollPersonUseCase implements EnrollPersonServicePort {

    private final PersonPersistencePort personPersistencePort;
    private final BootcampPersonPersistencePort bootcampPersonPersistencePort;
    private final BootcampGateway bootcampGateway;

    @Override
    public Mono<Void> enrollPerson(Person person, Long bootcampId, String messageId) {

        return personPersistencePort.saveOrGetPerson(person)
                .flatMap(personId ->
                        bootcampPersonPersistencePort.findBootcampIdsByPersonId(personId)
                                .collectList()
                                .flatMap(activeBootcampIds -> {

                                    if(activeBootcampIds.size() >= 5){
                                        return Mono.error(new BusinessException(
                                                Constants.MESSAGE_ERROR_ENROLL, TechnicalMessage.MAX_BOOTCAMPS_EXCEEDED));
                                    }

                                    if(activeBootcampIds.contains(bootcampId)){
                                        return Mono.error(new BusinessException(
                                                Constants.MESSAGE_ERROR_ENROLL, TechnicalMessage.ALREADY_ENROLLED));
                                    }

                                    List<Long> allBootcampIds = new java.util.ArrayList<>(activeBootcampIds);
                                    allBootcampIds.add(bootcampId);

                                    return bootcampGateway.getBootcampsDetails(allBootcampIds, messageId)
                                            .collectList()
                                            .flatMap(allBootcamps -> {

                                                Bootcamp targetBootcamp = allBootcamps.stream()
                                                        .filter(b -> b.getId().equals(bootcampId))
                                                        .findFirst()
                                                        .orElseThrow(() -> new BusinessException(
                                                                Constants.MESSAGE_ERROR_ENROLL, TechnicalMessage.INTERNAL_ERROR));

                                                List<Bootcamp> activeBootcamps = allBootcamps.stream()
                                                        .filter(b -> !b.getId().equals(bootcampId))
                                                        .toList();

                                                if (!activeBootcamps.isEmpty()) {
                                                    boolean hasOverlap = activeBootcamps.stream()
                                                            .anyMatch(active -> datesOverlap(active, targetBootcamp));

                                                    if (hasOverlap) {
                                                        return Mono.error(new BusinessException(
                                                                Constants.MESSAGE_ERROR_ENROLL, TechnicalMessage.DATE_OVERLAP));
                                                    }
                                                }
                                                return bootcampPersonPersistencePort.saveEnrollment(personId, bootcampId);
                                            });
                                })
                );
    }

    private boolean datesOverlap(Bootcamp existing, Bootcamp target) {
        return target.getStartDate().isBefore(existing.getEndDate()) &&
                target.getEndDate().isAfter(existing.getStartDate());
    }
}
