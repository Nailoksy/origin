package api.requests.skelethon.requests;

import api.models.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Endpoint {
    ADMIN_USER(
            "admin/users",
            CreateUserRequest.class,
            CreateUserResponse.class),
    LOGIN_USER(
            "auth/login",
            LoginUserRequest.class,
            LoginUserResponse.class),
    CREATE_ACCOUNTS(
            "/accounts",
            BaseModel.class,
            CreateAccountResponse.class),
    GET_ACCOUNTS(
            "customer/accounts",
            BaseModel.class,
            GetAccountsResponse.class),
    DEPOSIT(
            "accounts/deposit",
            DepositRequest.class,
            DepositResponse.class
    ),
    TRANSFER(
            "accounts/transfer",
            TransferRequest.class,
            TransferResponse.class
    ),
    UPDATE(
            "customer/profile",
            UpdateNameRequest.class,
            UpdateNameResponse.class
    ),
    DELETE(
            //в эндпоинте еще добавлен {id}, но его вписала в CrudRequester
            "admin/users/",
            DeleteUserRequest.class,
            BaseModel.class
    ),
    ADMIN_GET_ALL_USERS(
            "admin/users",
            BaseModel.class,
            GetAllUsersResponse.class
    );

    private final String url;
    private final Class<? extends BaseModel> requestModel;
    private final Class<? extends BaseModel> responseModel;
}
