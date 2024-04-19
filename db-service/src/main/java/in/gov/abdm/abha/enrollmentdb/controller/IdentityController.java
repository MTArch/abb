package in.gov.abdm.abha.enrollmentdb.controller;

import static in.gov.abdm.constant.ABDMConstant.REQUEST_ID;
import static in.gov.abdm.constant.ABDMConstant.TIMESTAMP;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import in.gov.abdm.abha.enrollmentdb.domain.idp.IdentityService;
import in.gov.abdm.identity.domain.Identity;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequestMapping("/api/v3/enrollmentdb/identity")
@Slf4j
@RestController
public class IdentityController {

	@Autowired
	IdentityService identityService;

	@GetMapping("/getUsersByHealthIdNumber/{healthIdNumber}/{abhaAddress}")
	public Mono<Identity> getAccountByHealthIdNumberAndAbhaAddress(
			@PathVariable("healthIdNumber") String healthIdNumber, @PathVariable("abhaAddress") String abhaAddress) {
		log.info("healthIdNumber : {}, abhaAddress: {}");
		return identityService.getAccountByHealthIdNumberAndAbhaAddress(healthIdNumber, abhaAddress)
				.switchIfEmpty(Mono.empty());
	}

	@GetMapping("/getUsersByHealthIdNumber/{healthIdNumber}")
	public Mono<Identity> getAccountByHealthIdNumber(@PathVariable("healthIdNumber") String healthIdNumber) {
		log.info("healthIdNumber : {}, abhaAddress: {}");
		return identityService.getAccountByHealthIdNumber(healthIdNumber).switchIfEmpty(Mono.empty());
	}

	@GetMapping("/getUserByAbhaAddress/{abhaAddress}")
	@ResponseStatus(HttpStatus.OK)
	public Mono<Identity> findUserByAbhaAddress(@RequestHeader(REQUEST_ID) String requestId,
			@RequestHeader(TIMESTAMP) Timestamp timestamp, @PathVariable String abhaAddress) {
		return identityService.findUserByAbhaAddress(requestId, timestamp, abhaAddress);
	}

	@GetMapping("/getUsersByMobileNumber/{mobileNumber}")
	@ResponseStatus(HttpStatus.OK)
	public Flux<Identity> findUsersByMobileNumber(@RequestHeader(REQUEST_ID) String requestId,
			@RequestHeader(TIMESTAMP) Timestamp timestamp, @PathVariable String mobileNumber) {
		return identityService.findUsersByMobileNumber(requestId, timestamp, mobileNumber);
	}

	@GetMapping("/getAllAbhaAddressByMobileNumber/{mobileNumber}")
	@ResponseStatus(HttpStatus.OK)
	public Flux<Identity> findAllAbhaAddressByMobileNumber(@RequestHeader(REQUEST_ID) String requestId,
			@RequestHeader(TIMESTAMP) Timestamp timestamp, @PathVariable String mobileNumber) {
		return identityService.findAllAbhaAddressByMobileNumber(requestId, timestamp, mobileNumber);
	}

	@GetMapping("/getUserByAbhaAddressWithoutStatus/{abhaAddress}")
	@ResponseStatus(HttpStatus.OK)
	public Mono<Identity> findUserWithoutStatusByAbhaAddress(@RequestHeader(REQUEST_ID) String requestId,
			@RequestHeader(TIMESTAMP) Timestamp timestamp, @PathVariable String abhaAddress) {
		return identityService.findUserWithoutStatusByAbhaAddress(requestId, timestamp, abhaAddress);
	}

	@GetMapping("/getUserByAbhaAddressList")
	@ResponseStatus(HttpStatus.OK)
	public Flux<Identity> findUserByAbhaAddressList(@RequestHeader(REQUEST_ID) String requestId,
			@RequestHeader(TIMESTAMP) Timestamp timestamp, @RequestParam List<String> abhaAddressList) {
		return identityService.findUserByAbhaAddressList(requestId, timestamp, abhaAddressList);
	}

}
