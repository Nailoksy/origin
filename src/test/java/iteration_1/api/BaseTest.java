package iteration_1.api;

import api.models.GetAllUsersResponse;
import common.extensions.RetryOnSystemFailureExtension;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import api.requests.skelethon.requests.CrudRequester;
import api.requests.skelethon.requests.Endpoint;
import api.requests.steps.AdminSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

import java.util.Arrays;

@ExtendWith(RetryOnSystemFailureExtension.class)
public class BaseTest {
    protected SoftAssertions softly;

    @BeforeEach
    public void setupTest() {
        this.softly = new SoftAssertions();
    }

    @AfterEach
    public void afterTest(){
        softly.assertAll();
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
