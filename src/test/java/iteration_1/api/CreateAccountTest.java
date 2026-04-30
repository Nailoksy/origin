package iteration_1.api;

import models.*;
import org.junit.jupiter.api.Test;
import requests.steps.AdminSteps;
import requests.steps.UserSteps;

public class CreateAccountTest extends BaseTest{
    @Test
    public void userCanCreateAccountTest() {
        //создание пользователя
        CreateUserRequest userRequest = AdminSteps.createUser();

        //создаем аккаунт(счет)
        CreateAccountResponse createdAccount = UserSteps.createAccount(userRequest);

        //получаем все аккаунты
        GetAccountsResponse[] accounts = UserSteps.getAccounts(userRequest);

        //проверяем, что массив не пустой и в нем есть наш аккаунт, сравниваем аккаунты
        softly.assertThat(accounts).isNotEmpty();
        softly.assertThat(accounts)
                .anyMatch(account ->
                        account.getAccountNumber().equals(createdAccount.getAccountNumber()));
    }

}
