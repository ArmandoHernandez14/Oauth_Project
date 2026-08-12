# Use a Java 21 runtime image
FROM eclipse-temurin:21-jre

# Set the working directory
WORKDIR /app

# Copy the JAR into the image
COPY target/oauth-server-0.0.1-SNAPSHOT.jar app.jar

# Document the port your app uses
EXPOSE 8080

# Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]