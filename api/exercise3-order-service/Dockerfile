# Etapa 1: build com Maven (tag corrigido para “-jdk-17-slim”)
FROM maven:3.8.8-jdk-17-slim AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Etapa 2: runtime leve com OpenJDK (tag corrigido para “17-slim”)
FROM openjdk:17-slim

WORKDIR /app

COPY --from=build /app/target/*.jar order-service.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "order-service.jar"]
