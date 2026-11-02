package com.pragma.onclass_person_service.infraestructure.adapters.persistenceadapters.repository;

import com.pragma.onclass_person_service.infraestructure.adapters.persistenceadapters.entity.BootcampPersonEntity;
import com.pragma.onclass_person_service.infraestructure.adapters.persistenceadapters.entity.PersonEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SpringDataBootcampPersonRepository extends ReactiveCrudRepository<BootcampPersonEntity, Long> {


    @Query("SELECT bootcamp_id FROM bootcamp_person WHERE person_id = :personId")
    Flux<Long> findBootcampIdsByPersonId(Long personId);

    @Query("SELECT bootcamp_id FROM bootcamp_person GROUP BY bootcamp_id ORDER BY COUNT(*) DESC LIMIT 1")
    Mono<Long> findMostPopularBootcampId();

    @Query("SELECT p.* FROM person p " +
            "JOIN bootcamp_person bp on bp.person_id = p.id "+
            "WHERE bootcamp_id = :bootcampId")
    Flux<PersonEntity> findPersonIdsByBootcampId(Long bootcampId);
}
