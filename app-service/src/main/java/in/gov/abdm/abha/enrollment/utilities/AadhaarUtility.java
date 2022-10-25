package in.gov.abdm.abha.enrollment.utilities;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.stream.Stream;

@UtilityClass
@Slf4j
public class AadhaarUtility {
	public String getPhoneNumber(String xmlPayload) {
		Optional<String> optionalResponse = Stream.of(Common.xmlToJson(xmlPayload).split(",")).filter(arg -> arg.startsWith("******")).findFirst();
		return optionalResponse.orElse(null);
	}
}
