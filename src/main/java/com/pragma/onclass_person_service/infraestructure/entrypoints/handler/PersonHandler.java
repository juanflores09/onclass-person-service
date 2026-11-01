package com.pragma.onclass_person_service.infraestructure.entrypoints.handler;

import com.pragma.onclass_person_service.domain.api.EnrollPersonServicePort;
import com.pragma.onclass_person_service.domain.enums.TechnicalMessage;
import com.pragma.onclass_person_service.domain.exceptions.BusinessException;
import com.pragma.onclass_person_service.infraestructure.entrypoints.dto.ApiResponseDTO;
import com.pragma.onclass_person_service.infraestructure.entrypoints.dto.PersonEnrollmentRequestDTO;
import com.pragma.onclass_person_service.infraestructure.entrypoints.util.Constants;
import com.pragma.onclass_person_service.infraestructure.entrypoints.util.ErrorDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class PersonHandler {

    private final EnrollPersonServicePort enrollPersonServicePort;
    private final Validator validator;

    public Mono<ServerResponse> enrollPerson(ServerRequest request) {
        String messageId = request.headers().firstHeader(Constants.X_MESSAGE_ID);

        return request.bodyToMono(PersonEnrollmentRequestDTO.class)
                .flatMap(requestDTO -> {

                    Set<ConstraintViolation<PersonEnrollmentRequestDTO>> violations = validator.validate(requestDTO);
                    if (!violations.isEmpty()) {
                        String errorMessage = violations.iterator().next().getMessage();
                        return Mono.error(new BusinessException(errorMessage, TechnicalMessage.INVALID_PARAMETERS));
                    }

                    return enrollPersonServicePort.enrollPerson(requestDTO, messageId)
                            .doOnSuccess(unused -> log.info("Person successfully enrolled with email: {} to bootcampId: {} with messageId: {}",
                                    requestDTO.email(), requestDTO.bootcampId(), messageId))
                            .then(ServerResponse.status(HttpStatus.CREATED).build());
                })
                .contextWrite(Context.of(Constants.X_MESSAGE_ID, Objects.requireNonNull(messageId)))
                .onErrorResume(BusinessException.class, ex -> buildErrorResponse(
                        HttpStatus.BAD_REQUEST,
                        messageId,
                        ex.getTechnicalMessage(),
                        List.of(ErrorDTO.builder()
                                .code(ex.getTechnicalMessage().getCode())
                                .message(ex.getTechnicalMessage().getMessage())
                                .build())
                ))
                .onErrorResume(ex -> {
                    log.error("[enrollPerson] Unexpected error occurred with messageId: {}", messageId, ex);
                    return buildErrorResponse(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            messageId,
                            TechnicalMessage.INTERNAL_ERROR,
                            List.of(ErrorDTO.builder()
                                    .code(TechnicalMessage.INTERNAL_ERROR.getCode())
                                    .message(TechnicalMessage.INTERNAL_ERROR.getMessage())
                                    .build())
                    );
                });
    }

    private Mono<ServerResponse> buildErrorResponse(HttpStatus httpStatus, String identifier,
                                                    TechnicalMessage error, List<ErrorDTO> errors) {
        return Mono.defer(() -> {
            ApiResponseDTO apiErrorResponse = ApiResponseDTO.builder()
                    .code(error.getCode())
                    .message(error.getMessage())
                    .identifier(identifier)
                    .date(Instant.now().toString())
                    .errors(errors)
                    .build();
            return ServerResponse.status(httpStatus).bodyValue(apiErrorResponse);
        });
    }
}
