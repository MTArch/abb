package in.gov.abdm.abha.enrollment.services.redis;

import in.gov.abdm.abha.enrollment.client.AbhaDBAccountFClient;
import in.gov.abdm.abha.enrollment.client.IntegratedProgramDBFClient;
import in.gov.abdm.abha.enrollment.client.NotificationDbFClient;
import in.gov.abdm.abha.enrollment.exception.application.AbhaUnProcessableException;
import in.gov.abdm.abha.enrollment.model.entities.IntegratedProgramDto;
import in.gov.abdm.abha.enrollment.model.notification.template.Templates;
import in.gov.abdm.abha.enrollment.model.redis.otp.ReceiverOtpTracker;
import in.gov.abdm.abha.enrollment.model.redis.otp.RedisOtp;
import in.gov.abdm.abha.enrollment.utilities.Common;
import in.gov.abdm.abha.profile.constants.StringConstants;
import in.gov.abdm.error.ABDMError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static in.gov.abdm.abha.enrollment.constants.PropertyConstants.*;

@Slf4j
@Service
public class RedisService {
    public static final String TEMPLATES_NOT_FOUND_DUE_TO = "Templates Not Found due to : ";
    private static final String ENROL_OTP = "ENROL_OTP";
    private static final String RECEIVER_OTP_TRACKER = "RECEIVER_OTP_TRACKER";
    private static final String NOTIFICATION_TEMPLATES = "NOTIFICATION_TEMPLATES";
    private static final String INTEGRATED_PROGRAMS = "INTEGRATED_PROGRAMS";
    public static final long YEAR_TIMEOUT = 360L;


    @Value(REDIS_EXPIRE_TIME_IN_MINUTES)
    private int redisOtpObjectTimeout;

    @Value(ENROLLMENT_OTP_USER_BLOCK_TIME_IN_MINUTES)
    private int userBlockTime;

    @Value(ENROLLMENT_OTP_MAX_SEND_OTP_COUNT)
    private int maxSendOtpCount;

    @Value(ENROLLMENT_OTP_MAX_VERIFY_OTP_COUNT)
    private int maxVerifyOtpCount;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private NotificationDbFClient notificationDBClient;

    @Autowired
    private IntegratedProgramDBFClient integratedProgramDBFClient;

    public void save(String key, String hashKey, Object value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    public void saveAll(String key, Map<String, Object> hashKeysAndValues) {
        redisTemplate.opsForHash().putAll(key, hashKeysAndValues);
    }

    public void delete(String key, String hashKey) {
        redisTemplate.opsForHash().delete(key, hashKey);
    }

    public boolean isAvailable(String key, String hashKey) {
        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }


    public void saveRedisOtp(String hashKey, RedisOtp redisOtp) {
        save(ENROL_OTP, hashKey, redisOtp);
        redisTemplate.expire(ENROL_OTP, redisOtpObjectTimeout, TimeUnit.MINUTES);
    }

    public void saveReceiverOtpTracker(String hashKey, ReceiverOtpTracker receiverOtpTracker) {
        save(RECEIVER_OTP_TRACKER.concat(hashKey), hashKey, receiverOtpTracker);
        redisTemplate.expire(RECEIVER_OTP_TRACKER.concat(hashKey), userBlockTime, TimeUnit.MINUTES);
    }

    public void saveNotificationTemplate(Templates templates) {
        save(NOTIFICATION_TEMPLATES, templates.getId().toString(), templates);
        redisTemplate.expire(NOTIFICATION_TEMPLATES, YEAR_TIMEOUT, TimeUnit.DAYS);
    }

    public void saveIntegratedProgram(IntegratedProgramDto integratedProgram) {
        save(INTEGRATED_PROGRAMS, integratedProgram.getClientId(), integratedProgram);
        redisTemplate.expire(INTEGRATED_PROGRAMS, YEAR_TIMEOUT, TimeUnit.DAYS);
    }

    public void saveAllIntegratedProgram(List<IntegratedProgramDto> integratedPrograms) {
        Map<String, Object> integratedProgramList = integratedPrograms.stream()
                .collect(Collectors.toMap(IntegratedProgramDto::getId, Function.identity()));
        saveAll(INTEGRATED_PROGRAMS, integratedProgramList);
    }

    public void deleteRedisOtp(String hashKey) {
        delete(ENROL_OTP, hashKey);
    }

    public void deleteReceiverOtpTracker(String hashKey) {
        delete(RECEIVER_OTP_TRACKER.concat(hashKey), hashKey);
    }

    public RedisOtp getRedisOtp(String hashKey) {
        return (RedisOtp) redisTemplate.opsForHash().get(ENROL_OTP, hashKey);
    }

    public ReceiverOtpTracker getReceiverOtpTracker(String hashKey) {
       return (ReceiverOtpTracker)redisTemplate.opsForHash().get(RECEIVER_OTP_TRACKER.concat(hashKey), hashKey);
    }

    public Mono<Templates> getNotificationTemplate(String templateId) {
        Templates template = (Templates) redisTemplate.opsForHash().get(NOTIFICATION_TEMPLATES, templateId);
        if (template != null) {
            return Mono.just(template);
        } else {
            return notificationDBClient.getById(UUID.randomUUID().toString(), Common.timeStampWithT(), templateId)
                    .flatMap(templates -> {
                        saveNotificationTemplate(templates);
                        return Mono.just(templates);
                    }).onErrorResume(throwable -> {
                        log.error(TEMPLATES_NOT_FOUND_DUE_TO + (throwable.getCause() != null ? throwable.getCause().getMessage() : StringConstants.EMPTY));
                        throw new AbhaUnProcessableException(ABDMError.NOTIFICATION_TEMPLATE_NOT_FOUND);
                    });
        }
    }

    public List<IntegratedProgramDto> getIntegratedPrograms() {
        List<IntegratedProgramDto> integratedPrograms = redisTemplate.opsForHash().entries(INTEGRATED_PROGRAMS).values().stream().map(o -> (IntegratedProgramDto)o).collect(Collectors.toList());
        if (!integratedPrograms.isEmpty()) {
            return integratedPrograms;
        }
        return Collections.emptyList();
    }

    public Mono<List<IntegratedProgramDto>> reloadAndGetIntegratedPrograms() {
        return integratedProgramDBFClient.getAll(UUID.randomUUID().toString(), Common.timeStampWithT())
                .collectList()
                .flatMap(integratedProgramDto -> {
                    saveAllIntegratedProgram(integratedProgramDto);
                    return Mono.just(integratedProgramDto);
                }).onErrorResume(throwable -> {
                    log.error(TEMPLATES_NOT_FOUND_DUE_TO + (throwable.getCause() != null ? throwable.getCause().getMessage() : StringConstants.EMPTY));
                    return Mono.just(Collections.emptyList());
                });
    }

    public boolean isRedisOtpAvailable(String hashKey) {
        return isAvailable(ENROL_OTP, hashKey);
    }

    public boolean isReceiverOtpTrackerAvailable(String hashKey) {
        return isAvailable(RECEIVER_OTP_TRACKER.concat(hashKey), hashKey);
    }

    public boolean isUserBlocked(String receiver) {
        if (!isReceiverOtpTrackerAvailable(receiver)) {
            return false;
        } else {
            return getReceiverOtpTracker(receiver).isBlocked();
        }
    }

    public boolean isResendOtpAllowed(String receiver) {
        if (isUserBlocked(receiver)) {
            return false;
        } else {
            ReceiverOtpTracker receiverOtpTracker = getReceiverOtpTracker(receiver);
            if (receiverOtpTracker != null && receiverOtpTracker.getSentOtpCount() >= maxSendOtpCount) {
                receiverOtpTracker.setBlocked(true);
                saveReceiverOtpTracker(receiver, receiverOtpTracker);
                return false;
            }
            return true;
        }
    }

    public boolean isMultipleOtpVerificationAllowed(String receiver) {
        if (isUserBlocked(receiver)) {
            return false;
        } else {
            ReceiverOtpTracker receiverOtpTracker = getReceiverOtpTracker(receiver);
            if (receiverOtpTracker != null && receiverOtpTracker.getVerifyOtpCount() >= maxVerifyOtpCount) {
                receiverOtpTracker.setBlocked(true);
                saveReceiverOtpTracker(receiver, receiverOtpTracker);
                return false;
            }
            return true;
        }
    }
}
