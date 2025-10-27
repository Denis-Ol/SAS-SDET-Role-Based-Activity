# Test Plan: "Users" REST API 

## 1. Introduction & Objective

This document outlines the testing strategy for the new REST API designed to manage system users. The objective of this 
test plan is to verify that all API endpoints function as expected, handle errors gracefully, and adhere to the defined
data schema.

This plan covers functional, negative, and integration testing for the five specified API endpoints. All testing will be
based on the API documentation provided at `https://fakestoreapi.com/docs#tag/Users`.
Where the provided documentation is incomplete or contradictory, this test plan assumes adherence to common RESTful API 
standards and best practices.

---

## 2. Scope

### In-Scope

* **Functional Testing** User Resource CRUD Operations:
    * `POST /users` (Add a new user)
    * `GET /users` (Get all users)
    * `GET /users/{id}` (Get a single user)
    * `PUT /users/{id}` (Update a user)
    * `DELETE /users/{id}` (Delete a user)
* **Validation** of HTTP status codes for all requests (e.g., 200, 201, 400, 404).
* **Schema Validation** to ensure JSON request and response bodies match the contract.
* **Negative Testing**, including invalid inputs, missing required fields, and requests for non-existent resources.
* **Integration Testing** to verify the C.R.U.D. flow (e.g., create a user, update it, then delete it).

### Out-of-Scope

* **Performance Testing** 
* **Security Testing** 
* **UI/Frontend Testing** 
* **Network-level testing**

---

## 3. Test Strategy & Approach

### Test Approach

1. **Manual Exploration:** Use an API client like Postman to manually interact with the API, understand its behavior,
   and validate the contract (or mock).
2. **Automation:** Develop automated tests using a chosen framework (Java/REST Assured).
3. **End-to-End Flow:** Prioritize an automated test that covers the full lifecycle of a user (Create -> Read ->
   Update -> Delete) to ensure data integrity and state management.

### Tools

* **Automation Framework:** Java + REST Assured + JUnit 5.
* **API Client:** Postman (for manual validation and test design).


---

## 4. Test Scenarios

Below are the high-level test scenarios grouped by endpoint.

### Endpoint: `POST /users` (Add a new user)

*Based on the schema requiring `username`, `email`, and `password`.*

| Test ID    | Scenario                                                                                                                             | Expected Result                                                                                                                 |
|------------|--------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------|
| **TC-01**  | **(Positive)** Add a new user with all valid and required fields (`username`, `email`, and `password`).                              | **Status:** 201 Created.  <br/>**Body:** Response contains the user object that was sent.                                       |
| **TC-02**  | **(Negative)** Attempt to add a user with a missing required field (e.g., send the request without the `username` field).            | **Status:** 400 Bad Request. <br/> **Body:** Contains a clear error message (e.g., "`username` is required").                   |
| **TC-03**  | **(Negative)** Attempt to add a user with invalid data types (e.g., send `id` as a string like `"123"` instead of an integer `123`). | **Status:** 400 Bad Request. <br/> **Body:** Contains a data validation error message.                                          |
| **TC-03b** | **(Negative)** Attempt to add a user with an `id` that already exists.                                                               | **Status:** 400 Bad Request (or 409 Conflict). <br/> **Body:** Contains a clear error message (e.g., "User ID already exists"). |
| **TC-03c** | **(Negative)** Attempt to add a user with an invalid `email` format (e.g., "not-an-email.com").                                      | **Status:** 400 Bad Request. <br/> **Body:** Contains a data validation error message.                                          |

### Endpoint: `GET /users` (Get all users)

| Test ID   | Scenario                                                                   | Expected Result                                                                                         |
|-----------|----------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------|
| **TC-04** | **(Positive)** Request the list of all users.                              | **Status:** 200 OK. <br/> **Body:** A JSON array of user objects.                                       |
| **TC-05** | **(Positive)** Verify the schema of at least one user object in the array. | **Status:** 200 OK. <br/> **Body:** The user object contains all expected fields (id, username, email). |
| **TC-06** | **(Integration)** Add a new user (TC-01) and then get all users.           | **Status:** 200 OK. <br/> **Body:** The newly created user is present in the returned array.            |

### Endpoint: `GET /users/{id}` (Get a single user)
* Based on the required path parameter 'id': 'integer'

| Test ID   | Scenario                                                                 | Expected Result                                                          |
|-----------|--------------------------------------------------------------------------|--------------------------------------------------------------------------|
| **TC-07** | **(Positive)** Request an existing user by their valid `id`.             | **Status:** 200 OK. <br/> **Body:** The correct user object is returned. |
| **TC-08** | **(Negative)** Request a user with a non-existent `id` (e.g., 9999).     | **Status:** 404 Not Found.                                               |
| **TC-09** | **(Negative)** Request a user with an invalid `id` format (e.g., "abc"). | **Status:** 400 Bad Request.                                             |

### Endpoint: `PUT /users/{id}` (Update a user)
* Based on the required path parameter 'id': 'integer'

| Test ID   | Scenario                                                                     | Expected Result                                                                                  |
|-----------|------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------|
| **TC-10** | **(Positive)** Update an existing user's data (f.e. change  `email`).        | **Status:** 200 OK. <br/> **Body:** Response contains the *fully updated* user object.           |
| **TC-11** | **(Integration)** Verify the update from TC-10 by calling `GET /users/{id}`. | **Status:** 200 OK. <br/> **Body:** The response from the `GET` call shows the persisted change. |
| **TC-12** | **(Negative)** Attempt to update a user with a non-existent `id`.            | **Status:** 404 Not Found.                                                                       |

### Endpoint: `DELETE /users/{id}` (Delete a user)
* Based on the required path parameter 'id': 'integer'

| Test ID   | Scenario                                                                             | Expected Result                                                                                 |
|-----------|--------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------|
| **TC-13** | **(Positive)** Delete an existing user by their valid `id`.                          | **Status:** 200 OK. <br/> **Body:** Response body is empty or contains the deleted user object. |
| **TC-14** | **(Negative)** Attempt to delete a user with a non-existent `id`.                    | **Status:** 404 Not Found.                                                                      |
| **TC-15** | **(Integration)** Verify deletion by calling `GET /users/{id}` for the deleted user. | **Status:** 404 Not Found.                                                                      |

---

## 5. Automation Candidate

For the automation sample, two candidates are proposed, depending on the state and persistence of the test environment.

### **Primary Choice:** `E2E_User_Lifecycle` (Assumes Persistent API)

A full end-to-end integration test is the best choice to demonstrate full confidence in the API's C.R.U.D. 
functionality.

* **Objective:** To verify that a user can be successfully created, retrieved, updated, and deleted, ensuring data is
  correctly persisted and removed.
* **Steps:**
    1. **POST `/users`:** Create a new user with a unique username. Assert 201 status. Store the returned `id`.
    2. **Verify (GET `/users/{id}`):** Call `GET` with the new `id`. Assert the status is 200 and the returned user data
       matches the created user.
    3. **PUT `/users/{id}`:** Update the user's `email`.
    4. **Verify (GET `/users/{id}`):** Call `GET` again. Assert the status is 200 and the `email` matches the
       updated value.
    5. **DELETE `/users/{id}`:** Delete the user using their `id`. Assert status is 200.
    6. **Verify (GET `/users/{id}`):** Call `GET` one last time. Assert the status is 404 Not Found.
* **JUnit Implementation:** This would be a single `@Test` method.  All 6 steps (**POST**, **GET**, **PUT**, **GET**, 
**DELETE**, **GET**) would be executed sequentially within that one method. We can use JUnit 5's `assertAll()` at each 
verification step to group multiple assertions together (f.e. checking status code and the email in the response body).
* **Requirement:** This test requires a persistent, stateful API (either the real one or a mock server like WireMock) 
to work, as it depends on the state changes (Create, Delete) being saved.

### **Second Choice:** `Get_All_Users_Schema_Validation` (For Non-Persistent API)

If the API is non-persistent or "stateless", the E2E test above will fail. 
In this scenario, the best alternative is a contract test that focuses on schema validation.
* **Chosen Test Case:** `Get_All_Users_Schema_Validation` (Corresponds to **TC-05**)
* **Objective:** To verify that the `GET /users` endpoint returns a 200 OK response and that the JSON body of that 
response strictly matches the defined data contract (schema).
* **Steps:**
    1. **Define Schema:** Create a formal JSON Schema file (e.g., `users-schema.json`) that defines the expected 
structure, data types, and required fields for the user list (e.g., an array of user objects, where each user has a 
required `username` string, `email` string, etc.).
    2. **GET `/users`:** Make a call to the `GET /users` endpoint.
    3. **Assert Status:** Verify the HTTP status code is 200 OK.
    4. **Validate Schema:** Use an automation library (e.g., REST Assured's `matchesJsonSchemaInClasspath()`) 
to automatically compare the entire response body against the `users-schema.json` file.
* **JUnit Implementation:** This would be a single `@Test` method. The key assertion is the schema validation, 
which checks the entire data structure in one line of code. 
This test is valuable because it's stateless and effectively verifies the API's "contract."
