package in.gov.abdm.abha.enrollment.exception.aadhaar.helper;

import in.gov.abdm.abha.enrollment.constants.StringConstants;
import in.gov.abdm.abha.enrollment.exception.aadhaar.model.ErrorCode;
import in.gov.abdm.abha.enrollment.exception.aadhaar.model.UidaiErrorCode;
import in.gov.abdm.abha.enrollment.model.aadhaar.AadhaarResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * Exception helper
 * helps to prepare aadhaar exception to client response
 */
@Slf4j
public class UidaiErrorHelper {

    /**
     * constants for logging
     */
    public static final String NO_ERROR_CODE_FOUND_FOR = "No Error code found for {}.";
    public static final String UIDAI_ERROR_CODE_REGEX = "^(?=.*[a-zA-Z])(?=.*[0-9])[A-Za-z0-9]+$";

    /**
     * UIDAI related error code helper
     * @param dOAadhaarResponseDto
     * @param defaultErrorCode
     * @return
     */
    public static ErrorCode errorCode(AadhaarResponseDto dOAadhaarResponseDto, ErrorCode defaultErrorCode) {
        ErrorCode code = defaultErrorCode;
        if (Objects.nonNull(dOAadhaarResponseDto)) {
            ErrorCode errorCode = getErrorCodeFromUidaiErrorCode(
                    getUidaiErrorCode(dOAadhaarResponseDto.getErrorCodeInternal()));
            code = Objects.nonNull(errorCode) ? errorCode : defaultErrorCode;
        }
        return code;
    }

    /**
     * get UIDAI code error message
     * @param dOAadhaarResponseDto
     * @param defaultMessage
     * @return
     */
    public static String errorMessage(AadhaarResponseDto dOAadhaarResponseDto, String defaultMessage) {
        return Objects.nonNull(dOAadhaarResponseDto) && !StringUtils.isBlank(dOAadhaarResponseDto.getReason())
                ? dOAadhaarResponseDto.getReason()
                : defaultMessage;
    }

    /**
     * get UIDAI error code
     * @param code
     * @return
     */
    private static ErrorCode getErrorCode(String code) {
        try {
            return ErrorCode.valueOf(code);
        } catch (Exception e) {
            log.error(NO_ERROR_CODE_FOUND_FOR, code);
        }
        return null;
    }

    /**
     * check null and get error code
     * @param uidaiErrorCode
     * @return
     */
    private static ErrorCode getErrorCodeFromUidaiErrorCode(UidaiErrorCode uidaiErrorCode) {
        return Objects.nonNull(uidaiErrorCode) ? getErrorCode(uidaiErrorCode.code()) : null;
    }

    /**
     * helper to get error code
     * @param code
     * @return
     */
    public static UidaiErrorCode getUidaiErrorCode(String code) {
        if (!code.isEmpty()) {
            try {
                if (!code.matches(UIDAI_ERROR_CODE_REGEX)) {
                    code = StringConstants.A.concat(code);
                }
                return UidaiErrorCode.valueOf(code);
            } catch (Exception e) {
                log.error(NO_ERROR_CODE_FOUND_FOR, code);
            }
        }
        return null;
    }
}