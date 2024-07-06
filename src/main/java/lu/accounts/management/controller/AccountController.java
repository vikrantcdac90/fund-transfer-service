package lu.accounts.management.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lu.accounts.management.dto.AccountDto;
import lu.accounts.management.dto.FundTransferDto;
import lu.accounts.management.entity.Account;
import lu.accounts.management.response.AccountServiceResponse;
import lu.accounts.management.service.AccountService;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
	private final AccountService accountService;

	@Autowired
	public AccountController(AccountService accountService) {
		this.accountService = accountService;
	}
	

	@PostMapping("/transfer")
	public ResponseEntity<AccountServiceResponse<?>> transferFunds(@Valid @RequestBody FundTransferDto fundTransferDto,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			Map<String, String> errors = new HashMap<>();
			for (FieldError error : bindingResult.getFieldErrors()) {
				errors.put(error.getField(), error.getDefaultMessage());
			}
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new AccountServiceResponse<>(false, "Validation errors", errors));
		}

		try {
			accountService.transferFunds(fundTransferDto.getFromOwnerId(), fundTransferDto.getToOwnerId(),
					fundTransferDto.getAmount());
			Map<String, String> response = new HashMap<>();
			response.put("message", "Transfer successful");
			return ResponseEntity.status(HttpStatus.CREATED)
					.body(new AccountServiceResponse<>(true, "amout transferred successfully", response));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new AccountServiceResponse<>(false, "Failed to transfer amount: " + e.getMessage()));
		}

	}

	@PostMapping("/create")
	public ResponseEntity<AccountServiceResponse<?>> createAccount(@Valid @RequestBody AccountDto createAccountDto,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			Map<String, String> errors = new HashMap<>();
			for (FieldError error : bindingResult.getFieldErrors()) {
				errors.put(error.getField(), error.getDefaultMessage());
			}
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new AccountServiceResponse<>(false, "Validation errors", errors));
		}

		try {
			UUID accountId = UUID.randomUUID();
			createAccountDto.setOwnerId(accountId);
			Account account = accountService.createAccount(createAccountDto);
			return ResponseEntity.status(HttpStatus.CREATED)
					.body(new AccountServiceResponse<>(true, "account created successfully", account));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new AccountServiceResponse<>(false, "Failed to create account: " + e.getMessage()));
		}
	}
}
