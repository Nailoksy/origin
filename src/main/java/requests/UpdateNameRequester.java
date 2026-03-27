package requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import models.BaseModel;

import static io.restassured.RestAssured.*;

public class UpdateNameRequester extends Request implements Put{
    public UpdateNameRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        super(requestSpecification, responseSpecification);
    }

    @Override
    public ValidatableResponse put(BaseModel model) {
        return given()
                .spec(requestSpecification)
                .body(model)
                .put("api/v1/customer/profile")
                .then()
                .assertThat()
                .spec(responseSpecification);
    }
}
