package models;

import java.util.List;

public class GetAccountsResponse extends BaseModel{
    private long id;
    private String accountNumber;
    private float balance;
    private List<Object> transactions;
}
