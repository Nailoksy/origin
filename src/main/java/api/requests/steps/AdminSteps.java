package api.requests.steps;

import api.generators.RandomModelGenerator;
import common.storage.SessionStorage;
import common.storage.UserDeleteRegistry;
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


public class AdminSteps {
    @Step("Создание пользователя админом")
    public static CreateUserRequest createUser(){
        CreateUserRequest userRequest = RandomModelGenerator.generate(CreateUserRequest.class);

        CreateUserResponse createUserResponse = new ValidatedCrudRequester<CreateUserResponse>(
                        RequestSpecs.adminSpec(),
                        Endpoint.ADMIN_USER,
                        ResponseSpecs.entityWasCreated())
                        .post(userRequest);

        userRequest.setId(createUserResponse.getId());
        SessionStorage.addUser(userRequest);
        UserDeleteRegistry.add(createUserResponse.getId());

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

    @Step("Удаление пользователя по id {id}")
    public static void deleteUserById(long id) {
        System.out.println("Пользователь с ID: " + id + " - будет удален.");
        new CrudRequester(
                RequestSpecs.adminSpec(),
                Endpoint.DELETE,
                ResponseSpecs.noValidation())
                .delete(id);

        System.out.println("Пользователь с ID: " + id + " - удален");
    }

    //Добавлено: шаг удаления ВСЕХ пользователей
    @Step("Удаление всех пользователей")
    public static void deleteAllUsers() {
        Arrays.stream(AdminSteps.getAllUsers()).filter(u -> u.getUsername().startsWith("TestUser_"))
                .map(GetAllUsersResponse::getId)
                .forEach(id -> deleteUserById(id)
                );
    }
}
