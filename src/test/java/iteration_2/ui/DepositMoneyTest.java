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


import static com.codeborne.selenide.Selenide.*;
import static io.restassured.RestAssured.given;
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
        CreateAccountResponse[] existingUserAccounts = given()
                .spec(RequestSpecs.authAsUser(user.getUsername(), user.getPassword()))
                .get("http://localhost:4111/api/v1/customer/accounts")
                .then().assertThat()
                .extract().as(CreateAccountResponse[].class);

        assertThat(existingUserAccounts).hasSize(1);

        CreateAccountResponse createdAccount = existingUserAccounts[0];

        assertThat(createdAccount).isNotNull();
        assertThat(createdAccount.getBalance()).isZero();

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

    //ШАГИ ТЕСТА
        //1. Юзер перешел на страницу Deposit
        Selenide.open("/deposit");

        //2. Юзер Нажал на выбор аккаунта Select Account и выбрал первый созданный аккаунт:
        $(".account-selector").selectOption(1);

        //3. Юзер нажал Enter Amount: и написал сумму депозита до 5000
        $(Selectors.byAttribute("placeholder", "Enter amount"))
                .sendKeys(Double.toString(AMOUNT_FOR_DEPOSIT));
        //находим все кнопки, ищем по имени депозит, клик.
        $$("button")
                .findBy(Condition.text("Deposit"))
                .click();

        //проверка, что деньги внеслись на UI(проверка алерта)
        Alert alert = switchTo().alert();
        assertThat(alert.getText()).contains("✅ Successfully deposited");
        alert.accept();

        //Проверка, что баланс соответствует на API
        GetAccountsResponse[] updateAccounts = UserSteps.getAccounts(user);
        double balanceAfterDeposit = updateAccounts[0].getBalance();
        assertThat(balanceAfterDeposit).isEqualTo(AMOUNT_FOR_DEPOSIT);


    }

    @Test
    public void userCanNotDepositHisAccountWithInvalidAmountTest() {
    //ПРЕДШАГИ на API:
        //1.создание пользователя
        CreateUserRequest user = AdminSteps.createUser();

        //2.создаем аккаунт(счет)
        CreateAccountResponse createAccount = UserSteps.createAccount(user);

        //проверяем, что массив не пустой и в нем есть наш аккаунт, сравниваем аккаунты
        CreateAccountResponse[] existingUserAccounts = given()
                .spec(RequestSpecs.authAsUser(user.getUsername(), user.getPassword()))
                .get("http://localhost:4111/api/v1/customer/accounts")
                .then().assertThat()
                .extract().as(CreateAccountResponse[].class);

        assertThat(existingUserAccounts).hasSize(1);

        CreateAccountResponse createdAccount = existingUserAccounts[0];

        assertThat(createdAccount).isNotNull();
        assertThat(createdAccount.getBalance()).isZero();

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

    //ШАГИ ТЕСТА
        //1. Юзер перешел на страницу Deposit
        Selenide.open("/deposit");

        //2. Юзер Нажал на выбор аккаунта Select Account и выбрал первый созданный аккаунт:
        $(".account-selector").selectOption(1);

        //3. Юзер нажал Enter Amount: и написал сумму депозита более 5000
        $(Selectors.byAttribute("placeholder", "Enter amount"))
                .sendKeys(Double.toString(AMOUNT_FOR_DEPOSIT + TOTAL_DEPOSIT));
        //находим все кнопки, ищем по имени депозит, клик.
        $$("button")
                .findBy(Condition.text("Deposit"))
                .click();

        //проверка, что деньги не внеслись на UI(проверка алерта)
        Alert alert = switchTo().alert();
        assertThat(alert.getText()).contains("❌ Please deposit less or equal to 5000$.");
        alert.accept();

        //Проверка, что баланс соответствует на API
        GetAccountsResponse[] updateAccounts = UserSteps.getAccounts(user);
        double balanceAfterDeposit = updateAccounts[0].getBalance();
        assertThat(balanceAfterDeposit).isEqualTo(createAccount.getBalance());


    }
}
