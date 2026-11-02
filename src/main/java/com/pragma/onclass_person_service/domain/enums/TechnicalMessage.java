package com.pragma.onclass_person_service.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TechnicalMessage {

    INTERNAL_ERROR("500", "Something went wrong, please try again"),
    INVALID_PARAMETERS("400", "Parámetros inválidos"),
    MAX_BOOTCAMPS_EXCEEDED("MAX_BOOTCAMPS_EXCEEDED", "La persona ha alcanzado el límite máximo de 5 bootcamps activos"),
    ALREADY_ENROLLED("ALREADY_ENROLLED", "La persona ya está inscrita en este bootcamp"),
    DATE_OVERLAP("DATE_OVERLAP", "Las fechas y duración del bootcamp se cruzan con otra inscripción activa"),
    NO_BOOTCAMPS_ENROLLED("NO_BOOTCAMPS_ENROLLED", "No existen inscripciones a bootcamps registradas en el sistema");

    private final String code;
    private final String message;

}
