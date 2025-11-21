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

To build and run the app locally:

`./gradlew bootRun`

To create a jar to package with a DockerFile:

`./gradlew bootJar` 

For more information please refer to the [BRAVA-Tools wiki](https://github.com/plantbreeding/BRAVA-Tools/wiki)
