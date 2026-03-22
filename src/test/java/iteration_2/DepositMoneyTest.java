package iteration_2;


import generators.RandomData;
import iteration_1.BaseTest;
import models.*;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import requests.AdminCreateUserRequester;
import requests.CreateAccountRequester;
import requests.GetAccountsRequester;
import requests.DepositRequester;
import requests.skelethon.requests.CrudRequester;
import requests.skelethon.requests.Endpoint;
import requests.skelethon.requests.ValidatedCrudRequester;
import requests.steps.AdminSteps;
import requests.steps.UserSteps;
import specs.RequestSpecs;
import specs.ResponseSpecs;


import java.util.stream.Stream;


public class DepositMoneyTest extends BaseTest {


    @ParameterizedTest
    @ValueSource(doubles = {0.01, 4999.99, 5000.00})
    public void userCanDepositHisAccountTest(double amount) {
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

        //депозит денег на созданный ранее счет
        DepositRequest depositRequest = DepositRequest.builder()
                .id(createdAccount.getId())
                .balance(amount)
                .build();

        CreateAccountResponse depositResponse = new DepositRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.requestReturnsOK())
                .post(depositRequest)
                .extract()
                .as(CreateAccountResponse.class);

        //проверка, что деньги зачислены (получили класс ответа и сравниваем баланс с суммой депозита)
        softly.assertThat(depositResponse.getBalance())
                .isEqualTo(amount);
    }


    private static Stream<Arguments> invalidDepositData() {
        return Stream.of(
                Arguments.of(-0.01, "Deposit amount must be at least 0.01"),
                Arguments.of(5000.01, "Deposit amount cannot exceed 5000")
        );
    }


    @ParameterizedTest
    @MethodSource("invalidDepositData")
    public void userCanNotDepositHisAccountWithInvalidAmountTest(double amount, String errorMessage) {
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

        //депозит денег на созданный ранее счет с невалидными данными
        DepositRequest depositRequest = DepositRequest.builder()
                .id(createdAccount.getId())
                .balance(amount)
                .build();

        new CrudRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsBadStringRequest(errorMessage))
                .post(depositRequest);

        // проверка, что деньги не зачислены(снова делаем запрос к аккаунтам и проверяем баланс, который
        // должен быть равен 0 как при создании аккаунта
        GetAccountsResponse[] updatedAccounts = UserSteps.getAccounts(userRequest);

        softly.assertThat(updatedAccounts)
                .anyMatch(account -> account.getId() == createdAccount.getId() &&
                        account.getBalance() == createdAccount.getBalance());
    }

    @Test
    public void userCanNotDepositToNonExistentAccountTest() {
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

        //депозит денег на созданный ранее счет с невалидными данными
        DepositRequest depositRequest = DepositRequest.builder()
                .id(RandomData.getRandomId())
                .balance(RandomData.getRandomAmount())
                .build();

        new CrudRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsForbidden())
                .post(depositRequest);

        // проверка, что деньги не зачислены(снова делаем запрос к аккаунтам и проверяем баланс, который
        // должен быть равен 0 как при создании аккаунта
        GetAccountsResponse[] updatedAccounts = UserSteps.getAccounts(userRequest);

        softly.assertThat(updatedAccounts)
                .anyMatch(account -> account.getId() == createdAccount.getId() &&
                        account.getBalance() == createdAccount.getBalance());
    }
}
