package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import common.utils.WaitUtils;

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
        WaitUtils.sleep(WaitUtils.WAIT_FOR_UI);
        enterNewName.click();
        WaitUtils.sleep(WaitUtils.WAIT_FOR_UI);
        enterNewName.sendKeys(name);
        WaitUtils.sleep(WaitUtils.WAIT_FOR_UI);
        enterNewName.shouldHave(Condition.value(name));
        WaitUtils.sleep(WaitUtils.WAIT_FOR_UI);
        saveChangesButton.click();
        WaitUtils.sleep(WaitUtils.WAIT_FOR_UI);
        return this;

    }
    public EditProfilePage checkUserName(String expectedName) {
        userName.shouldHave(Condition.exactText(expectedName));
        return this;
    }

}
