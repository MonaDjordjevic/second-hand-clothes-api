# Info

Second-Hand Clothes Marketplace
This project is a REST API for a second-hand clothes marketplace, built using Spring Boot, MySQL, and Docker. 
The application allows users to publish and manage their second-hand clothes, and authenticate using JWT.

# Prerequisites

Before running the application, make sure you have the following installed:

1. Docker: For containerizing the application and running the database.
2. Maven: For building the application.
3. Java (JDK 17): To run the application locally if you're not using Docker.
4. MySQL: A MySQL server running if you're not using Docker for the database.

# Setup

1. Clone the repository:
   - ```git clone https://github.com/your-username/second-hand-clothes-api.git```
   - ```cd second-hand-clothes-api```
2. Build the application with Maven:
   - ```mvn clean install```

# Running the Application

1. First, build the Docker images: 
   - ```docker compose build```
2. Start the application using Docker Compose:
   - ```docker compose up```
- This will start both the db (MySQL) and app (Spring Boot application) services. The MySQL database will be available 
on port 3307, and the application will be available on port 8080. You can access it on http://localhost:8080.

# Running the Application Locally (Without Docker)

To run the application locally without Docker, make sure you have the following prerequisites:

1. MySQL: Ensure that MySQL is running on your local machine
2. Database Configuration: Update your application.yml to reflect the local MySQL database configuration:
```yaml
   spring:
    datasource:
      url: jdbc:mysql://localhost:3306/second_hand_clothes
      username: your_username
      password: your_password
```
Once the database is set up, you can run the application using your IDE (such as IntelliJ or Eclipse) or 
using the command line with Maven:
```mvn spring-boot:run```

The application will start, and you can access it on http://localhost:8080.

# Endpoints

You can view and interact with the available endpoints through the Swagger UI by navigating to:
http://localhost:8080/swagger-ui.html

**Please note**: Some endpoints require authentication via JWT. You cannot interact with these endpoints directly via Swagger UI.
You will need to authenticate first using the /authenticate endpoint to obtain a JWT token, and then use **Postman** 
or **cURL** to make requests to the endpoints that require authentication.

Explanation of the endpoints:
 - **POST /authenticate**: Authenticate and get a JWT token.
   How it works:
   - You use this endpoint to authenticate and obtain a JWT token by providing a valid username and password. 
   - This token will then be used for subsequent requests to protected endpoints that require authentication 
 (`POST /clothes/publish`, `PUT /clothes/update/{id}`, and `DELETE /clothes/unpublish/{id}`).
   - **Important**: This authentication requires the username and password of a user that already exists in the database. 
Users pre-loaded in the database:
     - Username: johndoe, Password: password123
     - Username: janesmith, Password: mypassword
   - You can authenticate using any of these credentials to obtain a valid JWT token.
     - **Request body example**:
          ```json
          {
            "username": "johndoe",
            "password": "password123"
        }
        ```
 - **GET /clothes**: Retrieve garments from the marketplace based on search criteria.
    - This endpoint does not require authentication.
    - This endpoint functions as a search feature for garments in the marketplace.
    - If query parameters (such as type, size, etc.) are provided, it returns garments that match the specified criteria.
    - If no parameters are provided, it returns all garments available in the marketplace.
    - **Pagination**: You can use Pageable to control the page size and page number for the results. 
By default, the page size is 100, but you can adjust this through the query parameters.
    - Example:
       - Request with query parameters:
   ```shell
      curl -X GET "http://localhost:8080/clothes?type=Shirt&size=M&page=0&size=10"
    ```
      - Request without query parameters (returns all garments):
   ```shell
      curl -X GET "http://localhost:8080/clothes?page=0&size=10"
    ```
 - **POST /clothes/publish**: Publish a new garment.
    - Authorization: Requires JWT token for authentication.
    - The user who is authenticated and provides a valid JWT token is considered the publisher of the garment. 
    - Request body example:
   ```json
      {
        "type": "Shirt",
        "description": "A nice red shirt",
        "size": "L",
        "price": 25.0
      }
      ```
    - Example using cURL:
   ```shell
      curl -X POST http://localhost:8080/clothes/publish \
      -H "Authorization: Bearer <your-jwt-token>" \
      -H "Content-Type: application/json" \
      -d '{"type": "Shirt", "description": "A nice red shirt", "size": "L", "price": 25.0}'
    ```
 - **PUT /clothes/update/{id}**: Update an existing garment.
   - Authorization: Requires JWT token for authentication.
   - The id in the URL refers to the garment being updated.
   - The user can only update garments that they have previously published.
When a user attempts to update a garment,
the backend checks if the authenticated user (based on the JWT token) is the publisher of the garment with the corresponding id.
If the user is not the publisher, the request will be rejected.
   - Request body example:
   ```json
      {
        "type": "Shirt",
        "description": "A nice red shirt",
        "size": "L",
        "price": 25.0
      }
      ```
     - Example using cURL:
   ```shell
     curl -X PUT http://localhost:8080/clothes/update/1 \
    -H "Authorization: Bearer <your-jwt-token>" \
    -H "Content-Type: application/json" \
    -d '{"type": "Shirt", "description": "A blue shirt", "size": "M", "price": 20.0}'
    ```   
- **DELETE /clothes/unpublish/{id}**: Unpublish a garment.
  - Authorization: Requires JWT token for authentication.
  - The id in the URL refers to the garment to be unpublished.
  - The user can only unpublish garments that they have previously published.
When a user attempts to unpublish a garment, the backend checks if the authenticated user (based on the JWT token)
is the publisher of the garment with the corresponding id. If the user is not the publisher of that garment, the request will be rejected
  - Example using cURL:
   ```shell
     curl -X DELETE http://localhost:8080/clothes/unpublish/1 \
    -H "Authorization: Bearer <your-jwt-token>" \
    ```   

