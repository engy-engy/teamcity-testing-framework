package com.example.teamcity.api.requests;

import com.example.teamcity.api.models.BaseModel;

public interface CrudInterface {

    Object create(BaseModel model);

    Object read(String id);

    Object update(String path, BaseModel model);

    Object update(String path, String parameter);

    Object update(String projectLocator, BaseModel model, String parameter);

    Object delete(String id);

}
