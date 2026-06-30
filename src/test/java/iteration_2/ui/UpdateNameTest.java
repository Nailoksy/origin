package iteration_2.ui;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;

import common.annotation.Browsers;
import common.annotation.UserSession;
import common.storage.SessionStorage;
import iteration_1.ui.BaseTestUI;
import api.models.GetAllUsersResponse;
import org.junit.jupiter.api.Test;
import api.requests.steps.AdminSteps;
import org.junit.jupiter.api.parallel.ResourceAccessMode;
import ui.pages.BankAlerts;
import ui.pages.EditProfilePage;

import java.util.Arrays;

import static com.codeborne.selenide.Selenide.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;

public class UpdateNameTest extends BaseTestUI {
    private static final String NEW_NAME_CORRECT = "New Name";

    @Test
    @Browsers({"chrome"})
    @UserSession
    @ResourceLock(value = Resources.SYSTEM_PROPERTIES, mode = ResourceAccessMode.READ_WRITE)
    public void userCanUpdateNameWithCorrectDataTest() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        new EditProfilePage().open().updateName(NEW_NAME_CORRECT)
                .checkAlertMessageAndAccept(BankAlerts.NAME_UPDATED_SUCCESSFULLY.getMessage());

        Selenide.refresh();
//        $(".user-name")
//                .shouldBe(Condition.visible)
//                .shouldHave(Condition.exactText(NEW_NAME_CORRECT),
//                        Duration.ofSeconds(10));

        $(".user-name")
                .shouldHave(Condition.exactText(NEW_NAME_CORRECT));

        System.out.println("Всего всех юзеров " + AdminSteps.getAllUsers().length);
        //Проверка, что имя изменилось на API
        GetAllUsersResponse updateUser = Arrays.stream(AdminSteps.getAllUsers()).filter(u -> u.getUsername().equals(SessionStorage.getUser().getUsername())).findFirst().orElseThrow();
        assertThat(updateUser.getName())
                .isEqualTo(NEW_NAME_CORRECT);
    }

    @Test
    @Browsers({"chrome"})
    @UserSession
    public void userCanNotUpdateNameWithInvalidDataTest() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //сохраняем данные пользователя до изменения имени
        GetAllUsersResponse beforeName = Arrays.stream(AdminSteps.getAllUsers())
                .filter(u -> u.getUsername().equals(SessionStorage.getUser().getUsername()))
                .findFirst()
                .orElseThrow();

        //ШАГИ ТЕСТА
        new EditProfilePage().open().updateName(NEW_NAME_CORRECT.replaceAll(" ", ""))
                .checkAlertMessageAndAccept(BankAlerts.NAME_MUST_BE_CONTAIN_TWO_WORDS.getMessage());

        //проверка, что имя не изменилось на UI
        Selenide.refresh();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        $(".user-name")
                .shouldHave(Condition.exactText("Noname"));
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        //Проверка, что имя не изменилось на API
        GetAllUsersResponse updateUser = Arrays.stream(AdminSteps.getAllUsers()).filter(u -> u.getUsername().equals(SessionStorage.getUser().getUsername())).findFirst().orElseThrow();
        assertThat(updateUser.getName()).isEqualTo(beforeName.getName());
    }

//    @Test
//    public void delete (){
//        AdminSteps.deleteAllUsers();
//    }
}
