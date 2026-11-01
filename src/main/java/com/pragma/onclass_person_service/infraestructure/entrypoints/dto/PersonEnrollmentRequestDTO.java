package com.pragma.onclass_person_service.infraestructure.entrypoints.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PersonEnrollmentRequestDTO(
        @NotBlank(message = "El nombre no puede estar vacío")
        String firstName,

        @NotBlank(message = "El apellido no puede estar vacío")
        String lastName,

        @NotBlank(message = "El correo no puede estar vacío")
        @Email(message = "El formato del correo es inválido")
        String email,

        @Min(value = 18, message = "La edad mínima permitida es 18 años")
        Integer age,

        @Min(value = 18, message = "El número de documento no puede estar vacío")
        String documentNumber,

        @NotNull(message = "El ID del bootcamp es obligatorio")
        Long bootcampId
) {
}
