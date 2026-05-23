package iteration_1.api;

import api.generators.RandomData;
import api.models.CreateUserRequest;
import api.models.CreateUserResponse;
import api.models.GetAllUsersResponse;
import api.models.comparison.ModelAssertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import api.requests.skelethon.requests.CrudRequester;
import api.requests.skelethon.requests.Endpoint;
import api.requests.skelethon.requests.ValidatedCrudRequester;
import api.requests.steps.AdminSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

import java.util.List;
import java.util.stream.Stream;

public class CreateUserTest extends BaseTest {

    public static Stream<Arguments> userValidData() {
        return Stream.of(
                Arguments.of(RandomData.generateMinValidUsername(), "Password33$", "USER"),
                Arguments.of(RandomData.generateMinValidUsername(), "Kate2000#", "USER")
        );
    }

    @MethodSource("userValidData")
    @ParameterizedTest
    public void adminCanCreatedUserWithCorrectDataTest(String username, String password, String role) {
        //создание пользователя
        CreateUserRequest createUserRequest = CreateUserRequest.builder()
                .username(username)
                .password(password)
                .role(role)
                .build();

//т.к позитивный(экстрактим createUserRequest), то берем ValidatedCrudRequester
        CreateUserResponse createUserResponse = new ValidatedCrudRequester<CreateUserResponse>
                (RequestSpecs.adminSpec(),
                Endpoint.ADMIN_USER,
                ResponseSpecs.entityWasCreated())
                .post(createUserRequest);

        ModelAssertions.assertThatModels(createUserRequest, createUserResponse).match();
    }

    public static Stream<Arguments> userInvalidData() {
        return Stream.of(
                //username field validation
                Arguments.of(" ", "Password33$", "USER", "username",
                        List.of(
                                "Username must be between 3 and 15 characters",
                                "Username cannot be blank",
                                "Username must contain only letters, digits, dashes, underscores, and dots"
                        )),
                Arguments.of("ab", "Password33$", "USER", "username", List.of(
                        "Username must be between 3 and 15 characters")),
                Arguments.of("abc$", "Password33$", "USER", "username", List.of(
                        "Username must contain only letters, digits, dashes, underscores, and dots")),
                Arguments.of("Kate.67891123456", "Password33$", "USER", "username", List.of(
                        "Username must be between 3 and 15 characters")),
                Arguments.of("abc%", "Password33$", "USER", "username", List.of(
                        "Username must contain only letters, digits, dashes, underscores, and dots")),
                Arguments.of("@$%^&*()#", "Password33$", "USER", "username", List.of(
                        "Username must contain only letters, digits, dashes, underscores, and dots")),

                //password field validation
                Arguments.of("Kate_1991", "", "USER", "password", List.of(
                        "Password cannot be blank", "Password must contain " +
                                "at least one digit, one lower case, one upper case, one special character," +
                                " no spaces, and be at least 8 characters long")),
                Arguments.of("Kate_1991", "1234567", "USER", "password", List.of(
                        "Password must contain at least one digit, one lower case, one upper case," +
                                " one special character, no spaces, and be at least 8 characters long")),
                Arguments.of("Kate_1991", "KATE1997@", "USER", "password", List.of(
                        "Password must contain at least one digit, one lower case, one upper case," +
                                " one special character, no spaces, and be at least 8 characters long")),
                Arguments.of("Kate_1991", "kate1997@", "USER", "password", List.of(
                        "Password must contain at least one digit, one lower case, one upper case," +
                                " one special character, no spaces, and be at least 8 characters long")),
                Arguments.of("Kate_1991", "kate19977", "USER", "password", List.of(
                        "Password must contain at least one digit, one lower case, one upper case," +
                                " one special character, no spaces, and be at least 8 characters long")),
                Arguments.of("Kate_1991", "kate 19977", "USER", "password", List.of(
                        "Password must contain at least one digit, one lower case, one upper case," +
                                " one special character, no spaces, and be at least 8 characters long")),

                //role field validation
                Arguments.of("Kate1994", "Password33$", "EDITOR", "role", List.of("Role must be either 'ADMIN' or 'USER'"))

        );
    }

    @MethodSource("userInvalidData")
    @ParameterizedTest
    public void adminCanNotCreatedUserWithInvalidDataTest(String username, String password, String role, String errorKey, List<String> errorValue) {
        CreateUserRequest createUserRequest = CreateUserRequest.builder()
                .username(username)
                .password(password)
                .role(role)
                .build();

        //негативный, поэтому берем CrudeRequester
        new CrudRequester(RequestSpecs.adminSpec(),
                Endpoint.ADMIN_USER,
                ResponseSpecs.requestReturnsBadRequest(errorKey, errorValue))
                .post(createUserRequest);

        //проверка, что юзер не создан
        GetAllUsersResponse[] allUsers = AdminSteps.getAllUsers();
        softly.assertThat(allUsers)
                .noneMatch(user -> user.getUsername().equals(username));  }
}
