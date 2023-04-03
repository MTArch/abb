package in.gov.abdm.abha.enrollment.services.common;

import lombok.experimental.UtilityClass;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * abha context holder to have user details for incoming requests
 */
@UtilityClass
public class HealthIdContextHolder {

	/**
	 *  user health id number
	 */
	private static final ThreadLocal<String> HEALTH_ID_NUMBER = new InheritableThreadLocal<>();
	/**
	 * request id
	 */
	private static final ThreadLocal<String> UNIQUE_REQUEST_ID = new InheritableThreadLocal<>();
	/**
	 * client id who is calling this api
	 */
	private static final ThreadLocal<String> CLIENT_ID = new InheritableThreadLocal<>();
	/**
	 * x_hip_id of api request
	 */
	private static final ThreadLocal<String> X_HIP_ID = new InheritableThreadLocal<>();
	/**
	 * Facility id if operations are performed by facility login
	 */
	private static final ThreadLocal<String> FACILITY_ID = new InheritableThreadLocal<>();
	/**
	 * roles of client to access apis
	 */
	private static final ThreadLocal<Set<String>> CLIENT_ROLE = InheritableThreadLocal.withInitial(HashSet::new);
	/**
	 * keycloak access token to access apis
	 */
	private static final ThreadLocal<String> ACCESS_TOKEN = new InheritableThreadLocal<>();
	/**
	 * google captcha token to perform login operation
	 */
	private static final ThreadLocal<String> CAPTCHA_TOKEN_CHECKSUM = new InheritableThreadLocal<>();
	/**
	 *
	 */
	private static final ThreadLocal<String> CLIENT_IP = new InheritableThreadLocal<>();
	private static final ThreadLocal<Long> CAPTCHA_TOKEN_EXPIRY_TIME = new InheritableThreadLocal<>();
	private static final ThreadLocal<Map<String, Object>> MAP = new InheritableThreadLocal<>();
	private static final ThreadLocal<String> TOKEN_TYPE = new InheritableThreadLocal<>();
	private static final ThreadLocal<String> FLOW = new InheritableThreadLocal<>();

	public  String flow() {
		return FLOW.get();
	}

	public  void flow(String flowVar) {
		 FLOW.set(flowVar);
	}

	public String healthIdNumber() {
		return HEALTH_ID_NUMBER.get();
	}

	public void clientId(String clientId) {
		CLIENT_ID.set(clientId);
	}

	public String clientId() {
		return CLIENT_ID.get();
	}

	public String tokenType()
	{
		 return TOKEN_TYPE.get();
	}

	public void tokenType(String tokenType)
	{
		TOKEN_TYPE.set(tokenType);
	}

	public void accessToken(String token) {
		ACCESS_TOKEN.set(token);
	}

	public String accessToken() {
		return ACCESS_TOKEN.get();
	}

	public void healthIdNumber(String healthIdNumber) {
		HEALTH_ID_NUMBER.set(healthIdNumber);
	}

	public String uniqueRequestId() {
		return UNIQUE_REQUEST_ID.get();
	}

	public void uniqueRequestId(String healthIdNumber) {
		UNIQUE_REQUEST_ID.set(healthIdNumber);
	}

	public String xHipId() {
		return X_HIP_ID.get();
	}

	public void xHipId(String xHipId) {
		X_HIP_ID.set(xHipId);
	}

	public void facilityId(String facilityId) {
		FACILITY_ID.set(facilityId);
	}

	public String facilityId() {
		return FACILITY_ID.get();
	}

	public void captchaTokenChecksum(String checksum) {
		CAPTCHA_TOKEN_CHECKSUM.set(checksum);
	}

	public String captchaTokenChecksum() {
		return CAPTCHA_TOKEN_CHECKSUM.get();
	}

	public void captchaTokenExpiryTime(Long expiryTime) {
		CAPTCHA_TOKEN_EXPIRY_TIME.set(expiryTime);
	}

	public Long captchaTokenExpiryTime() {
		return CAPTCHA_TOKEN_EXPIRY_TIME.get();
	}


	public void clientIp(String ip ) {
		CLIENT_IP.set(ip);
	}

	public String clientIp() {
		return CLIENT_IP.get();
	}
	public void clientRole(Set<String> clientRole) {
		CLIENT_ROLE.set(clientRole);
	}

	public Set<String> clientRole() {
		return CLIENT_ROLE.get();
	}

	public void set(String key, Object value) {
		if (CollectionUtils.isEmpty(MAP.get())) {
			Map<String, Object> map = new HashMap<>();
			MAP.set(map);
		}
		Map<String, Object> map = MAP.get();
		map.put(key, value);
	}

	public Object get(String key) {
		Map<String, Object> map = MAP.get();
		return CollectionUtils.isEmpty(map) ? null : map.get(key);
	}

	public void remove() {
		HEALTH_ID_NUMBER.remove();
		UNIQUE_REQUEST_ID.remove();
		CLIENT_ID.remove();
		X_HIP_ID.remove();
		FACILITY_ID.remove();
		CLIENT_ROLE.remove();
		CAPTCHA_TOKEN_CHECKSUM.remove();
		CLIENT_IP.remove();
		CAPTCHA_TOKEN_EXPIRY_TIME.remove();
		MAP.remove();
		TOKEN_TYPE.remove();
		FLOW.remove();
		ACCESS_TOKEN.remove();
	}
}
