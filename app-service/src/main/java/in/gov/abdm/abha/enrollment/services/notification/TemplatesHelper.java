package in.gov.abdm.abha.enrollment.services.notification;

import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.model.notification.template.Templates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;

@Service
public class TemplatesHelper {
    @Autowired
    @Qualifier(AbhaConstants.MESSAGE_TEMPLATES)
    List<Templates> templates;

    public String prepareRegistrationOtpMessage(Long templateId, String otp) {
        return MessageFormat.format(templates.stream().filter(res-> res.getId().equals(templateId)).findAny().get().getMessage(), otp);
    }

    public String prepareSMSMessage(Long templateId,String... params) {
        return MessageFormat.format(templates.stream().filter(res-> res.getId().equals(templateId)).findAny().get().getMessage(), params);
    }
}
