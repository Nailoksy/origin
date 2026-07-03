package iteration_1.ui;

import api.models.CreateUserRequest;
import common.annotation.Browsers;
import common.annotation.UserSession;
import common.storage.SessionStorage;
import org.junit.jupiter.api.Test;
import ui.pages.AdminPanelPage;
import ui.pages.LoginPage;
import ui.pages.UserDashboardPage;

public class LoginUserTest extends BaseTestUI {
    @Test
    @Browsers({"chrome"})
    public void adminCanLoginWithCorrectDataTest() {
        CreateUserRequest admin = CreateUserRequest.getAdmin();
        new LoginPage()
                .open()
                .login(admin.getUsername(), admin.getPassword())
                .getPage(AdminPanelPage.class)
                .checkAdminPanelOpened();
    }

    @Test
    @Browsers({"chrome"})
    @UserSession
    public void userCanLoginWithCorrectDataTest() {
        CreateUserRequest user = SessionStorage.getUser();

        new LoginPage().open().login(user.getUsername(), user.getPassword())
                .getPage(UserDashboardPage.class)
                .checkUserDashboardPageOpened();
    }
}
