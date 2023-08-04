package in.gov.abdm.abha.enrollment.exception.application;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class BadRequestException extends RuntimeException {
    private final Map<String, String> errors;

    public BadRequestException(Map<String, String> message) {
        errors = new HashMap<>();
        for (Map.Entry<String, String> mapEntry : message.entrySet()) {
            errors.put(mapEntry.getKey(), mapEntry.getValue());
        }
    }
}
