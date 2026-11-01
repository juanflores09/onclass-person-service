package com.pragma.onclass_person_service.domain.spi;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BootcampPersonPersistencePort {

    Flux<Long> findBootcampIdsByPersonId(Long personId);
    Mono<Void> saveEnrollment(Long personId, Long bootcampId);
}
