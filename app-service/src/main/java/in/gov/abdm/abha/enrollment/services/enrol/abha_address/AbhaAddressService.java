package in.gov.abdm.abha.enrollment.services.enrol.abha_address;
import in.gov.abdm.abha.enrollment.model.enrol.abha_address.request.AbhaAddressRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.abha_address.response.AbhaAddressResponseDto;
import in.gov.abdm.abha.enrollment.model.enrol.abha_address.response.SuggestAbhaResponseDto;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;


public interface AbhaAddressService {

    public void validateRequest(String txnId);
    Mono<SuggestAbhaResponseDto> getAbhaAddress(String txnId);

    Mono<AbhaAddressResponseDto> createAbhaAddress(@RequestBody AbhaAddressRequestDto abhaAddressRequestDto);

    void validateAbhaAddress(AbhaAddressRequestDto abhaAddressRequestDto);
}
