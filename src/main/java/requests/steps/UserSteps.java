package requests.steps;

import io.qameta.allure.Step;
import models.*;
import requests.skelethon.requests.CrudRequester;
import requests.skelethon.requests.Endpoint;
import requests.skelethon.requests.ValidatedCrudRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

public class UserSteps {
    @Step("Создание аккаунта юзером")
    public static CreateAccountResponse createAccount(CreateUserRequest userRequest) {
        return new ValidatedCrudRequester<CreateAccountResponse>(RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.CREATE_ACCOUNTS,
                ResponseSpecs.entityWasCreated())
                .post(null);
    }

    @Step("Получение аккаунтов юзера")
    public static GetAccountsResponse[] getAccounts(CreateUserRequest userRequest) {
        return new CrudRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.GET_ACCOUNTS,
                ResponseSpecs.requestReturnsOK())
                .getAll()
                .extract()
                .as(GetAccountsResponse[].class);
    }

    @Step("Депозит денег {amount} на {id}")
    public static DepositResponse depositMoney(long id, double amount, CreateUserRequest userRequest){
        DepositRequest depositRequest = DepositRequest.builder()
                .id(id)
                .balance(amount)
                .build();

        return new ValidatedCrudRequester<DepositResponse>(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsOK())
                .post(depositRequest);
    }

    @Step("Перевод денег {amountTransfer} с {senderAccountId} на {receiverAccountId}")
    public static TransferResponse transferMoney(long senderAccountId, long receiverAccountId, double amountTransfer, CreateUserRequest userRequest){
        TransferRequest transferRequest = TransferRequest.builder()
                .senderAccountId(senderAccountId)
                .receiverAccountId(receiverAccountId)
                .amount(amountTransfer)
                .build();

        return new ValidatedCrudRequester<TransferResponse>(RequestSpecs.authAsUser(userRequest.getUsername(),
                userRequest.getPassword()),
                Endpoint.TRANSFER,
                ResponseSpecs.requestReturnsOK())
                .post(transferRequest);
    }

    @Step("Обновление имени пользователя на имя {newName}")
    public  static UpdateNameResponse updateName(String newName, CreateUserRequest userRequest) {
        UpdateNameRequest nameRequest = UpdateNameRequest.builder()
                .name(newName)
                .build();

        return new ValidatedCrudRequester<UpdateNameResponse>(RequestSpecs.authAsUser(
                userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.UPDATE,
                ResponseSpecs.requestReturnsOK())
                .update(nameRequest);
    }
}
