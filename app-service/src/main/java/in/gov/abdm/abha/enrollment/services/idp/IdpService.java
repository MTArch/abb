package in.gov.abdm.abha.enrollment.services.idp;


import in.gov.abdm.abha.enrollment.client.IdpClient;
import in.gov.abdm.abha.enrollment.enums.LoginHint;
import in.gov.abdm.abha.enrollment.exception.database.constraint.DatabaseConstraintFailedException;
import in.gov.abdm.abha.enrollment.model.idp.sendotp.IdpSendOtpRequest;
import in.gov.abdm.abha.enrollment.model.idp.sendotp.IdpSendOtpResponse;
import in.gov.abdm.abha.enrollment.model.idp.sendotp.Parameters;
import in.gov.abdm.abha.enrollment.model.otp_request.MobileOrEmailOtpRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
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
    private String DATE_TIME_FORMATTER = "yyyy-MM-dd HH:mm:ss";
    SimpleDateFormat dateTimeFormatter = new SimpleDateFormat(DATE_TIME_FORMATTER);

    private static final String AUTHORIZATION="12334";
    private static final String REQUEST_ID = "abha_ee2cf4ef-b3d3-494e-8d3a-27c75100e036";
    private static final String HIP_REQUEST_ID = "22222";

    @Autowired
    IdpClient idpClient;

    public Mono<IdpSendOtpResponse> sendOtp(MobileOrEmailOtpRequestDto mobileOrEmailOtpRequestDto) {
        IdpSendOtpRequest idpSendOtpRequest = new IdpSendOtpRequest();
        Parameters parameters = new Parameters();
        if (mobileOrEmailOtpRequestDto.getLoginHint().getValue().equals(LoginHint.ABHA_NUMBER.getValue())) {
            parameters.setKey(ABHA_NUMBER_KEY);
        } else if (mobileOrEmailOtpRequestDto.getLoginHint().equals(LoginHint.MOBILE.getValue())) {
            parameters.setKey(MOBILE_NUMBER_KEY);
        }
        parameters.setValue(mobileOrEmailOtpRequestDto.getLoginId());
        idpSendOtpRequest.setScope(OTP_SCOPE);
        idpSendOtpRequest.setParameters(List.of(parameters));
        String timestamp = dateTimeFormatter.format(new Date());
        return idpClient.sendOtp(idpSendOtpRequest,AUTHORIZATION,timestamp,HIP_REQUEST_ID,REQUEST_ID);
    }
}
