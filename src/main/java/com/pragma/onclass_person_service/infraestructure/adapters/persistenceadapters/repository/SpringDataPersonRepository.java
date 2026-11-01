package com.pragma.onclass_person_service.infraestructure.adapters.persistenceadapters.repository;

import com.pragma.onclass_person_service.infraestructure.adapters.persistenceadapters.entity.PersonEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface SpringDataPersonRepository extends ReactiveCrudRepository<PersonEntity, Long> {

    Mono<PersonEntity> findByDocumentNumber(String documentNumber);
    Mono<PersonEntity> findByEmail(String email);
}
