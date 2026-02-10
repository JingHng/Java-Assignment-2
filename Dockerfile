# Stage 1: Build the WAR file using Maven
FROM maven:3.9.5-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Deploy the WAR file to TomEE
FROM tomee:9.1.3-plus

# Remove default ROOT application
RUN rm -rf /usr/local/tomee/webapps/ROOT

# Copy the WAR file from the build stage
COPY --from=build /app/target/Assignment2_J2EE.war /usr/local/tomee/webapps/ROOT.war

# Configure TomEE to listen on all interfaces (0.0.0.0)
RUN sed -i 's/Connector port="8080"/Connector address="0.0.0.0" port="8080"/' /usr/local/tomee/conf/server.xml

# Expose port 8080
EXPOSE 8080

# TomEE will auto-deploy the WAR file
CMD ["catalina.sh", "run"]
