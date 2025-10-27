package com.denisol.tests;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public abstract class MockBaseTest extends BaseTest {

    public static WireMockServer wireMockServer;

    @BeforeAll
    public static void setup() {
        wireMockServer = new WireMockServer(8080);
        wireMockServer.start();

        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
    }

    @AfterAll
    public static void teardown() {
        wireMockServer.stop();
    }

    @BeforeEach
    public void resetMocks() {
        wireMockServer.resetAll();
    }
}