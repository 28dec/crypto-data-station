# Use a base image with Java
FROM ghcr.io/graalvm/jdk:ol8-java17

# Set the working directory
WORKDIR /app

# Copy the JAR file into the container
COPY build/libs/crypto-data-station.jar /app/app.jar

EXPOSE 8080

# Run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]
