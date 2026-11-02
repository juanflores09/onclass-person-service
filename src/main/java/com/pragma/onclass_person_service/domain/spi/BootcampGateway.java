package com.pragma.onclass_person_service.domain.spi;

import com.pragma.onclass_person_service.infraestructure.adapters.persistenceadapters.entity.Bootcamp;
import reactor.core.publisher.Flux;

import java.util.List;

public interface BootcampGateway {

    Flux<Bootcamp> getBootcampsDetails(List<Long> bootcampIds, String messageId);
}
