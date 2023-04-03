package in.gov.abdm.abha.enrollment.utilities;
import in.gov.abdm.abha.enrollment.client.AbhaDBAccountFClient;
import in.gov.abdm.abha.enrollment.model.de_duplication.DeDuplicationRequest;
import in.gov.abdm.abha.enrollment.model.entities.AccountDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class DeDuplicationUtils {

    @Autowired
    AbhaDBAccountFClient abhaDBAccountFClient;

    public Mono<AccountDto> checkDeDuplication(String firstName, String lastName, Integer dob, Integer mob, Integer yob, String gender) {
        return abhaDBAccountFClient.checkDeDuplication(firstName,lastName,dob,mob,yob,gender);
    }
    public DeDuplicationRequest prepareRequest(AccountDto accountDto) {
        return DeDuplicationRequest.builder()
                .fName(accountDto.getFirstName().toLowerCase())
                .lName(accountDto.getLastName().toLowerCase())
                .dob(Integer.valueOf(accountDto.getDayOfBirth()))
                .mob(Integer.valueOf(accountDto.getMonthOfBirth()))
                .yob(Integer.valueOf(accountDto.getYearOfBirth()))
                .gender(accountDto.getGender())
                .build();
    }
}
