package com.pragma.onclass_person_service.infraestructure;

import com.pragma.onclass_person_service.infraestructure.entrypoints.handler.PersonHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class PersonRouter {

    @Bean
    public RouterFunction<ServerResponse> personRoutes(PersonHandler handler) {
        return route()
                .POST("/api/v1/persons/enroll", handler::enrollPerson)
                .build();
    }
}
