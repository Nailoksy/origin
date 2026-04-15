package iteration_1;

import configs.Config;
import models.CreateUserRequest;
import models.CreateUserResponse;
import models.LoginUserRequest;
import org.junit.jupiter.api.Test;
import requests.skelethon.requests.CrudRequester;
import requests.skelethon.requests.Endpoint;
import requests.skelethon.requests.ValidatedCrudRequester;
import requests.steps.AdminSteps;
import specs.RequestSpecs;
import specs.ResponseSpecs;


public class LoginUserTest extends BaseTest {
    @Test
    public void adminCanGenerateAuthTokenTest() {
        LoginUserRequest userRequest = LoginUserRequest.builder()
                .username(Config.getProperty(Config.ADMIN_LOGIN))
                .password(Config.getProperty(Config.ADMIN_PASSWORD))
                .build();

        new ValidatedCrudRequester<CreateUserResponse>(RequestSpecs.unauthSpec(),
                Endpoint.LOGIN_USER,
                ResponseSpecs.requestReturnsOK())
                .post(userRequest);
    }

    @Test
    public void userCanGenerateAuthTokenTest() {
        //создание пользователя
        CreateUserRequest userRequest = AdminSteps.createUser();

        //получаем токен юзера
        //позитивный, но т.к в конце проверяем хедер, то берем CrudRequester
        new CrudRequester(RequestSpecs.unauthSpec(),
                Endpoint.LOGIN_USER,
                ResponseSpecs.returnsOkAndAuthHeader())
                .post(LoginUserRequest.builder().username(userRequest.getUsername())
                        .password(userRequest.getPassword()).build());
    }
}
