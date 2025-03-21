# Use an OpenJDK image to build and run the app
FROM openjdk:23-jdk-slim AS build

# Set working directory inside the container
WORKDIR /app

# Copy Gradle build files (or Maven, if you're using Maven)
COPY build.gradle.kts settings.gradle.kts gradlew /app/

# Optionally, copy Gradle wrapper if you have one
COPY gradle /app/gradle

# Copy the source files (src/ folder)
COPY src /app/src

# Download dependencies and build the application (assuming Gradle here)
RUN chmod +x ./gradlew
RUN ./gradlew clean shadowJar --no-daemon -x test

# Use OpenJDK again for runtime, so we can keep the image slim
FROM openjdk:23-jdk-slim

# Set working directory inside the container
WORKDIR /app

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