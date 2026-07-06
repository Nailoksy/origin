package api.specs;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;

import java.util.List;

public class ResponseSpecs {
    private ResponseSpecs() {}

    private static ResponseSpecBuilder defaultResponseBuilder() {
        return new ResponseSpecBuilder();
    }

    public static final String AUTHORIZATION_HEADER = "Authorization";

    //без асерта, для удаления
    public static ResponseSpecification noValidation() {
        return defaultResponseBuilder()
                .build();
    }

    //200
    public static ResponseSpecification requestReturnsOK() {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_OK)
                .build();
    }

    //201
    public static ResponseSpecification entityWasCreated() {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_CREATED)
                .build();
    }

    //400
    public static ResponseSpecification requestReturnsBadRequest(String errorKey, List<String> errorValue) {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_BAD_REQUEST)
                .expectBody(errorKey, Matchers.containsInAnyOrder(errorValue.toArray()))
                .build();
    }

    //400
    public static ResponseSpecification requestReturnsBadStringRequest(String errorMessage) {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_BAD_REQUEST)
                .build();
    }

    //403
    public static ResponseSpecification requestReturnsForbidden() {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_FORBIDDEN)
                .build();
    }

    public static ResponseSpecification returnsOkAndAuthHeader() {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_OK)
                //пришлось вручную добавить тип иначе компилятор не пропускал
                .expectHeader(AUTHORIZATION_HEADER, (org.hamcrest.Matcher) Matchers.notNullValue())
                .build();
    }
}
