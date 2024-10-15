package com.example.teamcity.api.spec;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;

public class ResponseSpecifications {

    // Спецификация для проверки status code 403 forbidden
    public static ResponseSpecification forbiddenRequestSpec(String expectedMessage) {
        return new ResponseSpecBuilder()
                .expectStatusCode(HttpStatus.SC_FORBIDDEN)
                .expectBody(Matchers.containsString(expectedMessage))
                .build();
    }

    // Спецификация для проверки status code 400 bad request
    public static ResponseSpecification badRequestSpec(String expectedMessage) {
        return new ResponseSpecBuilder()
                .expectStatusCode(HttpStatus.SC_BAD_REQUEST)
                .expectBody(Matchers.containsString(expectedMessage))
                .build();
    }

}
