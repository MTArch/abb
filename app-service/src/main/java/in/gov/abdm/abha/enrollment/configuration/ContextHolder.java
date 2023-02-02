package in.gov.abdm.abha.enrollment.configuration;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ContextHolder {

    private static final ThreadLocal<String> CLIENT_ID = new InheritableThreadLocal<>();
    private static final ThreadLocal<String> CLIENT_IP = new InheritableThreadLocal<>();

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

    public void removeAll() {
        CLIENT_ID.remove();
        CLIENT_IP.remove();
    }
}
