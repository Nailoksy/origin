package api.specs;

import api.configs.Config;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import api.models.LoginUserRequest;
import api.requests.skelethon.requests.CrudRequester;
import api.requests.skelethon.requests.Endpoint;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestSpecs {
    private static final ConcurrentHashMap<String, String> authHeaders =
        new ConcurrentHashMap<>(
                Map.of("admin", "Basic YWRtaW46YWRtaW4=")
        );

    private RequestSpecs() {
    }

    private static RequestSpecBuilder defaultRequestBuilder() {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilters(
                        List.of(
                                new RequestLoggingFilter(),
                                new ResponseLoggingFilter()
                        )
                )
                .setBaseUri(
                        Config.getProperty("apiBaseUrl")
                                + Config.getProperty("apiVersion")
                );
    }

    public static RequestSpecification unauthSpec() {
        return defaultRequestBuilder().build();
    }

    public static RequestSpecification adminSpec() {
        return defaultRequestBuilder()
                .addHeader("Authorization", authHeaders.get("admin"))
                .build();
    }

    public static RequestSpecification authAsUser(String username, String password) {
        return defaultRequestBuilder()
                .addHeader(
                        "Authorization",
                        getUserAuthHeader(username, password)
                )
                .build();
    }

    public static String getUserAuthHeader(String username, String password) {
        String token = authHeaders.get(username);
        if (token == null) {
            synchronized (RequestSpecs.class) {
                token = authHeaders.get(username);
                if (token == null) {
                    token = new CrudRequester(
                            RequestSpecs.unauthSpec(),
                            Endpoint.LOGIN_USER,
                            ResponseSpecs.requestReturnsOK()
                    )
                            .post(LoginUserRequest.builder()
                                    .username(username)
                                    .password(password)
                                    .build())
                            .extract()
                            .header("Authorization");
                    authHeaders.put(username, token);
                }
            }
        }
        return token;
    }
}
