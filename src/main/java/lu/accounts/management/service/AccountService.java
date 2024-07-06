package lu.accounts.management.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lu.accounts.management.dto.AccountDto;
import lu.accounts.management.entity.Account;
import lu.accounts.management.mapper.AccountMapper;
import lu.accounts.management.repository.AccountRepository;

@Service
@Slf4j
public class AccountService {
	private final AccountRepository accountRepository;
	private final RestTemplate restTemplate;
	private static final String CURRENCY_EXCHANGE_URL = "https://api.exchangerate-api.com/v4/latest/";

	@Autowired
	public AccountService(AccountRepository accountRepository, RestTemplate restTemplate) {
		this.accountRepository = accountRepository;
		this.restTemplate = restTemplate;
	}

	public List<Account> findAllAccounts() {
		log.info("Fetching all accounts");
		return accountRepository.findAll();
	}

	public void deleteAccountByOwnerId(UUID ownerId) {
		log.info("Deleting account with owner ID: {}", ownerId);
		accountRepository.deleteByOwnerId(ownerId);
	}

	public Optional<Account> findAccountByOwnerId(UUID ownerId) {
		log.info("find account by ID : {}", ownerId);
		return accountRepository.findByOwnerId(ownerId);
	}

	public Account createAccount(AccountDto createAccountDto) {
		log.info("create account : {}", createAccountDto);
		Account account = AccountMapper.INSTANCE.toEntity(createAccountDto);
		return accountRepository.save(account);
	}

	@Transactional
	public void transferFunds(UUID fromOwnerId, UUID toOwnerId, BigDecimal amount) {
		log.info("transfer funds : {}", amount);
		Account fromAccount = accountRepository.findByOwnerId(fromOwnerId)
				.orElseThrow(() -> new RuntimeException("Debit account does not exist"));

		Account toAccount = accountRepository.findByOwnerId(toOwnerId)
				.orElseThrow(() -> new RuntimeException("Credit account does not exist"));

		if (fromAccount.getBalance().compareTo(amount) < 0) {
			throw new RuntimeException("Insufficient balance in the debit account");
		}

		BigDecimal exchangeRate = getExchangeRate(fromAccount.getCurrency(), toAccount.getCurrency());
		BigDecimal convertedAmount = amount.multiply(exchangeRate);

		fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
		toAccount.setBalance(toAccount.getBalance().add(convertedAmount));

		accountRepository.save(fromAccount);
		accountRepository.save(toAccount);
	}

	private BigDecimal getExchangeRate(String fromCurrency, String toCurrency) {
		String url = CURRENCY_EXCHANGE_URL + fromCurrency;
		ExchangeRateResponse response = restTemplate.getForObject(url, ExchangeRateResponse.class);
		if (response == null || response.getRates() == null || !response.getRates().containsKey(toCurrency)) {
			throw new RuntimeException("Exchange rate not available");
		}
		return response.getRates().get(toCurrency);
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ExchangeRateResponse {
		private String base;
		private Map<String, BigDecimal> rates;

	}
}
