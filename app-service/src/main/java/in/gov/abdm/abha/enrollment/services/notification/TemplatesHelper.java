package in.gov.abdm.abha.enrollment.services.notification;

import in.gov.abdm.abha.enrollment.services.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.text.MessageFormat;

@Service
public class TemplatesHelper {

    @Autowired
    RedisService redisService;

    public Mono<String> prepareSMSMessage(Long templateId, String... params) {
        return redisService.getNotificationTemplate(templateId.toString()).flatMap(template -> Mono.just(MessageFormat.format(template.getMessage(), params)));
    }
    public String getMessage(Long templateId) {
        return templates.stream().filter(res-> res.getId().equals(templateId)).findAny().get().getMessage();
    }
}
