package in.gov.abdm.abha.enrollment.configuration;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.protocol.ProtocolVersion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import static in.gov.abdm.abha.enrollment.constants.AbhaConstants.*;

@Configuration
@Slf4j
public class BeanConfiguration {

    private final String redisServer;
    private final int redisPort;
    private final String redisPassword;
    private final int redisDatabase;

    @Autowired
    public BeanConfiguration(
            @Value(ABDM_REDIS_SERVER) String redisServer,
            @Value(ABDM_REDIS_PORT) int redisPort
            , @Value(ABDM_REDIS_PASSWORD) String redisPassword,
            @Value(ABDM_REDIS_DATABASE) int redisDatabase) {
        this.redisServer = redisServer;
        this.redisPort = redisPort;
        this.redisPassword = redisPassword;
        this.redisDatabase = redisDatabase;
    }


    @Bean
    public WebClient getClient() {
        return WebClient.builder().build();
    }

    @Bean
    public LettuceConnectionFactory redisStandAloneConnectionFactory() {
        final RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(redisServer);
        redisStandaloneConfiguration.setPort(redisPort);
        redisStandaloneConfiguration.setPassword(redisPassword);
        redisStandaloneConfiguration.setDatabase(redisDatabase);
        return new LettuceConnectionFactory(redisStandaloneConfiguration,
                LettuceClientConfiguration
                        .builder()
                        .clientOptions(ClientOptions
                                .builder()
                                .protocolVersion(ProtocolVersion.RESP2)
                                .build())
                        .build());
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisStandAloneConnectionFactory());
        return redisTemplate;
    }
}
