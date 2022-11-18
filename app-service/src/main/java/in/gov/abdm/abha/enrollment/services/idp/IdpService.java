package in.gov.abdm.abha.enrollment.services.idp;


import in.gov.abdm.abha.enrollment.client.IdpClient;
import in.gov.abdm.abha.enrollment.enums.LoginHint;
import in.gov.abdm.abha.enrollment.model.idp.sendotp.IdpSendOtpRequest;
import in.gov.abdm.abha.enrollment.model.idp.sendotp.IdpSendOtpResponse;
import in.gov.abdm.abha.enrollment.model.idp.sendotp.Parameters;
import in.gov.abdm.abha.enrollment.model.otp_request.MobileOrEmailOtpRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
/**
 * It is Service class IdpService
 */
public class IdpService {

    public static final String OTP_SCOPE = "OTP";
    public static final String ABHA_NUMBER_KEY = "abhaNumber";
    public static final String MOBILE_NUMBER_KEY = "mobileNumber";

    @Autowired
    IdpClient idpClient;

    public Mono<IdpSendOtpResponse> sendOtp(MobileOrEmailOtpRequestDto mobileOrEmailOtpRequestDto) {
        IdpSendOtpRequest idpSendOtpRequest = new IdpSendOtpRequest();
        Parameters parameters = new Parameters();
        if (mobileOrEmailOtpRequestDto.getLoginHint().equals(LoginHint.ABHA_NUMBER.getValue())) {
            parameters.setKey(ABHA_NUMBER_KEY);
        } else if (mobileOrEmailOtpRequestDto.getLoginHint().equals(LoginHint.MOBILE.getValue())) {
            parameters.setKey(MOBILE_NUMBER_KEY);
        }
        parameters.setValue(mobileOrEmailOtpRequestDto.getLoginId());
        idpSendOtpRequest.setScope(OTP_SCOPE);
        idpSendOtpRequest.setParameters(List.of(parameters));
        return idpClient.sendOtp(idpSendOtpRequest);
    }
}
