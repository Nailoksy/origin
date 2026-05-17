package iteration_2.api;

import api.models.CreateAccountResponse;
import api.models.CreateUserRequest;
import api.models.GetAccountsResponse;
import api.models.TransferRequest;
import iteration_1.api.BaseTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import api.requests.skelethon.requests.CrudRequester;
import api.requests.skelethon.requests.Endpoint;
import api.requests.steps.AdminSteps;
import api.requests.steps.UserSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

import java.util.Arrays;
import java.util.stream.Stream;
import static api.requests.steps.UserSteps.depositMoney;

public class TransferMoneyTest extends BaseTest {
    private static final double MAX_DEPOSIT = 5000;
    private static final double TOTAL_TRANSFER = 15000;


    private void repeat(int times, Runnable action) {
        for (int i = 0; i < times; i++) {
            action.run();
        }
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.01, 9999.99, 10000.00})
    public void userCanTransferMoneyFromOneAccountToAnotherTest(double amountTransfer) {
        //создание пользователя
        CreateUserRequest createUser1 = AdminSteps.createUser();

        //создаем аккаунт(счет)
        CreateAccountResponse createdAccount1 = UserSteps.createAccount(createUser1);

        //получаем все аккаунты
        GetAccountsResponse[] accounts1 = UserSteps.getAccounts(createUser1);

        //проверяем, что массив не пустой и в нем есть наш аккаунт, сравниваем аккаунты
        softly.assertThat(accounts1).isNotEmpty();
        softly.assertThat(accounts1)
                .anyMatch(account ->
                        account.getAccountNumber().equals(createdAccount1.getAccountNumber()));

        // депозит денег на первый счет 3 раза
        repeat(3, () -> depositMoney(createdAccount1.getId(), MAX_DEPOSIT, createUser1));

        //проверка, что деньги зачислены (получаем аккаунты, затем фильтруем аккаунты по айди
        //и у отфильтрованного сравниваем баланс с размером депозита)
        GetAccountsResponse[] updateAccounts = UserSteps.getAccounts(createUser1);
        GetAccountsResponse account = Arrays.stream(updateAccounts).filter(acc -> acc.getId() == createdAccount1.getId()).findFirst().orElseThrow();
        softly.assertThat(account.getBalance()).isEqualTo((float) TOTAL_TRANSFER);

        //создание пользователя 2
        CreateUserRequest createUser2 = AdminSteps.createUser();

        //создаем аккаунт(счет)
        CreateAccountResponse createdAccount2 = UserSteps.createAccount(createUser2);

        //получаем все аккаунты
        GetAccountsResponse[] accounts2 = UserSteps.getAccounts(createUser2);

        //проверяем, что массив не пустой и в нем есть наш аккаунт, сравниваем аккаунты
        softly.assertThat(accounts2).isNotEmpty();
        softly.assertThat(accounts2)
                .anyMatch(acc ->
                        acc.getAccountNumber().equals(createdAccount2.getAccountNumber()));

        // перевод с одного аккаунта на второй
        UserSteps.transferMoney(createdAccount1.getId(), createdAccount2.getId(), amountTransfer, createUser1);

        // проверка баланса получателя
        GetAccountsResponse[] updatedAccounts2 = UserSteps.getAccounts(createUser2);
        GetAccountsResponse account2 = Arrays.stream(updatedAccounts2).filter(acc -> acc.getId() == createdAccount2.getId()).findFirst().orElseThrow();
        softly.assertThat(account2.getBalance()).isEqualTo((float) amountTransfer);
    }

    private static Stream<Arguments> invalidDepositData() {
        return Stream.of(
                Arguments.of(-0.01, "Transfer amount must be at least 0.01"),
                Arguments.of(10000.01, "Transfer amount cannot exceed 10000"),
                Arguments.of(20000.00, "Transfer amount cannot exceed 10000")
        );
    }

    @ParameterizedTest
    @MethodSource("invalidDepositData")
    public void userCanNotTransferMoneyWithInvalidDataTest(double amountTransfer, String errorMessage) {
        //создание пользователя
        CreateUserRequest createUser1 = AdminSteps.createUser();

        //создаем аккаунт(счет)
        CreateAccountResponse createdAccount1 = UserSteps.createAccount(createUser1);

        //получаем все аккаунты
        GetAccountsResponse[] accounts1 = UserSteps.getAccounts(createUser1);

        //проверяем, что массив не пустой и в нем есть наш аккаунт, сравниваем аккаунты
        softly.assertThat(accounts1).isNotEmpty();
        softly.assertThat(accounts1)
                .anyMatch(account ->
                        account.getAccountNumber().equals(createdAccount1.getAccountNumber()));

        // депозит денег на первый счет 3 раза
        repeat(3, () -> depositMoney(createdAccount1.getId(), MAX_DEPOSIT, createUser1));

        //проверка, что деньги зачислены (получаем аккаунты, затем фильтруем аккаунты по айди
        //и у отфильтрованного сравниваем баланс с размером депозита)
        GetAccountsResponse[] updateAccounts = UserSteps.getAccounts(createUser1);
        GetAccountsResponse account = Arrays.stream(updateAccounts).filter(acc -> acc.getId() == createdAccount1.getId()).findFirst().orElseThrow();
        softly.assertThat(account.getBalance()).isEqualTo((float) TOTAL_TRANSFER);

        //создание пользователя 2
        CreateUserRequest createUser2 = AdminSteps.createUser();

        //создаем аккаунт(счет)
        CreateAccountResponse createdAccount2 = UserSteps.createAccount(createUser2);

        //получаем все аккаунты
        GetAccountsResponse[] accounts2 = UserSteps.getAccounts(createUser2);

        //проверяем, что массив не пустой и в нем есть наш аккаунт, сравниваем аккаунты
        softly.assertThat(accounts2).isNotEmpty();
        softly.assertThat(accounts2)
                .anyMatch(acc ->
                        acc.getAccountNumber().equals(createdAccount2.getAccountNumber()));

        //перевод с невалидными данными
        TransferRequest transferRequest = TransferRequest.builder()
                .senderAccountId(createdAccount1.getId())
                .receiverAccountId(createdAccount2.getId())
                .amount(amountTransfer)
                .build();

        new CrudRequester(RequestSpecs.authAsUser(createUser1.getUsername(), createUser1.getPassword()),
                Endpoint.TRANSFER,
                ResponseSpecs.requestReturnsBadStringRequest(errorMessage))
                .post(transferRequest);

        //Проверка, что деньги не поступили к получателю
        GetAccountsResponse[] updateAccounts2 = UserSteps.getAccounts(createUser2);

        GetAccountsResponse receiverAccount = Arrays.stream(updateAccounts2).filter(acc -> acc.getId() == createdAccount2.getId()).findFirst().orElseThrow();
        softly.assertThat(receiverAccount.getBalance() == 0.0F);

    }
}