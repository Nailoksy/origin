package common.extensions;

import api.models.CreateUserRequest;
import api.requests.steps.AdminSteps;
import common.annotation.UserSession;
import common.storage.SessionStorage;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import ui.pages.BasePage;

import java.util.LinkedList;
import java.util.List;

public class UserSessionExtension implements BeforeEachCallback, AfterEachCallback {
    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        //ШАГ 1: Проверка, что у теста есть аннотация UserSession
        UserSession annotation = extensionContext.getRequiredTestMethod().getAnnotation(UserSession.class);
        if (annotation != null) {
            int userCount = annotation.value();

            SessionStorage.clear();

            List<CreateUserRequest> users = new LinkedList<>();

            for (int i = 0; i < userCount; i++) {
                CreateUserRequest user = AdminSteps.createUser();
                users.add(user);

            }
            SessionStorage.addUsers(users);

            int authAsUser = annotation.auth();

            BasePage.authAsUser(SessionStorage.getUser(authAsUser));
        }
    }

    // AI правки: новый метод — сброс ThreadLocal SessionStorage после каждого @UserSession теста
    @Override
    public void afterEach(ExtensionContext extensionContext) {
        if (extensionContext.getRequiredTestMethod().getAnnotation(UserSession.class) != null) {

            if (extensionContext.getRequiredTestMethod().getAnnotation(UserSession.class) == null) {
                return;
            }

            List<Long> userIds = SessionStorage.getUserIds();

            try {
                for (Long id : userIds) {
                    System.out.println("Удаляем пользователя id=" + id);
                    AdminSteps.deleteUser(id);
                }

                System.out.println("Пользователь с ID: " + userIds + " удален.");

            } finally {
                SessionStorage.remove();
            }
        }
    }
//    @Override
//    public void beforeEach(ExtensionContext extensionContext) throws Exception {
//        //ШАГ 1: Проверка, что у теста есть аннотация UserSession
//        UserSession annotation = extensionContext.getRequiredTestMethod().getAnnotation(UserSession.class);
//        if (annotation != null) {
//            int userCount = annotation.value();
//
//            SessionStorage.clear();
//
//            ThreadLocal<List<CreateUserRequest>> users = ThreadLocal.withInitial(LinkedList::new);
//
//            for (int i = 0; i < userCount; i++) {
//                CreateUserRequest user = AdminSteps.createUser();
//                users.get().add(user);
//
//            }
//            SessionStorage.addUsers(users.get());
//
//            int authAsUser = annotation.auth();
//
//            BasePage.authAsUser(SessionStorage.getUser(authAsUser));
//        }
//    }
}
