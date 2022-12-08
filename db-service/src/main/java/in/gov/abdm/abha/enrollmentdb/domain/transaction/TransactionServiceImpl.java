package in.gov.abdm.abha.enrollmentdb.domain.transaction;

import java.time.LocalDateTime;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.gov.abdm.abha.enrollmentdb.model.transaction.TransactionDto;
import in.gov.abdm.abha.enrollmentdb.model.transaction.Transection;
import in.gov.abdm.abha.enrollmentdb.repository.TransactionRepository;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService {
	
	private int minusMinutes = 20;
	private int plusMinutes = 10;
	
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
        return transactionRepository.save(transaction);
    }

    @Override
    public Mono<TransactionDto> getTransaction(Long id) {
        return transactionRepository.findById(id)
                .map(transaction -> modelMapper.map(transaction, TransactionDto.class));
    }

    @Override
	public Mono<TransactionDto> getTransactionByTxnId(String txnId) {
		return transactionRepository
				.findByTxnId(txnId, LocalDateTime.now().minusMinutes(minusMinutes),
						LocalDateTime.now().plusMinutes(plusMinutes))
				.map(transaction -> modelMapper.map(transaction, TransactionDto.class));
	}

    @Override
    public Mono<Transection> updateTransactionById(TransactionDto transactionDto, String id) {
        Transection transaction = modelMapper.map(transactionDto, Transection.class);
        return transactionRepository.save(transaction);
    }
}
