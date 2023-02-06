package in.gov.abdm.abha.enrollment.configuration;

import in.gov.abdm.abha.enrollment.client.NotificationDBClient;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.model.notification.template.Templates;
import in.gov.abdm.abha.enrollment.utilities.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class BeanConfiguration {

    @Autowired
    NotificationDBClient<Templates> notificationDBClient;

    @Bean
    public WebClient getClient() {
        return WebClient.builder().build();
    }

    @Bean(AbhaConstants.MESSAGE_TEMPLATES)
    List<Templates> loadTemplate() {
        List<Templates> templates = new ArrayList<>();
        notificationDBClient.getAll(Templates.class).collectList().doOnError(throwable -> {
            templates.addAll(Common.loadDummyTemplates());
        }).subscribe(templates::addAll);
        //TODO remove
        //templates.addAll(Common.loadDummyTemplates());
        return templates;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }
}
