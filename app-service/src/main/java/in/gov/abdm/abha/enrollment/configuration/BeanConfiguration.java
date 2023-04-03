package in.gov.abdm.abha.enrollment.configuration;

import in.gov.abdm.abha.enrollment.client.NotificationDBClient;
import in.gov.abdm.abha.enrollment.client.NotificationDbFClient;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.exception.notification.NotificationDBGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.model.notification.template.Templates;
import in.gov.abdm.abha.enrollment.utilities.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactivefeign.ReactiveOptions;
import reactivefeign.webclient.WebReactiveOptions;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Configuration
public class BeanConfiguration {

//    @Autowired
//    NotificationDBClient<Templates> notificationoldDBClient;
    @Autowired
    NotificationDbFClient notificationDBClient;

    @Bean
    public WebClient getClient() {
        return WebClient.builder().build();
    }

//    @Bean(AbhaConstants.MESSAGE_TEMPLATES)
//    List<Templates> loadTemplate() {
//        List<Templates> templates = new ArrayList<>();
//        return notificationoldDBClient.getAll(Templates.class).collectList().onErrorResume(throwable -> {
//            templates.addAll(Common.loadDummyTemplates());
//            return Mono.error(new NotificationDBGatewayUnavailableException());
//        }).block();
//    }

    @Bean(AbhaConstants.MESSAGE_TEMPLATES)
    List<Templates> loadTemplate() {
        List<Templates> templates = new ArrayList<>();
        return notificationDBClient.getAll(UUID.randomUUID().toString(), Common.timeStampWithT()).collectList().onErrorResume(throwable -> {
            templates.addAll(Common.loadDummyTemplates());
            return Mono.error(new NotificationDBGatewayUnavailableException());
        }).block();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }
}
