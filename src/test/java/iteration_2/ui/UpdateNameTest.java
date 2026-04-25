package iteration_2.ui;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;

import iteration_1.ui.BaseTestUI;
import models.CreateUserRequest;
import models.GetAllUsersResponse;
import models.LoginUserRequest;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import requests.skelethon.requests.CrudRequester;
import requests.skelethon.requests.Endpoint;
import requests.steps.AdminSteps;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.util.Arrays;

import static com.codeborne.selenide.Selenide.*;
import static org.assertj.core.api.Assertions.assertThat;

public class UpdateNameTest extends BaseTestUI {
    private static final String NEW_NAME_CORRECT = "New Name";
    @Test
    public void userCanUpdateNameWithCorrectDataTest() {
    //Предшаги:
        //1. Админ залогинился
        //2. Админ создал юзера
        CreateUserRequest user = AdminSteps.createUser();
        //3. Админ выходит из аккаунта
        //4. Юзер залогинился
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
        //1. Юзер перешел на страницу изменения профиля
        Selenide.open("/edit-profile");
        $(Selectors.byText("✏\uFE0F Edit Profile")).shouldBe(Condition.visible);

        //2. Юзер ввел корректное имя
        $(Selectors.byAttribute("placeholder", "Enter new name")).sendKeys(NEW_NAME_CORRECT);

        //3. Нажал Save Changes
        $$("button")
                .findBy(Condition.text("\uD83D\uDCBE Save Changes"))
                .click();

        //Проверка, что имя изменилось на UI (алерт)
        Alert alert = switchTo().alert();
        assertThat(alert.getText()).contains("✅ Name updated successfully!");
        alert.accept();

        Selenide.refresh();
        $(".user-name")
                .shouldHave(Condition.exactText(NEW_NAME_CORRECT));

        //Проверка, что имя изменилось на API
        GetAllUsersResponse updateUser = Arrays.stream(AdminSteps.getAllUsers()).filter(u-> u.getUsername().equals(user.getUsername())).findFirst().orElseThrow();
        assertThat(updateUser.getName())
                .isEqualTo(NEW_NAME_CORRECT);

    }

    @Test
    public void userCanNotUpdateNameWithInvalidDataTest() {
        //Предшаги:
        //1. Админ залогинился
        //2. Админ создал юзера
        CreateUserRequest user = AdminSteps.createUser();

        //сохраняем данные пользователя до изменения имени
        GetAllUsersResponse beforeName = Arrays.stream(AdminSteps.getAllUsers())
                .filter(u -> u.getUsername().equals(user.getUsername()))
                .findFirst()
                .orElseThrow();

        //3. Админ выходит из аккаунта
        //4. Юзер залогинился
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
        //1. Юзер перешел на страницу изменения профиля
        Selenide.open("/edit-profile");
        $(Selectors.byText("✏\uFE0F Edit Profile")).shouldBe(Condition.visible);

        //2. Юзер ввел некорректное имя
        $(Selectors.byAttribute("placeholder", "Enter new name")).sendKeys(NEW_NAME_CORRECT.replaceAll(" ", ""));

        //3. Нажал Save Changes
        $$("button")
                .findBy(Condition.text("\uD83D\uDCBE Save Changes"))
                .click();

        //Проверка, что имя не изменилось на UI (алерт)
        Alert alert = switchTo().alert();
        assertThat(alert.getText()).contains("Name must contain two words with letters only");
        alert.accept();

        Selenide.refresh();
        $(".user-name")
                .shouldHave(Condition.exactText("Noname"));

        //Проверка, что имя не изменилось на API
        GetAllUsersResponse updateUser = Arrays.stream(AdminSteps.getAllUsers()).filter(u-> u.getUsername().equals(user.getUsername())).findFirst().orElseThrow();
        assertThat(updateUser.getName()).isEqualTo(beforeName.getName());
    }
}
