package common.extensions;

import api.requests.steps.AdminSteps;
import common.storage.UserDeleteRegistry;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class UserCleanupExtension implements AfterAllCallback {
//удаление юзеров из АПИ тестов
    @Override
    public void afterAll(ExtensionContext context) {

        // удаляем всех пользователей, созданных в этом потоке
        for (Long id : UserDeleteRegistry.getAll()) {
            try {
                AdminSteps.deleteUserById(id);
            } catch (Exception ignored) {
                // если уже удалён или тест упал раньше — игнорируем
            }
        }

        // чистим данные
        UserDeleteRegistry.clear();
        UserDeleteRegistry.remove();
    }
}