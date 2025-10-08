package finflow.Finflow_API.Extensions;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class SecurityConfig {

    @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                AuthenticationEntryPoint entryPoint = this::commenceUnauthorized;
                AccessDeniedHandler deniedHandler = this::handleDenied;

                http
                        .authorizeHttpRequests(auth -> auth
                                .requestMatchers(
                                                "/",
                                                "/index.html",
                                                "/v3/api-docs/**",
                                                "/swagger-ui/**",
                                                "/swagger-ui.html",
                                                "/swagger-resources/**",
                                                "/webjars/**",
                                                "/api/**",
                                                "/static/**",
                                                "/css/**",
                                                "/js/**",
                                                "/images/**"
                                ).permitAll()
                                .anyRequest().authenticated()
                        )
                        .csrf(csrf -> csrf.disable())
                        .httpBasic(Customizer.withDefaults())
                        .exceptionHandling(ex -> ex
                                .authenticationEntryPoint(entryPoint)
                                .accessDeniedHandler(deniedHandler)
                        );

                return http.build();
        }

        private void commenceUnauthorized(HttpServletRequest request, HttpServletResponse response, org.springframework.security.core.AuthenticationException authException) throws IOException {
                writeJson(response, 401, "Credenciais inválidas.");
        }

        private void handleDenied(HttpServletRequest request, HttpServletResponse response, org.springframework.security.access.AccessDeniedException accessDeniedException) throws IOException {
                writeJson(response, 403, "Acesso proibido.");
        }

        private void writeJson(HttpServletResponse response, int status, String message) throws IOException {
                if (response.isCommitted()) return;
                response.reset();
                response.setStatus(status);
                response.setContentType("application/json; charset=UTF-8");
                var payload = new ObjectMapper().writeValueAsString(Map.of(
                                "statusCode", status,
                                "message", message
                ));
                response.getWriter().write(payload);
        }
}
