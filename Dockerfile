# Use TomEE Plus image (includes all J2EE features)
FROM tomee:9.1.3-plus

# Remove default ROOT application
RUN rm -rf /usr/local/tomee/webapps/ROOT

# Copy your project WAR file
# (You'll export this from Eclipse)
COPY Assignment2_J2EE.war /usr/local/tomee/webapps/ROOT.war

# Expose port 8080
EXPOSE 8080

# TomEE will auto-deploy the WAR file
CMD ["catalina.sh", "run"]
