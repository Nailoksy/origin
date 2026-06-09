package iteration_1.ui;

import api.models.CreateUserRequest;
import api.models.GetAccountsResponse;
import api.requests.steps.UserSteps;
import com.codeborne.selenide.Selenide;
import common.annotation.Browsers;
import common.annotation.UserSession;
import common.storage.SessionStorage;
import org.junit.jupiter.api.Test;
import ui.pages.BankAlerts;
import ui.pages.UserDashboardPage;

import java.time.Duration;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

public class CreateAccountTest extends BaseTestUI {
    @Test
    @Browsers({"chrome"})
    @UserSession
    public void userCanCreateAccountTest() {
            // ШАГ 4: юзер создает аккаунт
            new UserDashboardPage().open().createNewAccount();

        UserSteps steps = SessionStorage.getSteps();
        CreateUserRequest user = SessionStorage.getUser();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //получение аккаунта на API
            GetAccountsResponse createdAccount = Arrays.stream(SessionStorage.getSteps().getAccounts(SessionStorage.getUser())).findFirst().get();

            // ШАГ 5: проверка, что аккаунт создался на UI(проверка алерта)
            new UserDashboardPage().checkAlertMessageAndAccept(BankAlerts.NEW_ACCOUNT_CREATED.getMessage() + createdAccount.getAccountNumber());

            // ШАГ 6: проверка, что аккаунт был создан на API
            assertThat(createdAccount.getBalance()).isZero();
    }
}
