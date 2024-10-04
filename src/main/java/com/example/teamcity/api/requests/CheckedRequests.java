package com.example.teamcity.api.requests;

import com.example.teamcity.api.enums.Endpoint;
import com.example.teamcity.api.models.BaseModel;
import com.example.teamcity.api.requests.checked.CheckedBase;
import io.restassured.specification.RequestSpecification;

import java.util.EnumMap;

/**
 * Класс, который управляет объектами типа CheckedBase для разных эндпоинтов API.
 * Хранит экземпляры запросов с проверками успешности выполнения запросов.
 *
 * Класс создает экземпляры CheckedBase для всех значений перечисления Endpoint и хранит их в виде карты (словаря).
 * Предоставляет метод getRequest для получения нужного запроса на основе конкретного эндпоинта.
 * Используется для централизованного управления запросами с проверками (валидацией), чтобы тесты могли легко получать
 * доступ к необходимым CRUD-операциям для любого эндпоинта без необходимости создавать их вручную каждый раз.
 */
public class CheckedRequests {
    private final EnumMap<Endpoint, CheckedBase> requests = new EnumMap<>(Endpoint.class);

    // Инициализирует карту запросов для каждого эндпоинта, создавая для них экземпляры CheckedBase с заданной спецификацией запроса.
    public CheckedRequests(RequestSpecification spec) {
        for (var endpoint: Endpoint.values()) {
            requests.put(endpoint, new CheckedBase(spec, endpoint));
        }
    }
    // Возвращает объект CheckedBase, который содержит CRUD-операции для заданного эндпоинта
    public <T extends BaseModel> CheckedBase<T> getRequest(Endpoint endpoint) {
        return (CheckedBase<T>) requests.get(endpoint);
    }
}