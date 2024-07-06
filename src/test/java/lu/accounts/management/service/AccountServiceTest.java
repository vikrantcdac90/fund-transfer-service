package lu.accounts.management.service;

import lu.accounts.management.dto.AccountDto;
import lu.accounts.management.entity.Account;
import lu.accounts.management.mapper.AccountMapper;
import lu.accounts.management.repository.AccountRepository;
import lu.accounts.management.service.AccountService.ExchangeRateResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
public class AccountServiceTest {

    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private RestTemplate restTemplate;

    private UUID fromOwnerId;
    private UUID toOwnerId;
    private Account fromAccount;
    private Account toAccount;
    private AccountDto accountDto;

    @BeforeEach
    void setUp() {
        fromOwnerId = UUID.randomUUID();
        toOwnerId = UUID.randomUUID();

        fromAccount = new Account();
        fromAccount.setOwnerId(fromOwnerId);
        fromAccount.setCurrency("USD");
        fromAccount.setBalance(BigDecimal.valueOf(1000));

        toAccount = new Account();
        toAccount.setOwnerId(toOwnerId);
        toAccount.setCurrency("EUR");
        toAccount.setBalance(BigDecimal.valueOf(500));

        accountDto = new AccountDto();
        accountDto.setOwnerId(fromOwnerId);
        accountDto.setCurrency("USD");
        accountDto.setBalance(BigDecimal.valueOf(1000));
    }

    @Test
    void testFindAccountByOwnerId() {
        when(accountRepository.findByOwnerId(fromOwnerId)).thenReturn(Optional.of(fromAccount));

        Optional<Account> account = accountService.findAccountByOwnerId(fromOwnerId);

        assertTrue(account.isPresent());
        assertEquals(fromAccount, account.get());
        verify(accountRepository, times(1)).findByOwnerId(fromOwnerId);
    }

    @Test
    void testCreateAccount() {
        Account account = AccountMapper.INSTANCE.toEntity(accountDto);
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        Account createdAccount = accountService.createAccount(accountDto);

        assertNotNull(createdAccount);
        assertEquals(accountDto.getOwnerId(), createdAccount.getOwnerId());
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void testTransferFunds() {
        BigDecimal amount = BigDecimal.valueOf(100);
        Map<String, BigDecimal> rates = new HashMap<>();
        rates.put("EUR", BigDecimal.valueOf(0.85));
        ExchangeRateResponse exchangeRateResponse = new ExchangeRateResponse("USD", rates);

        when(accountRepository.findByOwnerId(fromOwnerId)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByOwnerId(toOwnerId)).thenReturn(Optional.of(toAccount));
        when(restTemplate.getForObject(anyString(), eq(ExchangeRateResponse.class))).thenReturn(exchangeRateResponse);

        accountService.transferFunds(fromOwnerId, toOwnerId, amount);

        assertEquals(BigDecimal.valueOf(900), fromAccount.getBalance());
        verify(accountRepository, times(1)).findByOwnerId(fromOwnerId);
        verify(accountRepository, times(1)).findByOwnerId(toOwnerId);
        verify(accountRepository, times(1)).save(fromAccount);
        verify(accountRepository, times(1)).save(toAccount);
    }

    

    @Test
    void testTransferFunds_DebitAccountNotFound() {
        BigDecimal amount = BigDecimal.valueOf(100);

        when(accountRepository.findByOwnerId(fromOwnerId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            accountService.transferFunds(fromOwnerId, toOwnerId, amount);
        });

        String expectedMessage = "Debit account does not exist";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testTransferFunds_CreditAccountNotFound() {
        BigDecimal amount = BigDecimal.valueOf(100);

        when(accountRepository.findByOwnerId(fromOwnerId)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByOwnerId(toOwnerId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            accountService.transferFunds(fromOwnerId, toOwnerId, amount);
        });

        String expectedMessage = "Credit account does not exist";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testTransferFunds_ExchangeRateNotAvailable() {
        BigDecimal amount = BigDecimal.valueOf(100);

        when(accountRepository.findByOwnerId(fromOwnerId)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByOwnerId(toOwnerId)).thenReturn(Optional.of(toAccount));
        when(restTemplate.getForObject(anyString(), eq(ExchangeRateResponse.class))).thenReturn(null);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            accountService.transferFunds(fromOwnerId, toOwnerId, amount);
        });

        String expectedMessage = "Exchange rate not available";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}
