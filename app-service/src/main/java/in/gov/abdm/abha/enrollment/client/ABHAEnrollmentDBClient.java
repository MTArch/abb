package in.gov.abdm.abha.enrollment.client;

import in.gov.abdm.abha.enrollment.constant.ABHAEnrollmentConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.util.Optional;

@Component
public class ABHAEnrollmentDBClient<T> {
    @Autowired
    private DiscoveryClient discoveryClient;
    @Autowired
    private WebClient.Builder webClient;

    private WebClient webClient1;

    private String uri;
    @Value("${abdm.notificationdb.service}")
    private String notificationDBService;

    private void setDBURI() {
        if (uri == null || webClient == null) {
            Optional<URI> uri1 = discoveryClient.getInstances(notificationDBService).stream().findFirst().map(ServiceInstance::getUri);
            uri = uri1.isPresent() ? uri1.get().toString() : "NOT FOUND";
            webClient1 = webClient.baseUrl(uri).build();
        }
    }

    public Flux<T> transaction(Class<T> t, String arg) {
        setDBURI();
        return webClient1.get().uri(ABHAEnrollmentConstant.TRANSACTION_TRANSACTION + "/" + t.getSimpleName().toLowerCase() + "/transaction/" + arg).retrieve().bodyToFlux(t);
    }
}
