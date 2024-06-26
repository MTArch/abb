package in.gov.abdm.abha.enrollment.configuration;

import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class ContextHolder {

    private static final ThreadLocal<String> REQUEST_ID = new InheritableThreadLocal<>();
    private static final ThreadLocal<String> CLIENT_ID = new InheritableThreadLocal<>();
    private static final ThreadLocal<String> CLIENT_IP = new InheritableThreadLocal<>();
    private static final ThreadLocal<String> TIMESTAMP = new InheritableThreadLocal<>();
    private static final ThreadLocal<List<String>> BENEFIT_ROLES = new InheritableThreadLocal<>();

    public void setRequestId(String requestId) {
        REQUEST_ID.set(requestId);
    }

    public String getRequestId() {
        return REQUEST_ID.get();
    }

    public void setClientId(String clientId) {
        CLIENT_ID.set(clientId);
    }

    public String getClientId() {
        return CLIENT_ID.get();
    }

    public void setClientIp(String ip) {
        CLIENT_IP.set(ip);
    }

    public String getClientIp() {
        return CLIENT_IP.get();
    }

    public void setTimestamp(String ip) {
        TIMESTAMP.set(ip);
    }

    public String getTimestamp() {
        return TIMESTAMP.get();
    }

    public void setBenefitRoles(List<String> benefitRoles) {
        BENEFIT_ROLES.set(benefitRoles);
    }
    public List<String> getBenefitRoles() {
        return BENEFIT_ROLES.get();
    }

    public void removeAll() {
        REQUEST_ID.remove();
        CLIENT_ID.remove();
        CLIENT_IP.remove();
        TIMESTAMP.remove();
        BENEFIT_ROLES.remove();
    }
}
