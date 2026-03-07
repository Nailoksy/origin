package iteration_2;

import generators.RandomData;
import io.restassured.http.ContentType;
import iteration_1.BaseTest;
import models.CreateUserRequest;
import models.UpdateNameRequest;
import models.UserRole;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import requests.AdminCreateUserRequester;
import requests.UpdateNameRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.util.stream.Stream;

import static io.restassured.RestAssured.given;

public class UpdateNameTest extends BaseTest {


    @ParameterizedTest
    @ValueSource(strings = {"New Name", "new name"})
    public void userCanUpdateNameWithCorrectData(String name) {
        //создание пользователя
        CreateUserRequest userRequest = CreateUserRequest.builder()
                .username(RandomData.getUsername())
                .password(RandomData.getPassword())
                .role(UserRole.USER.toString())
                .build();

        new AdminCreateUserRequester(
                RequestSpecs.adminSpec(),
                ResponseSpecs.entityWasCreated())
                .post(userRequest);

        UpdateNameRequest nameRequest = UpdateNameRequest.builder()
                .name(name)
                .build();

        new UpdateNameRequester(RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.requestReturnsOK())
                .put(nameRequest)
                .body("customer.name", Matchers.equalTo(name));;


    }
    private static Stream<Arguments> invalidDepositData() {
        return Stream.of(
                Arguments.of("NewName", "Name must contain two words with letters only"),
                Arguments.of("Name", "Name must contain two words with letters only"),
                Arguments.of("New Name Name", "Name must contain two words with letters only"),
                Arguments.of("123 412", "Name must contain two words with letters only")
        );
    }

    @ParameterizedTest
    @MethodSource("invalidDepositData")
    public void userCanNotUpdateNameWithInvalidData(String name, String errorMessage) {
        //создание пользователя
        CreateUserRequest userRequest = CreateUserRequest.builder()
                .username(RandomData.getUsername())
                .password(RandomData.getPassword())
                .role(UserRole.USER.toString())
                .build();

        new AdminCreateUserRequester(
                RequestSpecs.adminSpec(),
                ResponseSpecs.entityWasCreated())
                .post(userRequest);

        //изменение имени
        UpdateNameRequest nameRequest = UpdateNameRequest.builder()
                .name(name)
                .build();

        new UpdateNameRequester(RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.requestReturnsBadStringRequest(errorMessage))
                .put(nameRequest)
                .body(Matchers.equalTo(errorMessage));;
    }


}
