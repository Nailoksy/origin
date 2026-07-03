package iteration_1.ui;

import api.models.GetAccountsResponse;
import common.annotation.Browsers;
import common.annotation.UserSession;
import common.storage.SessionStorage;
import common.utils.WaitUtils;
import org.junit.jupiter.api.Test;
import ui.pages.BankAlerts;
import ui.pages.UserDashboardPage;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateAccountTest extends BaseTestUI {
    @Test
    @Browsers({"chrome"})
    @UserSession
    public void userCanCreateAccountTest() {
            // ШАГ 4: юзер создает аккаунт
        new UserDashboardPage().open().createNewAccount();

        WaitUtils.sleep(WaitUtils.WAIT_FOR_UI);
        //получение аккаунта на API
        GetAccountsResponse createdAccount = Arrays.stream(SessionStorage.getSteps().getAccounts(SessionStorage.getUser())).findFirst().get();

            // ШАГ 5: проверка, что аккаунт создался на UI(проверка алерта)
        new UserDashboardPage().checkAlertMessageAndAccept(BankAlerts.NEW_ACCOUNT_CREATED.getMessage() + createdAccount.getAccountNumber());

            // ШАГ 6: проверка, что аккаунт был создан на API
        assertThat(createdAccount.getBalance()).isZero();
    }
}
