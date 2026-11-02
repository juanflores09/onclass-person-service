package com.pragma.onclass_person_service.domain.usecase;

import com.pragma.onclass_person_service.domain.exceptions.BusinessException;
import com.pragma.onclass_person_service.domain.model.Person;
import com.pragma.onclass_person_service.domain.spi.BootcampGateway;
import com.pragma.onclass_person_service.domain.spi.BootcampPersonPersistencePort;
import com.pragma.onclass_person_service.domain.spi.PersonPersistencePort;
import com.pragma.onclass_person_service.infraestructure.adapters.persistenceadapters.entity.Bootcamp;
import com.pragma.onclass_person_service.infraestructure.entrypoints.util.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollPersonUseCaseTest {

    @Mock
    private PersonPersistencePort personPersistencePort;

    @Mock
    private BootcampPersonPersistencePort bootcampPersonPersistencePort;

    @Mock
    private BootcampGateway bootcampGateway;

    @InjectMocks
    private EnrollPersonUseCase enrollPersonUseCase;

    private final String messageId = "msg-12345";
    private final Long personId = 100L;
    private final Long bootcampId = 1L;

    private Person person;

    @BeforeEach
    void setUp(){
        person = new Person();
        person.setLastName("Flores");
        person.setFirstName("Juan");
        person.setEmail("juan@example.com");
    }
    @Test
    void shouldEnrollPersonSuccessfully() {
        // Arrange
        LocalDate startDate = LocalDate.of(2026, 6, 1);
        Bootcamp targetBootcamp = new Bootcamp(bootcampId, "Java", startDate, 6);

        when(personPersistencePort.saveOrGetPerson(person)).thenReturn(Mono.just(personId));
        when(bootcampPersonPersistencePort.findBootcampIdsByPersonId(personId)).thenReturn(Flux.empty());
        when(bootcampGateway.getBootcampsDetails(List.of(bootcampId), messageId))
                .thenReturn(Flux.just(targetBootcamp));
        when(bootcampPersonPersistencePort.saveEnrollment(personId, bootcampId)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(enrollPersonUseCase.enrollPerson(person, bootcampId, messageId))
                .verifyComplete();

        // Verify interactions
        verify(personPersistencePort).saveOrGetPerson(person);
        verify(bootcampPersonPersistencePort).findBootcampIdsByPersonId(personId);
        verify(bootcampGateway).getBootcampsDetails(List.of(bootcampId), messageId);
        verify(bootcampPersonPersistencePort).saveEnrollment(personId, bootcampId);
    }

    @Test
    void shouldFailWhenMaxBootcampsExceeded() {
        // Arrange
        List<Long> activeBootcamps = List.of(10L, 11L, 12L, 13L, 14L); // 5 bootcamps

        when(personPersistencePort.saveOrGetPerson(person)).thenReturn(Mono.just(personId));
        when(bootcampPersonPersistencePort.findBootcampIdsByPersonId(personId))
                .thenReturn(Flux.fromIterable(activeBootcamps));

        // Act & Assert
        StepVerifier.create(enrollPersonUseCase.enrollPerson(person, bootcampId, messageId))
                .expectErrorSatisfies(error -> {
                    assertThat(error)
                            .isInstanceOf(BusinessException.class)
                            .hasMessage(Constants.MESSAGE_ERROR_ENROLL);
                })
                .verify();

        verify(personPersistencePort).saveOrGetPerson(person);
        verify(bootcampPersonPersistencePort).findBootcampIdsByPersonId(personId);
        verify(bootcampGateway, never()).getBootcampsDetails(any(), any());
        verify(bootcampPersonPersistencePort, never()).saveEnrollment(any(), any());
    }

    @Test
    void shouldFailWhenAlreadyEnrolled() {
        // Arrange
        List<Long> activeBootcamps = List.of(1L, 2L); // Contiene el bootcampId (1L)

        when(personPersistencePort.saveOrGetPerson(person)).thenReturn(Mono.just(personId));
        when(bootcampPersonPersistencePort.findBootcampIdsByPersonId(personId))
                .thenReturn(Flux.fromIterable(activeBootcamps));

        // Act & Assert
        StepVerifier.create(enrollPersonUseCase.enrollPerson(person, bootcampId, messageId))
                .expectErrorSatisfies(error -> {
                    assertThat(error)
                            .isInstanceOf(BusinessException.class)
                            .hasMessage(Constants.MESSAGE_ERROR_ENROLL);
                })
                .verify();

        verify(bootcampGateway, never()).getBootcampsDetails(any(), any());
        verify(bootcampPersonPersistencePort, never()).saveEnrollment(any(), any());
    }

    @Test
    void shouldFailWhenDateOverlapOccurs() {
        // Arrange
        Long activeBootcampId = 2L;
        List<Long> activeBootcampIds = List.of(activeBootcampId);

        // Bootcamp activo que se cruza en fechas (Junio 15 al Julio 15)
        Bootcamp activeBootcamp = new Bootcamp(activeBootcampId, "Python",
                LocalDate.of(2026, 6, 15), 6);

        // Bootcamp objetivo (Junio 1 al Junio 30) -> Hay solapamiento
        Bootcamp targetBootcamp = new Bootcamp(bootcampId, "Java",
                LocalDate.of(2026, 6, 1), 6);

        when(personPersistencePort.saveOrGetPerson(person)).thenReturn(Mono.just(personId));
        when(bootcampPersonPersistencePort.findBootcampIdsByPersonId(personId))
                .thenReturn(Flux.fromIterable(activeBootcampIds));
        when(bootcampGateway.getBootcampsDetails(any(), eq(messageId)))
                .thenReturn(Flux.just(activeBootcamp, targetBootcamp));

        // Act & Assert
        StepVerifier.create(enrollPersonUseCase.enrollPerson(person, bootcampId, messageId))
                .expectErrorSatisfies(error -> {
                    assertThat(error)
                            .isInstanceOf(BusinessException.class)
                            .hasMessage(Constants.MESSAGE_ERROR_ENROLL);
                })
                .verify();

        verify(bootcampGateway).getBootcampsDetails(any(), eq(messageId));
        verify(bootcampPersonPersistencePort, never()).saveEnrollment(any(), any());
    }
}
