package in.gov.abdm.abha.enrollmentdb.domain.transaction;

import in.gov.abdm.abha.enrollmentdb.model.transaction.TransactionDto;
import in.gov.abdm.abha.enrollmentdb.model.transaction.Transection;
import in.gov.abdm.abha.enrollmentdb.repository.TransactionRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    private ModelMapper modelMapper;

    private Mono<TransactionDto> convertTransactionToTransactionDto(Transection transaction) {
        return Mono.just(modelMapper.map(transaction, TransactionDto.class));
    }

    @Override
    public Mono<Transection> createTransaction(TransactionDto transactionDto) {
        Transection transaction = modelMapper.map(transactionDto, Transection.class);
        transaction.setAsNew();
        Mono<Long> i = transactionRepository.getMaxTransactionId();
        return i.flatMap(k -> handle(transaction, k));
    }

    private Mono<Transection> handle(Transection transaction, Long id) {
        transaction.setId(id + 1);
        return transactionRepository.save(transaction);
    }

    @Override
    public Mono<TransactionDto> getTransaction(Long id) {
        return transactionRepository.findById(id)
                .map(transaction -> modelMapper.map(transaction, TransactionDto.class));
    }

    @Override
    public Mono<TransactionDto> getTransactionByTxnId(String txnId) {
        return transactionRepository.findByTxnId(txnId)
                .map(transaction -> modelMapper.map(transaction, TransactionDto.class));
    }

    @Override
    public Mono<Transection> updateTransactionById(TransactionDto transactionDto, String id) {
        Transection transaction = modelMapper.map(transactionDto, Transection.class);
        return transactionRepository.save(transaction);
    }
}
