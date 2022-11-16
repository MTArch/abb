package in.gov.abdm.abha.enrollment.services.link.parent;
import in.gov.abdm.abha.enrollment.model.link.parent.request.LinkParentRequestDto;
import in.gov.abdm.abha.enrollment.model.link.parent.response.LinkParentResponseDto;
import reactor.core.publisher.Mono;

public interface LinkParentService {

    Mono<LinkParentResponseDto> linkDependentAccount(LinkParentRequestDto linkParentRequestDto);

  }
