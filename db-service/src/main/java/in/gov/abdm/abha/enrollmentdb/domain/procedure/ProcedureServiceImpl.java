package in.gov.abdm.abha.enrollmentdb.domain.procedure;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.gov.abdm.abha.account.Account;
import in.gov.abdm.abha.enrollmentdb.domain.kafka.KafkaService;
import in.gov.abdm.abha.enrollmentdb.enums.AbhaType;
import in.gov.abdm.abha.enrollmentdb.model.account.Accounts;
import in.gov.abdm.abha.enrollmentdb.model.procedure.SaveAllDataRequest;
import in.gov.abdm.abha.enrollmentdb.repository.HidPhrAddressRepository;
import in.gov.abdm.abha.enrollmentdb.repository.procedure.ProcedureRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class ProcedureServiceImpl implements ProcedureService {
    @Autowired
    ProcedureRepository procedureRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    KafkaService kafkaService;

    @Autowired
    HidPhrAddressRepository hidPhrAddressRepository;

    @Override
    public Mono<String> saveAllData(SaveAllDataRequest saveAllDataRequest) {
        ObjectMapper om = new ObjectMapper();
        try {
            saveAllDataRequest.getAccounts().get(0).setCreatedDate(null);
            saveAllDataRequest.getAccounts().get(0).setConsentDate(null);
            saveAllDataRequest.getAccounts().get(0).setUpdateDate(null);
            saveAllDataRequest.getHidPhrAddress().get(0).setLastModifiedDate(null);
            saveAllDataRequest.getHidPhrAddress().get(0).setCreatedDate(null);
            Accounts account = saveAllDataRequest.getAccounts().get(0);
            saveAllDataRequest.getAccounts().get(0).setKycVerified(account.getType() != AbhaType.CHILD && account.isKycVerified());
            return procedureRepository.saveAllDataProcedure(om.writeValueAsString(saveAllDataRequest.getAccounts()),
                            om.writeValueAsString(saveAllDataRequest.getHidPhrAddress()), om.writeValueAsString(saveAllDataRequest.getAccountAuthMethods()))
                    .flatMap(response -> {
                        log.info("response of sp {}", response);
                        return hidPhrAddressRepository.getPhrAddressByPhrAddress(saveAllDataRequest.getAccounts().get(0).getHealthId())
                                .flatMap(hidPhrAddress -> {
                                    kafkaService.publishPhrUserPatientEvent(hidPhrAddress).subscribe();
                                    return Mono.just(response);
                                });
                    });
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return Mono.empty();
    }
}
