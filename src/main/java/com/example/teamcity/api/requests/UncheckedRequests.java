package com.example.teamcity.api.requests;

import com.example.teamcity.api.enums.Endpoint;
import com.example.teamcity.api.requests.unchecked.UncheckedBase;
import io.restassured.specification.RequestSpecification;

import java.util.EnumMap;

/**
 * Класс, который управляет объектами типа UncheckedBase для разных эндпоинтов API.
 * Хранит экземпляры запросов без проверок успешности выполнения запросов.
 * Класс создает экземпляры UncheckedBase для всех значений перечисления Endpoint и хранит их в виде карты (словаря).
 * Предоставляет метод для получения нужного запроса на основе конкретного эндпоинта.
 * Используется для централизованного управления запросами без проверок, что позволяет гибко работать с CRUD-операциями,
 * когда не требуется проверка успешности HTTP-ответов.
 */
public class UncheckedRequests {

    private final EnumMap<Endpoint, UncheckedBase> requests = new EnumMap<>(Endpoint.class);

    public UncheckedRequests(RequestSpecification spec) {
        for (var endpoint: Endpoint.values()) {
            requests.put(endpoint, new UncheckedBase(spec, endpoint));
        }
    }

    public UncheckedBase getRequest(Endpoint endpoint) {
        return requests.get(endpoint);
    }

}
