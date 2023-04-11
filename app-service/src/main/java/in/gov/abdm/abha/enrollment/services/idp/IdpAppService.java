package in.gov.abdm.abha.enrollment.services.idp;

import in.gov.abdm.abha.enrollment.client.IdpAppFClient;
import in.gov.abdm.abha.enrollment.exception.idp.IdpGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.model.idp.idpverifyotpresponse.IdpVerifyOtpRequest;
import in.gov.abdm.abha.enrollment.model.idp.idpverifyotpresponse.IdpVerifyOtpResponse;
import in.gov.abdm.abha.enrollment.model.idp.sendotp.IdpSendOtpRequest;
import in.gov.abdm.abha.enrollment.model.idp.sendotp.IdpSendOtpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;
import static in.gov.abdm.abha.enrollment.constants.AbhaConstants.UTC_TIMEZONE_ID;

@Service
public class IdpAppService {
    @Autowired
    IdpAppFClient idpAppFClient;

    public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public Mono<IdpSendOtpResponse> sendOtp(IdpSendOtpRequest idpSendOtpRequest, String authorization, String timestamp, String hipRequestId, String requestId) {
        return idpAppFClient.sendOtp(idpSendOtpRequest, authorization, timestamp, hipRequestId, requestId).onErrorResume(throwable -> Mono.error(new IdpGatewayUnavailableException()));
    }

    public Mono<IdpVerifyOtpResponse> verifyOtp(IdpVerifyOtpRequest idpVerifyOtpRequest, String authorization, String timeStamp, String hipRequestId, String requestId) {
        return idpAppFClient.verifyOtp(idpVerifyOtpRequest, authorization, timeStamp, hipRequestId, requestId).onErrorResume(throwable -> Mono.error(new IdpGatewayUnavailableException()));
    }

    public Mono<Boolean> verifyAbhaAddressExists(String abhaAddress)
    {
        UUID requestId = UUID.randomUUID();
        SimpleDateFormat dateFormat = new SimpleDateFormat(TIMESTAMP_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone(UTC_TIMEZONE_ID));
        String timestamp = dateFormat.format(new Date());
        return idpAppFClient.verifyAbhaAddressExists(abhaAddress,requestId,timestamp)
                .onErrorResume(throwable -> Mono.error(new IdpGatewayUnavailableException()));
    }
}
