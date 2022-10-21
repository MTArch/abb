package in.gov.abdm.abha.enrollmentdb.domain.transaction;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionServiceImpl implements TransactionService{
    @Autowired
    private ModelMapper mapper;

//    @Autowired
//    private TransactionRepository repository;
}
