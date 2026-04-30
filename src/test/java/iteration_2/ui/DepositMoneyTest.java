package iteration_2.ui;

import iteration_1.ui.BaseTestUI;
import api.models.CreateUserRequest;
import api.models.GetAccountsResponse;
import org.junit.jupiter.api.Test;
import api.requests.steps.AdminSteps;
import api.requests.steps.UserSteps;
import ui.pages.BankAlerts;
import ui.pages.DepositPage;

import static org.assertj.core.api.Assertions.assertThat;

public class DepositMoneyTest extends BaseTestUI {
    private static final double AMOUNT_FOR_DEPOSIT = 100;
    private static final double TOTAL_DEPOSIT = 5000;

    @Test
    public void userCanDepositHisAccountTest() {
    //ПРЕДШАГИ на API:
        //1.создание пользователя
        CreateUserRequest user = AdminSteps.createUser();

        //2.создаем аккаунт(счет)
        UserSteps.createAccount(user);

        //проверяем, что массив не пустой и в нем есть наш аккаунт, сравниваем аккаунты
        GetAccountsResponse[] existingUserAccounts = UserSteps.getAccounts(user);
        assertThat(existingUserAccounts).hasSize(1);

        GetAccountsResponse createdAccount = existingUserAccounts[0];
        assertThat(createdAccount).isNotNull();
        assertThat(createdAccount.getBalance()).isZero();

    //ШАГИ ТЕСТА
        //1. Юзер перешел на страницу Deposit(авторизация как пользователь)
        authAsUser(user);

        //2. Юзер Нажал на выбор аккаунта Select Account и выбрал первый созданный аккаунт:
        //3. Юзер нажал Enter Amount: и написал сумму депозита до 5000
        //находим все кнопки, ищем по имени депозит, клик.
        //проверка, что деньги внеслись на UI(проверка алерта)

        new DepositPage().open().deposit(AMOUNT_FOR_DEPOSIT)
                .checkAlertMessageAndAccept(BankAlerts.SUCCESSFULLY_DEPOSITED.getMessage() + AMOUNT_FOR_DEPOSIT);

        //Проверка, что баланс соответствует на API
        double balanceAfterDeposit = UserSteps.getAccounts(user)[0].getBalance();
        assertThat(balanceAfterDeposit).isEqualTo(AMOUNT_FOR_DEPOSIT);


    }

    @Test
    public void userCanNotDepositHisAccountWithInvalidAmountTest() {
    //ПРЕДШАГИ на API:
        //1.создание пользователя
        CreateUserRequest user = AdminSteps.createUser();

        //2.создаем аккаунт(счет)
        UserSteps.createAccount(user);

        //проверяем, что массив не пустой и в нем есть наш аккаунт, сравниваем аккаунты
        GetAccountsResponse[] existingUserAccounts = UserSteps.getAccounts(user);

        assertThat(existingUserAccounts).hasSize(1);

        GetAccountsResponse createdAccount = existingUserAccounts[0];

        assertThat(createdAccount).isNotNull();
        assertThat(createdAccount.getBalance()).isZero();

    //ШАГИ ТЕСТА
        //1. Юзер перешел на страницу Deposit(авторизация как пользователь)
        authAsUser(user);
        //2. Юзер Нажал на выбор аккаунта Select Account и выбрал первый созданный аккаунт:
        //3. Юзер нажал Enter Amount: и написал сумму депозита до 5000
        //находим все кнопки, ищем по имени депозит, клик.
        //проверка, что деньги внеслись на UI(проверка алерта)

        new DepositPage().open().deposit(AMOUNT_FOR_DEPOSIT + TOTAL_DEPOSIT)
                .checkAlertMessageAndAccept(BankAlerts.PLEASE_DEPOSIT_LESS_OR_EQUAL_TO_5000.getMessage());

        //Проверка, что баланс соответствует на API
        double balanceAfterDeposit = UserSteps.getAccounts(user)[0].getBalance();
        assertThat(balanceAfterDeposit).isEqualTo(createdAccount.getBalance());


    }
}
