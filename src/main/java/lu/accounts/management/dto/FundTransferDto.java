package lu.accounts.management.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FundTransferDto {

	@NotNull
	private UUID fromOwnerId;

	@NotNull
	private UUID toOwnerId;

	@NotNull
	private BigDecimal amount;

}
