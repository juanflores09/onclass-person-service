package com.pragma.onclass_person_service.infraestructure.entrypoints.mapper;

import com.pragma.onclass_person_service.domain.model.Person;
import com.pragma.onclass_person_service.infraestructure.adapters.persistenceadapters.entity.PersonEntity;
import com.pragma.onclass_person_service.infraestructure.entrypoints.dto.PersonEnrollmentRequestDTO;
import org.springframework.stereotype.Component;

@Component
public class PersonMapper {

    public PersonEntity personToPersonEntity(Person person){
        PersonEntity personEntity = new PersonEntity();
        personEntity.setFirstName(person.getFirstName());
        personEntity.setLastName(person.getLastName());
        personEntity.setEmail(person.getEmail());
        personEntity.setDocumentNumber(person.getDocumentNumber());
        personEntity.setAge(person.getAge());
        return personEntity;
    }

    public Person personEnrollmentRequestDTOToPerson(PersonEnrollmentRequestDTO enrollmentRequestDTO){
        return new Person(
                enrollmentRequestDTO.lastName(),
                enrollmentRequestDTO.firstName(),
                enrollmentRequestDTO.email(),
                enrollmentRequestDTO.documentNumber(),
                enrollmentRequestDTO.age()
        );
    }
}
