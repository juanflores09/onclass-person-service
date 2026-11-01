package com.pragma.onclass_person_service.infraestructure.entrypoints.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreatePersonRequestDTO(

        @NotBlank(message = "El nombre no puede estar vacío")
        @Size(max = 50, message = "El tamaño máximo del nombre debe ser de 50 caracteres")
        String lastName,

        @NotBlank(message = "El nombre no puede estar vacío")
        @Size(max = 50, message = "El tamaño máximo del apellido debe ser de 50 caracteres")
        String firstName,

        @NotBlank(message = "El email no puede estar vacío")
        @Email(message = "El formato del email es inválido")
        @Size(max = 100, message = "El tamaño máximo del email debe ser de 100 caracteres")
        String email,

        @NotBlank(message = "El número de documento no puede estar vacío")
        @Size(min = 8, max = 8, message = "El número de documento debe tener exactamente 8 caracteres")
        @Pattern(regexp = "^\\d{8}$", message = "El número de documento debe contener solo dígitos")
        String documentNumber,

        @NotBlank(message = "La edad no puede estar vacío")
        int age
) {
}
