package com.pragma.onclass_person_service.infraestructure.adapters.bootcampwebclientadapter.dto;

import java.time.LocalDate;

public record BootcampDetailsDTO(

        Long id,
        String name,
        String description,
        LocalDate launchDate,
        Integer durationInWeeks
) {

}
