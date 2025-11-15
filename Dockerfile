FROM 10.212.113.11:5000/openjdk-21-debian-13:1.0.0

# Set the working directory for the appliocation
WORKDIR /app

# Copy the built JAR file from the build stage
COPY target/*.jar /app/app.jar

RUN chown -R  app_user:appgroup /app/

USER app_user

# Expose the port that the application will run on (if applicable)

EXPOSE 9128 

# Command to run the JAR file
CMD ["java", "-jar", "app.jar"]
