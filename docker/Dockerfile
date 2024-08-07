# Use the official OpenJDK image as a base image
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the JAR file from the target directory of the build context
COPY target/KafkaTemplate-1.0-SNAPSHOT.jar /app/KafkaTemplate.jar

# Expose the port on which the app will run
EXPOSE 8080

# Set the entrypoint to run the JAR file
ENTRYPOINT ["java", "-jar", "KafkaTemplate.jar"]
