package iteration_1.ui;

import api.generators.RandomData;
import api.models.GetAllUsersResponse;
import api.requests.steps.AdminSteps;
import com.codeborne.selenide.*;
import api.generators.RandomModelGenerator;
import api.models.CreateUserRequest;
import api.models.comparison.ModelAssertions;
import org.junit.jupiter.api.Test;
import ui.pages.AdminPanelPage;
import ui.pages.BankAlerts;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateUserTest extends BaseTestUI {
    @Test
    public void adminCanCreateUserTest() {
        // ШАГ 1: админ залогинился в банке
        CreateUserRequest admin = CreateUserRequest.getAdmin();

        //авторизация через локалсторадж
        authAsUser(admin);

        //ШАГ 2: админ создает пользователя в банке
        CreateUserRequest newUser = RandomModelGenerator.generate(CreateUserRequest.class);

        //ШАГ 3: проверка, что есть алерт "User created successfully!"
        //в нем же ШАГ 4: проверка, что пользователь отображается на UI
        new AdminPanelPage().open().createUser(newUser).checkAlertMessageAndAccept(BankAlerts.USER_CREATED_SUCCESSFULLY.getMessage())
                .getAllUsers().findBy(Condition.exactText(newUser.getUsername() + "\n" + AdminPanelPage.USER_ROLE)).shouldBe(Condition.visible);

        //ШАГ 5: проверка, что пользователь был создан на API
        GetAllUsersResponse createUser = Arrays.stream(AdminSteps.getAllUsers())
                .filter(user -> user.getUsername().equals(newUser.getUsername()))
                .findFirst().get();

        ModelAssertions.assertThatModels(newUser, createUser).match();
    }

    @Test
    public void adminCanNotCreateUserWithInvalidDataTest() {
        // ШАГ 1: админ залогинился в банке
        CreateUserRequest admin = CreateUserRequest.getAdmin();

        authAsUser(admin);

        //ШАГ 2: админ создает пользователя в банке
        CreateUserRequest newUser = RandomModelGenerator.generate(CreateUserRequest.class);
        newUser.setUsername(RandomData.getUsernameWithLength(1));

        //ШАГ 3: проверка, что есть алерт и что нет нашего юзера
        new AdminPanelPage().open().createUser(newUser)
                .checkAlertMessageAndAccept(BankAlerts.USERNAME_MUST_BE_BETWEEN_3_AND_15_CHARACTERS.getMessage())
                .getAllUsers().findBy(Condition.exactText(newUser.getUsername() + "\nUSER")).shouldNotBe(Condition.exist);

        //ШАГ 5: проверка, что пользователь НЕ был создан на API
        long usersWithUsernameAsNewUser = Arrays.stream(AdminSteps.getAllUsers())
                .filter(user -> user.getUsername().equals(newUser.getUsername()))
                .count();

        assertThat(usersWithUsernameAsNewUser).isZero();
    }
}
