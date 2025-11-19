FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src


RUN mvn clean package -DskipTests || { echo "Build failed"; exit 1; }

FROM eclipse-temurin:21-jdk AS runner

WORKDIR /app

COPY --from=builder /app/target/MqttRestApp-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
