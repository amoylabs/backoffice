# Back Office
The Back Office system built by general requirement

## Prerequisites
- [Docker](https://docs.docker.com/get-docker)
- [Adopt Open JDK 15](https://adoptopenjdk.net/index.html?variant=openjdk15&jvmVariant=hotspot)


#### IntelliJ Idea (Optional)
It is highly-recommended to install the [IntelliJ Idea IDE](https://www.jetbrains.com/idea) for development.


## Set up
_____

### Docker Setup
There are many services containerized using [Docker](https://docs.docker.com).
For local development, it is recommended to set up required infrastructure using [Docker Compose](https://docs.docker.com/compose).

### Quick Start
1. Run CMD `docker-compose up` or `docker-compose start` to get docker services ready
2. Run `WebApplication.java` within IDE to start up the system

### Development Environment
This repo contains a Docker Compose file that will help you setup most of the backing services used with minimal effort.

Services include:
- MySQL <http://localhost:3306>
- MongoDB <http://localhost:27017>
- Redis <http://localhost:6379>
