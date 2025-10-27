package com.denisol.tests;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;

public abstract class SchemaBaseTest extends BaseTest {

    @BeforeAll
    public static void setup() {

        RestAssured.baseURI = "https://fakestoreapi.com";
    }
}