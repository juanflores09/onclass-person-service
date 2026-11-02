package com.pragma.onclass_person_service.infraestructure.entrypoints.mapper;

import com.pragma.onclass_person_service.infraestructure.adapters.bootcampwebclientadapter.dto.BootcampDetailsDTO;
import com.pragma.onclass_person_service.infraestructure.adapters.persistenceadapters.entity.Bootcamp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class BootcampMapperTest {

    @InjectMocks
    private BootcampMapper bootcampMapper;

    @Test
    void bootcampDetailDtoToBootcamp_Success() {
        // Given
        LocalDate startDate = LocalDate.of(2026, 6, 1);
        BootcampDetailsDTO dto = new BootcampDetailsDTO(1L, "Java Bootcamp", "desc",startDate, 12);

        // When
        Bootcamp bootcamp = bootcampMapper.bootcampDetailDtoToBootcamp(dto);

        // Then
        assertThat(bootcamp).isNotNull();
        assertThat(bootcamp.getId()).isEqualTo(1L);
        assertThat(bootcamp.getName()).isEqualTo("Java Bootcamp");
        assertThat(bootcamp.getStartDate()).isEqualTo(startDate);
        assertThat(bootcamp.getDurationInWeeks()).isEqualTo(12);
    }
}
