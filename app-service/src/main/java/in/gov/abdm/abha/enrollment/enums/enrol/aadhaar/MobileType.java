package in.gov.abdm.abha.enrollment.enums.enrol.aadhaar;


import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public enum MobileType {
    SELF("self"),
    FAMILY("family"),
    WRONG("wrong");
    private final String value;

    @JsonCreator
    public static MobileType fromText(String text){
        for(MobileType r : MobileType.values()){
            if(r.getValue().equals(text)){
                return r;
            }
        }
        return MobileType.WRONG;
    }
}
