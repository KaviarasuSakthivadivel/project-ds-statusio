# Status.io

StatusIO is an incident tracking tool for the websites. Multiple Users can subscribe to multiple services either they will be notified via email or slack, discord channels or in the service's twitter handle. 

For the Phase I, we have simple server which accepts subcriber details that will be stored in the DB. 

## Technologies used
1. Spring Boot Web Application which runs in port 8080
2. Postgres DB which runs in the port 5432

## Prerequisites
The following items should be installed in your system:
- Git command line tool
- Maven
- Java
- Docker

## Usage
    $ https://github.com/KaviarasuSakthivadivel/project-ds-statusio.git`
    
    $ cd project-ds-statusio

To execute the backend with Docker, first clean and package using maven commands below
    
    $ ./mvnw clean package -DskipTests

After this, use docker-compose to spin up the server.  
    
    $ docker-compose up

Then, visit

    $ http://localhost:8080

## Curl

*To get the list of subscribers*

```curl --location --request GET 'http://localhost:8080/subscriber/'```

*To create a subscriber* 
    
    curl --location --request POST 'http://localhost:8080/subscriber/' \
    --header 'Content-Type: application/json' \
    --data-raw '{
        "firstName": "Kaviarasu",
        "lastName": "Sakthivadivel",
        "email":"kascatchme@gmail.com"
    }'



# Phase 2 Submission

Since we didn't get a real-time API for website monitoring, we have created a project (incident-tracker) module in the repo 
that runs a CRON job in the background and fetches the data for the Phase II. For tracking the website health/ status, we have created CRUD operations for the same. We can add / update / delete websites for health tracking. 




