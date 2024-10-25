package com.example.teamcity.api.spec;

import com.example.teamcity.api.config.Config;
import com.example.teamcity.api.models.User;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

/**
 * Вспомогательный класс для создания спецификаций HTTP-запросов в тестах.
 * Служит для создания различных конфигураций спецификаций запросов (RequestSpecification) в зависимости от типа пользователя.
 * Упрощает создание запросов с предустановленными фильтрами (логирование запросов/ответов),
 * типом контента, а также базовыми данными для авторизации.
 */
public class Specifications {

    //	Метод, создающий базовый билдер для запросов с добавленными фильтрами для логирования и заданным типом контента (JSON).
    private static RequestSpecBuilder reqBuilder() {
        var requestBuilder = new RequestSpecBuilder();
        requestBuilder.addFilter(new RequestLoggingFilter());
        requestBuilder.addFilter(new ResponseLoggingFilter());
        requestBuilder.setContentType(ContentType.JSON);
        requestBuilder.setAccept(ContentType.JSON);
        return requestBuilder;
    }

    // Создает спецификацию для суперпользователя с использованием токена superUserToken и хоста из конфигурации
    public static RequestSpecification superUserSpec() {
        var requestBuilder = reqBuilder();
        requestBuilder.setBaseUri("http://%s:%s@%s/httpAuth".formatted("", Config.getProperty("superUserToken"), Config.getProperty("host")));
        return requestBuilder.build();
    }

    // Возвращает спецификацию запроса для неавторизованного пользователя
    public static RequestSpecification unauthSpec() {
        var requestBuilder = reqBuilder();
        return requestBuilder.build();
    }

    // Создает спецификацию запроса для авторизованного пользователя с указанием его имени и пароля, а также хоста из конфигурации.
    public static RequestSpecification authSpec(User user) {
        var requestBuilder = reqBuilder();
        requestBuilder.setBaseUri("http://%s:%s@%s".formatted(user.getUsername(), user.getPassword(), Config.getProperty("host")));
        return requestBuilder.build();
    }
}
