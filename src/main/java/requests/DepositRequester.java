package requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import models.BaseModel;

import static io.restassured.RestAssured.given;

public class DepositRequester extends Request implements Post {
    public DepositRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        super(requestSpecification, responseSpecification);
    }

    @Override
    public ValidatableResponse post (BaseModel model){
        return given()
                .spec(requestSpecification)
                .body(model)
                .post("accounts/deposit")
                .then()
                .assertThat()
                .spec(responseSpecification);
        }

}
