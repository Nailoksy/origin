package iteration_2.ui;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;

import iteration_1.ui.BaseTestUI;
import api.models.CreateUserRequest;
import api.models.GetAllUsersResponse;
import org.junit.jupiter.api.Test;
import api.requests.steps.AdminSteps;
import ui.pages.BankAlerts;
import ui.pages.EditProfilePage;

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
        authAsUser(user);

    //ШАГИ ТЕСТА
        //1. Юзер перешел на страницу изменения профиля
        new EditProfilePage().open().updateName(NEW_NAME_CORRECT)
                .checkAlertMessageAndAccept(BankAlerts.NAME_UPDATED_SUCCESSFULLY.getMessage());
        //Проверка, что имя изменилось на UI (рефреш не запихнулось в метод, поэтому проверка отдельно)
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
        authAsUser(user);

        //ШАГИ ТЕСТА
        //1. Юзер перешел на страницу изменения профиля
        //1. Юзер перешел на страницу изменения профиля
        new EditProfilePage().open().updateName(NEW_NAME_CORRECT.replaceAll(" ", ""))
                .checkAlertMessageAndAccept(BankAlerts.NAME_MUST_BE_CONTAIN_TWO_WORDS.getMessage());

        //проверка, что имя не изменилось на UI
        Selenide.refresh();
        $(".user-name")
                .shouldHave(Condition.exactText("Noname"));

        //Проверка, что имя не изменилось на API
        GetAllUsersResponse updateUser = Arrays.stream(AdminSteps.getAllUsers()).filter(u-> u.getUsername().equals(user.getUsername())).findFirst().orElseThrow();
        assertThat(updateUser.getName()).isEqualTo(beforeName.getName());
    }
}
