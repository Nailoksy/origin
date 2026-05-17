package ui.pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class LoginPage extends BasePage<LoginPage> {
    //мне кажется, строка с приветствием всё должна быть в классе UserDashboard, а не здесь
   public static final String NONAME_WELCOME_TEXT = "Welcome, noname!";

   private SelenideElement button = $("button");

    @Override
    public String url() {
        return "/login";
    }

    public LoginPage login(String username, String password) {
        usernameInput.sendKeys(username);
        passwordInput.sendKeys(password);
        button.click();
        return this;
    }
}
