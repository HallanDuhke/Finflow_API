package finflow.Finflow_API.Service.Interfaces;

import finflow.Finflow_API.Data.ClientDto;
import finflow.Finflow_API.Data.CreateClientRequest;
import finflow.Finflow_API.Data.UpdateClientRequest;
import java.util.List;

public interface ClientService {
    List<ClientDto> getAllClients();
    ClientDto getClientById(Long id);
    ClientDto createClient(CreateClientRequest request);
    ClientDto updateClient(Long id, UpdateClientRequest request);
    void deleteClient(Long id);
}