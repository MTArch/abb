package in.gov.abdm.abha.enrollment.utilities;
import in.gov.abdm.abha.enrollment.client.AbhaDBAccountFClient;
import in.gov.abdm.abha.enrollment.model.de_duplication.DeDuplicationRequest;
import in.gov.abdm.abha.enrollment.model.entities.AccountDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class DeDuplicationUtils {

    @Value("${enrollment.enableDeduplication: false}")
    private boolean ENABLE_DEDUPLICATION;
    @Autowired
    AbhaDBAccountFClient abhaDBAccountFClient;
    public Mono<AccountDto> checkDeDuplication(DeDuplicationRequest request) {
        if(!ENABLE_DEDUPLICATION)
            return Mono.empty();
        else
            return abhaDBAccountFClient.checkDeDuplication(request);
    }
    public DeDuplicationRequest prepareRequest(AccountDto accountDto) {
        return DeDuplicationRequest.builder()
                .firstName(accountDto.getFirstName().toLowerCase())
                .lastName(accountDto.getLastName().toLowerCase())
                .dob(Integer.valueOf(accountDto.getDayOfBirth()))
                .mob(Integer.valueOf(accountDto.getMonthOfBirth()))
                .yob(Integer.valueOf(accountDto.getYearOfBirth()))
                .gender(accountDto.getGender())
                .build();
    }
}
