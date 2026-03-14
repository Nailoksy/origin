package iteration_2;


import generators.RandomData;
import iteration_1.BaseTest;
import models.CreateAccountResponse;
import models.CreateUserRequest;
import models.DepositRequest;
import models.UserRole;
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
import specs.RequestSpecs;
import specs.ResponseSpecs;


import java.util.stream.Stream;

import static io.restassured.RestAssured.given;


public class DepositMoneyTest extends BaseTest {


    @ParameterizedTest
    @ValueSource(doubles = {0.01, 4999.99, 5000.00})
    public void userCanDepositHisAccountTest(double amount) {
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

        //создаем аккаунт(счет) и получаем ID
        CreateAccountResponse createdAccount = new CreateAccountRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.entityWasCreated())
                .post(null)
                .extract()
                .as(CreateAccountResponse.class);

        long accountId1 = createdAccount.getId();

        //проверка, что счет создан(сначала получаем аккаунты)
        CreateAccountResponse[] accounts = new GetAccountsRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.requestReturnsOK())
                .get(null)
                .extract()
                .as(CreateAccountResponse[].class);

        //затем проверяем, что аккаунт совпадает с созданным
        softly.assertThat(accounts).isNotEmpty();
        softly.assertThat(accounts)
                .anyMatch(account -> account.getId() == accountId1);

        //депозит денег на созданный ранее счет
        DepositRequest depositRequest = DepositRequest.builder()
                .id(accountId1)
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
        CreateUserRequest userRequest = CreateUserRequest.builder()
                .username(RandomData.getUsername())
                .password(RandomData.getPassword())
                .role(UserRole.USER.toString())
                .build();

        new AdminCreateUserRequester(
                RequestSpecs.adminSpec(),
                ResponseSpecs.entityWasCreated())
                .post(userRequest);

        //создаем аккаунт(счет) и получаем ID
        CreateAccountResponse createAccount = new CreateAccountRequester(RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.entityWasCreated())
                .post(null)
                .extract()
                .as(CreateAccountResponse.class);

        long accountId1 = createAccount.getId();

        //проверка, что счет создан(сначала получаем аккаунты)
        CreateAccountResponse[] accounts = new GetAccountsRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.requestReturnsOK())
                .get(null)
                .extract()
                .as(CreateAccountResponse[].class);

        //затем проверяем, что аккаунт совпадает с созданным
        softly.assertThat(accounts).isNotEmpty();
        softly.assertThat(accounts)
                .anyMatch(account -> account.getId() == accountId1);

        //депозит денег на созданный ранее счет с невалидными данными
        DepositRequest depositRequest = DepositRequest.builder()
                .id(accountId1)
                .balance(amount)
                .build();

        new DepositRequester(RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.requestReturnsBadStringRequest(errorMessage))
                .post(depositRequest);

        // проверка, что деньги не зачислены(снова делаем запрос к аккаунтам и проверяем баланс, который
        // должен быть равен 0)
        CreateAccountResponse[] updatedAccounts = new GetAccountsRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.requestReturnsOK())
                .get(null)
                .extract()
                .as(CreateAccountResponse[].class);

        softly.assertThat(updatedAccounts)
                .anyMatch(account -> account.getId() == accountId1 && account.getBalance() == 0.0F);
    }

    @Test
    public void userCanNotDepositToNonExistentAccountTest() {
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

        //создаем аккаунт(счет) и получаем ID
        CreateAccountResponse createAccount = new CreateAccountRequester(RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.entityWasCreated())
                .post(null)
                .extract()
                .as(CreateAccountResponse.class);

        long accountId1 = createAccount.getId();

        //проверка, что счет создан(сначала получаем аккаунты)
        CreateAccountResponse[] accounts = new GetAccountsRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.requestReturnsOK())
                .get(null)
                .extract()
                .as(CreateAccountResponse[].class);

        //затем проверяем, что аккаунт совпадает с созданным
        softly.assertThat(accounts).isNotEmpty();
        softly.assertThat(accounts)
                .anyMatch(account -> account.getId() == accountId1);

        //депозит денег на созданный ранее счет с невалидными данными
        DepositRequest depositRequest = DepositRequest.builder()
                .id(10)
                .balance(100)
                .build();

        new DepositRequester(RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.requestReturnsForbidden())
                .post(depositRequest);

        // проверка, что деньги не зачислены(снова делаем запрос к аккаунтам и проверяем баланс, который
        // должен быть равен 0)
        CreateAccountResponse[] updatedAccounts = new GetAccountsRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.requestReturnsOK())
                .get(null)
                .extract()
                .as(CreateAccountResponse[].class);

        softly.assertThat(updatedAccounts)
                .anyMatch(account -> account.getId() == accountId1 && account.getBalance() == 0.0F);


    }
}
