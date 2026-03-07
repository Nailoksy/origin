package iteration_2;


import generators.RandomData;
import iteration_1.BaseTest;
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
    private static final String PASSWORD = "Kate2000#";


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
        int accountId1 = new CreateAccountRequester(RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.entityWasCreated())
                .post(null)
                .extract()
                .path("id");

        //проверка, что счет создан
        new GetAccountsRequester(RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.requestReturnsOK())
                .get(null)
                .body("size()", Matchers.greaterThan(0));

        //депозит денег на созданный ранее счет
        DepositRequest depositRequest = DepositRequest.builder()
                .id(accountId1)
                .balance(amount)
                .build();

    new DepositRequester(RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
            ResponseSpecs.requestReturnsOK())
            .post(depositRequest)
            .body("balance", Matchers.equalTo((float) amount));

        // проверка, что деньги зачислены
        new GetAccountsRequester(RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.requestReturnsOK())
                .get(null)
                .body("balance", Matchers.hasItem((float) amount));
    }


    private static Stream<Arguments> invalidDepositData() {
        return Stream.of(
                Arguments.of(-0.01, "Deposit amount must be at least 0.01"),
                Arguments.of(5000.01, "Deposit amount cannot exceed 5000")
        );
    }


    @ParameterizedTest
    @MethodSource("invalidDepositData")
    public void userCanNotDepositHisAccountWithInvalidAmountTest (double amount, String errorMessage) {
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
        int accountId1 = new CreateAccountRequester(RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.entityWasCreated())
                .post(null)
                .extract()
                .path("id");

        //проверка, что счет создан
        new GetAccountsRequester(RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.requestReturnsOK())
                .get(null)
                .body("size()", Matchers.greaterThan(0));

        //депозит денег на созданный ранее счет с невалидными данными
        DepositRequest depositRequest = DepositRequest.builder()
                .id(accountId1)
                .balance(amount)
                .build();

        new DepositRequester(RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.requestReturnsBadStringRequest(errorMessage))
                .post(depositRequest);

        // проверка, что деньги не зачислены
        new GetAccountsRequester(RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.requestReturnsOK())
                .get(null)
                .body("balance", Matchers.hasItem((0.00F)));
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
        int accountId1 = new CreateAccountRequester(RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.entityWasCreated())
                .post(null)
                .extract()
                .path("id");

        //проверка, что счет создан
        new GetAccountsRequester(RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.requestReturnsOK())
                .get(null)
                .body("size()", Matchers.greaterThan(0));

        //депозит денег на созданный ранее счет с невалидными данными
        DepositRequest depositRequest = DepositRequest.builder()
                .id(10)
                .balance(100)
                .build();

        new DepositRequester(RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.requestReturnsForbidden())
                .post(depositRequest);

        // проверка, что деньги не зачислены
        new GetAccountsRequester(RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.requestReturnsOK())
                .get(null)
                .body("balance", Matchers.hasItem((0.00F)));

    }
}
