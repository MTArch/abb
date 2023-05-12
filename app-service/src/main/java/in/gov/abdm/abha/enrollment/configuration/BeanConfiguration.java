package in.gov.abdm.abha.enrollment.configuration;

import in.gov.abdm.abha.enrollment.client.IntegratedProgramDBFClient;
import in.gov.abdm.abha.enrollment.client.NotificationDbFClient;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.exception.abha_db.AbhaDBGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.exception.notification.NotificationDBGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.model.entities.IntegratedProgramDto;
import in.gov.abdm.abha.enrollment.model.notification.template.Templates;
import in.gov.abdm.abha.enrollment.utilities.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static in.gov.abdm.abha.enrollment.constants.AbhaConstants.*;

@Configuration
public class BeanConfiguration {


    private final NotificationDbFClient notificationDBClient;
    private final IntegratedProgramDBFClient integratedProgramDBFClient;
    private final String redisServer;
    private final int redisPort;
    private final String redisPassword;
    private final int redisDatabase;

    @Autowired
    public BeanConfiguration(
            NotificationDbFClient notificationDBClient,
            IntegratedProgramDBFClient integratedProgramDBFClient,
            @Value(ABDM_REDIS_SERVER) String redisServer,
            @Value(ABDM_REDIS_PORT) int redisPort
            , @Value(ABDM_REDIS_PASSWORD) String redisPassword,
            @Value(ABDM_REDIS_DATABASE) int redisDatabase) {
        this.notificationDBClient = notificationDBClient;
        this.integratedProgramDBFClient = integratedProgramDBFClient;
        this.redisServer = redisServer;
        this.redisPort = redisPort;
        this.redisPassword = redisPassword;
        this.redisDatabase = redisDatabase;
    }


    @Bean
    public WebClient getClient() {
        return WebClient.builder().build();
    }

    @Bean(AbhaConstants.MESSAGE_TEMPLATES)
    List<Templates> loadTemplate() {
        List<Templates> templates = new ArrayList<>();
        return notificationDBClient.getAll(UUID.randomUUID().toString(), Common.timeStampWithT()).collectList().onErrorResume(throwable -> {
            templates.addAll(Common.loadDummyTemplates());
            return Mono.error(new NotificationDBGatewayUnavailableException());
        }).block();
    }

    @Bean(AbhaConstants.INTEGRATED_PROGRAMS)
    List<IntegratedProgramDto> loadIntegratedPrograms() {
        return integratedProgramDBFClient.getAll(UUID.randomUUID().toString(), Common.timeStampWithT())
                                         .collectList().onErrorResume(throwable -> Mono.error(new AbhaDBGatewayUnavailableException()))
                                         .block();
    }

    @Bean
    public LettuceConnectionFactory redisStandAloneConnectionFactory() {
        final RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(redisServer);
        redisStandaloneConfiguration.setPort(redisPort);
        redisStandaloneConfiguration.setPassword(redisPassword);
        redisStandaloneConfiguration.setDatabase(redisDatabase);
        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisStandAloneConnectionFactory());
        redisTemplate.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
        return redisTemplate;
    }
}
