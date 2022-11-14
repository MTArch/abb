package in.gov.abdm.abha.enrollment.controller;
import in.gov.abdm.abha.enrollment.constants.ABHAEnrollmentConstant;
import in.gov.abdm.abha.enrollment.model.link.parent.request.LinkParentRequestDto;
import in.gov.abdm.abha.enrollment.model.link.parent.response.LinkParentResponseDto;
import in.gov.abdm.abha.enrollment.services.link.parent.LinkParentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@CrossOrigin
@RequestMapping(ABHAEnrollmentConstant.LINK_PARENT_REQUEST_ENDPOINT)
public class ProfileLinkController {

    @Autowired
    LinkParentService linkParentService;

    @PostMapping(ABHAEnrollmentConstant.PROFILE_LINK_PARENT_ENDPOINT)
    public Mono<LinkParentResponseDto> linkParent(@RequestBody LinkParentRequestDto linkParentRequestDto)
    {
        return linkParentService.linkDependentAccount(linkParentRequestDto);
    }
}
