package com.pragma.onclass_person_service.infraestructure.adapters.persistenceadapters;

import com.pragma.onclass_person_service.domain.model.Person;
import com.pragma.onclass_person_service.domain.spi.BootcampPersonPersistencePort;
import com.pragma.onclass_person_service.infraestructure.adapters.persistenceadapters.entity.BootcampPersonEntity;
import com.pragma.onclass_person_service.infraestructure.adapters.persistenceadapters.repository.SpringDataBootcampPersonRepository;
import com.pragma.onclass_person_service.infraestructure.entrypoints.mapper.PersonMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class BootcampPersonPersistenceAdapter implements BootcampPersonPersistencePort {

    private final SpringDataBootcampPersonRepository bootcampPersonRepository;
    private final PersonMapper personMapper;

    @Override
    public Flux<Long> findBootcampIdsByPersonId(Long personId) {
        return bootcampPersonRepository.findBootcampIdsByPersonId(personId);
    }

    @Override
    public Mono<Void> saveEnrollment(Long personId, Long bootcampId) {
        BootcampPersonEntity entity = new BootcampPersonEntity(personId, bootcampId);
        return bootcampPersonRepository.save(entity).then();
    }

    @Override
    public Mono<Long> findMostPopularBootcampId() {
        return bootcampPersonRepository.findMostPopularBootcampId();
    }

    @Override
    public Flux<Person> findPersonIdsByBootcampId(Long bootcampId) {
        return bootcampPersonRepository.findPersonIdsByBootcampId(bootcampId)
                .map(personMapper::personEntityToPerson);
    }
}
