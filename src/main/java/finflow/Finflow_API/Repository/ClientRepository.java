package finflow.Finflow_API.Repository;

import finflow.Finflow_API.Model.Client;
import finflow.Finflow_API.Repository.Interfaces.ClientRepositoryInterface;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long>, ClientRepositoryInterface {
}