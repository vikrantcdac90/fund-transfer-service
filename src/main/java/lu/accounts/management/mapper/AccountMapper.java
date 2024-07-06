package lu.accounts.management.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import lu.accounts.management.dto.AccountDto;
import lu.accounts.management.entity.Account;

@Mapper
public interface AccountMapper {
	AccountMapper INSTANCE = Mappers.getMapper(AccountMapper.class);

	Account toEntity(AccountDto dto);

	AccountDto toDto(Account entity);
}
