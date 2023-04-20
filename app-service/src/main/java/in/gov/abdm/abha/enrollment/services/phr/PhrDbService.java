package in.gov.abdm.abha.enrollment.services.phr;
import in.gov.abdm.abha.enrollment.client.PhrDbFClient;
import in.gov.abdm.abha.enrollment.model.phr.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PhrDbService {
    @Autowired
    PhrDbFClient phrDbFClient;

    public static final String REQUEST_ID = "phr-app-backend";

    public Flux<User> getUsersByAbhaAddressList(List<String> abhaAddressList)
    {
        String requestId = REQUEST_ID;
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
        return phrDbFClient.getUsersByAbhaAddressList(requestId,timestamp,abhaAddressList.stream().collect(Collectors.joining(",")));
    }
}
