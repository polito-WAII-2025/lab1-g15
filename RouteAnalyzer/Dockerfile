#################
## BUILD STAGE ##
#################
FROM openjdk:23-jdk-slim AS build

# Set working directory inside the container
WORKDIR /app

# Copy Gradle build files (or Maven, if you're using Maven)
COPY build.gradle.kts settings.gradle.kts gradlew /app/

# Optionally, copy Gradle wrapper if you have one
COPY gradle /app/gradle

# Copy the source files (src/ folder)
COPY src /app/src

# Convert line endings and give execution permissions
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew

# Build the application (skip tests)
RUN ./gradlew clean shadowJar --no-daemon -x test

###################
## RUNTIME STAGE ##
###################
FROM openjdk:23-jdk-slim

# Set working directory inside the container
WORKDIR /app

# Run as non-root user
RUN adduser --disabled-password --gecos "" appuser

# Create the resources directory and set correct permissions
RUN mkdir -p /app/resources && chown -R appuser:appuser /app

# Switch to the non-root user
USER appuser

# Create the resources directory
RUN mkdir -p resources

# Copy the built JAR file from the build stage to the container
COPY --from=build /app/build/libs/RouteAnalyzer-1.0-all.jar app.jar

# Set runtime environment variables
ENV CONTAINER=true
ENV CONFIG_FILE="custom-parameters.yml"
ENV WAYPOINTS_FILE="waypoints.csv"

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]