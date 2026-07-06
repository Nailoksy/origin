package ui.pages;

import api.models.CreateUserRequest;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import common.utils.RetryUtils;
import lombok.Getter;
import ui.elements.UserBage;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

import static com.codeborne.selenide.Selenide.$;

@Getter
public class AdminPanelPage extends BasePage<AdminPanelPage>{
    public static final String USER_ROLE = "USER";
    private SelenideElement adminPanelText =  $(Selectors.byText("Admin Panel"));
    private SelenideElement addUserButton =  $(Selectors.byText("Add User"));

    @Override
    public String url() {
        return "/admin";
    }

    public AdminPanelPage createUser(String username, String password) {
        usernameInput.sendKeys(username);
        passwordInput.sendKeys(password);
        addUserButton.click();
        return this;
    }
//перегружаем, чтобы передать только реквест
    public AdminPanelPage createUser(CreateUserRequest createUserRequest) {
        usernameInput.sendKeys(createUserRequest.getUsername());
        passwordInput.sendKeys(createUserRequest.getPassword());
        addUserButton.click();
        return this;
    }

    public List<UserBage> getAllUsers() {
        ElementsCollection elementsCollection=  $(Selectors.byText("All Users")).parent().findAll("li");
        return generatePageElements(elementsCollection, UserBage::new);
    }

    public UserBage findUserByUsername (String username) {
        SelenideElement element = RetryUtils.retry(
                () -> $(Selectors.byText("All Users"))
                        .parent()
                        .$$("li")
                        .findBy(com.codeborne.selenide.Condition.text(username)),
                Objects::nonNull,
                10,
                500
        );
        return new UserBage(element);
    }
    public AdminPanelPage checkAdminPanelOpened() {
        adminPanelText.shouldBe(Condition.visible, Duration.ofSeconds(15));
        return this;
    }
}
