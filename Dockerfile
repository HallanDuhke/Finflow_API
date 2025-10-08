# ---- Build stage ----
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
# Copiar mvnw e arquivos de configuração primeiro (melhor cache)
COPY mvnw* pom.xml ./
COPY .mvn .mvn
RUN chmod +x mvnw || true
# Baixar dependências (criando camadas cacheáveis)
RUN ./mvnw -q dependency:go-offline
# Copiar código fonte
COPY src src
# Build sem testes (ajustável via ARG)
ARG SKIP_TESTS=true
RUN if [ "$SKIP_TESTS" = "true" ]; then ./mvnw -q clean package -DskipTests; else ./mvnw -q clean package; fi

# ---- Runtime stage ----
FROM eclipse-temurin:17-jre-alpine AS runtime
WORKDIR /app
# Criar usuário não root
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
ARG APP_NAME=Finflow_API
# Copiar jar gerado
COPY --from=build /app/target/Finflow_API-0.0.1-SNAPSHOT.jar app.jar
# Expor porta
EXPOSE 8080
# Variáveis de ambiente default (podem ser sobrescritas)
ENV JAVA_OPTS="" \
    SPRING_PROFILES_ACTIVE=docker \
    TZ=UTC
# Health (opcional) -> depende do actuator habilitado
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s CMD wget -qO- http://localhost:8080/actuator/health | grep '"status":"UP"' || exit 1
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
