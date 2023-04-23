package in.gov.abdm.abha.enrollment.enums.hidbenefit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;
import java.util.Optional;

@Getter
@AllArgsConstructor
public enum HidBenefitStatus {

    LINKED(1), DELINKED(0);

    private int value;

    public int value() {
        return value;
    }

    public static String status(int value) {
        Optional<HidBenefitStatus> statusOptional = Arrays.asList(HidBenefitStatus.values()).stream()
                .filter(se -> se.value() == value).findFirst();
        return statusOptional.isPresent() ? statusOptional.get().name() : "";
    }
}
