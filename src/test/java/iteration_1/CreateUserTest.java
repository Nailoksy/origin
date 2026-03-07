package iteration_1;

import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;

public class CreateUserTest extends StepsBeforeTest {

    public static Stream<Arguments> userValidData() {
        return Stream.of(
                Arguments.of("a1._-", "Password33$", "USER"),
                Arguments.of("Kate2003", "Kate2000#", "USER")
        );
    }

    @MethodSource("userValidData")
    @ParameterizedTest
    public void adminCanCreatedUserWithCorrectDataTest(String username, String password, String role) {
        String requestBody = String.format("""
                {
                  "username": "%s",
                  "password": "%s",
                  "role": "%s"
                }""", username, password, role);
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic YWRtaW46YWRtaW4=")
                .body(requestBody)
                .post("http://localhost:4111/api/v1/admin/users")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .body("username", Matchers.equalTo(username))
                .body("password", Matchers.not(Matchers.equalTo(password)))
                .body("role", Matchers.equalTo(role));
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
        String requestBody = String.format("""
                {
                  "username": "%s",
                  "password": "%s",
                  "role": "%s"
                }""", username, password, role);

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic YWRtaW46YWRtaW4=")
                .body(requestBody)
                .post("http://localhost:4111/api/v1/admin/users")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                //.body(errorKey, Matchers.equalTo(errorValue)); //не работает,тк в первом варианте аргументов массив строк в теле ответа
                .body(errorKey,
                        Matchers.containsInAnyOrder(errorValue.toArray()));
    }
}
