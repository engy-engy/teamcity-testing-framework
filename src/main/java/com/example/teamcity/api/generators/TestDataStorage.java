package com.example.teamcity.api.generators;

import com.example.teamcity.api.enums.Endpoint;
import com.example.teamcity.api.models.BaseModel;
import com.example.teamcity.api.requests.unchecked.UncheckedBase;
import com.example.teamcity.api.spec.Specifications;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Управляет хранилищем сгенерированных данных, чтобы отслеживать созданные в ходе тестов сущности.
 * Помогает управлять жизненным циклом тестовых данных (например, создавать, удалять сущности после завершения тестов).
 */
public class TestDataStorage {

    private static TestDataStorage testDataStorage;
    private final EnumMap<Endpoint, Set<String>> createdEntitiesMap;

    private TestDataStorage() {
        createdEntitiesMap = new EnumMap<>(Endpoint.class);
    }

    // Возвращает экземпляр TestDataStorage (синглтон).
    public static TestDataStorage getStorage() {
        if (testDataStorage == null) {
            testDataStorage = new TestDataStorage();
        }
        return testDataStorage;
    }
    // Добавляет в хранилище сущности, которые были созданы в ходе тестов.
    // Каждая сущность привязывается к определенному Endpoint.
    private void addCreatedEntity(Endpoint endpoint, String id) {
        if (id != null) {
            createdEntitiesMap.computeIfAbsent(endpoint,
                    key -> new HashSet<>()).add(id);
        }
    }
    // Извлекает ID или локатор из переданной модели данных (используется для идентификации сущности).
    private String getEntityIdOrLocator(BaseModel model) {
        try {
            var idField = model.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            var idFieldValue = Objects.toString(idField.get(model), null);
            idField.setAccessible(false);
            return idFieldValue;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            try {
                var locatorField = model.getClass().getDeclaredField("id");
                locatorField.setAccessible(true);
                var locatorFieldValue = Objects.toString(locatorField.get(model), null);
                locatorField.setAccessible(false);
                return locatorFieldValue;
            } catch (NoSuchFieldException | IllegalAccessException ex) {
                throw new IllegalStateException("Cannot get ID or locator of entities", ex);
            }
        }
    }

    // Добавляет сущность в хранилище, автоматически получая ее ID или локатор
    public void addCreatedEntity(Endpoint endpoint, BaseModel model) {
        addCreatedEntity(endpoint, getEntityIdOrLocator(model));
    }

    // Удаляет все сущности, созданные во время тестов, через запросы к соответствующему Endpoint
    public void deleteCreatedEntities() {
        createdEntitiesMap.forEach(((endpoint, ids) ->
                ids.forEach(id ->
                        new UncheckedBase(Specifications.superUserSpec(), endpoint).delete(id)))

        );
        createdEntitiesMap.clear();
    }
}
