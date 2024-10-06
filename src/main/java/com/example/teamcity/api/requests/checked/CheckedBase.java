package com.example.teamcity.api.requests.checked;

import com.example.teamcity.api.enums.Endpoint;
import com.example.teamcity.api.generators.TestDataStorage;
import com.example.teamcity.api.models.BaseModel;
import com.example.teamcity.api.requests.CrudInterface;
import com.example.teamcity.api.requests.Request;
import com.example.teamcity.api.requests.unchecked.UncheckedBase;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpStatus;


/**
 * Класс нужен для того, чтобы выполнять запросы к API с проверкой успешности этих запросов (валидных HTTP-ответов).
 * Также сохраняет данные созданных сущностей, что упрощает управление тестовыми данными.
 * @param <T>
 */
@SuppressWarnings("unchecked")
public final class CheckedBase<T extends BaseModel> extends Request implements CrudInterface {

    private final UncheckedBase unchekedBase;

    public CheckedBase(RequestSpecification spec, Endpoint endpoint) {
        super(spec, endpoint);
        this.unchekedBase = new UncheckedBase(spec,endpoint);
    }

    /**
     * Создает новую сущность, проверяет, что запрос завершился успешно (код 200),
     * и сохраняет созданную сущность в TestDataStorage
     * @param model
     * @return
     */
    @Override
    public T create(BaseModel model) {
        var createdModel = (T) unchekedBase
                .create(model)
                .then().assertThat().statusCode(HttpStatus.SC_OK)
                .extract().as(endpoint.getModelClass());
        TestDataStorage.getStorage().addCreatedEntity(endpoint, createdModel);
        return createdModel;
    }

    @Override
    public T read(String id) {
        return (T) unchekedBase
                .read(id)
                .then().assertThat().statusCode(HttpStatus.SC_OK)
                .extract().as(endpoint.getModelClass());
    }

    @Override
    public T update(String id, BaseModel model) {
        var createdModel = (T) unchekedBase
                .update(id, model)
                .then().assertThat().statusCode(HttpStatus.SC_OK)
                .extract().as(endpoint.getModelClass());
        TestDataStorage.getStorage().addCreatedEntity(endpoint, createdModel);
        return createdModel;
    }

    @Override
    public Object delete(String id) {
        return unchekedBase
                .read(id)
                .then().assertThat().statusCode(HttpStatus.SC_OK)
                .extract().asString();
    }

    @Override
    public Response updateWithPath(String path, BaseModel model, String is) {
        String formattedUrl = String.format(endpoint.getUrl() + "/%s", path);  // динамическая подстановка параметра
        return RestAssured
                .given()
                .spec(spec)
                .accept("text/plain")
                .contentType("text/plain")
                .body(is)
                .put(formattedUrl);
    }
}
