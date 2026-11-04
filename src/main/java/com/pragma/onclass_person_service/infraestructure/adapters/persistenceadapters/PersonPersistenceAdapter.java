package com.pragma.onclass_person_service.infraestructure.adapters.persistenceadapters;

import com.pragma.onclass_person_service.domain.model.Person;
import com.pragma.onclass_person_service.domain.spi.PersonPersistencePort;
import com.pragma.onclass_person_service.infraestructure.adapters.persistenceadapters.entity.PersonEntity;
import com.pragma.onclass_person_service.infraestructure.adapters.persistenceadapters.repository.SpringDataPersonRepository;
import com.pragma.onclass_person_service.infraestructure.entrypoints.mapper.PersonMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Repository
public class PersonPersistenceAdapter implements PersonPersistencePort {

    private final SpringDataPersonRepository repository;
    private final PersonMapper personMapper;

    @Override
    public Mono<Long> saveOrGetPerson(Person person) {
        return repository.findByEmailOrDocumentNumber(person.getEmail(), person.getDocumentNumber())
                .map(PersonEntity::getId)
                .switchIfEmpty(
                        repository.save(personMapper.personToPersonEntity(person))
                                .map(PersonEntity::getId)
                );
    }

}
