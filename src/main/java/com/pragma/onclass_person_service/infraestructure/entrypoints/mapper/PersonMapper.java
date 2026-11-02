package com.pragma.onclass_person_service.infraestructure.entrypoints.mapper;

import com.pragma.onclass_person_service.domain.model.EnrolledPerson;
import com.pragma.onclass_person_service.domain.model.MostPopularEnrollment;
import com.pragma.onclass_person_service.domain.model.Person;
import com.pragma.onclass_person_service.infraestructure.adapters.persistenceadapters.entity.PersonEntity;
import com.pragma.onclass_person_service.infraestructure.entrypoints.dto.EnrolledPersonDTO;
import com.pragma.onclass_person_service.infraestructure.entrypoints.dto.MostPopularEnrollmentResponseDTO;
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

    public Person personEntityToPerson(PersonEntity entity){
        return new Person(
                entity.getLastName(),
                entity.getFirstName(),
                entity.getEmail(),
                entity.getDocumentNumber(),
                entity.getAge()
        );
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

    public MostPopularEnrollmentResponseDTO mostPopularEnrollmentToResponseDTO(MostPopularEnrollment mostPopularEnrollment){
        return new MostPopularEnrollmentResponseDTO(
                mostPopularEnrollment.getBootcampId(),
                mostPopularEnrollment.getTotalEnrolled(),
                mostPopularEnrollment.getEnrolledPersons().stream()
                        .map(this::enrolledPersonToResponseDTO)
                        .toList()
        );
    }

    public EnrolledPersonDTO enrolledPersonToResponseDTO(EnrolledPerson enrolledPerson){
        return new EnrolledPersonDTO(
                enrolledPerson.getName(),
                enrolledPerson.getEmail()
        );
    }
}
