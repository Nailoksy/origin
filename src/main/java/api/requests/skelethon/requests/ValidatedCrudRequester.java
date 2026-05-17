package api.requests.skelethon.requests;

import api.models.GetAllUsersResponse;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import api.models.BaseModel;
import api.requests.skelethon.interfaces.CrudEndpointInterface;

import java.util.Arrays;
import java.util.List;

//когда нужно сериализовать ответ
//для позитивных кейсов
public class ValidatedCrudRequester <T extends BaseModel> extends HttpRequest implements CrudEndpointInterface {
    private CrudRequester crudRequester;

    public ValidatedCrudRequester(RequestSpecification requestSpecification, Endpoint endpoint, ResponseSpecification responseSpecification) {
        super(requestSpecification, endpoint, responseSpecification);
        this.crudRequester = new CrudRequester(requestSpecification, endpoint, responseSpecification);
    }

    @Override
    public T post(BaseModel model) {
        return (T) crudRequester.post(model).extract().as(endpoint.getResponseModel());
    }

    @Override
    public T get(long id) {
        return (T) crudRequester.get(id).extract()
                .as(endpoint.getResponseModel());
    }

    @Override
    public T update(BaseModel model) {
        return (T) crudRequester.update(model).extract().as(endpoint.getResponseModel());
    }

    @Override
    public ValidatableResponse delete(long id) {
        return crudRequester.delete(id);
    }

    @Override
    public GetAllUsersResponse[] getAll(){
        return crudRequester.getAll().extract().as(GetAllUsersResponse[].class);
    }
}
