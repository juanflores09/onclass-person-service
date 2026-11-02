package com.pragma.onclass_person_service.infraestructure.adapters;

import com.pragma.onclass_person_service.domain.model.Person;
import com.pragma.onclass_person_service.infraestructure.adapters.persistenceadapters.PersonPersistenceAdapter;
import com.pragma.onclass_person_service.infraestructure.adapters.persistenceadapters.entity.PersonEntity;
import com.pragma.onclass_person_service.infraestructure.adapters.persistenceadapters.repository.SpringDataPersonRepository;
import com.pragma.onclass_person_service.infraestructure.entrypoints.mapper.PersonMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonPersistenceAdapterTest {

    @Mock
    private SpringDataPersonRepository repository;

    @Mock
    private PersonMapper personMapper;

    @InjectMocks
    private PersonPersistenceAdapter personPersistenceAdapter;

    @Test
    void shouldReturnExistingPersonIdWhenEmailExists() {
        // Given
        Person person = new Person();
        person.setLastName("Flores");
        person.setFirstName("Juan");
        person.setEmail("juan@example.com");

        PersonEntity existingEntity = new PersonEntity();
        existingEntity.setId(10L);
        existingEntity.setEmail("juan@example.com");

        when(repository.findByEmail("juan@example.com"))
                .thenReturn(Mono.just(existingEntity));
        lenient().when(repository.save(any())).thenReturn(Mono.just(new PersonEntity()));

        // When
        Mono<Long> result = personPersistenceAdapter.saveOrGetPerson(person);

        // Then
        StepVerifier.create(result)
                .expectNext(10L)
                .verifyComplete();

        verify(repository, times(1)).findByEmail("juan@example.com");
    }
    @Test
    void shouldSaveAndReturnNewPersonIdWhenEmailDoesNotExist() {
        // Given
        Person person = new Person();
        person.setLastName("Flores");
        person.setFirstName("Juan");
        person.setEmail("juan@example.com");

        PersonEntity mappedEntity = new PersonEntity();
        PersonEntity savedEntity = new PersonEntity();
        savedEntity.setId(15L);

        when(repository.findByEmail("juan@example.com"))
                .thenReturn(Mono.empty());
        when(personMapper.personToPersonEntity(person))
                .thenReturn(mappedEntity);
        when(repository.save(mappedEntity))
                .thenReturn(Mono.just(savedEntity));

        // When
        Mono<Long> result = personPersistenceAdapter.saveOrGetPerson(person);

        // Then
        StepVerifier.create(result)
                .expectNext(15L)
                .verifyComplete();

        verify(repository, times(1)).findByEmail("juan@example.com");
        verify(personMapper, times(1)).personToPersonEntity(person);
        verify(repository, times(1)).save(mappedEntity);
    }
}
