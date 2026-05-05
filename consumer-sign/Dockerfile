# Estágio de build 
FROM maven:3.8-eclipse-temurin-11 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package

# Estágio de execução
FROM eclipse-temurin:11-jre
WORKDIR /app
COPY --from=build /app/target/app-1.0-SNAPSHOT-jar-with-dependencies.jar /app/app.jar

CMD ["java", "-cp", "app.jar", "com.sistema.SignConsumer"]