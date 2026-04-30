package iteration_2.api;

import iteration_1.api.BaseTest;
import models.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import requests.skelethon.requests.CrudRequester;
import requests.skelethon.requests.Endpoint;
import requests.steps.AdminSteps;
import requests.steps.UserSteps;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.util.Arrays;
import java.util.stream.Stream;

public class UpdateNameTest extends BaseTest {
    @ParameterizedTest
    @ValueSource(strings = {"New Name", "new name"})
    public void userCanUpdateNameWithCorrectData(String name) {
        //создание пользователя
        CreateUserRequest createUser = AdminSteps.createUser();

        //обновление имени
        UpdateNameResponse updateNameResponse = UserSteps.updateName(name, createUser);

        //проверка, что имя обновлено
        softly.assertThat(updateNameResponse.getCustomer().getName()).as(
                "Check that user name was updated").isEqualTo(name);
    }

    private static Stream<Arguments> invalidNameData() {
        return Stream.of(
                Arguments.of("NewName", "Name must contain two words with letters only"),
                Arguments.of("Name", "Name must contain two words with letters only"),
                Arguments.of("New Name Name", "Name must contain two words with letters only"),
                Arguments.of("123 412", "Name must contain two words with letters only")
        );
    }

    @ParameterizedTest
    @MethodSource("invalidNameData")
    public void userCanNotUpdateNameWithInvalidData(String name, String errorMessage) {
        //создание пользователя
        CreateUserRequest createUser = AdminSteps.createUser();

        //сохраняем данные пользователя до изменения имени
        GetAllUsersResponse beforeName = Arrays.stream(AdminSteps.getAllUsers())
                .filter(user -> user.getUsername().equals(createUser.getUsername()))
                .findFirst()
                .orElseThrow();

        //изменение имени
        UpdateNameRequest nameRequest = UpdateNameRequest.builder()
                .name(name)
                .build();

         new CrudRequester(RequestSpecs.authAsUser(
                createUser.getUsername(), createUser.getPassword()),
                Endpoint.UPDATE,
                ResponseSpecs.requestReturnsBadStringRequest(errorMessage))
                .update(nameRequest);
        //запрашиваем данные пользователя после попытки изменения имени
         GetAllUsersResponse afterName = Arrays.stream(AdminSteps.getAllUsers())
                .filter(user -> user.getUsername().equals(createUser.getUsername()))
                .findFirst()
                .orElseThrow();

        // проверяем, что имя после неудачной попытки обновления не изменилось
        softly.assertThat(beforeName.getName())
                .isEqualTo(afterName.getName());



    }


}
