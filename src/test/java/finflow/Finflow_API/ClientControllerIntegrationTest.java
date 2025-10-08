package finflow.Finflow_API;

import com.fasterxml.jackson.databind.ObjectMapper;
import finflow.Finflow_API.Data.CreateClientRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("demo") // usar banco em memória para testes rápidos
class ClientControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @Test
    @DisplayName("GET /api/v1/clients/{id} deve retornar 404 quando não existir")
    void getClientNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/clients/{id}", 9999))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/v1/clients cria cliente e retorna 201 + Location")
    void createClient() throws Exception {
        CreateClientRequest req = new CreateClientRequest("Usuario Teste","teste@example.com","12345678900");
        mockMvc.perform(post("/api/v1/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("teste@example.com"));
    }
}
