package in.gov.abdm.abha.enrollment.enums.link.parent;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * define enum values for Relationship
 */

@Getter
@AllArgsConstructor
@ToString
public enum Relationship {

    MOTHER("mother"),
    FATHER("father"),
    SISTER("sister"),
    BROTHER("brother"),
    GUARDIAN("guardian"),
    OTHERS("others"),
    WRONG("wrong");


    private final String value;

	public static boolean isValid(String value) {
		if (value.equals(Relationship.WRONG.toString()))
			return false;

		Relationship[] values = Relationship.values();
		for (Relationship relationship : values) {
			if (relationship.toString().equals(value)) {
				return true;
			}
		}
		return false;
	}

    @JsonCreator
    public static Relationship fromText(String text) {
        for (Relationship relationship : Relationship.values()) {
            if (relationship.getValue().equals(text)) {
                return relationship;
            }
        }
        return Relationship.WRONG;
    }

}
