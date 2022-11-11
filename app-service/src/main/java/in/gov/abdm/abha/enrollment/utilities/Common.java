package in.gov.abdm.abha.enrollment.utilities;

import in.gov.abdm.abha.enrollment.constants.StringConstants;
import in.gov.abdm.abha.enrollment.services.common.HealthIdContextHolder;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Date;

@UtilityClass
@Slf4j
public class Common {

    public static final String EXCEPTION_OCCURRED_WHILE_CONVERTING_XML_TO_JSON_STRING = "Exception occurred while converting xml to json String";
    public static final String HIDDEN_DIGIT = "******";
    public static final String FILE_LOADED_SUCCESSFULLY = "{} file loaded successfully";
    public static final String EXCEPTION_OCCURRED_WHILE_READING_FILE_ERROR_MSG = "Exception occurred while reading file: {} Error Msg : ";
    public static final String YYYY_MM_DD_T_HH_MM_SS_MMM = "YYYY-MM-dd'T'HH:MM:ss.mmm";
    public static final String YYYY_MM_DD_HH_MM_SS = "YYYYMMddHHMMss";

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

    public int calculateYearDifference(int startYear, int startMonth, int startDay, LocalDate till) {
        LocalDate startDate = LocalDate.of(startYear, startMonth, startDay);
        return calculateYearDifference(startDate, till);
    }

    public int calculateYearDifference(LocalDate start, LocalDate end) {
        Period period = Period.between(start, end);
        return period.getYears();
    }
}
