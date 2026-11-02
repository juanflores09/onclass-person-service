package com.pragma.onclass_person_service.infraestructure.adapters;

import com.pragma.onclass_person_service.domain.model.Person;
import com.pragma.onclass_person_service.infraestructure.adapters.persistenceadapters.BootcampPersonPersistenceAdapter;
import com.pragma.onclass_person_service.infraestructure.adapters.persistenceadapters.entity.BootcampPersonEntity;
import com.pragma.onclass_person_service.infraestructure.adapters.persistenceadapters.entity.PersonEntity;
import com.pragma.onclass_person_service.infraestructure.adapters.persistenceadapters.repository.SpringDataBootcampPersonRepository;
import com.pragma.onclass_person_service.infraestructure.entrypoints.mapper.PersonMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BootcampPersonPersistenceAdapterTest {

    @Mock
    private SpringDataBootcampPersonRepository bootcampPersonRepository;

    @Mock
    private PersonMapper personMapper;

    @InjectMocks
    private BootcampPersonPersistenceAdapter adapter;

    @Test
    void findBootcampIdsByPersonId_Success() {
        // Given
        Long personId = 1L;
        when(bootcampPersonRepository.findBootcampIdsByPersonId(personId))
                .thenReturn(Flux.just(10L, 20L));

        // When
        Flux<Long> result = adapter.findBootcampIdsByPersonId(personId);

        // Then
        StepVerifier.create(result)
                .expectNext(10L, 20L)
                .verifyComplete();

        verify(bootcampPersonRepository, times(1)).findBootcampIdsByPersonId(personId);
    }

    @Test
    void saveEnrollment_Success() {
        // Given
        Long personId = 1L;
        Long bootcampId = 5L;
        BootcampPersonEntity savedEntity = new BootcampPersonEntity(personId, bootcampId);

        when(bootcampPersonRepository.save(any(BootcampPersonEntity.class)))
                .thenReturn(Mono.just(savedEntity));

        // When
        Mono<Void> result = adapter.saveEnrollment(personId, bootcampId);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(bootcampPersonRepository, times(1)).save(any(BootcampPersonEntity.class));
    }

    @Test
    void findMostPopularBootcampId_Success() {
        // Given
        Long popularBootcampId = 3L;
        when(bootcampPersonRepository.findMostPopularBootcampId())
                .thenReturn(Mono.just(popularBootcampId));

        // When
        Mono<Long> result = adapter.findMostPopularBootcampId();

        // Then
        StepVerifier.create(result)
                .expectNext(popularBootcampId)
                .verifyComplete();

        verify(bootcampPersonRepository, times(1)).findMostPopularBootcampId();
    }

    @Test
    void findPersonIdsByBootcampId_Success() {
        // Given
        Long bootcampId = 3L;
        PersonEntity entity = new PersonEntity();
        Person personDomain = new Person();
        personDomain.setFirstName("Juan");
        personDomain.setEmail("juan@example.com");

        when(bootcampPersonRepository.findPersonIdsByBootcampId(bootcampId))
                .thenReturn(Flux.just(entity));
        when(personMapper.personEntityToPerson(entity))
                .thenReturn(personDomain);

        // When
        Flux<Person> result = adapter.findPersonIdsByBootcampId(bootcampId);

        // Then
        StepVerifier.create(result)
                .expectNext(personDomain)
                .verifyComplete();

        verify(bootcampPersonRepository, times(1)).findPersonIdsByBootcampId(bootcampId);
        verify(personMapper, times(1)).personEntityToPerson(entity);
    }
}
