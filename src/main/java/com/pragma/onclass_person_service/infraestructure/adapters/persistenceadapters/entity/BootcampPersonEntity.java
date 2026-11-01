package com.pragma.onclass_person_service.infraestructure.adapters.persistenceadapters.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("bootcamp_person")
public class BootcampPersonEntity {

    @Id
    private Long id;
    private Long personId;
    private Long bootcampId;

    public BootcampPersonEntity(Long personId, Long bootcampId) {
        this.personId = personId;
        this.bootcampId = bootcampId;
    }
}
