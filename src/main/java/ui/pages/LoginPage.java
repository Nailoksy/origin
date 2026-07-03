package ui.pages;

import com.codeborne.selenide.SelenideElement;
import common.utils.WaitUtils;

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
        WaitUtils.sleep(WaitUtils.WAIT_FOR_UI);
        usernameInput.sendKeys(username);
        passwordInput.sendKeys(password);
        WaitUtils.sleep(WaitUtils.WAIT_FOR_UI);
        button.click();
        WaitUtils.sleep(WaitUtils.WAIT_FOR_UI);
        return this;
    }
}
