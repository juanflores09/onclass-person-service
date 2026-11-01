package com.pragma.onclass_person_service.infraestructure.adapters.bootcampwebclientadapter.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BootcampDetailsDTO {

    private Long id;
    private String name;

    @JsonProperty("launchDate")
    private LocalDate startDate;

    private Integer durationInWeeks;

    public LocalDate getEndDate() {
        if (startDate == null || durationInWeeks == null) {
            return null;
        }
        return startDate.plusWeeks(durationInWeeks);
    }

}
