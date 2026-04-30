package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@Getter
public class TransferMoneyPage extends BasePage <TransferMoneyPage>{

    private SelenideElement makeTransfer = $(Selectors.byText("\uD83D\uDD04 Make a Transfer"));
    private SelenideElement  accountSelector = $(".account-selector");
    private SelenideElement enterRecipientName = $(Selectors.byAttribute("placeholder", "Enter recipient name"));
    private SelenideElement enterRecipientAccountNumber = $(Selectors.byAttribute("placeholder", "Enter recipient account number"));
    private SelenideElement enterAmount = $(Selectors.byAttribute("placeholder", "Enter amount"));
    private SelenideElement confirmChek = $(Selectors.byAttribute("id", "confirmCheck"));
    private SelenideElement sendTransfer = $$("button")
                .findBy(Condition.text("\uD83D\uDE80 Send Transfer"));

    @Override
    public String url() {
        return "/transfer";
    }

    public TransferMoneyPage transfer(String fromAccountNumber, String toAccountNumber, double transferAmount){
        makeTransfer.shouldBe(Condition.visible);
        accountSelector.selectOptionContainingText(fromAccountNumber);
        enterRecipientName.sendKeys(toAccountNumber);
        enterRecipientAccountNumber.sendKeys(toAccountNumber);
        enterAmount.sendKeys(Double.toString(transferAmount));
        confirmChek.click();
        sendTransfer.click();
        return this;
    }
}
