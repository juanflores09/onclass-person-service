package com.pragma.onclass_person_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MostPopularEnrollment {

    private Long bootcampId;
    private int totalEnrolled;
    private List<EnrolledPerson> enrolledPersons;

}
