package in.gov.abdm.abha.enrollment.exception.application;

import lombok.Getter;

import java.util.Map;

@Getter
public class BadRequestException extends RuntimeException {
    private final Map<String, String> errors;

    public BadRequestException(Map<String, String> message) {
        errors = message;
    }
}
