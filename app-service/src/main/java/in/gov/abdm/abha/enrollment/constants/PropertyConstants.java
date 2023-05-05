package in.gov.abdm.abha.enrollment.constants;

public class PropertyConstants {

    private PropertyConstants() {
    }

    public static final String KAFKA_ABHA_PATIENT_SYNC_TOPIC = "${kafka.abha.patient.sync.topic}";
    public static final String ENROLLMENT_GATEWAY_DOCUMENT_BASEURI = "${enrollment.gateway.document.baseuri}";
    public static final String ENROLLMENT_GATEWAY_DOCUMENTDB_BASEURI = "${enrollment.gateway.documentdb.baseuri}";
    public static final String ENROLLMENT_GATEWAY_LGD_BASEURI = "${enrollment.gateway.lgd.baseuri}";
    public static final String ENROLLMENT_ENABLE_DEDUPLICATION = "${enrollment.enableDeduplication: false}";
    public static final String ENROLLMENT_DOCUMENT_PHOTO_MIN_SIZE_IN_KB = "${enrollment.documentPhoto.minSizeInKB}";
    public static final String ENROLLMENT_DOCUMENT_PHOTO_MAX_SIZE_IN_KB = "${enrollment.documentPhoto.maxSizeInKB}";
    public static final String ENROLLMENT_MAX_MOBILE_LINKING_COUNT = "${enrollment.maxMobileLinkingCount:6}";
    public static final String ENROLLMENT_DOMAIN = "${enrollment.domain}";
    public static final String ENROLLMENT_PHOTO_MIN_SIZE_IN_KB = "${enrollment.photo.minSizeInKB}";
    public static final String ENROLLMENT_PHOTO_MAX_SIZE_IN_KB = "${enrollment.photo.maxSizeInKB}";
    public static final String REDIS_EXPIRE_TIME_IN_MINUTES = "${redis.expireTimeInMinutes: 30}";
    public static final String ENROLLMENT_OTP_USER_BLOCK_TIME_IN_MINUTES = "${enrollment.otp.userBlockTimeInMinutes: 30}";
    public static final String ENROLLMENT_OTP_MAX_SEND_OTP_COUNT = "${enrollment.otp.maxSendOtpCount: 3}";
    public static final String ENROLLMENT_OTP_MAX_VERIFY_OTP_COUNT = "${enrollment.otp.maxVerifyOtpCount: 3}";
    public static final String CIPHER_SECRET_KEY = "${cipher.secretKey}";
    public static final String JWT_TOKEN_VALIDITY_IN_SEC = "${jwt.token.validityInSec: 1800}";
    public static final String JWT_TOKEN_REFRESH_VALIDITY_IN_SEC = "${jwt.token.refresh.validityInSec: 1296000}";
    public static final String RSA_PRIVATE_KEY_NHA_RSA_PRIVATE_KEY_PEM = "${rsa.private.key: nha_rsa_private_key.pem}";
    public static final String RSA_PUBLIC_KEY_NHA_RSA_PUBLIC_KEY_PEM = "${rsa.public.key: nha_rsa_public_key.pem}";
    public static final String RSA_TRANSFORMATION_ALGORITHM = "${rsa.transformation.algorithm: RSA/ECB/OAEPWithSHA-1AndMGF1Padding}";
    public static final String ENCRYPTION_ALGORITHM = "${rsa.algorithm: RSA}";
    public static final String ENROLLMENT_GATEWAY_AADHAAR_BASEURI = "${enrollment.gateway.aadhaar.baseuri}";
    public static final String ENROLLMENT_GATEWAY_ENROLLMENTDB_BASEURI = "${enrollment.gateway.enrollmentdb.baseuri}";
    public static final String ENROLLMENT_GATEWAY_IDP_BASEURI = "${enrollment.gateway.idp.baseuri}";
    public static final String ENROLLMENT_GATEWAY_NOTIFICATION_BASEURI = "${enrollment.gateway.notification.baseuri}";
    public static final String ENROLLMENT_GATEWAY_NOTIFICATIONDB_BASEURI = "${enrollment.gateway.notificationdb.baseuri}";
    public static final String ENROLLMENT_GATEWAY_PHR_BASEURI = "${enrollment.gateway.phr.baseuri}";
}
