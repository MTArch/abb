package in.gov.abdm.abha.enrollment.exception.aadhaar;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AadhaarErrorCodes {
    E_312("FMR and FIR cannot be used in same transaction."),
    E_313("Single FIR record contains more than one finger."),
    E_314("Number of FMR/FIR should not exceed 10."),
    E_315("Number of IIR should not exceed 2."),
    E_316("Number of FID should not exceed 1."),
    E_330("Biometrics locked by Aadhaar holder."),
    E_400("Invalid Aadhaar OTP value."),
    E_402("“txn” value did not match with “txn” value used in Request OTP API."),
    E_403("Maximum number of attempts for OTP match is exceeded or OTP is not generated. Please generate a fresh OTP and try to authenticate again"),
    E_500("Invalid encryption of session key."),
    E_501("Invalid certificate identifier in “ci” attribute of “Skey”."),
    E_502("Invalid encryption of PID."),
    E_503("Invalid encryption of Hmac."),
    E_504("Session key rE_initiation required due to expiry or key out of sync."),
    E_505("Synchronized Key usage not allowed for the AUA."),
    E_510("Invalid Auth XML format."),
    E_511("Invalid PID XML format."),
    E_512("Invalid Aadhaar holder consent in “rc” attribute of “Auth”."),
    E_520("Invalid “tid” value."),
    E_521("Invalid “dc” code under Meta tag."),
    E_524("Invalid “mi” code under Meta tag."),
    E_527("Invalid “mc” code under Meta tag."),
    E_530("Invalid authenticator code."),
    E_540("Invalid Auth XML version."),
    E_541("Invalid PID XML version."),
    E_542("AUA not authorized for ASA. This error will be returned if AUA and ASA do not have linking in the portal."),
    E_543("Sub-AUA not associated with “AUA”. This error will be returned if Sub-AUA specified in “sa” attribute is not added as “Sub-AUA” in portal."),
    E_550("Invalid “Uses” element attributes."),
    E_551("Invalid “tid” value."),
    E_553("Registered devices currently not supported.This feature is being implemented in a phased manner."),
    E_554("Public devices are not allowed to be used."),
    E_555("rdsId is invalid and not part of certification registry."),
    E_556("rdsVer is invalid and not part of certification registry."),
    E_557("dpId is invalid and not part of certification registry."),
    E_558("Invalid dih"),
    E_559("Device Certificat has expired"),
    E_560("DP Master Certificate has expired"),
    E_561("Request expired (“Pid->ts” value is older than N hours where N is a configured threshold in authentication server)."),
    E_562("Timestamp value is future time (value specified “Pid->ts” is ahead of authentication server time beyond acceptable threshold)."),
    E_563("Duplicate request (this error occurs when exactly same authentication request was rE_sent by AUA)."),
    E_564("HMAC Validation failed."),
    E_565("AUA license has expired."),
    E_566("Invalid non-decryptable license key."),
    E_567("Invalid input (this error occurs when unsupported characters were found in Indian language values, “lname” or “lav”)."),
    E_568("Unsupported Language."),
    E_569("Digital signature verification failed (means that authentication request XML was modified after it was signed)."),
    E_570("Invalid key info in digital signature (this means that certificate used for signing the authentication request is not valid – it is either expired, or does not belong to the AUA or is not created by a well-known Certification Authority)."),
    E_571("PIN requires reset."),
    E_572("Invalid biometric position."),
    E_573("Pi usage not allowed as per license."),
    E_574("Pa usage not allowed as per license."),
    E_575("Pfa usage not allowed as per license."),
    E_576("FMR usage not allowed as per license."),
    E_577("FIR usage not allowed as per license."),
    E_578("IIR usage not allowed as per license."),
    E_579("OTP usage not allowed as per license."),
    E_580("PIN usage not allowed as per license."),
    E_581("Fuzzy matching usage not allowed as per license."),
    E_582("Local language usage not allowed as per license."),
    E_586("FID usage not allowed as per license. This feature is being implemented in a phased manner."),
    E_587("Name space not allowed."),
    E_588("Registered device not allowed as per license."),
    E_590("Public device not allowed as per license."),
    E_710("Missing “Pi” data as specified in “Uses”."),
    E_720("Missing “Pa” data as specified in “Uses”."),
    E_721("Missing “Pfa” data as specified in “Uses”."),
    E_730("Missing PIN data as specified in “Uses”."),
    E_740("Missing OTP data as specified in “Uses”."),
    E_800("Invalid biometric data."),
    E_810("Missing biometric data as specified in “Uses”."),
    E_811("Missing biometric data in CIDR for the given Aadhaar number."),
    E_812("Aadhaar holder has not done “Best Finger Detection”. Application should initiate BFD to help Aadhaar holder identify their best fingers."),
    E_820("Missing or empty value for “bt” attribute in “Uses” element."),
    E_821("Invalid value in the “bt” attribute of “Uses” element."),
    E_822("Invalid value in the “bs” attribute of “Bio” element within “Pid”."),
    E_901("No authentication data found in the request (this corresponds to a scenario wherein none of the auth data – Demo, Pv, or Bios – is present)."),
    E_902("Invalid “dob” value in the “Pi” element (this corresponds to a scenarios wherein “dob” attribute is not of the format “YYYY” or “YYYYMM- DD”, or the age is not in valid range)."),
    E_910("Invalid “mv” value in the “Pi” element."),
    E_911("Invalid “mv” value in the “Pfa” element."),
    E_912("Invalid “ms” value."),
    E_913("Both “Pa” and “Pfa” are present in the authentication request (Pa and Pfa are mutually exclusive)."),
    E_930("Technical error that are internal to authentication server."),
    E_931("Technical error that are internal to authentication server."),
    E_932("Technical error that are internal to authentication server."),
    E_933("Technical error that are internal to authentication server."),
    E_934("Technical error that are internal to authentication server."),
    E_935("Technical error that are internal to authentication server."),
    E_936("Technical error that are internal to authentication server."),
    E_937("Technical error that are internal to authentication server."),
    E_938("Technical error that are internal to authentication server."),
    E_939("Technical error that are internal to authentication server."),
    E_940("Unauthorized ASA channel."),
    E_941("Unspecified ASA channel."),
    E_950("OTP store related technical error."),
    E_951("Biometric lock related technical error."),
    E_980("Unsupported option."),
    E_995("Aadhaar suspended by competent authority."),
    E_996("Aadhaar cancelled (Aadhaar is no in authenticable status)."),
    E_997("Aadhaar suspended (Aadhaar is not in authenticatable status)."),
    E_998("Invalid Aadhaar Number."),
    E_999("Unknown error."),

    OTHER_ERROR("Please try again in some time.");
    private final String value;
}
