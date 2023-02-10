package in.gov.abdm.abha.enrollment.services.idp;


import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.gov.abdm.abha.enrollment.enums.LoginHint;
import in.gov.abdm.abha.enrollment.model.idp.sendotp.IdpSendOtpRequest;
import in.gov.abdm.abha.enrollment.model.idp.sendotp.IdpSendOtpResponse;
import in.gov.abdm.abha.enrollment.model.otp_request.MobileOrEmailOtpRequestDto;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
/**
 * It is Service class IdpService
 */
public class IdpService {

	public static final String OTP_SCOPE = "OTP";
	public static final String ABHA_NUMBER_KEY = "abha_number";
	public static final String MOBILE_NUMBER_KEY = "mobile";
	private String DATE_TIME_FORMATTER = "yyyy-MM-dd HH:mm:ss.SSS";
	SimpleDateFormat dateTimeFormatter = new SimpleDateFormat(DATE_TIME_FORMATTER);

	private static final String AUTHORIZATION = "123344";
	//	private static final String REQUEST_ID = "abha_ee2cf4ef-b3d3-494e-8d3a-27c75100e036";
	private static final String HIP_REQUEST_ID = "22222";

	@Autowired
    IdpAppService idpAppService;

	public Mono<IdpSendOtpResponse> sendOtp(MobileOrEmailOtpRequestDto mobileOrEmailOtpRequestDto) {
		String requestId = "abha_".concat(UUID.randomUUID().toString());
		IdpSendOtpRequest idpSendOtpRequest = new IdpSendOtpRequest();
		Map<String,String> parameters = new HashMap<>();
		if (mobileOrEmailOtpRequestDto.getLoginHint().equals(LoginHint.ABHA_NUMBER)) {
			parameters.put(ABHA_NUMBER_KEY,mobileOrEmailOtpRequestDto.getLoginId().replace("-",""));

		} else if (mobileOrEmailOtpRequestDto.getLoginHint().equals(LoginHint.MOBILE)) {
			parameters.put(MOBILE_NUMBER_KEY,mobileOrEmailOtpRequestDto.getLoginId());
		}
		idpSendOtpRequest.setScope(OTP_SCOPE);
		idpSendOtpRequest.setParameters(parameters);
		String timestamp = LocalDateTime.now().plusMinutes(3).format(DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER)).toString();
		return idpAppService.sendOtp(idpSendOtpRequest, AUTHORIZATION, timestamp, HIP_REQUEST_ID, requestId);
	}
}
