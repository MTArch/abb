package in.gov.abdm.abha.enrollment.security;

import in.gov.abdm.validator.ReplayAttack;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;
import java.time.Instant;

import static in.gov.abdm.abha.enrollment.constants.AbhaConstants.ABHA_ENROL_LOG_PREFIX;
import static in.gov.abdm.constant.ABDMConstant.REPLAY_ATTACK_FOUND;

@Component
@Slf4j
public class RequestManager implements ReactiveAuthenticationManager {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        Request request = (Request) authentication;
        String requestId = request.getRequestId();
        String timestamp = request.getTimestamp();
        if(requestId != null && timestamp != null) {
            authentication.setAuthenticated(true);
            String key = "ABHA_ENROL_DEV_TEST_" + requestId;
            ReplayAttack.checkForReplayAttack(redisTemplate, key,
                            Timestamp.from(Instant.now()))
                    .onErrorResume(throwable -> {
                        log.warn(ABHA_ENROL_LOG_PREFIX + REPLAY_ATTACK_FOUND, requestId, timestamp);
                        authentication.setAuthenticated(false);
                        return Mono.empty();
                    }).subscribe();
        }
        return Mono.just(authentication);
    }
}
