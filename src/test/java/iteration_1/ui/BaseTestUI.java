package iteration_1.ui;

import api.configs.Config;
import api.models.CreateUserRequest;
import com.codeborne.selenide.Configuration;
import api.models.GetAllUsersResponse;
import com.codeborne.selenide.Selenide;
import iteration_1.api.BaseTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import api.requests.skelethon.requests.CrudRequester;
import api.requests.skelethon.requests.Endpoint;
import api.requests.steps.AdminSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

import java.util.Arrays;
import java.util.Map;

import static com.codeborne.selenide.Selenide.executeJavaScript;

public class BaseTestUI extends BaseTest {
    @BeforeAll
    public static void setupSelenoid() {
        Configuration.remote = Config.getProperty("uiRemote");
        Configuration.baseUrl = Config.getProperty("uiBaseUrl");
        Configuration.browser = Config.getProperty("uiBrowser");
        Configuration.browserSize = Config.getProperty("uiBrowserSize");

        Configuration.browserCapabilities.setCapability("selenoid:options",
                Map.of("enableVNC", true, "enableLog", true)
        );
    }

    //положить токен в локалСторадж
    public void authAsUser(String username, String password) {
        Selenide.open("/login");
        String userAuthHeader = RequestSpecs.getUserAuthHeader(username, password);
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userAuthHeader);
    }
    //положить токен в локалСторадж - перегрузка, чтобы передать реквест
    public void authAsUser(CreateUserRequest createUserRequest) {
        authAsUser(createUserRequest.getUsername(), createUserRequest.getPassword());
    }
}
