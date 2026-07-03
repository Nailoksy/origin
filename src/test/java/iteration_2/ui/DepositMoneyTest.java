package iteration_2.ui;

import common.annotation.Browsers;
import common.annotation.UserSession;
import common.storage.SessionStorage;
import common.utils.WaitUtils;
import iteration_1.ui.BaseTestUI;
import api.models.GetAccountsResponse;
import org.junit.jupiter.api.Test;
import api.requests.steps.UserSteps;
import ui.pages.BankAlerts;
import ui.pages.DepositPage;

import static org.assertj.core.api.Assertions.assertThat;

public class DepositMoneyTest extends BaseTestUI {
    private static final double AMOUNT_FOR_DEPOSIT = 100;
    private static final double TOTAL_DEPOSIT = 5000;

    @Test
    @Browsers({"chrome"})
    @UserSession
    public void userCanDepositHisAccountTest() {
        UserSteps.createAccount(SessionStorage.getUser());

        GetAccountsResponse[] existingUserAccounts = UserSteps.getAccounts(SessionStorage.getUser());
        assertThat(existingUserAccounts).hasSize(1);

        GetAccountsResponse createdAccount = existingUserAccounts[0];
        assertThat(createdAccount).isNotNull();
        assertThat(createdAccount.getBalance()).isZero();

        WaitUtils.sleep(WaitUtils.WAIT_FOR_UI);
        new DepositPage().open().deposit(AMOUNT_FOR_DEPOSIT)
                .checkAlertMessageAndAccept(BankAlerts.SUCCESSFULLY_DEPOSITED.getMessage() + AMOUNT_FOR_DEPOSIT);
        WaitUtils.sleep(WaitUtils.WAIT_FOR_UI);

        double balanceAfterDeposit = UserSteps.getAccounts(SessionStorage.getUser())[0].getBalance();
        assertThat(balanceAfterDeposit).isEqualTo(AMOUNT_FOR_DEPOSIT);
    }

    @Test
    @Browsers({"chrome"})
    @UserSession
    public void userCanNotDepositHisAccountWithInvalidAmountTest() {
        UserSteps.createAccount(SessionStorage.getUser());

        GetAccountsResponse[] existingUserAccounts = UserSteps.getAccounts(SessionStorage.getUser());

        assertThat(existingUserAccounts).hasSize(1);

        GetAccountsResponse createdAccount = existingUserAccounts[0];

        assertThat(createdAccount).isNotNull();
        assertThat(createdAccount.getBalance()).isZero();

        new DepositPage().open().deposit(AMOUNT_FOR_DEPOSIT + TOTAL_DEPOSIT)
                .checkAlertMessageAndAccept(BankAlerts.PLEASE_DEPOSIT_LESS_OR_EQUAL_TO_5000.getMessage());

        double balanceAfterDeposit = UserSteps.getAccounts(SessionStorage.getUser())[0].getBalance();
        assertThat(balanceAfterDeposit).isEqualTo(createdAccount.getBalance());
    }
}
