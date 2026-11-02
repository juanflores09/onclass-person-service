package com.pragma.onclass_person_service.infraestructure.entrypoints.mapper;

import com.pragma.onclass_person_service.domain.model.EnrolledPerson;
import com.pragma.onclass_person_service.domain.model.MostPopularEnrollment;
import com.pragma.onclass_person_service.domain.model.Person;
import com.pragma.onclass_person_service.infraestructure.adapters.persistenceadapters.entity.PersonEntity;
import com.pragma.onclass_person_service.infraestructure.entrypoints.dto.EnrolledPersonDTO;
import com.pragma.onclass_person_service.infraestructure.entrypoints.dto.MostPopularEnrollmentResponseDTO;
import com.pragma.onclass_person_service.infraestructure.entrypoints.dto.PersonEnrollmentRequestDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PersonMapperTest {

    @InjectMocks
    private PersonMapper personMapper;

    @Test
    void personToPersonEntity_Success() {
        // Given
        Person person = new Person("Pérez", "Juan", "juan@example.com", "12345678", 25);

        // When
        PersonEntity entity = personMapper.personToPersonEntity(person);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getFirstName()).isEqualTo("Juan");
        assertThat(entity.getLastName()).isEqualTo("Pérez");
        assertThat(entity.getEmail()).isEqualTo("juan@example.com");
        assertThat(entity.getDocumentNumber()).isEqualTo("12345678");
        assertThat(entity.getAge()).isEqualTo(25);
    }

    @Test
    void personEntityToPerson_Success() {
        // Given
        PersonEntity entity = new PersonEntity();
        entity.setFirstName("Juan");
        entity.setLastName("Pérez");
        entity.setEmail("juan@example.com");
        entity.setDocumentNumber("12345678");
        entity.setAge(25);

        // When
        Person person = personMapper.personEntityToPerson(entity);

        // Then
        assertThat(person).isNotNull();
        assertThat(person.getFirstName()).isEqualTo("Juan");
        assertThat(person.getLastName()).isEqualTo("Pérez");
        assertThat(person.getEmail()).isEqualTo("juan@example.com");
        assertThat(person.getDocumentNumber()).isEqualTo("12345678");
        assertThat(person.getAge()).isEqualTo(25);
    }

    @Test
    void personEnrollmentRequestDTOToPerson_Success() {
        // Given
        PersonEnrollmentRequestDTO dto = new PersonEnrollmentRequestDTO(
                "Juan", "Pérez", "juan@example.com",  25, "789456",1L
        );

        // When
        Person person = personMapper.personEnrollmentRequestDTOToPerson(dto);

        // Then
        assertThat(person).isNotNull();
        assertThat(person.getFirstName()).isEqualTo("Juan");
        assertThat(person.getLastName()).isEqualTo("Pérez");
        assertThat(person.getEmail()).isEqualTo("juan@example.com");
        assertThat(person.getDocumentNumber()).isEqualTo("789456");
        assertThat(person.getAge()).isEqualTo(25);
    }

    @Test
    void mostPopularEnrollmentToResponseDTO_Success() {
        // Given
        EnrolledPerson enrolledPerson = new EnrolledPerson("Juan Pérez", "juan@example.com");
        MostPopularEnrollment model = new MostPopularEnrollment(1L, 10, List.of(enrolledPerson));

        // When
        MostPopularEnrollmentResponseDTO responseDTO = personMapper.mostPopularEnrollmentToResponseDTO(model);

        // Then
        assertThat(responseDTO).isNotNull();
        assertThat(responseDTO.getBootcampId()).isEqualTo(1L);
        assertThat(responseDTO.getTotalEnrolled()).isEqualTo(10L);
        assertThat(responseDTO.getEnrolledPersons()).hasSize(1);
        assertThat(responseDTO.getEnrolledPersons().getFirst().getName()).isEqualTo("Juan Pérez");
        assertThat(responseDTO.getEnrolledPersons().getFirst().getEmail()).isEqualTo("juan@example.com");
    }

    @Test
    void enrolledPersonToResponseDTO_Success() {
        // Given
        EnrolledPerson enrolledPerson = new EnrolledPerson("María Gómez", "maria@example.com");

        // When
        EnrolledPersonDTO dto = personMapper.enrolledPersonToResponseDTO(enrolledPerson);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getName()).isEqualTo("María Gómez");
        assertThat(dto.getEmail()).isEqualTo("maria@example.com");
    }
}
