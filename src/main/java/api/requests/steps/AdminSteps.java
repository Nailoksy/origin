package api.requests.steps;

import api.generators.RandomModelGenerator;
import common.storage.SessionStorage;
import io.qameta.allure.Step;
import api.models.CreateUserRequest;
import api.models.CreateUserResponse;
import api.models.GetAllUsersResponse;
import api.requests.skelethon.requests.CrudRequester;
import api.requests.skelethon.requests.Endpoint;
import api.requests.skelethon.requests.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

import java.util.Arrays;
import java.util.List;


public class AdminSteps {
    @Step("Создание пользователя админом")
    public static CreateUserRequest createUser(){
        CreateUserRequest userRequest = RandomModelGenerator.generate(CreateUserRequest.class);

        CreateUserResponse response =
                new ValidatedCrudRequester<CreateUserResponse>(
                        RequestSpecs.adminSpec(),
                        Endpoint.ADMIN_USER,
                        ResponseSpecs.entityWasCreated())
                        .post(userRequest);

        SessionStorage.addUserId(response.getId());

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

    //Добавлено: шаг удаления тестовых пользователей
    @Step("Удаление пользователя по id {id}")
    public static void deleteUser(long id) {
        new CrudRequester(
                RequestSpecs.adminSpec(),
                Endpoint.DELETE,
                ResponseSpecs.noValidation())
                .delete(id);
    }

    //Добавлено: шаг удаления всех пользователей
    @Step("Удаление всех пользователей")
    public static void deleteAllUsers() {
        Arrays.stream(AdminSteps.getAllUsers()).filter(u -> u.getUsername().startsWith("TestUser"))
                .map(GetAllUsersResponse::getId)
                .forEach(id ->
                        new CrudRequester(
                                RequestSpecs.adminSpec(),
                                Endpoint.DELETE,
                                ResponseSpecs.noValidation())
                                .delete(id)
                );
    }
}
