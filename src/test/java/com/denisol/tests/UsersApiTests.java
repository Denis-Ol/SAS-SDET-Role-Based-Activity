package com.denisol.tests;

import com.denisol.models.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UsersApiTests extends MockBaseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void e2eUserLifecycleTestWithMockServer() throws JsonProcessingException {

        int testUserId = 123;
        User newUser = new User("test@example.com", "testuser", "pass123");

        User createdUser = new User("test@example.com", "testuser", "pass123");
        createdUser.setId(testUserId);

        User updatedUser = new User("updated@example.com", "testuser", "pass123");
        updatedUser.setId(testUserId);

        String createdUserJson = objectMapper.writeValueAsString(createdUser);
        String updatedUserJson = objectMapper.writeValueAsString(updatedUser);

        setupMockStubs(testUserId, createdUserJson, updatedUserJson);

        // POST (Create user)
        Response createResponse = usersApiClient.createUser(newUser);
        createResponse.then()
                .statusCode(201);

        User extractedUser = createResponse.as(User.class);

        assertAll(
                () -> assertEquals(testUserId, extractedUser.getId()),
                () -> assertEquals(newUser.getUsername(), extractedUser.getUsername())
        );

        // GET (Verify Creation)
        usersApiClient.getUser(testUserId)
                .then()
                .statusCode(200);

        // PUT (Update)
        newUser.setEmail("updated@example.com");
        Response updateResponse = usersApiClient.updateUser(testUserId, newUser);
        updateResponse.then()
                .statusCode(200);
        User extractedUpdatedUser = updateResponse.as(User.class);

        assertEquals("updated@example.com", extractedUpdatedUser.getEmail());

        // GET (Verify Update)
        usersApiClient.getUser(testUserId)
                .then()
                .statusCode(200);

        // DELETE
        usersApiClient.deleteUser(testUserId)
                .then()
                .statusCode(200);

        // GET (Verify Deletion)
        usersApiClient.getUser(testUserId)
                .then()
                .statusCode(404);
    }

    private void setupMockStubs(int testUserId, String createdUserJson, String updatedUserJson) {
        String scenarioName = "User C.R.U.D. Lifecycle";
        String stateUserCreated = "User has been created";
        String stateUserUpdated = "User has been updated";
        String stateUserDeleted = "User has been deleted";

        // Step 1: POST /users
        stubFor(post(urlEqualTo("/users"))
                .inScenario(scenarioName)
                .whenScenarioStateIs(STARTED)
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody(createdUserJson))
                .willSetStateTo(stateUserCreated));

        // Step 2: GET /users/123 (After Creation)
        stubFor(get(urlEqualTo("/users/" + testUserId))
                .inScenario(scenarioName)
                .whenScenarioStateIs(stateUserCreated)
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(createdUserJson)));

        // Step 3: PUT /users/123
        stubFor(put(urlEqualTo("/users/" + testUserId))
                .inScenario(scenarioName)
                .whenScenarioStateIs(stateUserCreated)
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(updatedUserJson))
                .willSetStateTo(stateUserUpdated));

        // Step 4: GET /users/123 (After Update)
        stubFor(get(urlEqualTo("/users/" + testUserId))
                .inScenario(scenarioName)
                .whenScenarioStateIs(stateUserUpdated)
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(updatedUserJson)));

        // Step 5: DELETE /users/123
        stubFor(delete(urlEqualTo("/users/" + testUserId))
                .inScenario(scenarioName)
                .whenScenarioStateIs(stateUserUpdated)
                .willReturn(aResponse().withStatus(200))
                .willSetStateTo(stateUserDeleted));

        // Step 6: GET /users/123 (After Delete)
        stubFor(get(urlEqualTo("/users/" + testUserId))
                .inScenario(scenarioName)
                .whenScenarioStateIs(stateUserDeleted)
                .willReturn(aResponse().withStatus(404)));
    }
}