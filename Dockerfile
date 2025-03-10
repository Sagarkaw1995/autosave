FROM 10.212.113.11:5000/java-21-dev-slim

# Set the working directory for the application
WORKDIR /app

# Copy the built JAR file from the build stage
COPY target/*.jar /app/app.jar

# Expose the port that the application will run on (if applicable)
EXPOSE 9218


# Command to run the JAR file
CMD ["java", "-jar", "app.jar"]
