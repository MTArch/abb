package in.gov.abdm.abha.enrollment.controller;

import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.constants.URIConstant;
import in.gov.abdm.abha.enrollment.model.hidbenefit.RequestHeaders;
import in.gov.abdm.abha.enrollment.model.link.parent.request.LinkParentRequestDto;
import in.gov.abdm.abha.enrollment.model.link.parent.response.LinkParentResponseDto;
import in.gov.abdm.abha.enrollment.model.profile.children.ChildrenProfiles;
import in.gov.abdm.abha.enrollment.services.enrol.child.EnrolChildService;
import in.gov.abdm.abha.enrollment.services.link.parent.LinkParentService;
import in.gov.abdm.abha.enrollment.utilities.RequestMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.UUID;

import static in.gov.abdm.abha.enrollment.constants.AbhaConstants.REQUEST_ID;
import static in.gov.abdm.abha.enrollment.constants.AbhaConstants.TIMESTAMP;

@RestController
@CrossOrigin
@RequestMapping(URIConstant.PROFILE_ENDPOINT)
@ResponseStatus(HttpStatus.OK)
public class ProfileController {

    @Autowired
    LinkParentService linkParentService;
    @Autowired
    EnrolChildService enrolChildService;


    @PostMapping(URIConstant.LINK_PARENT_ENDPOINT)
    public Mono<LinkParentResponseDto> linkParent(@RequestHeader(value = REQUEST_ID, required = false) final UUID requestId,
                                                  @RequestHeader(value = TIMESTAMP, required = false) final String timestamp,
                                                  @Valid @RequestBody LinkParentRequestDto linkParentRequestDto) {
        return linkParentService.linkDependentAccount(linkParentRequestDto);
    }

    @GetMapping(URIConstant.PARENT_CHILDREN_ENDPOINT)
    public Mono<ChildrenProfiles> getChildren(@RequestHeader(value = AbhaConstants.BENEFIT_NAME, required = false) String benefitName,
                                              @RequestHeader(value = AbhaConstants.AUTHORIZATION, required = false) String authorization,
                                              @RequestHeader(value = AbhaConstants.X_TOKEN, required = false) String xToken) {
        RequestHeaders requestHeaders = RequestMapper.prepareRequestHeaders(benefitName, authorization, null, xToken);
        return enrolChildService.validateChildHeaders(requestHeaders)
                .flatMap(isValid -> enrolChildService.getChildren(requestHeaders));
    }
}
