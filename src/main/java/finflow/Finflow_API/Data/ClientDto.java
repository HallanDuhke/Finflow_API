package finflow.Finflow_API.Data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.OffsetDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientDto {
    private Long id;
    private String fullName;
    private String email;
    private String cpfCnpj;
    private OffsetDateTime createdAt;
}