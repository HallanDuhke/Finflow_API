package finflow.Finflow_API.Helpers;

import finflow.Finflow_API.Data.AccountDto;
import finflow.Finflow_API.Model.Account;

import java.util.List;
import java.util.stream.Collectors;

public final class AccountMapper {

    private AccountMapper() {}

    public static AccountDto toDto(Account entity) {
        if (entity == null) return null;
        return new AccountDto(
                entity.getId(),
                entity.getClient() != null ? entity.getClient().getId() : null,
                entity.getAccountNumber(),
                entity.getAccountType(),
                entity.getBalance(),
                entity.getCreatedAt()
        );
    }

    public static List<AccountDto> toDtoList(List<Account> entities) {
        return entities.stream().map(AccountMapper::toDto).collect(Collectors.toList());
    }
}