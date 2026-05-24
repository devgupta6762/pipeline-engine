# --- Stage 1: Compile and Package the Spring Boot Application ---
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy the build manifest and source directories
COPY pom.xml .
COPY src ./src

# Compile the source files and package the executable JAR file while bypassing local database checks
RUN mvn clean package -DskipTests

# --- Stage 2: Minimalist Lightweight Production Runtime Environment ---
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Pull the compiled production artifact safely out of Stage 1
COPY --from=build /app/target/pipeline-engine-0.0.1-SNAPSHOT.jar app.jar

# Expose our Web API communication channel mapping port
EXPOSE 8080

# Kick off our multi-batch execution engine on startup
ENTRYPOINT ["java", "-jar", "app.jar"]