package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@Getter
public class DepositPage extends BasePage <DepositPage>{
    private SelenideElement accountSelector = $(".account-selector");
    private SelenideElement enterAmount = $(Selectors.byAttribute("placeholder", "Enter amount"));
    private SelenideElement depositButton = $$("button")
                .findBy(Condition.text("Deposit"));

    @Override
    public String url() {
        return "/deposit";
    }

    public DepositPage deposit(double amount){
        accountSelector.selectOption(1);
        enterAmount.sendKeys(Double.toString(amount));
        depositButton.click();
        return this;
    }

}
