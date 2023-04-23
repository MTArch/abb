package in.gov.abdm.abha.enrollment.services.redis;

import in.gov.abdm.abha.enrollment.model.redis.otp.ReceiverOtpTracker;
import in.gov.abdm.abha.enrollment.model.redis.otp.RedisOtp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static in.gov.abdm.abha.enrollment.constants.PropertyConstants.*;

@Service
public class RedisService {
    private static final String ENROL_OTP = "ENROL_OTP";
    private static final String RECEIVER_OTP_TRACKER = "RECEIVER_OTP_TRACKER";


    @Value(REDIS_EXPIRE_TIME_IN_MINUTES)
    private int REDIS_OTP_OBJECT_TIMEOUT;

    @Value(ENROLLMENT_OTP_USER_BLOCK_TIME_IN_MINUTES)
    private int USER_BLOCK_TIME;

    @Value(ENROLLMENT_OTP_MAX_SEND_OTP_COUNT)
    private int MAX_SEND_OTP_COUNT;

    @Value(ENROLLMENT_OTP_MAX_VERIFY_OTP_COUNT)
    private int MAX_VERIFY_OTP_COUNT;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    public void save(String key, String hashKey, Object value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
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
        save(RECEIVER_OTP_TRACKER, hashKey, receiverOtpTracker);
        redisTemplate.expire(RECEIVER_OTP_TRACKER, userBlockTime, TimeUnit.MINUTES);
    }

    public void deleteRedisOtp(String hashKey) {
        delete(ENROL_OTP, hashKey);
    }

    public void deleteReceiverOtpTracker(String hashKey) {
        delete(RECEIVER_OTP_TRACKER, hashKey);
    }

    public RedisOtp getRedisOtp(String hashKey) {
        return (RedisOtp) redisTemplate.opsForHash().get(ENROL_OTP, hashKey);
    }

    public ReceiverOtpTracker getReceiverOtpTracker(String hashKey) {
        return (ReceiverOtpTracker) redisTemplate.opsForHash().get(RECEIVER_OTP_TRACKER, hashKey);
    }

    public boolean isRedisOtpAvailable(String hashKey) {
        return isAvailable(ENROL_OTP, hashKey);
    }

    public boolean isReceiverOtpTrackerAvailable(String hashKey) {
        return isAvailable(RECEIVER_OTP_TRACKER, hashKey);
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

    public boolean isMultipleOtpVerificationAllowed(String receiver){
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
