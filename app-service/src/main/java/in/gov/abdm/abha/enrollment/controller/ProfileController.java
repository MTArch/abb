package in.gov.abdm.abha.enrollment.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import in.gov.abdm.abha.enrollment.constants.URIConstant;
import in.gov.abdm.abha.enrollment.model.link.parent.request.LinkParentRequestDto;
import in.gov.abdm.abha.enrollment.model.link.parent.response.LinkParentResponseDto;
import in.gov.abdm.abha.enrollment.services.link.parent.LinkParentService;
import reactor.core.publisher.Mono;

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

    @PostMapping(URIConstant.LINK_PARENT_ENDPOINT)
    public Mono<LinkParentResponseDto> linkParent(@RequestHeader(value = REQUEST_ID, required = false) final UUID requestId,
                                                  @RequestHeader(value = TIMESTAMP, required = false) final String timestamp,
                                                  @Valid @RequestBody LinkParentRequestDto linkParentRequestDto)
    {
        return linkParentService.linkDependentAccount(linkParentRequestDto);
    }
}
