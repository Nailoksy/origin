package iteration_1.ui;

import api.generators.RandomData;
import api.models.GetAllUsersResponse;
import api.requests.steps.AdminSteps;
import api.generators.RandomModelGenerator;
import api.models.CreateUserRequest;
import api.models.comparison.ModelAssertions;
import common.annotation.AdminSession;
import common.annotation.Browsers;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import ui.elements.UserBage;
import ui.pages.AdminPanelPage;
import ui.pages.BankAlerts;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CreateUserTest extends BaseTestUI {
    @Test
    @Browsers({"chrome"})
    @AdminSession
    public void adminCanCreateUserTest() {
        //ШАГ 2: админ создает пользователя в банке
        CreateUserRequest newUser = RandomModelGenerator.generate(CreateUserRequest.class);

        //ШАГ 3: проверка, что есть алерт "User created successfully!"
        //в нем же ШАГ 4: проверка, что пользователь отображается на UI
        UserBage newUserBage = new AdminPanelPage().open().createUser(newUser).checkAlertMessageAndAccept(BankAlerts.USER_CREATED_SUCCESSFULLY.getMessage())
                .findUserByUsername(newUser.getUsername());

        assertThat(newUserBage).as("UserBage should exist on Dashboard after user creation").isNotNull();


        //ШАГ 5: проверка, что пользователь был создан на API
        GetAllUsersResponse createUser = Arrays.stream(AdminSteps.getAllUsers())
                .filter(user -> user.getUsername().equals(newUser.getUsername()))
                .findFirst().get();

        ModelAssertions.assertThatModels(newUser, createUser).match();
        AdminSteps.deleteUserById(createUser.getId());
    }

    @Test
    @Browsers({"chrome"})
    @AdminSession //логинимся как админ
    public void adminCanNotCreateUserWithInvalidDataTest() {
        //ШАГ 2: админ создает пользователя в банке
        CreateUserRequest newUser = RandomModelGenerator.generate(CreateUserRequest.class);
        newUser.setUsername(RandomData.getUsernameWithLength(1));

        //ШАГ 3: проверка, что есть алерт и что нет нашего юзера
        assertTrue(new AdminPanelPage().open().createUser(newUser)
                .checkAlertMessageAndAccept(BankAlerts.USERNAME_MUST_BE_BETWEEN_3_AND_15_CHARACTERS.getMessage())
                .getAllUsers().stream().noneMatch(userBage -> userBage.getUsername().equals(newUser.getUsername())));

        //ШАГ 5: проверка, что пользователь НЕ был создан на API
        long usersWithUsernameAsNewUser = Arrays.stream(AdminSteps.getAllUsers())
                .filter(user -> user.getUsername().equals(newUser.getUsername()))
                .count();

        assertThat(usersWithUsernameAsNewUser).isZero();
    }
}
