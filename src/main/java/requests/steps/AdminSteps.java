package requests.steps;

import generators.RandomModelGenerator;
import io.qameta.allure.Step;
import models.CreateUserRequest;
import models.CreateUserResponse;
import models.GetAllUsersResponse;
import requests.skelethon.requests.CrudRequester;
import requests.skelethon.requests.Endpoint;
import requests.skelethon.requests.ValidatedCrudRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.util.stream.Stream;

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
