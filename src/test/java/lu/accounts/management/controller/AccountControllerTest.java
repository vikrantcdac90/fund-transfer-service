package lu.accounts.management.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import lu.accounts.management.dto.AccountDto;
import lu.accounts.management.entity.Account;
import lu.accounts.management.response.AccountServiceResponse;
import lu.accounts.management.service.AccountService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AccountController.class)
public class AccountControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AccountService accountService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void testCreateAccount() throws Exception {
		AccountDto accountDto = new AccountDto();
		accountDto.setOwnerId(UUID.randomUUID());
		accountDto.setCurrency("USD");
		accountDto.setBalance(BigDecimal.valueOf(1000));

		Account account = new Account();
		account.setOwnerId(accountDto.getOwnerId());
		account.setCurrency("USD");
		account.setBalance(BigDecimal.valueOf(1000));

		when(accountService.createAccount(any(AccountDto.class))).thenReturn(account);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/accounts/create").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(accountDto))).andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().json(objectMapper.writeValueAsString(
						new AccountServiceResponse<>(true, "account created successfully", account))));
	}

	@Test
	void testCreateAccount_InternalServerError() throws Exception {
		AccountDto accountDto = new AccountDto();
		accountDto.setOwnerId(UUID.randomUUID());
		accountDto.setCurrency("USD");
		accountDto.setBalance(BigDecimal.valueOf(1000));

		Mockito.doThrow(new RuntimeException("Failed to create account")).when(accountService)
				.createAccount(any(AccountDto.class));

		mockMvc.perform(MockMvcRequestBuilders.post("/api/accounts/create").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(accountDto))).andExpect(status().isInternalServerError())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().json(objectMapper.writeValueAsString(new AccountServiceResponse<>(false,
						"Failed to create account: Failed to create account", null))));
	}
}
