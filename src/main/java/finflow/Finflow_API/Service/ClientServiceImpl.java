package finflow.Finflow_API.Service;

import finflow.Finflow_API.Data.ClientDto;
import finflow.Finflow_API.Data.CreateClientRequest;
import finflow.Finflow_API.Data.UpdateClientRequest;
import finflow.Finflow_API.Helpers.ClientMapper;
import finflow.Finflow_API.Model.Client;
import finflow.Finflow_API.Repository.Interfaces.ClientRepositoryInterface;
import finflow.Finflow_API.Service.Interfaces.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepositoryInterface clientRepository;

    @Autowired
    public ClientServiceImpl(ClientRepositoryInterface clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public List<ClientDto> getAllClients() {
        return ClientMapper.toDtoList(clientRepository.findAll());
    }

    @Override
    public ClientDto getClientById(Long id) {
        return clientRepository.findById(id)
                .map(ClientMapper::toDto)
                .orElse(null);
    }

    @Override
    public ClientDto createClient(CreateClientRequest request) {
        Client entity = ClientMapper.fromCreateRequest(request);
        Client saved = clientRepository.save(entity);
        return ClientMapper.toDto(saved);
    }

    @Override
    public ClientDto updateClient(Long id, UpdateClientRequest request) {
        return clientRepository.findById(id)
                .map(existing -> {
                    ClientMapper.applyUpdate(existing, request);
                    Client updated = clientRepository.save(existing);
                    return ClientMapper.toDto(updated);
                })
                .orElse(null);
    }

    @Override
    public void deleteClient(Long id) {
        clientRepository.deleteById(id);
    }
}