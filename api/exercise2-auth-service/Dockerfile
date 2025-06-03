# -----------------------------------------------------
# 1) ETAPA DE BUILD: usa Maven para gerar o JAR
# -----------------------------------------------------
FROM maven:3.8.8-jdk-17-slim AS build
WORKDIR /app

# Copia o pom.xml e o código-fonte
COPY pom.xml .
COPY src ./src

# Compila e gera o jar, pulando testes
RUN mvn clean package -DskipTests

# -----------------------------------------------------
# 2) ETAPA DE RUNTIME: imagem leve de OpenJDK
# -----------------------------------------------------
FROM openjdk:17-slim
WORKDIR /app

# Copia o JAR da etapa de build
COPY --from=build /app/target/*.jar app.jar

# (confirme que seu application.properties usa essa mesma porta)
EXPOSE 8001

ENTRYPOINT ["java", "-jar", "app.jar"]
