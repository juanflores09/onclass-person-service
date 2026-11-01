package com.pragma.onclass_person_service.application.config;

import com.pragma.onclass_person_service.domain.api.EnrollPersonServicePort;
import com.pragma.onclass_person_service.domain.spi.BootcampGateway;
import com.pragma.onclass_person_service.domain.spi.BootcampPersonPersistencePort;
import com.pragma.onclass_person_service.domain.spi.PersonPersistencePort;
import com.pragma.onclass_person_service.domain.usecase.EnrollPersonUseCase;
import com.pragma.onclass_person_service.infraestructure.entrypoints.mapper.PersonMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public EnrollPersonServicePort enrollPersonServicePort(
            PersonPersistencePort personPersistencePort,
            BootcampPersonPersistencePort bootcampPersonPersistencePort,
            BootcampGateway bootcampGateway,
            PersonMapper personMapper
    ){
        return new EnrollPersonUseCase(personPersistencePort, bootcampPersonPersistencePort,
                bootcampGateway, personMapper);
    }
}
