package in.gov.abdm.abha.enrollment.constants;

public interface EnrollErrorConstants {
    String EXCEPTION_OCCURRED_POSTGRES_DATABASE_CONSTRAINT_FAILED_WHILE_CREATE = "c10Exception occurred while create, Postgres Database - ABHA DB - Constraint Failed";
    String EXCEPTION_OCCURRED_POSTGRES_DATABASE_CONSTRAINT_FAILED_WHILE_UPDATE = "Exception occurred while update, Postgres Database - ABHA DB - Constraint Failed";
    String EXCEPTION_OCCURRED_POSTGRES_DATABASE_CONSTRAINT_FAILED_WHILE_GET = "Exception occurred while get, Postgres Database - ABHA DB - Constraint Failed";
    String EXCEPTION_OCCURRED_WHILE_COMMUNICATING_WITH_DL_GATEWAY_PLEASE_TRY_AGAIN = "Exception occurred while communicating with DL gateway, please try again.";
    String RESEND_OR_REMATCH_OTP_EXCEPTION = "You have requested multiple OTPs Or Exceeded maximum number of attempts for OTP match in this transaction. Please try again in 30 minutes.";
    String UNABLE_TO_CONNECT_TO_REDIS_PLEASE_TRY_AGAIN = "Unable to connect to Redis, Please try again.";
}
