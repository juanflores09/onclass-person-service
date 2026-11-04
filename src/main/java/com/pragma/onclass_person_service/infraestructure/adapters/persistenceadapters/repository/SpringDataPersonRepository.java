package com.pragma.onclass_person_service.infraestructure.adapters.persistenceadapters.repository;

import com.pragma.onclass_person_service.infraestructure.adapters.persistenceadapters.entity.PersonEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SpringDataPersonRepository extends ReactiveCrudRepository<PersonEntity, Long> {

    Mono<PersonEntity> findByEmailOrDocumentNumber(String email, String documentNumber);


    @Query("SELECT p.* FROM person p " +
            "JOIN bootcamp_person bp on bp.person_id = p.id "+
            "WHERE bootcamp_id = :bootcampId")
    Flux<PersonEntity> findPersonsByBootcampId(Long bootcampId);
}
