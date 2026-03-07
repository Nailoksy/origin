package requests;

import io.restassured.response.ValidatableResponse;
import models.BaseModel;

public interface Get <T extends BaseModel> {
    public abstract ValidatableResponse get(T model);
}
