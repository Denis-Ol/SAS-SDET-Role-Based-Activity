package com.denisol.clients;

import com.denisol.models.User;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class UsersApiClient {

    public Response createUser(User user) {
        Response response = given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post("/users");
        return logAndExtractResponse(response);
    }

    public Response getAllUsers() {
        Response response = given()
                .when()
                .get("/users");
        return logAndExtractResponse(response);
    }

    public Response getUser(int userId) {
        Response response = given()
                .pathParam("id", userId)
                .when()
                .get("/users/{id}");
        return logAndExtractResponse(response);
    }

    public Response updateUser(int userId, User user) {
        Response response = given()
                .pathParam("id", userId)
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .put("/users/{id}");
        return logAndExtractResponse(response);
    }

    public Response deleteUser(int userId) {
        Response response = given()
                .pathParam("id", userId)
                .when()
                .delete("/users/{id}");
        return logAndExtractResponse(response);
    }

    private Response logAndExtractResponse(Response response) {
        return response.then()
                .log().ifError()
                .extract().response();
    }
}