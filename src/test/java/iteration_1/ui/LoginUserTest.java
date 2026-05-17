package iteration_1.ui;

import com.codeborne.selenide.Condition;
import api.models.CreateUserRequest;
import common.annotation.Browsers;
import org.junit.jupiter.api.Test;
import api.requests.steps.AdminSteps;
import ui.pages.AdminPanelPage;
import ui.pages.LoginPage;
import ui.pages.UserDashboardPage;

public class LoginUserTest extends BaseTestUI {
    @Test
    @Browsers({"chrome"})
    public void adminCanLoginWithCorrectDataTest() {
        CreateUserRequest admin = CreateUserRequest.getAdmin();

        new LoginPage().open().login(admin.getUsername(), admin.getPassword())
                .getPage(AdminPanelPage.class).getAdminPanelText().shouldBe(Condition.visible);
    }

    @Test
    @Browsers({"chrome"})
    public void userCanLoginWithCorrectDataTest() {
        CreateUserRequest user = AdminSteps.createUser();

        new LoginPage().open().login(user.getUsername(), user.getPassword())
                .getPage(UserDashboardPage.class).getWelcomeText()
                .shouldBe(Condition.visible).shouldHave(Condition.text(LoginPage.NONAME_WELCOME_TEXT));
    }
}
