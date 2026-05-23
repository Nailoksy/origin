package api.models;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetAllUsersResponse extends BaseModel{
    private long id;
    private String username;
    private String password;
    private String name;
    private String role;
    private List<Account> accounts;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Account {
        private long id;
        private String accountNumber;
        private double balance;

        private List<Transaction> transactions;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Transaction {
        private long id;
        private double amount;
        private String type;
        private String timestamp;
        private long relatedAccountId;
    }
}
