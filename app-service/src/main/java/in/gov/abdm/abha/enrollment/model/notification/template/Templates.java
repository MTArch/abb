package in.gov.abdm.abha.enrollment.model.notification.template;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * pojo to load message templates coming from notification db service
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Templates implements Serializable {
    private Long id;
    private String name;
    private String message;
    private String header;
}
