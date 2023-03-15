package in.gov.abdm.abha.enrollment.utilities;

import in.gov.abdm.abha.enrollment.model.lgd.LgdDistrictResponse;
import in.gov.abdm.abha.enrollment.services.lgd.LgdAppService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Slf4j
public class LgdUtility {
    @Autowired
    LgdAppService lgdAppService;
    public static final String DISTRICT = "district";

    public Mono<List<LgdDistrictResponse>> getLgdData(String pinCode, String state) {
        return lgdAppService.getDetailsByAttribute(pinCode, DISTRICT, null)
                .flatMap(lgdDistrictResponses -> {
                    if (lgdDistrictResponses.isEmpty())
                        return getLgdByState(state);
                    return Mono.just(lgdDistrictResponses);
                });
    }

    public Mono<List<LgdDistrictResponse>> getLgdByState(String state) {
        log.info("Pin-code not found in lgd so trying with state name");
        return lgdAppService.getDetailsByAttribute(null, null, state);
    }
}
