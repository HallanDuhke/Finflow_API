package finflow.Finflow_API.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateClientRequest {

    @Size(max = 200)
    private String fullName;

    @Email
    @Size(max = 150)
    private String email;

    @Size(max = 30)
    private String cpfCnpj;
}