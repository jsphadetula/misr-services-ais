FROM openjdk:17-jdk-slim

EXPOSE 8080

# Set OS time zone
ENV TZ Africa/Lagos

# Setup
RUN mkdir -p /opt/ais

WORKDIR /opt/ais

# Bundle config source
COPY ./ /opt/ais

ADD ./target/ais-1.0.0.jar ./ais.jar

ENTRYPOINT ["java", "-jar", "ais.jar"]
