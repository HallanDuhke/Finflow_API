package finflow.Finflow_API.Extensions;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;


@Component
@Order(Ordered.HIGHEST_PRECEDENCE) 
public class ErrorMiddleware extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(ErrorMiddleware.class);
    private static final String TRACE_ID_KEY = "traceId";

    private final ObjectMapper mapper;
    private final boolean isDev;

    public ErrorMiddleware() {
        this.mapper = new ObjectMapper();
        this.mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        String springProfiles = System.getProperty("spring.profiles.active", System.getenv().getOrDefault("SPRING_PROFILES_ACTIVE", ""));
        this.isDev = springProfiles.contains("dev") || springProfiles.contains("local") || springProfiles.isBlank();
    }

    public static class TokenNotFoundException extends RuntimeException {
        public TokenNotFoundException() { super("O token informado não foi encontrado ou está expirado."); }
        public TokenNotFoundException(String message) { super(message); }
    }

    public static class LoginNotFoundException extends RuntimeException {
        public LoginNotFoundException(int userId) { super("Não foi encontrado login para o usuário com Id " + userId + "."); }
        public LoginNotFoundException(String message) { super(message); }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String traceId = getOrCreateTraceId(request);
        MDC.put(TRACE_ID_KEY, traceId); 
        try {
            filterChain.doFilter(request, response);

            // Se a resposta terminou com 401/403 sem exceção explícita
            if (!response.isCommitted() && (response.getStatus() == HttpStatus.UNAUTHORIZED.value() ||
                    response.getStatus() == HttpStatus.FORBIDDEN.value())) {
                writeJson(response, response.getStatus(),
                        response.getStatus() == HttpStatus.UNAUTHORIZED.value() ? "Credenciais inválidas." : "Acesso proibido.", traceId);
            }
        } catch (Exception ex) {
            logger.error("Erro capturado (TraceId: {}): {} - {}", traceId, ex.getClass().getSimpleName(), ex.getMessage(), ex);
            if (!response.isCommitted()) {
                handleException(ex, response, traceId);
            }
        } finally {
            MDC.remove(TRACE_ID_KEY); 
        }
    }

    private void handleException(Exception ex, HttpServletResponse response, String traceId) throws IOException {
        int status;
        String message;

        if (ex instanceof TokenNotFoundException || ex instanceof SecurityException || ex instanceof org.springframework.security.access.AccessDeniedException) {
            status = HttpStatus.UNAUTHORIZED.value();
            message = isDev ? ex.getMessage() : "Credenciais inválidas.";
        } else if (ex instanceof LoginNotFoundException || ex instanceof NoSuchElementException) {
            status = HttpStatus.NOT_FOUND.value();
            message = isDev ? ex.getMessage() : "Recurso não encontrado.";
        } else if (ex instanceof SecurityException) {
            status = HttpStatus.FORBIDDEN.value();
            message = isDev ? ex.getMessage() : "Acesso proibido.";
        } else if (ex instanceof java.util.concurrent.TimeoutException) {
            status = HttpStatus.REQUEST_TIMEOUT.value();
            message = isDev ? ex.getMessage() : "Tempo de requisição expirado.";
        } else if (ex instanceof IllegalArgumentException || ex instanceof ValidationException) {
            status = HttpStatus.BAD_REQUEST.value();
            message = isDev ? ex.getMessage() : "Requisição inválida.";
        } else if (ex instanceof UnsupportedOperationException) {
            status = HttpStatus.NOT_IMPLEMENTED.value();
            message = isDev ? ex.getMessage() : "Funcionalidade não implementada.";
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR.value();
            message = isDev ? ex.getMessage() : "Erro interno no servidor.";
        }

        writeJson(response, status, message, traceId);
    }

    private void writeJson(HttpServletResponse response, int status, String message, String traceId) throws IOException {
        response.reset();
        response.setStatus(status);
        response.setContentType("application/json; charset=UTF-8");
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, proxy-revalidate");

        Map<String, Object> body = Map.of(
                "statusCode", status,
                "message", message,
                "traceId", traceId,
                "timestamp", Instant.now().toString()
        );

        String payload = serialize(body);
        response.getWriter().write(payload);
        logger.info("Resposta de erro enviada (Status: {}, TraceId: {})", status, traceId);
    }

    private String serialize(Object obj) throws JsonProcessingException {
        return mapper.writeValueAsString(obj);
    }

    private String getOrCreateTraceId(HttpServletRequest request) {
        String existing = request.getHeader("X-Trace-Id");
        return existing != null && !existing.isBlank() ? existing : UUID.randomUUID().toString();
    }
}
