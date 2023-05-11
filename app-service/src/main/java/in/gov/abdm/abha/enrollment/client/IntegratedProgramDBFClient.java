package in.gov.abdm.abha.enrollment.client;

import in.gov.abdm.abha.enrollment.configuration.AppConfigurations;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.constants.URIConstant;
import in.gov.abdm.abha.enrollment.model.entities.IntegratedProgramDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Flux;

import static in.gov.abdm.abha.enrollment.constants.PropertyConstants.ENROLLMENT_GATEWAY_ENROLLMENTDB_BASEURI;
import static in.gov.abdm.abha.enrollment.constants.URIConstant.BENEFIT_NAME;
import static in.gov.abdm.abha.enrollment.constants.URIConstant.TIMESTAMP;
import static in.gov.abdm.constant.ABDMConstant.REQUEST_ID;

@ReactiveFeignClient(name = AbhaConstants.ABHA_DB_INTEGRATED_PROGRAM_CLIENT, url = ENROLLMENT_GATEWAY_ENROLLMENTDB_BASEURI, configuration = AppConfigurations.class)
public interface IntegratedProgramDBFClient {
    @GetMapping(URIConstant.DB_GET_ALL_INTEGRATED_PROGRAMS_URI)
    Flux<IntegratedProgramDto> getAll(@RequestHeader(REQUEST_ID) String requestId,
                                      @RequestHeader(TIMESTAMP) String timestamp);

    @GetMapping(URIConstant.DB_GET_INTEGRATED_PROGRAMS_BY_BENEFIT_NAME_URI)
    Flux<IntegratedProgramDto> getIntegratedProgramByBenefitName(@RequestHeader(REQUEST_ID) String requestId,
                                                                 @RequestHeader(TIMESTAMP) String timestamp,
                                                                 @PathVariable(BENEFIT_NAME) String benefitName);
}
