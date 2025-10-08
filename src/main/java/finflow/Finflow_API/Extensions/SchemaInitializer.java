package finflow.Finflow_API.Extensions;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Inicializa/garante a existência das tabelas caso as migrations ainda não tenham sido aplicadas.
 * NÃO substitui o uso de Flyway. Serve apenas como fallback em ambientes vazios.
 */
@Slf4j
@Component
public class SchemaInitializer {

    private final JdbcTemplate jdbcTemplate;

    @Value("${app.schema.auto-create:false}")
    private boolean autoCreate;

    public SchemaInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    @Transactional
    public void init() {
        if (!autoCreate) {
            log.info("[SchemaInitializer] autoCreate=false - nenhuma ação executada");
            return;
        }
        try {
            log.info("[SchemaInitializer] Verificando existência de tabelas base...");
            List<String> faltantes = tabelasFaltantes();
            if (faltantes.isEmpty()) {
                log.info("[SchemaInitializer] Todas as tabelas principais já existem.");
                return;
            }
            log.warn("[SchemaInitializer] Tabelas faltantes detectadas: {}. Criando...", faltantes);
            criarTabelas(faltantes);
        } catch (Exception ex) {
            log.error("[SchemaInitializer] Erro ao garantir schema: {}", ex.getMessage(), ex);
        }
    }

    private List<String> tabelasFaltantes() {
        List<String> esperadas = List.of("clients", "accounts");
        List<String> faltantes = new ArrayList<>();
        for (String t : esperadas) {
            Boolean exists = jdbcTemplate.query(
                    "SELECT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = ?)",
                    rs -> {
                        if (rs.next()) return rs.getBoolean(1); else return false;
                    }, t
            );
            if (Boolean.FALSE.equals(exists)) {
                faltantes.add(t);
            }
        }
        return faltantes;
    }

    private void criarTabelas(List<String> faltantes) {
        for (String tabela : faltantes) {
            switch (tabela) {
                case "clients" -> criarClients();
                case "accounts" -> criarAccounts();
                default -> log.warn("Tabela inesperada ignorada: {}", tabela);
            }
        }
    }

    private void criarClients() {
        log.info("[SchemaInitializer] Criando tabela clients");
        jdbcTemplate.execute("CREATE TABLE clients (" +
                "id SERIAL PRIMARY KEY," +
                "full_name VARCHAR(200) NOT NULL," +
                "email VARCHAR(150) UNIQUE NOT NULL," +
                "cpf_cnpj VARCHAR(30) UNIQUE," +
                "created_at TIMESTAMPTZ DEFAULT now()" +
                ")");
    }

    private void criarAccounts() {
        // Garantir dependência clients antes
        Boolean clientsExist = jdbcTemplate.query(
                "SELECT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'clients')",
                rs -> rs.next() && rs.getBoolean(1)
        );
        if (!clientsExist) {
            criarClients();
        }
        log.info("[SchemaInitializer] Criando tabela accounts");
        jdbcTemplate.execute("CREATE TABLE accounts (" +
                "id SERIAL PRIMARY KEY," +
                "client_id INT NOT NULL REFERENCES clients(id)," +
                "account_number VARCHAR(50) NOT NULL," +
                "account_type VARCHAR(50)," +
                "balance NUMERIC(18,2) DEFAULT 0," +
                "created_at TIMESTAMPTZ DEFAULT now()" +
                ")");
    }
}
