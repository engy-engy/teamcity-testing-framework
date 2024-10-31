package com.example.teamcity.api.requests.unchecked;

import com.example.teamcity.api.enums.Endpoint;
import com.example.teamcity.api.models.BaseModel;
import com.example.teamcity.api.requests.CrudInterface;
import com.example.teamcity.api.requests.Request;
import com.example.teamcity.api.requests.SearchInterface;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 * Класс, отвечающий за выполнение CRUD-операций без валидации успешности HTTP-ответов.
 * Этот класс отвечает только за отправку запросов, бещ проверок запросов
 */
public class UncheckedBase extends Request implements CrudInterface, SearchInterface {
    public UncheckedBase(RequestSpecification spec, Endpoint endpoint) {
        super(spec, endpoint);
    }

    @Override
    public Response create(BaseModel model) {
        return RestAssured
                .given()
                .spec(spec)
                .body(model)
                .post(endpoint.getUrl());
    }

    @Override
    public Response read(String locator) {
        return RestAssured
                .given()
                .spec(spec)
                .get(endpoint.getUrl() + "/" + locator);
    }

    @Override
    public Response update(String locator, BaseModel model) {
        return RestAssured
                .given()
                .spec(spec)
                .body(model)
                .put(endpoint.getUrl() + "/" + locator);
    }

    @Override
    public Response update(String path, String parameters) {

        return RestAssured
                .given()
                .spec(spec)
                .accept("text/plain")
                .contentType("text/plain")
                .body(parameters)
                .put(String.format(endpoint.getUrl() + "/%s", path));
    }

    @Override
    public Response update(String projectLocator, BaseModel model, String parameter) {
        return RestAssured
                .given()
                .spec(spec)
                .body(model)
                .put(String.format(endpoint.getUrl() + "/%s/parameters/%s", projectLocator, parameter));
    }

    @Override
    public Response delete(String locator) {
        return RestAssured
                .given()
                .spec(spec)
                .delete(endpoint.getUrl() + "/" + locator);
    }

    @Override
    public Response search(String query, String value) {
        return RestAssured
                .given()
                .spec(spec)
                .get(endpoint.getUrl()  + "?" + "locator=" + query + ":" + value);
    }
}
