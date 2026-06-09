package iteration_2.ui;

import api.requests.steps.AdminSteps;
import common.annotation.Browsers;
import common.annotation.UserSession;
import common.storage.SessionStorage;
import iteration_1.ui.BaseTestUI;
import api.models.CreateAccountResponse;
import api.models.GetAccountsResponse;
import org.junit.jupiter.api.Test;
import api.requests.steps.UserSteps;
import ui.pages.BankAlerts;
import ui.pages.TransferMoneyPage;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static api.requests.steps.UserSteps.depositMoney;

public class TransferMoneyTest extends BaseTestUI {
    private static final double MAX_DEPOSIT = 5000;
    private static final double INVALID_TRANSFER = -1;

    @Test
    @Browsers({"chrome"})
    @UserSession
    public void userCanTransferMoneyFromOneAccountToAnotherTest() {
        //ПРЕДШАГИ на API:
        //2.создаем два аккаунта(счета)
        CreateAccountResponse account1 = UserSteps.createAccount(SessionStorage.getUser());
        CreateAccountResponse account2 = UserSteps.createAccount(SessionStorage.getUser());

        //проверяем, что массив не пустой и в нем есть наши 2 аккаунта
        GetAccountsResponse[] existingUserAccounts = UserSteps.getAccounts(SessionStorage.getUser());
        assertThat(existingUserAccounts).hasSize(2);

        GetAccountsResponse createdAccount1 = Arrays.stream(existingUserAccounts).filter(acc -> acc.getAccountNumber().equals(account1.getAccountNumber())).findFirst().get();
        assertThat(createdAccount1).isNotNull();
        assertThat(createdAccount1.getBalance()).isZero();

        //проверяем второй аккаунт
        GetAccountsResponse createdAccount2 = Arrays.stream(existingUserAccounts).filter(acc -> acc.getAccountNumber().equals(account2.getAccountNumber())).findFirst().get();
        assertThat(createdAccount2).isNotNull();
        assertThat(createdAccount2.getBalance()).isZero();

        // депозит денег на первый счет
        depositMoney(account1.getId(), MAX_DEPOSIT, SessionStorage.getUser());

        //проверка, что деньги зачислены
        GetAccountsResponse[] updateAccounts = UserSteps.getAccounts(SessionStorage.getUser());
        GetAccountsResponse updateAcc2 = Arrays.stream(updateAccounts).filter(acc -> acc.getId() == account1.getId()).findAny().orElseThrow();
        assertThat(updateAcc2.getBalance()).isEqualTo((float) MAX_DEPOSIT);

        //Шаги:
        //1. Юзер нажал Make a Transfer
        //2. Нажал Select Your Account и выбрал аккаунт с которого отправляет
        //3. Нажал Recipient Name: и написал имя получателя(пусть имя будет номером аккаунта)
        //4. Нажал Recipient Account Number: и написал номер аккаунта получателя
        //5. Нажал Amount: Вписал сумму для перевода
        //6. Поставил галочку Confirm details are correct  и кнопка send transfer
        //7. Проверка, что деньги переведены на UI (Проверка алерта)
        new TransferMoneyPage().open().transfer(createdAccount1.getAccountNumber(), createdAccount2.getAccountNumber(), MAX_DEPOSIT)
                .checkAlertMessageAndAccept(BankAlerts.SUCCESSFULLY_TRANSFERRED.getMessage());


        //2. Проверка после перевода, что баланс второго аккаунта соответствует переводу на API
        GetAccountsResponse[] updateAccountsAfterTransfer = UserSteps.getAccounts(SessionStorage.getUser());
        GetAccountsResponse updateAcc2AfterTransfer = Arrays.stream(updateAccountsAfterTransfer).filter(acc -> acc.getId() == account2.getId()).findAny().orElseThrow();
        assertThat(updateAcc2AfterTransfer.getBalance()).isEqualTo((float) MAX_DEPOSIT);

        //баланс первого аккаунта стал ноль после трансфера
        GetAccountsResponse updateAcc1AfterTransfer = Arrays.stream(UserSteps.getAccounts(SessionStorage.getUser())).filter(acc -> acc.getId() == account1.getId()).findAny().orElseThrow();
        assertThat(updateAcc1AfterTransfer.getBalance()).isZero();
    }

    @Test
    @Browsers({"chrome"})
    @UserSession
    public void userCanNotTransferMoneyWithInvalidDataTest() {
        //ПРЕДШАГИ на API:
        //2.создаем два аккаунта(счета)
        CreateAccountResponse account1 = UserSteps.createAccount(SessionStorage.getUser());
        CreateAccountResponse account2 = UserSteps.createAccount(SessionStorage.getUser());

        //проверяем, что массив не пустой и в нем есть наши 2 аккаунта
        GetAccountsResponse[] existingUserAccounts = UserSteps.getAccounts(SessionStorage.getUser());
        assertThat(existingUserAccounts).hasSize(2);

        GetAccountsResponse createdAccount1 = Arrays.stream(existingUserAccounts).filter(acc -> acc.getAccountNumber().equals(account1.getAccountNumber())).findFirst().get();
        assertThat(createdAccount1).isNotNull();
        assertThat(createdAccount1.getBalance()).isZero();

        //проверяем второй аккаунт
        GetAccountsResponse createdAccount2 = Arrays.stream(existingUserAccounts).filter(acc -> acc.getAccountNumber().equals(account2.getAccountNumber())).findFirst().get();
        assertThat(createdAccount2).isNotNull();
        assertThat(createdAccount2.getBalance()).isZero();

        // депозит денег на первый счет
        depositMoney(account1.getId(), MAX_DEPOSIT, SessionStorage.getUser());

        //проверка, что деньги зачислены
        GetAccountsResponse[] updateAccounts = UserSteps.getAccounts(SessionStorage.getUser());
        GetAccountsResponse updateAcc2 = Arrays.stream(updateAccounts).filter(acc -> acc.getId() == account1.getId()).findAny().orElseThrow();
        assertThat(updateAcc2.getBalance()).isEqualTo((float) MAX_DEPOSIT);

        //Шаги:
        //1. Юзер нажал Make a Transfer
        //2. Нажал Select Your Account и выбрал аккаунт с которого отправляет
        //3. Нажал Recipient Name: и написал имя получателя(пусть имя будет номером аккаунта)
        //4. Нажал Recipient Account Number: и написал номер аккаунта получателя
        //5. Нажал Amount: Вписал сумму для перевода
        //6. Поставил галочку Confirm details are correct  и кнопка send transfer
        //7. Проверка, что деньги переведены на UI (Проверка алерта)
        new TransferMoneyPage().open().transfer(account1.getAccountNumber(), account2.getAccountNumber(), INVALID_TRANSFER)
                .checkAlertMessageAndAccept(BankAlerts.ERROR_TRANSFER_AMOUNT.getMessage());

        //2. Проверка после перевода, что баланс второго аккаунта остался равен 0 на API
        GetAccountsResponse[] updateAccountsAfterTransfer = UserSteps.getAccounts(SessionStorage.getUser());
        GetAccountsResponse updateAcc2AfterTransfer = Arrays.stream(updateAccountsAfterTransfer).filter(acc -> acc.getId() == account2.getId()).findAny().orElseThrow();
        assertThat(updateAcc2AfterTransfer.getBalance()).isZero();

        //баланс первого аккаунта остался прежний после неудачного перевода
        GetAccountsResponse updateAcc1AfterTransfer = Arrays.stream(updateAccountsAfterTransfer).filter(acc -> acc.getId() == account1.getId()).findAny().orElseThrow();
        assertThat(updateAcc1AfterTransfer.getBalance()).isEqualTo((float) MAX_DEPOSIT);

    }

}