package com.pragma.onclass_person_service.infraestructure.entrypoints.mapper;

import com.pragma.onclass_person_service.infraestructure.adapters.bootcampwebclientadapter.dto.BootcampDetailsDTO;
import com.pragma.onclass_person_service.infraestructure.adapters.persistenceadapters.entity.Bootcamp;
import org.springframework.stereotype.Component;

@Component
public class BootcampMapper {

    public Bootcamp bootcampDetailDtoToBootcamp(BootcampDetailsDTO dto){

        return new Bootcamp(
                dto.id(),
                dto.name(),
                dto.launchDate(),
                dto.durationInWeeks()
        );
    }
}
