package iteration_2;

import generators.RandomData;
import iteration_1.BaseTest;
import models.CreateAccountResponse;
import models.CreateUserRequest;
import models.DepositRequest;
import models.TransferRequest;
import models.UserRole;
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
        //создание пользователя1
        CreateUserRequest userRequest = CreateUserRequest.builder()
                .username(RandomData.getUsername())
                .password(RandomData.getPassword())
                .role(UserRole.USER.toString())
                .build();

        new AdminCreateUserRequester(
                RequestSpecs.adminSpec(),
                ResponseSpecs.entityWasCreated())
                .post(userRequest);

        //создаем аккаунт(счет) и получаем ID1
        CreateAccountResponse account1 = new CreateAccountRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.entityWasCreated())
                .post(null)
                .extract()
                .as(CreateAccountResponse.class);

        long accountId1 = account1.getId();

        // проверка, что счет создан
        CreateAccountResponse[] accountsUser1 = new GetAccountsRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.requestReturnsOK())
                .get(null)
                .extract()
                .as(CreateAccountResponse[].class);

        softly.assertThat(accountsUser1).isNotEmpty();
        softly.assertThat(accountsUser1)
                .anyMatch(acc -> acc.getId() == accountId1);

        // депозит денег на первый счет частями
        double totalDeposit = 15000; // всего хотим положить
        double maxDeposit = 5000;    // максимум за один депозит
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

        // проверка баланса после депозита
        accountsUser1 = new GetAccountsRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.requestReturnsOK())
                .get(null)
                .extract()
                .as(CreateAccountResponse[].class);

        softly.assertThat(accountsUser1)
                .anyMatch(acc -> acc.getId() == accountId1 && acc.getBalance() == totalDeposit);

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
        CreateAccountResponse account2 = new CreateAccountRequester(
                RequestSpecs.authAsUser(userRequest2.getUsername(), userRequest2.getPassword()),
                ResponseSpecs.entityWasCreated())
                .post(null)
                .extract()
                .as(CreateAccountResponse.class);

        long accountId2 = account2.getId();

        // проверка, что счет создан
        CreateAccountResponse[] accountsUser2 = new GetAccountsRequester(
                RequestSpecs.authAsUser(userRequest2.getUsername(), userRequest2.getPassword()),
                ResponseSpecs.requestReturnsOK())
                .get(null)
                .extract()
                .as(CreateAccountResponse[].class);

        softly.assertThat(accountsUser2).isNotEmpty();
        softly.assertThat(accountsUser2)
                .anyMatch(acc -> acc.getId() == accountId2);

        // перевод с одного аккаунта на второй
        TransferRequest transferRequest = TransferRequest.builder()
                .senderAccountId(accountId1)
                .receiverAccountId(accountId2)
                .amount(amountTransfer)
                .build();

        new TransferRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.requestReturnsOK())
                .post(transferRequest);

        // проверка баланса получателя
        accountsUser2 = new GetAccountsRequester(
                RequestSpecs.authAsUser(userRequest2.getUsername(), userRequest2.getPassword()),
                ResponseSpecs.requestReturnsOK())
                .get(null)
                .extract()
                .as(CreateAccountResponse[].class);

        softly.assertThat(accountsUser2)
                .anyMatch(acc -> acc.getId() == accountId2 && acc.getBalance() == amountTransfer);
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
        //создание пользователя1
        CreateUserRequest userRequest = CreateUserRequest.builder()
                .username(RandomData.getUsername())
                .password(RandomData.getPassword())
                .role(UserRole.USER.toString())
                .build();

        new AdminCreateUserRequester(
                RequestSpecs.adminSpec(),
                ResponseSpecs.entityWasCreated())
                .post(userRequest);

        //создаем аккаунт и получаем ID1
        CreateAccountResponse account1 = new CreateAccountRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.entityWasCreated())
                .post(null)
                .extract()
                .as(CreateAccountResponse.class);

        long accountId1 = account1.getId();

        // депозит на первый счет частями
        double totalDeposit = 15000;
        double maxDeposit = 5000;
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

        // проверка баланса после депозита
        CreateAccountResponse[] accountsUser1 = new GetAccountsRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.requestReturnsOK())
                .get(null)
                .extract()
                .as(CreateAccountResponse[].class);

        softly.assertThat(accountsUser1)
                .anyMatch(acc -> acc.getId() == accountId1 && acc.getBalance() == totalDeposit);

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

        //создаем аккаунт и получаем ID2
        CreateAccountResponse account2 = new CreateAccountRequester(
                RequestSpecs.authAsUser(userRequest2.getUsername(), userRequest2.getPassword()),
                ResponseSpecs.entityWasCreated())
                .post(null)
                .extract()
                .as(CreateAccountResponse.class);

        long accountId2 = account2.getId();

        //перевод с одного аккаунта на второй с невалидной суммой
        TransferRequest transferRequest = TransferRequest.builder()
                .senderAccountId(accountId1)
                .receiverAccountId(accountId2)
                .amount(amountTransfer)
                .build();

        new TransferRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.requestReturnsBadStringRequest(errorMessage))
                .post(transferRequest);

        // проверка, что деньги не поступили
        CreateAccountResponse[] accountsUser2 = new GetAccountsRequester(
                RequestSpecs.authAsUser(userRequest2.getUsername(), userRequest2.getPassword()),
                ResponseSpecs.requestReturnsOK())
                .get(null)
                .extract()
                .as(CreateAccountResponse[].class);

        softly.assertThat(accountsUser2)
                .anyMatch(acc -> acc.getId() == accountId2 && acc.getBalance() == 0.0F);
    }
}