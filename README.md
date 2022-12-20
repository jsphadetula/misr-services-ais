# Banque Misr Automatic Irrigation System
Banque Misr Automatic Irrigation System (AIS) project helps to configure plots of land to schedule automatic irrigation. Postgres database is used to store data and redis to schedule irrigation on expiry.

### Prerequisites

To build the app locally, You need to have docker and maven installed on your machine.
You can find instructions on how to install it here https://docs.docker.com/install/
You can find instructions on how to install maven here https://maven.apache.org/install.html

## Getting Started

Once you have the project on your local system

1. Navigate to the folder where this readme file is, through any command line interface
2. Run mvn clean package
3. Run "docker-compose up" without the quotes
4. The command brings up the service and dependencies required for this project to run within a docker container

The service can be accessed with the details below:

1. Spring boot backend: http://localhost:8090

## API Documentation

4 endpoints have been provided. Below is a brief description of the endpoints;

1. HTTP POST http://localhost:8090/ais/api/v1/lands
   This endpoint add new plot of land. It takes name and geometry points to determine location and area.
2. HTTP PUT http://localhost:8090/ais/api/v1/lands/{id}
   This endpoint updates existing plot of land data.
3. HTTP GET http://localhost:8090/ais/api/v1/lands
   This endpoint lists all added plots of land.
4. HTTP POST http://localhost:8090/ais/api/v1/lands/{id}/configurations
   This endpoint adds or update an existing irrigation configuration.

