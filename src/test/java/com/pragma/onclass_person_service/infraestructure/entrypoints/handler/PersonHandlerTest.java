package com.pragma.onclass_person_service.infraestructure.entrypoints.handler;

import com.pragma.onclass_person_service.domain.api.EnrollPersonServicePort;
import com.pragma.onclass_person_service.domain.api.GetMostPopularEnrollmentsServicePort;
import com.pragma.onclass_person_service.domain.enums.TechnicalMessage;
import com.pragma.onclass_person_service.domain.exceptions.BusinessException;
import com.pragma.onclass_person_service.domain.model.MostPopularEnrollment;
import com.pragma.onclass_person_service.domain.model.Person;
import com.pragma.onclass_person_service.infraestructure.entrypoints.dto.MostPopularEnrollmentResponseDTO;
import com.pragma.onclass_person_service.infraestructure.entrypoints.dto.PersonEnrollmentRequestDTO;
import com.pragma.onclass_person_service.infraestructure.entrypoints.mapper.PersonMapper;
import com.pragma.onclass_person_service.infraestructure.entrypoints.util.Constants;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class PersonHandlerTest {

    @Mock
    private EnrollPersonServicePort enrollPersonServicePort;

    @Mock
    private GetMostPopularEnrollmentsServicePort getMostPopularEnrollmentsServicePort;

    @Mock
    private PersonMapper personMapper;

    @Mock
    private Validator validator;

    @Mock
    private ServerRequest serverRequest;

    @Mock
    private ServerRequest.Headers headers;

    @InjectMocks
    private PersonHandler personHandler;

    @BeforeEach
    void setUp() {
        lenient().when(serverRequest.headers()).thenReturn(headers);
        lenient().when(headers.firstHeader(Constants.X_MESSAGE_ID)).thenReturn("msg-12345");
    }

    @Test
    void enrollPerson_Success() {
        // Given
        PersonEnrollmentRequestDTO requestDTO = new PersonEnrollmentRequestDTO(
                "Juan", "Pérez", "juan@example.com", 25,"123456",1L
        );
        Person person = mock(Person.class);

        when(serverRequest.bodyToMono(PersonEnrollmentRequestDTO.class)).thenReturn(Mono.just(requestDTO));
        when(validator.validate(requestDTO)).thenReturn(Collections.emptySet());
        when(personMapper.personEnrollmentRequestDTOToPerson(requestDTO)).thenReturn(person);
        when(enrollPersonServicePort.enrollPerson(person, 1L, "msg-12345"))
                .thenReturn(Mono.empty());

        // When
        Mono<ServerResponse> responseMono = personHandler.enrollPerson(serverRequest);

        // Then
        StepVerifier.create(responseMono)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.CREATED))
                .verifyComplete();

        verify(enrollPersonServicePort, times(1)).enrollPerson(person, 1L, "msg-12345");
    }

    @Test
    void enrollPerson_ValidationFailed_ReturnsBadRequest() {
        // Given
        PersonEnrollmentRequestDTO requestDTO = new PersonEnrollmentRequestDTO("", "", "invalid-email", 15,"123",null);
        ConstraintViolation<PersonEnrollmentRequestDTO> violation = mock(ConstraintViolation.class);
        when(violation.getMessage()).thenReturn("El correo no puede estar vacío");

        when(serverRequest.bodyToMono(PersonEnrollmentRequestDTO.class)).thenReturn(Mono.just(requestDTO));
        when(validator.validate(requestDTO)).thenReturn(Set.of(violation));

        // When
        Mono<ServerResponse> responseMono = personHandler.enrollPerson(serverRequest);

        // Then
        StepVerifier.create(responseMono)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.BAD_REQUEST))
                .verifyComplete();

        verify(enrollPersonServicePort, never()).enrollPerson(any(), any(), any());
    }

    @Test
    void enrollPerson_BusinessException_ReturnsBadRequest() {
        // Given
        PersonEnrollmentRequestDTO requestDTO = new PersonEnrollmentRequestDTO(
                "Juan", "Pérez", "juan@example.com", 28, "123456",1L
        );
        Person person = mock(Person.class);

        when(serverRequest.bodyToMono(PersonEnrollmentRequestDTO.class)).thenReturn(Mono.just(requestDTO));
        when(validator.validate(requestDTO)).thenReturn(Collections.emptySet());
        when(personMapper.personEnrollmentRequestDTOToPerson(requestDTO)).thenReturn(person);
        when(enrollPersonServicePort.enrollPerson(any(), any(), any()))
                .thenReturn(Mono.error(new BusinessException(TechnicalMessage.ALREADY_ENROLLED)));

        // When
        Mono<ServerResponse> responseMono = personHandler.enrollPerson(serverRequest);

        // Then
        StepVerifier.create(responseMono)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.BAD_REQUEST))
                .verifyComplete();
    }

    @Test
    void enrollPerson_UnexpectedException_ReturnsInternalServerError() {
        // Given
        PersonEnrollmentRequestDTO requestDTO = new PersonEnrollmentRequestDTO(
                "Juan", "Pérez", "juan@example.com", 24, "78456123", 1L
        );
        Person person = mock(Person.class);

        when(serverRequest.bodyToMono(PersonEnrollmentRequestDTO.class)).thenReturn(Mono.just(requestDTO));
        when(validator.validate(requestDTO)).thenReturn(Collections.emptySet());
        when(personMapper.personEnrollmentRequestDTOToPerson(requestDTO)).thenReturn(person);
        when(enrollPersonServicePort.enrollPerson(any(), any(), any()))
                .thenReturn(Mono.error(new RuntimeException("Database timeout")));

        // When
        Mono<ServerResponse> responseMono = personHandler.enrollPerson(serverRequest);

        // Then
        StepVerifier.create(responseMono)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR))
                .verifyComplete();
    }

    @Test
    void getMostPopularEnrollments_Success() {
        // Given
        MostPopularEnrollment enrollment = mock(MostPopularEnrollment.class);

        // Creamos la variable previamente para evitar crear mocks inline en thenReturn (cumpliendo SonarQube)
        MostPopularEnrollmentResponseDTO responseDTO = mock(MostPopularEnrollmentResponseDTO.class);

        when(getMostPopularEnrollmentsServicePort.execute("msg-12345"))
                .thenReturn(Mono.just(enrollment));
        when(personMapper.mostPopularEnrollmentToResponseDTO(enrollment))
                .thenReturn(responseDTO);

        // When
        Mono<ServerResponse> responseMono = personHandler.getMostPopularEnrollments(serverRequest);

        // Then
        StepVerifier.create(responseMono)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.OK))
                .verifyComplete();

        verify(getMostPopularEnrollmentsServicePort, times(1)).execute("msg-12345");
    }

    @Test
    void getMostPopularEnrollments_BusinessException_ReturnsBadRequest() {
        // Given
        when(getMostPopularEnrollmentsServicePort.execute("msg-12345"))
                .thenReturn(Mono.error(new BusinessException(TechnicalMessage.NO_BOOTCAMPS_ENROLLED)));

        // When
        Mono<ServerResponse> responseMono = personHandler.getMostPopularEnrollments(serverRequest);

        // Then
        StepVerifier.create(responseMono)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.BAD_REQUEST))
                .verifyComplete();
    }

    @Test
    void getMostPopularEnrollments_UnexpectedException_ReturnsInternalServerError() {
        // Given
        when(getMostPopularEnrollmentsServicePort.execute("msg-12345"))
                .thenReturn(Mono.error(new RuntimeException("Unexpected error")));

        // When
        Mono<ServerResponse> responseMono = personHandler.getMostPopularEnrollments(serverRequest);

        // Then
        StepVerifier.create(responseMono)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR))
                .verifyComplete();
    }
}
