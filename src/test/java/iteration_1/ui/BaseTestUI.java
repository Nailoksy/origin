package iteration_1.ui;

import com.codeborne.selenide.Configuration;
import models.GetAllUsersResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import requests.skelethon.requests.CrudRequester;
import requests.skelethon.requests.Endpoint;
import requests.steps.AdminSteps;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.util.Arrays;
import java.util.Map;

public class BaseTestUI {
    @BeforeAll
    public static void setupSelenoid() {
        Configuration.remote = "http://localhost:4444/wd/hub";
        Configuration.baseUrl = "http://192.168.1.240:3000";
        Configuration.browser = "chrome";
        Configuration.browserSize = "1920x1080";

        Configuration.browserCapabilities.setCapability("selenoid:options",
                Map.of("enableVNC", true, "enableLog", true)
        );
    }

    @AfterAll
    public static void cleanAllUsers() {
        GetAllUsersResponse[] users = AdminSteps.getAllUsers();

        // Удаляем пользователей по id
        Arrays.stream(users).filter(user -> user.getUsername().startsWith("TestUser_"))
                .map(GetAllUsersResponse::getId)
                .forEach(id -> new CrudRequester(
                        RequestSpecs.adminSpec(),
                        Endpoint.DELETE,
                        ResponseSpecs.requestReturnsOK())
                        .delete(id));
        System.out.println("Тестовый пользователь удален");
    }
}
