package requests;

import io.restassured.response.ValidatableResponse;
import models.BaseModel;

public interface Put <T extends BaseModel> {
    public abstract ValidatableResponse put(T model);
}
