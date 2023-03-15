package in.gov.abdm.abha.enrollment.services.lgd;

import in.gov.abdm.abha.enrollment.client.LgdAppFClient;
import in.gov.abdm.abha.enrollment.exception.lgd.LgdGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.model.lgd.LgdDistrictResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class LgdAppService {

    @Autowired
    LgdAppFClient lgdAppFClient;

    public Mono<List<LgdDistrictResponse>> getDetailsByAttribute(String pinCode, String district, String stateName) {
        if (stateName != null)
            return lgdAppFClient.getDetailsByAttributeState(stateName).onErrorResume(throwable -> Mono.error(new LgdGatewayUnavailableException()));
        return lgdAppFClient.getDetailsByAttribute(pinCode, district).onErrorResume(throwable -> Mono.error(new LgdGatewayUnavailableException()));
    }
}
