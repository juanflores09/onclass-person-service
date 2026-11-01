package com.pragma.onclass_person_service.infraestructure.adapters.persistenceadapters.repository;

import com.pragma.onclass_person_service.infraestructure.adapters.persistenceadapters.entity.BootcampPersonEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface SpringDataBootcampPersonRepository extends ReactiveCrudRepository<BootcampPersonEntity, Long> {


    @Query("SELECT bootcamp_id FROM bootcamp_person WHERE person_id = :personId")
    Flux<Long> findBootcampIdsByPersonId(Long personId);
}
