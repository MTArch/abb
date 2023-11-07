package in.gov.abdm.abha.enrollment.utilities;

import in.gov.abdm.abha.enrollment.client.LgdAppFClient;
import in.gov.abdm.abha.enrollment.exception.lgd.LgdGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.model.lgd.LgdDistrictResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
@Slf4j
public class LgdUtility {
    @Autowired
    LgdAppFClient lgdAppFClient;

    public static final String DISTRICT = "district";
    private static final String LGD_ERROR_MESSAGE = "LGD service error {}";

    public Mono<List<LgdDistrictResponse>> getLgdData(String pinCode, String state) {
        if(pinCode.isEmpty() && Objects.isNull(state)){
            return Mono.empty();
        }
        if (pinCode == null || pinCode.isBlank()) {
            return getDetailsByAttribute(null, null, state);
        }
        return getDetailsByAttribute(pinCode, DISTRICT, null)
                .flatMap(lgdDistrictResponses -> {
                    if (lgdDistrictResponses.isEmpty())
                        return getLgdByState(state);
                    return Mono.just(lgdDistrictResponses);
                }).switchIfEmpty(getLgdByState(state));
    }

    public Mono<List<LgdDistrictResponse>> getLgdByState(String state) {
        log.info("Pin-code not found in lgd so trying with state name");
        return getDetailsByAttribute(null, null, state);
    }

    public Mono<List<LgdDistrictResponse>> getDetailsByAttribute(String pinCode, String district, String stateName) {
        if (stateName != null)
            return lgdAppFClient.getDetailsByAttributeState(UUID.randomUUID(), Common.isoTimestamp(), stateName).onErrorResume(throwable -> {
                log.error(LGD_ERROR_MESSAGE, throwable.getMessage());
                return Mono.error(new LgdGatewayUnavailableException());
            });
        return lgdAppFClient.getDetailsByAttribute(UUID.randomUUID(), Common.isoTimestamp(), pinCode, district).onErrorResume(throwable -> {
            log.error(LGD_ERROR_MESSAGE, throwable.getMessage());
            return Mono.empty();
        });
    }

    public Mono<List<LgdDistrictResponse>> getDistrictCode(String districtCode) {
        return lgdAppFClient.getByDistrictCode(UUID.randomUUID(), Common.isoTimestamp(), districtCode)
                .onErrorResume(throwable -> {
                    log.error(LGD_ERROR_MESSAGE, throwable.getMessage());
                    return Mono.error(new LgdGatewayUnavailableException()); // Create without the original exception
                });
    }
}