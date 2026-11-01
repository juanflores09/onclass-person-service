package com.pragma.onclass_person_service.domain.spi;

import com.pragma.onclass_person_service.infraestructure.adapters.bootcampwebclientadapter.dto.BootcampDetailsDTO;
import reactor.core.publisher.Flux;

import java.util.List;

public interface BootcampGateway {

    Flux<BootcampDetailsDTO> getBootcampsDetails(List<Long> bootcampIds, String messageId);
}
