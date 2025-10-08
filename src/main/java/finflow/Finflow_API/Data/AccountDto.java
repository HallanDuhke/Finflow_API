package finflow.Finflow_API.Data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.OffsetDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {
    private Long id;
    private Long clientId;
    private String accountNumber;
    private String accountType;
    private BigDecimal balance;
    private OffsetDateTime createdAt;
}