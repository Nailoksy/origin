package iteration_1.api;

import api.configs.Config;
import api.models.CreateUserRequest;
import api.models.CreateUserResponse;
import api.models.LoginUserRequest;
import org.junit.jupiter.api.Test;
import api.requests.skelethon.requests.CrudRequester;
import api.requests.skelethon.requests.Endpoint;
import api.requests.skelethon.requests.ValidatedCrudRequester;
import api.requests.steps.AdminSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;


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
