package api.requests.steps;

import api.generators.RandomModelGenerator;
import io.qameta.allure.Step;
import api.models.CreateUserRequest;
import api.models.CreateUserResponse;
import api.models.GetAllUsersResponse;
import api.requests.skelethon.requests.CrudRequester;
import api.requests.skelethon.requests.Endpoint;
import api.requests.skelethon.requests.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;


public class AdminSteps {
    @Step("Создание пользователя админом")
    public static CreateUserRequest createUser(){
        CreateUserRequest userRequest = RandomModelGenerator.generate(CreateUserRequest.class);

        new ValidatedCrudRequester<CreateUserResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.ADMIN_USER,
                ResponseSpecs.entityWasCreated())
                .post(userRequest);

        return userRequest;
    }

    @Step("Получение массива всех пользователей")
    public static GetAllUsersResponse[] getAllUsers(){
        return new CrudRequester(RequestSpecs.adminSpec(),
                Endpoint.ADMIN_GET_ALL_USERS,
                ResponseSpecs.requestReturnsOK())
                .get()
                .extract()
                .as(GetAllUsersResponse[].class);
    }
}
