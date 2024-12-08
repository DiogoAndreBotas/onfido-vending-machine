# Vending Machine

This vending machine is a public REST API that exposes several endpoints, which can be called in order to interact with the machine.

## Running the Machine Locally

### Docker

- Install Docker Desktop
- Clone the repository
- Run `docker-compose up web_api` in the terminal, inside the repository's directory
- The REST API should now be accessible in http://localhost:8080

### IntelliJ IDEA

- Install IntelliJ IDEA and PostgreSQL
- Clone the repository
- Open the folder using IntelliJ IDEA
- Run [VendingMachineApplication.kt](/src/main/kotlin/diogoandrebotas/onfido/vendingmachine/VendingMachineApplication.kt)
- The REST API should now be accessible in http://localhost:8080

## Technologies Used

- **[Kotlin](https://kotlinlang.org/) + [Spring Boot](https://spring.io/projects/spring-boot)** for developing the REST API and the CLI app. Kotlin is the programming language I enjoy coding on the most, and Spring Boot is a framework for developing applications that is both developer friendly and highly customizable. In terms of out of the box support for concurrent access in the REST API, by default, Spring provides an embedded Apache Tomcat build (a web server and servlet container) that has a default thread count of 200.
- **[PostgreSQL](https://www.postgresql.org/)** as the DBMS. I chose a relational database given that this is a system whose data is structured, and PostgreSQL is well-supported in Spring Boot (using Spring JPA), and ACID-compliant.
- **[JUnit](https://junit.org/junit5/)** as the testing framework.
- **[Mockito](https://site.mockito.org/)** as the mocking framework.

## Requirements

The requirements for the vending machine's features are the following:
- Once an item is selected and the appropriate amount of money is inserted, the vending machine should return the correct product.
- It should also return change if too much money is provided, or ask for more money if insufficient funds have been inserted.
- The machine should take an initial load of products and change. The change will be of denominations 1p, 2p, 5p, 10p, 20p, 50p, £1, £2. There should be a way of reloading either products or change at a later point.
- The machine should keep the state of products and change that it contains up to date.

In order to meet the requirements, a REST API was developed with the following endpoints:
- `GET /products`: retrieve all available products
- `GET /products/{id}`: retrieve a product identified by ID
- `POST /products/{id}/purchase`: returns the purchased product and the change in coins
- `POST /products/reset`: restore the available quantity for all products
- `GET /change`: retrieve the coins and their amount
- `POST /change/reset`: restore the amount of coins to their initial values

## Testing

### Automated Testing

Unit and integration tests run everytime a commit is pushed to a branch. In this case, a failure does not block the merge, but in a scenario where code is only merged to `main` using Pull Requests, a failure in the CI pipeline would block merging the PR into `main`.

### Manual Testing

A Postman collection is available [here](Vending%20Machine.postman_collection.json). You can import it and test the API manually.

## Bonuses

TBD

### Architecture Diagram

![Architecture Diagram](/resources/architecture_diagram.drawio.png)

## Big O Complexity of Algorithms

The most complex business logic present in the project is the calculation of the change after a product is purchased.

In relation to the other operations, their time and space complexity are the following:
- `GET /products`: **O(n)** time and space complexity, n being the number of products
- `GET /products/{id}`: **O(1)** space complexity, **O(logn)** time complexity, as an index will be used for searching the product. In PostgreSQL, an index for the primary key column is automatically created.
- `POST /products/{id}/purchase`: **O(n)** time and space complexity, n being the number of unique coins
- `POST /products/reset`: **O(n)** time and space complexity, n being the number of products
- `GET /change`: **O(n)** time and space complexity, n being the number of coins
- `POST /change/reset`:  **O(n)** time and space complexity, n being the number of coins

## Future Work

- Improve Big O query times by adding a caching mechanism
- Hosting the services on a cloud provider
- Logging and Metrics (e.g. NewRelic, Datadog)
- Error Reporting (e.g. Bugsnag)
- Building a frontend using React + TypeScript
