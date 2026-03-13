package iteration_1;

import generators.RandomData;
import generators.RandomModelGenerator;
import models.*;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import requests.AdminCreateUserRequester;
import requests.CreateAccountRequester;
import requests.GetAccountsRequester;
import requests.skelethon.requests.CrudRequester;
import requests.skelethon.requests.Endpoint;
import requests.skelethon.requests.ValidatedCrudRequester;
import requests.steps.AdminSteps;
import requests.steps.UserSteps;
import specs.RequestSpecs;
import specs.ResponseSpecs;


public class CreateAccountTest extends BaseTest{
    @Test
    public void userCanCreateAccountTest() {
        //создание пользователя
        CreateUserRequest userRequest = AdminSteps.createUser();

        //создаем аккаунт(счет)
        UserSteps.createAccount(userRequest);
//        new CrudRequester(RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
//                Endpoint.CREATE_ACCOUNTS,
//                ResponseSpecs.entityWasCreated())
//                .post(null);

//     //проверка, что счет создан
//        new CrudRequester(RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
//                Endpoint.LOGIN_USER,
//                ResponseSpecs.requestReturnsOK())
//                .get(null)
//                .body("size()", Matchers.greaterThan(0));
    }

}
