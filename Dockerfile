# Use a base image with Java
FROM adoptopenjdk:17-jdk-hotspot

# Set the working directory
WORKDIR /app

# Copy the JAR file into the container
COPY build/libs/crypto-data-station.jar /app/app.jar

# Run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]
