package com.pragma.onclass_person_service.infraestructure.adapters.persistenceadapters.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Bootcamp {


    private Long id;
    private String name;
    private LocalDate startDate;

    private Integer durationInWeeks;

    public LocalDate getEndDate() {
        if (startDate == null || durationInWeeks == null) {
            return null;
        }
        return startDate.plusWeeks(durationInWeeks);
    }

}
