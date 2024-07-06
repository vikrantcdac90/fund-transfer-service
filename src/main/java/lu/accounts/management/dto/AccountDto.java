package lu.accounts.management.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {
   	
    private UUID ownerId;

    @NotNull
    @NotBlank(message = "Currency is mandatory (USD,EUR etc.)")
    private String currency;


    @NotNull(message = "Initial balance is mandatory")
    private BigDecimal balance;
}
