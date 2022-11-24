package in.gov.abdm.abha.enrollment.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.gov.abdm.abha.enrollment.constants.URIConstant;
import in.gov.abdm.abha.enrollment.model.link.parent.request.LinkParentRequestDto;
import in.gov.abdm.abha.enrollment.model.link.parent.response.LinkParentResponseDto;
import in.gov.abdm.abha.enrollment.services.link.parent.LinkParentService;
import reactor.core.publisher.Mono;

@RestController
@CrossOrigin
@RequestMapping(URIConstant.PROFILE_ENDPOINT)
public class ProfileController {

    @Autowired
    LinkParentService linkParentService;

    @PostMapping(URIConstant.LINK_PARENT_ENDPOINT)
    public Mono<LinkParentResponseDto> linkParent(@Valid @RequestBody LinkParentRequestDto linkParentRequestDto)
    {
        return linkParentService.linkDependentAccount(linkParentRequestDto);
    }
}
