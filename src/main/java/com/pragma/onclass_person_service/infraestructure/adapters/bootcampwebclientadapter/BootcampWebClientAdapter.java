package com.pragma.onclass_person_service.infraestructure.adapters.bootcampwebclientadapter;

import com.pragma.onclass_person_service.domain.exceptions.ExternalServiceException;
import com.pragma.onclass_person_service.domain.spi.BootcampGateway;
import com.pragma.onclass_person_service.infraestructure.adapters.bootcampwebclientadapter.dto.BootcampDetailsDTO;
import com.pragma.onclass_person_service.infraestructure.adapters.bootcampwebclientadapter.dto.BootcampIdsRequestDTO;
import com.pragma.onclass_person_service.infraestructure.adapters.persistenceadapters.entity.Bootcamp;
import com.pragma.onclass_person_service.infraestructure.entrypoints.dto.ApiResponseDTO;
import com.pragma.onclass_person_service.infraestructure.entrypoints.mapper.BootcampMapper;
import com.pragma.onclass_person_service.infraestructure.entrypoints.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BootcampWebClientAdapter implements BootcampGateway {

    private final WebClient bootcampWebClient;
    private final BootcampMapper bootcampMapper;

    @Override
    public Flux<Bootcamp> getBootcampsDetails(List<Long> bootcampIds, String messageId) {
        return bootcampWebClient.post()
                .uri("/api/v1/bootcamps/batch")
                .header(Constants.X_MESSAGE_ID, messageId)
                .bodyValue(new BootcampIdsRequestDTO(bootcampIds))
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(ApiResponseDTO.class)
                                .flatMap(errorResponse -> Mono.error(new ExternalServiceException(errorResponse)))
                )
                .bodyToFlux(BootcampDetailsDTO.class)
                .map(bootcampMapper::bootcampDetailDtoToBootcamp);
    }

}
