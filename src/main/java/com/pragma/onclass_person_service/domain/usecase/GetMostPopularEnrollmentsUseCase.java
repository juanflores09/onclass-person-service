package com.pragma.onclass_person_service.domain.usecase;

import com.pragma.onclass_person_service.domain.api.GetMostPopularEnrollmentsServicePort;
import com.pragma.onclass_person_service.domain.enums.TechnicalMessage;
import com.pragma.onclass_person_service.domain.exceptions.BusinessException;
import com.pragma.onclass_person_service.domain.model.EnrolledPerson;
import com.pragma.onclass_person_service.domain.model.MostPopularEnrollment;
import com.pragma.onclass_person_service.domain.spi.BootcampPersonPersistencePort;
import com.pragma.onclass_person_service.domain.spi.PersonPersistencePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class GetMostPopularEnrollmentsUseCase implements GetMostPopularEnrollmentsServicePort {

    private final BootcampPersonPersistencePort bootcampPersonPersistencePort;
    private final PersonPersistencePort personPersistencePort;

    @Override
    public Mono<MostPopularEnrollment> execute(String messageId) {
        return bootcampPersonPersistencePort.findMostPopularBootcampId()
                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.NO_BOOTCAMPS_ENROLLED)))
                .flatMap(bootcampId ->
                        personPersistencePort.findPersonsByBootcampId(bootcampId)
                                .map(person -> new EnrolledPerson(
                                        person.getFirstName() + " " + person.getLastName(),
                                        person.getEmail()
                                ))
                                .collectList()
                                .map(persons -> new MostPopularEnrollment(bootcampId, persons.size(), persons))
                );
    }
}
