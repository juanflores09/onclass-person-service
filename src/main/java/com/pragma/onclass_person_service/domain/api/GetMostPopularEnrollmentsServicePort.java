package com.pragma.onclass_person_service.domain.api;

import com.pragma.onclass_person_service.domain.model.MostPopularEnrollment;
import reactor.core.publisher.Mono;

public interface GetMostPopularEnrollmentsServicePort {

    Mono<MostPopularEnrollment> execute(String messageId);
}
