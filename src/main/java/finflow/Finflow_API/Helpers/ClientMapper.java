package finflow.Finflow_API.Helpers;

import finflow.Finflow_API.Data.ClientDto;
import finflow.Finflow_API.Data.CreateClientRequest;
import finflow.Finflow_API.Data.UpdateClientRequest;
import finflow.Finflow_API.Model.Client;

import java.util.List;
import java.util.stream.Collectors;

public final class ClientMapper {

    private ClientMapper() {}

    public static ClientDto toDto(Client entity) {
        if (entity == null) return null;
        return new ClientDto(
                entity.getId(),
                entity.getFullName(),
                entity.getEmail(),
                entity.getCpfCnpj(),
                entity.getCreatedAt()
        );
    }

    public static List<ClientDto> toDtoList(List<Client> entities) {
        return entities.stream().map(ClientMapper::toDto).collect(Collectors.toList());
    }

    public static Client fromCreateRequest(CreateClientRequest request) {
        Client c = new Client();
        c.setFullName(request.getFullName());
        c.setEmail(request.getEmail());
        c.setCpfCnpj(request.getCpfCnpj());
        return c;
    }

    public static void applyUpdate(Client entity, UpdateClientRequest request) {
        if (request.getFullName() != null) entity.setFullName(request.getFullName());
        if (request.getEmail() != null) entity.setEmail(request.getEmail());
        if (request.getCpfCnpj() != null) entity.setCpfCnpj(request.getCpfCnpj());
    }
}