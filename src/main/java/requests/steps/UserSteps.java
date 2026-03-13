package requests.steps;

import io.restassured.response.ValidatableResponse;
import models.CreateUserRequest;
import requests.skelethon.requests.CrudRequester;
import requests.skelethon.requests.Endpoint;
import specs.RequestSpecs;
import specs.ResponseSpecs;

public class UserSteps {
    public static ValidatableResponse createAccount(CreateUserRequest userRequest){
        return new CrudRequester(RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.CREATE_ACCOUNTS,
                ResponseSpecs.entityWasCreated())
                .post(null);
    }
}
