# BRAVA Tools
A backend application and API which provides tools to validate BrAPI compatible servers

## Prerequisites
* Java 21
* Gradle 9.2.1

## Tools
### The Plant Breeding API Validator (BRAVA)
Validates endpoints in a BrAPI compatible server using the [BrAPI spec](https://app.swaggerhub.com/apis-docs/PlantBreedingAPI/BrAPI-Core/2.1).

The validation process verifies data is stored and that each endpoint produces the data expected via the spec.

### Use Case Checker
Validates endpoints have been implemented pursuant to specific use cases required by certain BrApps, like [Field Book](https://github.com/PhenoApps/Field-Book)

## Build and Run

### Locally hosted
To build and run the app locally:

`./gradlew bootRun`

To create a jar to package with a DockerFile:

`./gradlew bootJar` 

### Docker

For development, you will likely want to test a locally built image.

To build the image:

`docker build -t brava-tools-be .`

You will also want to build the frontend image if you want to test both end to end.

You can do that using the [BRAVA-Tools-Website](https://github.com/plantbreeding/BRAVA-Tools-Website),

and building that image locally by cd-ing into that directory and running:

`docker build -t brava-tools-fe .`

Optionally you can grab the latest image of the fe from `brapicoordinatorselby/brava-tools-fe:latest` in the docker compose.

Finally, you can run the images with:

`docker compose -f docker-compose-local.yml up`

The regular docker-compose.yml is reserved for production deployments, and will always use the latest images

## Releases
Releases will trigger an action to build and upload an image to Docker Hub

For more information please refer to the [BRAVA-Tools wiki](https://github.com/plantbreeding/BRAVA-Tools/wiki)
