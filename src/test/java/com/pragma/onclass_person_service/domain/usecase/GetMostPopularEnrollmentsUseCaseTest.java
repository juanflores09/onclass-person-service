package com.pragma.onclass_person_service.domain.usecase;

import com.pragma.onclass_person_service.domain.enums.TechnicalMessage;
import com.pragma.onclass_person_service.domain.exceptions.BusinessException;
import com.pragma.onclass_person_service.domain.model.EnrolledPerson;
import com.pragma.onclass_person_service.domain.model.Person;
import com.pragma.onclass_person_service.domain.spi.BootcampPersonPersistencePort;
import com.pragma.onclass_person_service.domain.spi.PersonPersistencePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetMostPopularEnrollmentsUseCaseTest {

    @Mock
    private BootcampPersonPersistencePort bootcampPersonPersistencePort;

    @Mock
    private PersonPersistencePort personPersistencePort;

    @InjectMocks
    private GetMostPopularEnrollmentsUseCase getMostPopularEnrollmentsUseCase;

    private final String messageId = "msg-12345";

    @Test
    void shouldReturnMostPopularEnrollmentSuccessfully() {
        // Arrange
        Long bootcampId = 1L;

        Person person1 = mock(Person.class);
        when(person1.getFirstName()).thenReturn("Juan");
        when(person1.getLastName()).thenReturn("Pérez");
        when(person1.getEmail()).thenReturn("juan@example.com");

        Person person2 = mock(Person.class);
        when(person2.getFirstName()).thenReturn("María");
        when(person2.getLastName()).thenReturn("Gómez");
        when(person2.getEmail()).thenReturn("maria@example.com");

        when(bootcampPersonPersistencePort.findMostPopularBootcampId())
                .thenReturn(Mono.just(bootcampId));
        when(personPersistencePort.findPersonsByBootcampId(bootcampId))
                .thenReturn(Flux.just(person1, person2));

        // Act & Assert
        StepVerifier.create(getMostPopularEnrollmentsUseCase.execute(messageId))
                .assertNext(result -> {
                    assertThat(result).isNotNull();
                    assertThat(result.getBootcampId()).isEqualTo(bootcampId);
                    assertThat(result.getTotalEnrolled()).isEqualTo(2);
                    assertThat(result.getEnrolledPersons()).hasSize(2);

                    EnrolledPerson ep1 = result.getEnrolledPersons().getFirst();
                    assertThat(ep1.getName()).isEqualTo("Juan Pérez");
                    assertThat(ep1.getEmail()).isEqualTo("juan@example.com");

                    EnrolledPerson ep2 = result.getEnrolledPersons().get(1);
                    assertThat(ep2.getName()).isEqualTo("María Gómez");
                    assertThat(ep2.getEmail()).isEqualTo("maria@example.com");
                })
                .verifyComplete();

        // Verify interactions
        verify(bootcampPersonPersistencePort).findMostPopularBootcampId();
        verify(personPersistencePort).findPersonsByBootcampId(bootcampId);
    }

    @Test
    void shouldFailWhenNoBootcampsEnrolled() {
        // Arrange
        when(bootcampPersonPersistencePort.findMostPopularBootcampId())
                .thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(getMostPopularEnrollmentsUseCase.execute(messageId))
                .expectErrorSatisfies(error -> {
                    assertThat(error)
                            .isInstanceOf(BusinessException.class)
                            .hasMessage(TechnicalMessage.NO_BOOTCAMPS_ENROLLED.getMessage());
                })
                .verify();

        // Verify interactions
        verify(bootcampPersonPersistencePort).findMostPopularBootcampId();
        verify(personPersistencePort, never()).findPersonsByBootcampId(any());
    }
}
