package in.gov.abdm.abha.enrollment.model.phr;
import com.fasterxml.jackson.annotation.JsonFormat;
import in.gov.abdm.phr.enrollment.address.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    private Long id;
    private String healthIdNumber;
    private String phrAddress;
    private String password;
    private String phrProvider;
    private String fullName;
    private String firstName;
    private String lastName;
    private String middleName;
    private String gender;
    private String emailId;
    private String mobileNumber;
    private String mobileCountryCode;
    private String dayOfBirth;
    private String monthOfBirth;
    private String yearOfBirth;
    private String dateOfBirth;
    private String profilePhoto;
    private String status;
    private String kycStatus;
    private boolean mobileNumberVerified;
    private boolean emailIdVerified;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "Asia/Kolkata")
    private Timestamp createdDate;
    private String createdBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "Asia/Kolkata")
    private Timestamp updatedDate;
    private String updatedBy;
    private boolean isProfilePhotoCompressed;
    private String reasonCode;
    private Address userAddress;
}
