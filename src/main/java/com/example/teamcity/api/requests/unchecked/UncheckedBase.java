package com.example.teamcity.api.requests.unchecked;

import com.example.teamcity.api.enums.Endpoint;
import com.example.teamcity.api.models.BaseModel;
import com.example.teamcity.api.requests.CrudInterface;
import com.example.teamcity.api.requests.Request;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 * Класс, отвечающий за выполнение CRUD-операций без валидации успешности HTTP-ответов.
 * Этот класс отвечает только за отправку запросов, бещ проверок запросов
 */
public class UncheckedBase extends Request implements CrudInterface {
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
    public Response read(String query, String value) {
        return RestAssured
                .given()
                .spec(spec)
                .get(endpoint.getUrl() + "/" + query + ":" + value);
    }

    @Override
    public Response readByLocator(String query, String value) {
        return RestAssured
                .given()
                .spec(spec)
                .get(endpoint.getUrl()  + "?" + "locator=" + query + ":" + value);
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
    public Response delete(String locator) {
        return RestAssured
                .given()
                .spec(spec)
                .delete(endpoint.getUrl() + "/" + locator);
    }

    @Override
    public Response update(String path, BaseModel model, String is) {
        return RestAssured
                .given()
                .spec(spec)
                .accept("text/plain")
                .contentType("text/plain")
                .body(is)
                .put(String.format(endpoint.getUrl() + "/%s", path));
    }

    @Override
    public Response updateWithParameters(String projectLocator, BaseModel model, String parameter) {
        return RestAssured
                .given()
                .spec(spec)
                .contentType("application/json")
                .body(model)
                .put(String.format(endpoint.getUrl() + "/%s/parameters/%s", projectLocator, parameter));
    }
}
