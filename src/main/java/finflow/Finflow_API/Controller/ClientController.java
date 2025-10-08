package finflow.Finflow_API.Controller;

import finflow.Finflow_API.Data.ClientDto;
import finflow.Finflow_API.Data.CreateClientRequest;
import finflow.Finflow_API.Data.UpdateClientRequest;
import finflow.Finflow_API.Service.Interfaces.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/clients")
public class ClientController {

    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public ResponseEntity<List<ClientDto>> getAllClients() {
        return ResponseEntity.ok(clientService.getAllClients());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientDto> getClientById(@PathVariable Long id) {
        ClientDto client = clientService.getClientById(id);
        if (client == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(client);
    }

    @PostMapping
    public ResponseEntity<ClientDto> createClient(@Validated @RequestBody CreateClientRequest request) {
        ClientDto created = clientService.createClient(request);

        URI location = URI.create(String.format("/api/v1/clients/%d", created.getId()));
        return ResponseEntity.created(location).body(created);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ClientDto> updateClient(@PathVariable Long id,
                                                  @Validated @RequestBody UpdateClientRequest request) {
        ClientDto updated = clientService.updateClient(id, request);
        if (updated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        ClientDto existing = clientService.getClientById(id);
        if (existing == null) return ResponseEntity.notFound().build();
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }
}
