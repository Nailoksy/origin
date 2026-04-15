package iteration_1;

import models.GetAllUsersResponse;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import requests.skelethon.requests.CrudRequester;
import requests.skelethon.requests.Endpoint;
import requests.steps.AdminSteps;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
