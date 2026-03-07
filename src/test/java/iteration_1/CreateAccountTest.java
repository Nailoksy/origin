package iteration_1;

import generators.RandomData;
import models.CreateUserRequest;
import models.UserRole;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import requests.AdminCreateUserRequester;
import requests.CreateAccountRequester;
import requests.GetAccountsRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;


public class CreateAccountTest extends BaseTest{
    @Test
    public void userCanCreateAccountTest() {
        //создание пользователя
        CreateUserRequest userRequest = CreateUserRequest.builder()
                .username(RandomData.getUsername())
                .password(RandomData.getPassword())
                .role(UserRole.USER.toString())
                .build();

        new AdminCreateUserRequester(
                RequestSpecs.adminSpec(),
                ResponseSpecs.entityWasCreated())
                .post(userRequest);

        //создаем аккаунт(счет)
        new CreateAccountRequester(RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
        ResponseSpecs.entityWasCreated())
                .post(null);

        //проверка, что счет создан
        new GetAccountsRequester(RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.requestReturnsOK())
                .get(null)
                .body("size()", Matchers.greaterThan(0));
    }

}
