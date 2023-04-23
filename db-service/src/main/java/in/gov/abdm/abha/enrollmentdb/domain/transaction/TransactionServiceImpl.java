package in.gov.abdm.abha.enrollmentdb.domain.transaction;

import java.time.LocalDateTime;
import java.util.Base64;

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

	@Override
	public Mono<TransactionDto> createTransaction(TransactionDto transactionDto) {
		Transection transaction = modelMapper.map(transactionDto, Transection.class).setAsNew();
		return transactionRepository.save(transaction).flatMap(txn -> updatePic(transactionDto, txn));
	}

	private Mono<TransactionDto> updatePic(TransactionDto transactionDto, Transection transection) {
		if (transactionDto.getKycPhoto() != null) {
			return transactionRepository.updateKycPhoto(transactionDto.getKycPhoto().getBytes(), transection.getId())
					.switchIfEmpty(Mono.defer(() -> {
						transactionDto.setId(transection.getId());
						return Mono.just(transactionDto);
					}));
		}
		return Mono.just(transactionDto);
	}

	@Override
	public Mono<TransactionDto> getTransaction(Long id) {
		return transactionRepository.findById(id).flatMap(txn -> getPic(id, txn));
	}

	private Mono<TransactionDto> getPic(Long id, Transection txn) {
		return transactionRepository.getProfilePhoto(id).map(pic -> {
			TransactionDto transactionDto = modelMapper.map(txn, TransactionDto.class);
			transactionDto.setKycPhoto(new String(Base64.getDecoder().decode(pic.replace("\n", ""))));
			return transactionDto;
		}).switchIfEmpty(Mono.just(modelMapper.map(txn, TransactionDto.class)));
	}

	@Override
	public Mono<TransactionDto> getTransactionByTxnId(String txnId) {
		return transactionRepository
				.findByTxnId(txnId, LocalDateTime.now().minusMinutes(minusMinutes),
						LocalDateTime.now().plusMinutes(plusMinutes)).flatMap(txn -> getPic(txn.getId(), txn));
	}

	@Override
	public Mono<TransactionDto> updateTransactionById(TransactionDto transactionDto, String id) {
		Transection transaction = modelMapper.map(transactionDto, Transection.class);
		return transactionRepository.save(transaction).flatMap(txn -> updatePic(transactionDto, txn));
	}

    @Override
    public Mono<Void> deleteTransactionByTxnId(String txnId) {
        return transactionRepository.deleteByTxnId(txnId);
    }
}
