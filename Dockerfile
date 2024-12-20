# Use a Java runtime as the base image
FROM openjdk:11-jdk-slim

# Download and install GlassFish
RUN apt-get update && apt-get install -y wget unzip && \
    wget https://download.eclipse.org/ee4j/glassfish/glassfish-7.0.2.zip && \
    unzip glassfish-7.0.2.zip -d /opt && \
    rm glassfish-7.0.2.zip

# Copy the war file to the container
COPY target/Data.war /opt/glassfish7/glassfish/domains/domain1/autodeploy/
EXPOSE 8080
# Start GlassFish when the container starts
CMD ["/opt/glassfish7/glassfish/bin/asadmin", "start-domain", "--verbose", "domain1"]