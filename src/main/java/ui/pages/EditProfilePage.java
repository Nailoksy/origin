package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class EditProfilePage extends BasePage <EditProfilePage>{
    private SelenideElement editProfile = $(Selectors.byText("✏\uFE0F Edit Profile"));
    private SelenideElement enterNewName = $(Selectors.byAttribute("placeholder", "Enter new name"));
    private SelenideElement saveChangesButton = $$("button")
                .findBy(Condition.text("\uD83D\uDCBE Save Changes"));
    private SelenideElement userName = $(".user-name");

    @Override
    public String url() {
        return "/edit-profile";
    }

    public EditProfilePage updateName(String name) {
        editProfile.shouldBe(Condition.visible);
        enterNewName.sendKeys(name);
        saveChangesButton.click();
        //userName.shouldHave(Condition.exactText(name));
        return this;

    }
}
