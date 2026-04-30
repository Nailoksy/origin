package iteration_1.ui;

import api.models.GetAccountsResponse;
import api.requests.steps.UserSteps;
import api.models.CreateUserRequest;
import org.junit.jupiter.api.Test;
import api.requests.steps.AdminSteps;
import ui.pages.BankAlerts;
import ui.pages.UserDashboardPage;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateAccountTest extends BaseTestUI {
    @Test
    public void userCanCreateAccountTest() {
        //ПРЕДШАГИ на API
        // ШАГ 1: админ залогинился в банке
        // ШАГ 1: админ создает юзера
        // ШАГ 1: юзер логинится в банке
        CreateUserRequest user = AdminSteps.createUser();

        authAsUser(user);

        // ШАГИ ТЕСТА
        // ШАГ 4: юзер создает аккаунт
        new UserDashboardPage().open().createNewAccount();

        //получение аккаунта на API
        GetAccountsResponse createdAccount = Arrays.stream(UserSteps.getAccounts(user)).findFirst().get();

        // ШАГ 5: проверка, что аккаунт создался на UI(проверка алерта)
        new UserDashboardPage().checkAlertMessageAndAccept(BankAlerts.NEW_ACCOUNT_CREATED.getMessage() + createdAccount.getAccountNumber());

        // ШАГ 6: проверка, что аккаунт был создан на API
        assertThat(createdAccount.getBalance()).isZero();
    }
}
