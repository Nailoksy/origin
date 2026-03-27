package requests;

import io.restassured.response.ValidatableResponse;
import models.BaseModel;

public interface Post <T extends BaseModel> {
    public abstract ValidatableResponse post(T model);
}
