package in.gov.abdm.abha.enrollmentdb.domain.procedure;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.gov.abdm.abha.enrollmentdb.model.procedure.SaveAllDataRequest;
import in.gov.abdm.abha.enrollmentdb.repository.procedure.ProcedureRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class ProcedureServiceImpl implements ProcedureService{
    @Autowired
    ProcedureRepository procedureRepository;

    @Autowired
    private ModelMapper modelMapper;
    @Override
    public Mono<String> saveAllData(SaveAllDataRequest saveAllDataRequest){
        ObjectMapper om = new ObjectMapper();
        try {
            saveAllDataRequest.getAccounts().get(0).setCreatedDate(null);
           return procedureRepository.saveAllDataProcedure(om.writeValueAsString(saveAllDataRequest.getAccounts()),
                    om.writeValueAsString(saveAllDataRequest.getHidPhrAddress()), om.writeValueAsString(saveAllDataRequest.getAccountAuthMethods()));
        } catch (Exception e) {
           log.info(e.getMessage());
        }
        return Mono.empty();
    }
}
