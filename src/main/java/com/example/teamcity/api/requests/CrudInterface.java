package com.example.teamcity.api.requests;

import com.example.teamcity.api.models.BaseModel;

public interface CrudInterface {
    Object create(BaseModel model);
    Object read(String id);
    Object read(String query, String value);
    Object update(String path, BaseModel model);
    Object update(String path, BaseModel model, String is);
    Object delete(String id);
    Object updateWithParameters(String projectLocator, BaseModel model, String parameter);
}
