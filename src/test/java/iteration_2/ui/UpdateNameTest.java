package iteration_2.ui;

import api.generators.RandomData;
import com.codeborne.selenide.Selenide;

import common.annotation.Browsers;
import common.annotation.UserSession;
import common.storage.SessionStorage;
import common.utils.WaitUtils;
import iteration_1.ui.BaseTestUI;
import api.models.GetAllUsersResponse;
import org.junit.jupiter.api.Test;
import api.requests.steps.AdminSteps;
import org.junit.jupiter.api.parallel.ResourceAccessMode;
import ui.pages.BankAlerts;
import ui.pages.EditProfilePage;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;

public class UpdateNameTest extends BaseTestUI {
    private final String NEW_NAME_CORRECT = RandomData.generateCorrectName();
    private final String INVALID_NAME = RandomData.generateInvalidName();
    private final String DEFAULT_NAME = "Noname";

    @Test
    @Browsers({"chrome"})
    @UserSession
    @ResourceLock(value = Resources.SYSTEM_PROPERTIES, mode = ResourceAccessMode.READ_WRITE)
    public void userCanUpdateNameWithCorrectDataTest() {
        WaitUtils.sleep(WaitUtils.WAIT_FOR_UI);
        new EditProfilePage().open().updateName(NEW_NAME_CORRECT)
                .checkAlertMessageAndAccept(BankAlerts.NAME_UPDATED_SUCCESSFULLY.getMessage());

        Selenide.refresh();
        WaitUtils.sleep(WaitUtils.WAIT_FOR_UI);

        new EditProfilePage().checkUserName(NEW_NAME_CORRECT);
        WaitUtils.sleep(WaitUtils.WAIT_FOR_UI);

        //Проверка, что имя изменилось на API
        GetAllUsersResponse updateUser = Arrays.stream(AdminSteps.getAllUsers()).filter(u -> u.getUsername().equals(SessionStorage.getUser().getUsername())).findFirst().orElseThrow();
        assertThat(updateUser.getName())
                .isEqualTo(NEW_NAME_CORRECT);
    }

    @Test
    @Browsers({"chrome"})
    @UserSession
    public void userCanNotUpdateNameWithInvalidDataTest() {
        WaitUtils.sleep(WaitUtils.WAIT_FOR_UI);
        //сохраняем данные пользователя до изменения имени
        GetAllUsersResponse beforeName = Arrays.stream(AdminSteps.getAllUsers())
                .filter(u -> u.getUsername().equals(SessionStorage.getUser().getUsername()))
                .findFirst()
                .orElseThrow();

        WaitUtils.sleep(WaitUtils.WAIT_FOR_UI);

        //ШАГИ ТЕСТА
        new EditProfilePage().open().updateName(INVALID_NAME)
                .checkAlertMessageAndAccept(BankAlerts.NAME_MUST_BE_CONTAIN_TWO_WORDS.getMessage());

        //проверка, что имя не изменилось на UI
        Selenide.refresh();
        WaitUtils.sleep(WaitUtils.WAIT_FOR_UI);

        new EditProfilePage().checkUserName(DEFAULT_NAME);
        WaitUtils.sleep(WaitUtils.WAIT_FOR_UI);

        //Проверка, что имя не изменилось на API
        GetAllUsersResponse updateUser = Arrays.stream(AdminSteps.getAllUsers()).filter(u -> u.getUsername().equals(SessionStorage.getUser().getUsername())).findFirst().orElseThrow();
        assertThat(updateUser.getName()).isEqualTo(beforeName.getName());
    }
}
