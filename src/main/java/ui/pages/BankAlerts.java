package ui.pages;

import lombok.Getter;

@Getter
public enum BankAlerts {
    USER_CREATED_SUCCESSFULLY("✅ User created successfully!"),
    USERNAME_MUST_BE_BETWEEN_3_AND_15_CHARACTERS("Username must be between 3 and 15 characters"),
    NEW_ACCOUNT_CREATED("✅ New Account Created! Account Number:"),
    SUCCESSFULLY_DEPOSITED("✅ Successfully deposited"),
    PLEASE_DEPOSIT_LESS_OR_EQUAL_TO_5000("❌ Please deposit less or equal to 5000$."),
    SUCCESSFULLY_TRANSFERRED("✅ Successfully transferred"),
    ERROR_TRANSFER_AMOUNT("❌ Error: Transfer amount must be at least 0.01"),
    NAME_UPDATED_SUCCESSFULLY("✅ Name updated successfully!"),
    NAME_MUST_BE_CONTAIN_TWO_WORDS("Name must contain two words with letters only");

    private final String message;
    BankAlerts(String message) {
        this.message = message;
    }
}
