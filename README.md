# Spring (Reactive) Recipe Web App

## Overview
The Spring (Reactive) Recipe Web App is a modern, scalable, and high-performance web application designed to provide users with a comprehensive collection of recipes. Leveraging the reactive programming model provided by Spring WebFlux, this application ensures non-blocking operations, making it highly responsive and capable of handling a large number of concurrent users efficiently. The application allows users to browse, search, and manage recipes, offering a seamless and interactive user experience.

## Technology Stack
- **Thymeleaf:** Template engine for rendering views
- **Backend:** Spring Boot, Spring WebFlux
- **Database:** MongoDB (NoSQL)
- **Build Tool:** Gradle
- **Version Control:** Git
- **Testing Frameworks:** JUnit, Mockito, Reactor Test

## Architecture
The system follows a layered architecture with the following components:
- **Controller Layer:** Handles HTTP requests and maps them to service methods.
- **Service Layer:** Contains the business logic.
- **Repository Layer:** Manages data access using Spring Data JPA.

## Key Features
- **Spring WebFlux:** Enables reactive programming and non-blocking I/O operations.
- **Reactive Repositories:** MongoDB repositories are used in a reactive manner, ensuring scalability and high performance.
- **Recipe Management:** Create, read, update, and delete recipes.
- **User Authentication:** Secure login and registration using JWT (JSON Web Token).
- **Search and Filter:** Advanced search and filtering capabilities to find recipes based on ingredients, cuisine, and other criteria.
- **Project Lombok:** provides annotations to generate getters, setters, constructors, toString, equals, hashCode, and other commonly used methods, reducing the need for boilerplate code.
- **Thymeleaf**: Template engine used to render dynamic content in web pages.
Bootstrap: Used for styling the web application to make it responsive and visually appealing.
- **Validation**: Ensures that the data provided by users is valid before processing.
- Exception Handling: Provides user-friendly error messages and handles unexpected errors gracefully.
- **Logging**: Implements logging using SLF4J and Logback for debugging and monitoring purposes.
- **Testing**: Includes unit tests and integration tests to ensure the application works as expected.

## Testing
### Unit Testing:

- **JUnit:** For writing and running unit tests.
- **Mockito:** For mocking dependencies and verifying interactions.
- **Reactor Test:** For testing reactive streams and verifying their behavior.

### Integration Testing:
- Testing the integration between different components such as the Recipe Service and the MongoDB database.
- Ensuring that the services interact correctly and data flows as expected.
