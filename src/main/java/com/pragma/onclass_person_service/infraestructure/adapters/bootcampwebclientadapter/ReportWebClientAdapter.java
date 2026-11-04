package com.pragma.onclass_person_service.infraestructure.adapters.bootcampwebclientadapter;

import com.pragma.onclass_person_service.domain.spi.ReportGatewayPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.scheduler.Schedulers;


@Slf4j
@Component
public class ReportWebClientAdapter implements ReportGatewayPort {

    private final WebClient webClient;

    public ReportWebClientAdapter(@Qualifier("reportWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public void notifyEnrollmentIncrement(Long bootcampId) {
        webClient.patch()
                .uri("/api/v1/reports/bootcamps/{bootcampId}/increment-enrollment", bootcampId)
                .retrieve()
                .toBodilessEntity()
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(
                        response -> log.info("Increase in enrollment announced for the bootcamp: {}", bootcampId),
                        error -> log.error("Error when notifying the reporting service of an increase in enrollment", error)
                );
    }
}
