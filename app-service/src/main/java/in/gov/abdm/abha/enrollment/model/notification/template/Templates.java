package in.gov.abdm.abha.enrollment.model.notification.template;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * pojo to load message templates coming from notification db service
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Templates {
    private Long id;
    private String name;
    private String message;
    private String header;
    private TemplateType type;
}
