package in.gov.abdm.abha.enrollment.exception.application;

import lombok.Getter;

import java.util.LinkedHashMap;

@Getter
public class BadRequestException extends RuntimeException {
    private LinkedHashMap<String, String> errors;

    public BadRequestException(LinkedHashMap<String, String> message) {
        errors = message;
    }
}
