package com.pragma.onclass_person_service.domain.api;

import com.pragma.onclass_person_service.infraestructure.entrypoints.dto.PersonEnrollmentRequestDTO;
import reactor.core.publisher.Mono;

public interface EnrollPersonServicePort {

    Mono<Void> enrollPerson(PersonEnrollmentRequestDTO requestDTO, String messageId);
}
