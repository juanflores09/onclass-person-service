package com.pragma.onclass_person_service.infraestructure.entrypoints.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnrolledPersonDTO {

    private String name;
    private String email;
}
