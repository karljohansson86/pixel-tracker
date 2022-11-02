# Pixel-tracker

Pixel-tracker used to help track visitors activity on a website

## Prerequisites

- [Docker](https://docs.docker.com/engine/install/)
- [Docker-Compose](https://docs.docker.com/compose/install/)

## Dependency

- Postgres docker container

# How to run tests:
- `mvn clean test`
- `mvn clean integration-test`

# How to start application

- Build using `mvn clean install`
- Start postgres db using `docker-compose up -d`
- Start application `mvn spring-boot:run`

# Use case

Two html pages are provided to register page visits

- http://localhost:8080/contact.html
- http://localhost:8080/about.html

# Create and display report

### CommandLine
Generate report from command line by setting property `report.generate=true`. 
This will generate a report for page visits last 24hrs
- `mvn spring-boot:run -Dspring-boot.run.arguments="--report.generate=true"`

### Endpoints
Two endpoints are exposed to create reports for a given time range

- http://localhost:8080/v1/page-visit/report/last-day
- http://localhost:8080/v1/page-visit/report/?start={start}&end={end}

Example format of timestamp: start = 2022-10-12T13:22:13.439Z, end = 2022-10-14T13:22:13.439Z




