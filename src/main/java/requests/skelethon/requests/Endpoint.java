package requests.skelethon.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import models.*;

@Getter
@AllArgsConstructor
public enum Endpoint {
    ADMIN_USER(
            "admin/users",
            CreateUserRequest.class,
            CreateUserResponce.class),
    LOGIN_USER(
            "auth/login",
            LoginUserRequest.class,
            LoginUserResponce.class),
    CREATE_ACCOUNTS(
            "/accounts",
            BaseModel.class,
            CreateAccountResponce.class),
    GET_ACCOUNTS(
            "customer/accounts",
            BaseModel.class,
            GetAccountsResponce.class),
    DEPOSIT(
            "accounts/deposit",
            DepositRequest.class,
            DepositResponce.class
    ),
    TRANSFER(
            "accounts/transfer",
            TransferRequest.class,
            TransferResponce.class
    ),
    UPDATE(
            "customer/profile",
            UpdateNameRequest.class,
            UpdateNameResponce.class
    );

    private final String url;
    private final Class<? extends BaseModel> requestModel;
    private final Class<? extends BaseModel> responseModel;
}
