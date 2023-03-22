package in.gov.abdm.abha.enrollment.services.enrol.pid;

import in.gov.abdm.abha.enrollment.constants.StringConstants;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.EnrolByAadhaarResponseDto;
import in.gov.abdm.abha.enrollment.model.enrol.pid.PidDto;
import in.gov.abdm.abha.enrollment.utilities.rsa.RSAUtil;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class PidService {

    private static final String ENROL_PID = "ENROL_PID";
    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    RSAUtil rsaUtil;

    @Value("${redis.expireTimeInMinutes: 30}")
    private int REDIS_OTP_OBJECT_TIMEOUT;

    public Mono<EnrolByAadhaarResponseDto> addPidAndScanStatus(PidDto pidDto){
        redisTemplate.opsForHash().put(ENROL_PID,pidDto.getTxnId(),pidDto);
        redisTemplate.expire(ENROL_PID,REDIS_OTP_OBJECT_TIMEOUT, TimeUnit.MINUTES);
        return Mono.just(EnrolByAadhaarResponseDto.builder().message(StringConstants.SUCCESS).txnId(pidDto.getTxnId()).build());
    }

    public Mono<PidDto> getPidAndScanStatus(String txnId) {
        PidDto pidData = (PidDto) redisTemplate.opsForHash().get(ENROL_PID, txnId);
        redisTemplate.opsForHash().delete(ENROL_PID,txnId);
        return Mono.just(pidData);
    }
}
