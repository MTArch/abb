package in.gov.abdm.abha.enrollment.configuration;

import lombok.experimental.UtilityClass;

@UtilityClass
public class FacilityContextHolder {

    private static final ThreadLocal<String> SUB = new InheritableThreadLocal<>();
    private static final ThreadLocal<String> CLIENT_ID = new InheritableThreadLocal<>();
    private static final ThreadLocal<String> SYSTEM = new InheritableThreadLocal<>();
    private static final ThreadLocal<String> USER_TYPE = new InheritableThreadLocal<>();
    private static final ThreadLocal<String> ROLES = new InheritableThreadLocal<>();

    public void setClientId(String clientId) {
        CLIENT_ID.set(clientId);
    }

    public String getClientId() {
        return CLIENT_ID.get();
    }

    public void setSubject(String subject) {
        SUB.set(subject);
    }

    public String getSubject() {
        return SUB.get();
    }

    public void setSystem(String system) {
        SYSTEM.set(system);
    }

    public String getSystem() {
        return SYSTEM.get();
    }

    public void setUserType(String userType) {
        USER_TYPE.set(userType);
    }

    public String getUserType() {
        return USER_TYPE.get();
    }

    public void setRole(String role) {
        ROLES.set(role);
    }

    public String getRole() {
        return ROLES.get();
    }

    public void removeAll() {
        CLIENT_ID.remove();
        SUB.remove();
        SYSTEM.remove();
        USER_TYPE.remove();
        ROLES.remove();
    }
}
