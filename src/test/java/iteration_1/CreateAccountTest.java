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
        CreateAccountResponse createdAccount = new CreateAccountRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.entityWasCreated())
                .post()
                .extract()
                .as(CreateAccountResponse.class);

        long accountId = createdAccount.getId();

        //проверка, что счет создан
        CreateAccountResponse[] accounts = new GetAccountsRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.requestReturnsOK())
                .get()
                .extract()
                .as(CreateAccountResponse[].class);

        //затем проверяем, что аккаунт совпадает с созданным
        softly.assertThat(accounts).isNotEmpty();
        softly.assertThat(accounts)
                .anyMatch(account -> account.getId() == accountId);
    }

}
