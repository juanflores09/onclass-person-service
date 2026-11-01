package com.pragma.onclass_person_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Person {

    private Long id;
    private String lastName;
    private String firstName;
    private String email;
    private String documentNumber;
    private int age;

    public Person(String lastName, String firstName, String email, String documentNumber, int age) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.email = email;
        this.documentNumber = documentNumber;
        this.age = age;
    }
}
