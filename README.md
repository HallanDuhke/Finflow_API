# Finflow API (Minimalista para Discussão Técnica)

Este repositório foi deliberadamente mantido ENXUTO para servir como base de entrevista e discussão arquitetural. O foco não é cobrir todo o escopo de um backend financeiro, mas demonstrar práticas iniciais corretas e abrir espaço para explorar evoluções.

## Objetivo
API simples de gestão de clientes com versionamento de endpoint, migrações de banco (Flyway), testes de integração básicos e observabilidade mínima (traceId em logs).

## Stack Principal
- Java 17 + Spring Boot 3.5.x
- JPA / Hibernate
- Flyway (versionamento de schema)
- Testcontainers (ambiente isolado para testes)
- Springdoc OpenAPI (Swagger UI)
- Docker (multi-stage build)

## Melhorias Implementadas (neste sprint de 1 dia)
- Versionamento REST: `/api/v1/...`
- Retorno `201 Created` com `Location` em criação de recurso
- Middleware de erro customizado + correlação via `traceId` (injetado em MDC)
- Logging configurado via `logback-spring.xml`
- Teste de integração (`MockMvc`) validando 404 e criação de cliente
- Profiles separados (`application.properties`, `-demo`, `-docker`)
- Multi-stage Dockerfile leve

## Endpoints Principais
| Método | Rota | Descrição |
|--------|------|-----------|
| GET | /api/v1/clients | Lista clientes |
| GET | /api/v1/clients/{id} | Busca por id |
| POST | /api/v1/clients | Cria cliente |
| PATCH | /api/v1/clients/{id} | Atualiza campos parciais |
| DELETE | /api/v1/clients/{id} | Remove cliente |

## Executando
### Local (profile demo - H2)
```
./mvnw spring-boot:run -Dspring-boot.run.profiles=demo
```
Acessar H2 Console: `http://localhost:8080/h2-console`

### Docker (profile docker)
```
docker build -t finflow-api:latest .
docker run -p 8080:8080 finflow-api:latest
```

### Swagger
`http://localhost:8080/swagger-ui/index.html`

## Testes
```
./mvnw test
```

## Trade-offs Deliberados
| O que não foi incluído | Motivo |
|------------------------|--------|
| Autenticação/JWT | Mantido fora para focar em base limpa e discutir segurança na entrevista |
| Circuit Breaker / Resiliência | Pode ser adicionado (Resilience4j) em etapa futura |
| Observabilidade completa (Prometheus/Grafana) | Intencionalmente omitido para simplicidade |
| Microsserviços / Mensageria | Escopo mínimo single service para discutir evolução modular |
| CI/CD pipeline | Preferido descrever verbalmente / diagramar durante entrevista |
| Validação de conflitos (e-mail duplicado) | Pode ser adicionado com verificação + 409 |
| Logs JSON estruturados | Config comentada para mostrar caminho |

## Próximas Evoluções (Roadmap Exploratório)
1. Segurança (JWT + RBAC)
2. Métricas (Micrometer) + Prometheus
3. Resilience Patterns (Retry, CircuitBreaker)
4. Sonar + Checkstyle + Pipeline CI/CD
5. Módulo / serviço Accounts separado (bounded context)
6. Tracing (OpenTelemetry + Jaeger)
7. Deploy em Kubernetes (manifests + probes)
8. Auditoria (createdBy/updatedBy) + eventos de domínio
9. Testes de contrato (Consumer-Driven)
10. Feature flags / toggle gradual

## Perguntas que Este Projeto Permite Discutir
- Como evoluir de monólito modular para microsserviços?
- Como introduzir segurança sem quebrar compatibilidade?
- Estratégias de versionamento de API (URI vs Header)
- Observabilidade: qual a ordem prática de adoção?
- Boas práticas de modelagem: onde introduzir DDD leve?
- Quebrar acoplamento: onde entra uma camada de Application / Domain?

## Estrutura Simplificada
```
src/main/java
  finflow/Finflow_API
    Controller (exposição REST)
    Service (regras simples)
    Repository (JPA + interface custom)
    Model (entidades)
    Data (DTOs e requests)
    Extensions (middleware e config)
```

## Decisões Arquiteturais
- `ErrorMiddleware` em filtro inicial: garante formato consistente de erro e traceId.
- `ClientMapper` estático: simplicidade > injeção (pode migrar para MapStruct depois).
- DTO separado de entidade: evita vazamento de model interno.

## Observabilidade Mínima
Cada log inclui `traceId=...`. Em ambiente distribuído, poderia ser propagado via headers (`X-Trace-Id`). Isso abre conversa para OpenTelemetry.

## Licença
Uso livre para fins de demonstração técnica.

---
Se quiser ver uma versão expandida (segurança, métricas, pipeline), posso preparar um branch de evolução incremental.
