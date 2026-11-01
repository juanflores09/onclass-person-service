package com.pragma.onclass_person_service.domain.spi;

import com.pragma.onclass_person_service.domain.model.Person;
import reactor.core.publisher.Mono;

public interface PersonPersistencePort {

    Mono<Long> saveOrGetPerson(Person person);
}
