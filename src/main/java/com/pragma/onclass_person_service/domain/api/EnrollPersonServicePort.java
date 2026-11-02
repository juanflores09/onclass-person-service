package com.pragma.onclass_person_service.domain.api;

import com.pragma.onclass_person_service.domain.model.Person;
import reactor.core.publisher.Mono;

public interface EnrollPersonServicePort {

    Mono<Void> enrollPerson(Person person, Long bootcampId, String messageId);
}
