# SafetyNet Alerts - README

## Project Description

SafetyNet Alerts is a Spring Boot-based application designed for emergency management. It provides several endpoints that allow users to interact with data about fire stations, medical records, and individuals in a community. The application also integrates unit testing, code coverage, and logging functionalities to ensure high quality and maintainability.

## Documentation

- [Jacoco Documentation](https://GuillBuj.github.io/SafetyNetAlerts/jacoco/)
- [Surefire Documentation](https://GuillBuj.github.io/SafetyNetAlerts/surefire/)
- [Spring Boot Documentation](https://GuillBuj.github.io/SafetyNetAlerts/apidocs/)


## Technology Stack

- **Java 21**
- **Spring Boot**
- **Maven** 
- **JUnit** 
- **Jacoco**
- **Surefire** 
- **Spring Web** 
- **Jackson** 
- **Mockito** 
- **Lombok**

## Prerequisites

- Java 21 or higher
- Maven

## Setup Instructions

1. **Clone the Repository**  
   Clone the repository to your local machine:
   ```bash
   git clone <repository_url>
   ```

2. **Install Dependencies**  
   Navigate to the project folder and install the required dependencies:
   ```bash
   cd <project_folder>
   mvn install
   ```

3. **Run the Application**  
   To run the application locally:
   ```bash
   mvn spring-boot:run
   ```

   The application will be accessible at [http://localhost:8080](/endpoint).

## Endpoints

Here are the available endpoints:

- **GET /firestation?stationNumber=<station_number>**  
  Returns a list of people covered by the fire station. Includes the names, addresses, phone numbers, and the number of adults and children.

- **GET /childAlert?address=<address>**  
  Returns a list of children living at a specified address, including their age and household members.

- **GET /phoneAlert?firestation=<firestation_number>**  
  Returns a list of phone numbers of residents served by a fire station.

- **GET /fire?address=<address>**  
  Returns a list of people living at the specified address, along with their medical history and fire station number.

- **GET /flood/stations?stations=<station_numbers>**  
  Returns a list of homes served by multiple fire stations, grouped by address.

- **GET /personInfolastName=<lastName>**  
  Returns the details (name, address, age, email, medical history) of all people with the given last name.

- **GET /communityEmail?city=<city>**  
  Returns the email addresses of all residents in the specified city.

## Development Guidelines

- **Architecture**: MVC
- **SOLID Principles**: The code adheres to SOLID principles ensuring maintainability and scalability.
  
## Testing and Code Coverage

- **Unit Tests**: Unit tests are implemented using JUnit.
- **Code Coverage**: Jacoco is used to measure code coverage. The build will include a report to ensure 80%+ coverage.
- **Test Reports**: Maven Surefire plugin generates detailed reports for the unit tests.