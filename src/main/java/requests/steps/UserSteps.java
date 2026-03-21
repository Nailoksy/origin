package requests.steps;

import models.CreateAccountResponse;
import models.CreateUserRequest;
import models.GetAccountsResponse;
import requests.skelethon.requests.CrudRequester;
import requests.skelethon.requests.Endpoint;
import specs.RequestSpecs;
import specs.ResponseSpecs;

public class UserSteps {
    public static CreateAccountResponse createAccount(CreateUserRequest userRequest){
        return new CrudRequester(RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.CREATE_ACCOUNTS,
                ResponseSpecs.entityWasCreated())
                .post(null)
                .extract()
                .as(CreateAccountResponse.class);
    }

    public static GetAccountsResponse[] getAccounts(CreateUserRequest userRequest) {
        return new CrudRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.GET_ACCOUNTS,
                ResponseSpecs.requestReturnsOK())
                .getAll()
                .extract()
                .as(GetAccountsResponse[].class);
    }

}
