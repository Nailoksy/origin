package iteration_2.ui;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import iteration_1.ui.BaseTestUI;
import models.CreateAccountResponse;
import models.CreateUserRequest;
import models.GetAccountsResponse;
import models.LoginUserRequest;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import requests.skelethon.requests.CrudRequester;
import requests.skelethon.requests.Endpoint;
import requests.steps.AdminSteps;
import requests.steps.UserSteps;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.util.Arrays;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Selenide.$;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static requests.steps.UserSteps.depositMoney;

public class TransferMoneyTest extends BaseTestUI {
    private static final double MAX_DEPOSIT = 5000;
    private static final double INVALID_TRANSFER = -1;

    @Test
    public void userCanTransferMoneyFromOneAccountToAnotherTest() {
            //ПРЕДШАГИ на API:
        //1.создание пользователя
        CreateUserRequest user = AdminSteps.createUser();

        //2.создаем два аккаунта(счета)
        CreateAccountResponse account1 = UserSteps.createAccount(user);
        CreateAccountResponse account2 = UserSteps.createAccount(user);

        //проверяем, что массив не пустой и в нем есть наши 2 аккаунта
        CreateAccountResponse[] existingUserAccounts = given()
                .spec(RequestSpecs.authAsUser(user.getUsername(), user.getPassword()))
                .get("http://localhost:4111/api/v1/customer/accounts")
                .then().assertThat()
                .extract().as(CreateAccountResponse[].class);

        assertThat(existingUserAccounts).hasSize(2);

        CreateAccountResponse createdAccount1 = existingUserAccounts[0];
        assertThat(createdAccount1).isNotNull();
        assertThat(createdAccount1.getBalance()).isZero();

        //проверяем второй аккаунт
        CreateAccountResponse createdAccount2 = existingUserAccounts[1];
        assertThat(createdAccount2).isNotNull();
        assertThat(createdAccount2.getBalance()).isZero();

        // депозит денег на первый счет
        depositMoney(account1.getId(), MAX_DEPOSIT, user);

        //проверка, что деньги зачислены
        GetAccountsResponse[] updateAccounts = UserSteps.getAccounts(user);
        GetAccountsResponse updateAcc2 = Arrays.stream(updateAccounts).filter(acc -> acc.getId() == account1.getId()).findAny().orElseThrow();
        assertThat(updateAcc2.getBalance()).isEqualTo((float) MAX_DEPOSIT);

        //сохраняем токен для LocalStorage
        String userAuthHeader = new CrudRequester(
                RequestSpecs.unauthSpec(),
                Endpoint.LOGIN_USER,
                ResponseSpecs.requestReturnsOK())
                .post(LoginUserRequest.builder().username(user.getUsername()).password(user.getPassword()).build())
                .extract()
                .header("Authorization");
        //Вносим токен в LocalStorage
        Selenide.open("/");
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userAuthHeader);

        //Шаги:
            //1. Юзер нажал Make a Transfer
        Selenide.open("/transfer");
        $(Selectors.byText("\uD83D\uDD04 Make a Transfer")).shouldBe(Condition.visible);

            //2. Нажал Select Your Account и выбрал аккаунт с которого отправляет
        $$(".account-selector option")
                .findBy(Condition.text(Double.toString(MAX_DEPOSIT)))
                .click();

            //3. Нажал Recipient Name: и написал имя получателя(пусть имя будет номером аккаунта)
        $(Selectors.byAttribute("placeholder", "Enter recipient name"))
                .sendKeys(account2.getAccountNumber());
            //4. Нажал Recipient Account Number: и написал номер аккаунта получателя
        $(Selectors.byAttribute("placeholder", "Enter recipient account number"))
                .sendKeys(account2.getAccountNumber());
            //5. Нажал Amount: Вписал сумму для перевода
        $(Selectors.byAttribute("placeholder", "Enter amount"))
                .sendKeys(Double.toString(MAX_DEPOSIT));
            //6. Поставил галочку Confirm details are correct
        $(Selectors.byAttribute("id", "confirmCheck"))
                .click();
            //7. Нажал Send Transfer
        $$("button")
                .findBy(Condition.text("\uD83D\uDE80 Send Transfer"))
                .click();

    //Ожидания:
            //1. Проверка, что деньги перевелись на второй аккаунт на UI(алерт)
        Alert alert = switchTo().alert();
        assertThat(alert.getText()).contains("✅ Successfully transferred");
        alert.accept();

            //2. Проверка после перевода, что баланс второго аккаунта соответствует переводу на API
        GetAccountsResponse[] updateAccountsAfterTransfer = UserSteps.getAccounts(user);
        GetAccountsResponse updateAcc2AfterTransfer = Arrays.stream(updateAccountsAfterTransfer).filter(acc -> acc.getId() == account2.getId()).findAny().orElseThrow();
        assertThat(updateAcc2AfterTransfer.getBalance()).isEqualTo((float) MAX_DEPOSIT);

        //баланс первого аккаунта стал ноль после трансфера
        GetAccountsResponse updateAcc1AfterTransfer = Arrays.stream(updateAccountsAfterTransfer).filter(acc -> acc.getId() == account1.getId()).findAny().orElseThrow();
        assertThat(updateAcc1AfterTransfer.getBalance()).isZero();
    }

    @Test
    public void userCanNotTransferMoneyWithInvalidDataTest() {
        //ПРЕДШАГИ на API:
        //1.создание пользователя
        CreateUserRequest user = AdminSteps.createUser();

        //2.создаем два аккаунта(счета)
        CreateAccountResponse account1 = UserSteps.createAccount(user);
        CreateAccountResponse account2 = UserSteps.createAccount(user);

        //проверяем, что массив не пустой и в нем есть наши 2 аккаунта
        CreateAccountResponse[] existingUserAccounts = given()
                .spec(RequestSpecs.authAsUser(user.getUsername(), user.getPassword()))
                .get("http://localhost:4111/api/v1/customer/accounts")
                .then().assertThat()
                .extract().as(CreateAccountResponse[].class);

        assertThat(existingUserAccounts).hasSize(2);

        CreateAccountResponse createdAccount1 = existingUserAccounts[0];
        assertThat(createdAccount1).isNotNull();
        assertThat(createdAccount1.getBalance()).isZero();

        //проверяем второй аккаунт
        CreateAccountResponse createdAccount2 = existingUserAccounts[1];
        assertThat(createdAccount2).isNotNull();
        assertThat(createdAccount2.getBalance()).isZero();

        // депозит денег на первый счет
        depositMoney(account1.getId(), MAX_DEPOSIT, user);

        //проверка, что деньги зачислены
        GetAccountsResponse[] updateAccounts = UserSteps.getAccounts(user);
        GetAccountsResponse updateAcc2 = Arrays.stream(updateAccounts).filter(acc -> acc.getId() == account1.getId()).findAny().orElseThrow();
        assertThat(updateAcc2.getBalance()).isEqualTo((float) MAX_DEPOSIT);

        //сохраняем токен для LocalStorage
        String userAuthHeader = new CrudRequester(
                RequestSpecs.unauthSpec(),
                Endpoint.LOGIN_USER,
                ResponseSpecs.requestReturnsOK())
                .post(LoginUserRequest.builder().username(user.getUsername()).password(user.getPassword()).build())
                .extract()
                .header("Authorization");
        //Вносим токен в LocalStorage
        Selenide.open("/");
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userAuthHeader);

        //Шаги:
        //1. Юзер нажал Make a Transfer
        Selenide.open("/transfer");
        $(Selectors.byText("\uD83D\uDD04 Make a Transfer")).shouldBe(Condition.visible);

        //2. Нажал Select Your Account и выбрал аккаунт с которого отправляет
        $$(".account-selector option")
                .findBy(Condition.text(Double.toString(MAX_DEPOSIT)))
                .click();

        //3. Нажал Recipient Name: и написал имя получателя(пусть имя будет номером аккаунта)
        $(Selectors.byAttribute("placeholder", "Enter recipient name"))
                .sendKeys(account2.getAccountNumber());
        //4. Нажал Recipient Account Number: и написал номер аккаунта получателя
        $(Selectors.byAttribute("placeholder", "Enter recipient account number"))
                .sendKeys(account2.getAccountNumber());
        //5. Нажал Amount: Вписал сумму для перевода
        $(Selectors.byAttribute("placeholder", "Enter amount"))
                .sendKeys(Double.toString(INVALID_TRANSFER));
        //6. Поставил галочку Confirm details are correct
        $(Selectors.byAttribute("id", "confirmCheck"))
                .click();
        //7. Нажал Send Transfer
        $$("button")
                .findBy(Condition.text("\uD83D\uDE80 Send Transfer"))
                .click();

        //Ожидания:
        //1. Проверка, что деньги не перевелись на второй аккаунт на UI(алерт)
        Alert alert = switchTo().alert();
        assertThat(alert.getText()).contains("❌ Error: Transfer amount must be at least 0.01");
        alert.accept();

        //2. Проверка после перевода, что баланс второго аккаунта остался равен 0 на API
        GetAccountsResponse[] updateAccountsAfterTransfer = UserSteps.getAccounts(user);
        GetAccountsResponse updateAcc2AfterTransfer = Arrays.stream(updateAccountsAfterTransfer).filter(acc -> acc.getId() == account2.getId()).findAny().orElseThrow();
        assertThat(updateAcc2AfterTransfer.getBalance()).isZero();

        //баланс первого аккаунта остался прежний после неудачного перевода
        GetAccountsResponse updateAcc1AfterTransfer = Arrays.stream(updateAccountsAfterTransfer).filter(acc -> acc.getId() == account1.getId()).findAny().orElseThrow();
        assertThat(updateAcc1AfterTransfer.getBalance()).isEqualTo((float) MAX_DEPOSIT);

    }

}