package com.denisol.tests;

import org.junit.jupiter.api.Test;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class UsersApiSchemaTest extends SchemaBaseTest {

    @Test
    public void getAllUsersSchemaValidationTest() {
        usersApiClient.getAllUsers()
                .then()
                .statusCode(200)
                .and()
                .body(matchesJsonSchemaInClasspath("users-schema.json"));
    }
}