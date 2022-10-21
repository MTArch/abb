package in.gov.abdm.abha.enrollmentdb.domain.template;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TemplateServiceImpl implements TemplateService{
    @Autowired
    private ModelMapper mapper;

//    @Autowired
//    private TemplateRepository repository;
}
