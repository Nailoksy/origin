package requests.skelethon.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import models.*;
import models.DeleteUserRequest;

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
            "admin/users/{id}",
            DeleteUserRequest.class,
            BaseModel.class
    );

    private final String url;
    private final Class<? extends BaseModel> requestModel;
    private final Class<? extends BaseModel> responseModel;
}
