package com.pragma.onclass_person_service.infraestructure.entrypoints.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MostPopularEnrollmentResponseDTO {

    private Long bootcampId;
    private int totalEnrolled;
    private List<EnrolledPersonDTO> enrolledPersons;

}
