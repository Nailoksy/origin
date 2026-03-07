package iteration_2;

import generators.RandomData;
import iteration_1.BaseTest;
import models.CreateUserRequest;
import models.DepositRequest;
import models.UserRole;
import models.TransferRequest;
import org.hamcrest.Matchers;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import requests.*;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.util.stream.Stream;

public class TransferMoneyTest extends BaseTest {

    @ParameterizedTest
    @ValueSource(doubles = {0.01, 9999.99, 10000.00})
    public void userCanTransferMoneyFromOneAccountToAnotherTest(double amountTransfer) {
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

        double totalDeposit = 15000; // хотим положить всего на первый счет
        double maxDeposit = 5000;    // максимум за один депозит

        // депозит денег на первый счет частями по maxDeposit (помог ГПТ с циклом, чтобы три раза пост запрос не делать)
        int iterations = (int) (totalDeposit / maxDeposit);
        for (int i = 0; i < iterations; i++) {
            double amountToDeposit = Math.min(maxDeposit, totalDeposit - i * maxDeposit);
            DepositRequest depositRequest = DepositRequest.builder()
                    .id(accountId1)
                    .balance(amountToDeposit)
                    .build();

            new DepositRequester(
                    RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                    ResponseSpecs.requestReturnsOK())
                    .post(depositRequest);
        }


        // проверка, что деньги зачислены
        new GetAccountsRequester(RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.requestReturnsOK())
                .get(null)
                .body("balance", Matchers.hasItem((float) totalDeposit));


        //создание пользователя2
        CreateUserRequest userRequest2 = CreateUserRequest.builder()
                .username(RandomData.getUsername())
                .password(RandomData.getPassword())
                .role(UserRole.USER.toString())
                .build();

        new AdminCreateUserRequester(
                RequestSpecs.adminSpec(),
                ResponseSpecs.entityWasCreated())
                .post(userRequest2);

        //создаем аккаунт(счет) и получаем ID2
        int accountId2 = new CreateAccountRequester(RequestSpecs.authAsUser(userRequest2.getUsername(), userRequest2.getPassword()),
                ResponseSpecs.entityWasCreated())
                .post(null)
                .extract()
                .path("id");

        //проверка, что счет создан
        new GetAccountsRequester(RequestSpecs.authAsUser(userRequest2.getUsername(), userRequest2.getPassword()),
                ResponseSpecs.requestReturnsOK())
                .get(null)
                .body("size()", Matchers.greaterThan(0));

        //перевод с одного аккаунта на второй
        TransferRequest transferRequest = TransferRequest.builder()
                .senderAccountId(accountId1)
                .receiverAccountId(accountId2)
                .amount(amountTransfer)
                .build();

        new TransferRequester(RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.requestReturnsOK())
                .post(transferRequest);

        // проверка, что деньги поступили
        new GetAccountsRequester(RequestSpecs.authAsUser(userRequest2.getUsername(), userRequest2.getPassword()),
                ResponseSpecs.requestReturnsOK())
                .get(null)
                .body("balance", Matchers.hasItem((float) amountTransfer));

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

        double totalDeposit = 15000; // хотим положить всего на первый счет
        double maxDeposit = 5000;    // максимум за один депозит

        // депозит денег на первый счет частями по maxDeposit (помог ГПТ с циклом, чтобы три раза пост запрос не делать)
        int iterations = (int) (totalDeposit / maxDeposit);
        for (int i = 0; i < iterations; i++) {
            double amountToDeposit = Math.min(maxDeposit, totalDeposit - i * maxDeposit);
            DepositRequest depositRequest = DepositRequest.builder()
                    .id(accountId1)
                    .balance(amountToDeposit)
                    .build();

            new DepositRequester(
                    RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                    ResponseSpecs.requestReturnsOK())
                    .post(depositRequest);
        }


        // проверка, что деньги зачислены
        new GetAccountsRequester(RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.requestReturnsOK())
                .get(null)
                .body("balance", Matchers.hasItem((float) totalDeposit));


        //создание пользователя2
        CreateUserRequest userRequest2 = CreateUserRequest.builder()
                .username(RandomData.getUsername())
                .password(RandomData.getPassword())
                .role(UserRole.USER.toString())
                .build();

        new AdminCreateUserRequester(
                RequestSpecs.adminSpec(),
                ResponseSpecs.entityWasCreated())
                .post(userRequest2);

        //создаем аккаунт(счет) и получаем ID2
        int accountId2 = new CreateAccountRequester(RequestSpecs.authAsUser(userRequest2.getUsername(), userRequest2.getPassword()),
                ResponseSpecs.entityWasCreated())
                .post(null)
                .extract()
                .path("id");

        //проверка, что счет создан
        new GetAccountsRequester(RequestSpecs.authAsUser(userRequest2.getUsername(), userRequest2.getPassword()),
                ResponseSpecs.requestReturnsOK())
                .get(null)
                .body("size()", Matchers.greaterThan(0));

        //перевод с одного аккаунта на второй
        TransferRequest transferRequest = TransferRequest.builder()
                .senderAccountId(accountId1)
                .receiverAccountId(accountId2)
                .amount(amountTransfer)
                .build();

        new TransferRequester(RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.requestReturnsBadStringRequest(errorMessage))
                .post(transferRequest);

        // проверка, что деньги не поступили
        new GetAccountsRequester(RequestSpecs.authAsUser(userRequest2.getUsername(), userRequest2.getPassword()),
                ResponseSpecs.requestReturnsOK())
                .get(null)
                .body("balance", Matchers.hasItem((0.00F)));

    }
}
