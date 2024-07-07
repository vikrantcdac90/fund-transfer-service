package lu.accounts.management.exceptions;


import lu.accounts.management.response.AccountServiceResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(Exception.class)
	public ResponseEntity<AccountServiceResponse<?>> handleException(Exception e) {

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new AccountServiceResponse<>(false, "Internal Server Error: " + e.getMessage()));
	}
}