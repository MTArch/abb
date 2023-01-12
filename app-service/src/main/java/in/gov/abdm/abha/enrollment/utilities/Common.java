package in.gov.abdm.abha.enrollment.utilities;

import in.gov.abdm.abha.enrollment.constants.StringConstants;
import in.gov.abdm.abha.enrollment.enums.request.OtpSystem;
import in.gov.abdm.abha.enrollment.enums.request.Scopes;
import in.gov.abdm.abha.enrollment.model.lgd.LgdDistrictResponse;
import in.gov.abdm.abha.enrollment.model.notification.template.TemplateType;
import in.gov.abdm.abha.enrollment.model.notification.template.Templates;
import in.gov.abdm.abha.enrollment.services.common.HealthIdContextHolder;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@UtilityClass
@Slf4j
public class Common {

	
    public static final String EXCEPTION_OCCURRED_WHILE_CONVERTING_XML_TO_JSON_STRING = "Exception occurred while converting xml to json String";
    public static final String HIDDEN_DIGIT = "******";
    public static final String FILE_LOADED_SUCCESSFULLY = "{} file loaded successfully";
    public static final String EXCEPTION_OCCURRED_WHILE_READING_FILE_ERROR_MSG = "Exception occurred while reading file: {} Error Msg : ";
    public static final String YYYY_MM_DD_T_HH_MM_SS_MMM = "YYYY-MM-dd'T'HH:MM:ss.mmm";
    public static final String YYYY_MM_DD_HH_MM_SS = "YYYYMMddHHMMss";

    private static final long SMS_TEMPLATE_ID = 1007164181681962323L;
    private static final String ABHA = "ABHA";
    private static final String MESSAGE = "OTP for creating your ABHA is {0}. This One Time Password will be valid for 10 mins.\n\nABDM, National Health Authority";
    private static final String SUBJECT = "OTP verification";

    @Autowired
    HealthIdContextHolder healthIdContextHolder;

    public String getTimeStamp(boolean isTS) {
        SimpleDateFormat sdf;
        if (isTS) {
            sdf = new SimpleDateFormat(YYYY_MM_DD_T_HH_MM_SS_MMM);
        } else {
            sdf = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
        }
        Date now = new Date();
        return sdf.format(now);
    }

    /**
     * Do not change date format, it is getting used in multiple places and required same format
     * @return
     */
    public String timeStampWithT(){
        //Don't change date format
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).toString();
    }

    public static String loadFileData(String fileName) {
        String content = null;
        try {
            InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
            StringBuilder contentBuilder = new StringBuilder();
            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(resourceAsStream));
            String line;
            while ((line = bufferReader.readLine()) != null) {
                contentBuilder.append(line + System.lineSeparator());
            }
            content = contentBuilder.toString();
        } catch (Exception e) {
            log.error(EXCEPTION_OCCURRED_WHILE_READING_FILE_ERROR_MSG, fileName, e.getMessage());
        }
        return content.replace(StringConstants.SLASH_N, StringConstants.EMPTY).replace(StringConstants.SLASH_R, StringConstants.EMPTY).trim();
    }

    public String xmlToJson(String xmlPayload) {
        String jsonResponse = null;
        try {
            JSONObject xmlJSONObj = XML.toJSONObject(xmlPayload);
            jsonResponse = xmlJSONObj.toString(4);
        } catch (JSONException ex) {
            log.error(EXCEPTION_OCCURRED_WHILE_CONVERTING_XML_TO_JSON_STRING, ex);
        }
        return jsonResponse;
    }

    public String hidePhoneNumber(String phoneNumber) {
        return HIDDEN_DIGIT + phoneNumber.substring(6);
    }

    public String getIpAddress() {
        //TODO
        return HealthIdContextHolder.clientIp();
    }

    public int calculateYearDifference(String startYear, String startMonth, String startDay) {
        LocalDate startDate = LocalDate.of(Integer.parseInt(startYear), Integer.parseInt(startMonth), Integer.parseInt(startDay));
        return Period.between(startDate, LocalDate.now()).getYears();
    }

    public boolean isPhoneNumberMatching(String value1, String value2) {
        return value1.substring(6).equals(value2.replace("*",""));
    }

    public boolean isAllScopesAvailable(List<Scopes> scopes, List<Scopes> scopesToMatch) {
        return new HashSet<>(scopes).containsAll(scopesToMatch);
    }

    public boolean isScopeAvailable(List<Scopes> scopes, Scopes scopesToMatch) {
        return new HashSet<>(scopes).contains(scopesToMatch);
    }
    
    public boolean isOtpSystem(OtpSystem otpSystem, OtpSystem otpSystemToMatch) {
        return otpSystem.equals(otpSystemToMatch);
    }
    
    public boolean isExactScopesMatching(List<Scopes> scopes, List<Scopes> scopesToMatch) {
        return scopes.equals(scopesToMatch);
    }

    public String base64Encode(String value){
        return Base64.getEncoder().encodeToString(value.getBytes());
    }

    public List<Templates> loadDummyTemplates() {
        List<Templates> templates = new ArrayList<>();
        templates.add(new Templates(
                SMS_TEMPLATE_ID,
                ABHA,
                MESSAGE,
                SUBJECT,
                TemplateType.SMS_OTP));
        //OTP for updating the mobile number linked with your ABHA is {0}. This One Time Password will be valid for 10 mins.\n\nABDM, NHA##1007164725434022866
        return templates;
    }

    public Date dateOf(LocalDateTime date){
        return Date.from(date.atZone(ZoneId.systemDefault()).toInstant());
    }

    public String getStringIgnoreNull(String value) {
        return StringUtils.isEmpty(value) ? StringConstants.EMPTY : value;
    }

    public String getByCommaIgnoreNull(String value){
        return StringUtils.isEmpty(value) ? StringConstants.EMPTY : value + StringConstants.COMMA_SPACE;
    }

    public String removeSpecialChar(String str) {
        return str.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
    }

    public LgdDistrictResponse getLGDDetails(List<LgdDistrictResponse> lgdDistrictResponses){
        LgdDistrictResponse lgdDistrictResponse = new LgdDistrictResponse();
        try {
            lgdDistrictResponse = lgdDistrictResponses.stream()
                    .filter(lgd -> lgd.getDistrictCode() != null)
                    .findAny().get();
        } catch (NoSuchElementException noSuchElementException) {
            if (!lgdDistrictResponses.isEmpty()) {
                lgdDistrictResponse = lgdDistrictResponses.get(0);
            }
        }
        if (lgdDistrictResponse != null) {
            String districtCode = lgdDistrictResponse.getDistrictCode() != null ? lgdDistrictResponse.getDistrictCode() : StringConstants.UNKNOWN;
            String districtName = lgdDistrictResponse.getDistrictName() != null ? lgdDistrictResponse.getDistrictName() : StringConstants.UNKNOWN;

            lgdDistrictResponse.setDistrictCode(districtCode);
            lgdDistrictResponse.setDistrictName(districtName);
        }
        return lgdDistrictResponse;
    }

    /**
     * expecting list of first name middle name and last name and return joined name by space
     * @param name
     * @return
     */
    public String getName(String ...name){
        return String.join(" ", name);
    }

    /**
     * expecting yyyy-mm-dd and will return dd
     * @param dob
     * @return
     */
    public String getDayOfBirth(String dob){
        return dob.split("-")[2];
    }

    /**
     * expecting yyyy-mm-dd and will return mm
     * @param dob
     * @return
     */
    public String getMonthOfBirth(String dob){
        return dob.split("-")[1];
    }

    /**
     * expecting yyyy-mm-dd and will return yyyy
     * @param dob
     * @return
     */
    public String getYearOfBirth(String dob){
        return dob.split("-")[0];
    }

    public boolean validStringSize(String value, int size){
        return value.length() <= size;
    }
}
