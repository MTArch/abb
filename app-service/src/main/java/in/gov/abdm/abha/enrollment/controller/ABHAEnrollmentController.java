package in.gov.abdm.abha.enrollment.controller;

import in.gov.abdm.abha.enrollment.constant.ABHAEnrollmentConstant;
import in.gov.abdm.abha.enrollment.domain.message.Message;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(ABHAEnrollmentConstant.API_VERSION)
public class ABHAEnrollmentController {

    @PostMapping(ABHAEnrollmentConstant.MESSAGE_TRANSACTION)
    public Mono message(@RequestBody Message message) {
        return Mono.empty();
    }
}
