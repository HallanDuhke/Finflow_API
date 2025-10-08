package finflow.Finflow_API.Repository.Interfaces;

import finflow.Finflow_API.Model.Client;
import java.util.List;
import java.util.Optional;

public interface ClientRepositoryInterface {

    List<Client> findAll();

    Optional<Client> findById(Long id);

    Client save(Client client);

    void deleteById(Long id);
}