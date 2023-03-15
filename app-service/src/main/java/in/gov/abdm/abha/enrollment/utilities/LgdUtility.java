package in.gov.abdm.abha.enrollment.utilities;

import in.gov.abdm.abha.enrollment.client.LgdAppFClient;
import in.gov.abdm.abha.enrollment.exception.lgd.LgdGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.model.lgd.LgdDistrictResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Slf4j
public class LgdUtility {
    @Autowired
    LgdAppFClient lgdAppFClient;

    public static final String DISTRICT = "district";

    public Mono<List<LgdDistrictResponse>> getLgdData(String pinCode, String state) {
        if (pinCode == null || pinCode.isBlank()) {
            return getDetailsByAttribute(null, null, state);
        }
        return getDetailsByAttribute(pinCode, DISTRICT, null)
                .flatMap(lgdDistrictResponses -> {
                    if (lgdDistrictResponses.isEmpty())
                        return getLgdByState(state);
                    return Mono.just(lgdDistrictResponses);
                });
    }

    public Mono<List<LgdDistrictResponse>> getLgdByState(String state) {
        log.info("Pin-code not found in lgd so trying with state name");
        return getDetailsByAttribute(null, null, state);
    }

    public Mono<List<LgdDistrictResponse>> getDetailsByAttribute(String pinCode, String district, String stateName) {
        if (stateName != null)
            return lgdAppFClient.getDetailsByAttributeState(stateName).onErrorResume(throwable -> Mono.error(new LgdGatewayUnavailableException()));
        return lgdAppFClient.getDetailsByAttribute(pinCode, district).onErrorResume(throwable -> Mono.error(new LgdGatewayUnavailableException()));
    }
}
