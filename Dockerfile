# Etapa de construcción
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew build -x test --no-daemon

# Etapa final con JRE optimizado
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Variables de entorno para optimizar la JVM y Spring
ENV SPRING_PROFILES_ACTIVE=prod \
    SPRING_SECURITY_USER_NAME=admin \
    SPRING_SECURITY_USER_PASSWORD=none \
    JAVA_OPTS="-XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=75.0 \
    -XX:InitialRAMPercentage=50.0 \
    -XX:+UseG1GC \
    -XX:+ExitOnOutOfMemoryError \
    -Djava.security.egd=file:/dev/./urandom"

# Crear un usuario no root
RUN addgroup -S spring && adduser -S spring -G spring

# Copiar el jar y los recursos
COPY --from=builder /app/build/libs/*.jar app.jar
COPY --from=builder /app/src/main/resources/application*.yml /app/config/

# Asignar ownership al usuario no root
RUN chown -R spring:spring /app
USER spring

# Puerto expuesto
EXPOSE 8080

# Comando de inicio con configuración explícita
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS \
    -Dspring.config.location=file:/app/config/ \
    -jar app.jar"]