package com.pragma.onclass_person_service.infraestructure.adapters.persistenceadapters.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("person")
public class PersonEntity {

    @Id
    private Long id;
    private String lastName;
    private String firstName;
    private String email;
    private String documentNumber;
    private int age;
}
