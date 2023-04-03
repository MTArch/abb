package in.gov.abdm.abha.enrollment.constants;

public class EnrollErrorConstants {
    public static final String EXCEPTION_OCCURRED_POSTGRES_DATABASE_CONSTRAINT_FAILED_WHILE_CREATE = "c10Exception occurred while create, Postgres Database - ABHA DB - Constraint Failed";
    public static final String EXCEPTION_OCCURRED_POSTGRES_DATABASE_CONSTRAINT_FAILED_WHILE_UPDATE = "Exception occurred while update, Postgres Database - ABHA DB - Constraint Failed";
    public static final String EXCEPTION_OCCURRED_POSTGRES_DATABASE_CONSTRAINT_FAILED_WHILE_GET = "Exception occurred while get, Postgres Database - ABHA DB - Constraint Failed";
    public static final String EXCEPTION_OCCURRED_WHILE_COMMUNICATING_WITH_DL_GATEWAY_PLEASE_TRY_AGAIN = "Exception occurred while communicating with DL gateway, please try again.";
    public static final String RESEND_OR_REMATCH_OTP_EXCEPTION = "You have requested multiple OTPs Or Exceeded maximum number of attempts for OTP match in this transaction. Please try again in 30 minutes.";
    public static final String UNABLE_TO_CONNECT_TO_REDIS_PLEASE_TRY_AGAIN = "Unable to connect to Redis, Please try again.";

    private EnrollErrorConstants() {
    }
}
