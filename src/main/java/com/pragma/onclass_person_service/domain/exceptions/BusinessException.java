package com.pragma.onclass_person_service.domain.exceptions;

import com.pragma.onclass_person_service.domain.enums.TechnicalMessage;
import lombok.Getter;

@Getter
public class BusinessException extends ProcessorException{

    public BusinessException(String message, TechnicalMessage technicalMessage) {
        super(message, technicalMessage);
    }
}
