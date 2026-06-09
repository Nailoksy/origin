package common.storage;

import api.models.CreateUserRequest;
import api.requests.steps.UserSteps;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

//Где хранятся созданные пользователи и шаги юзера. Паттерн Синглтон.
public class SessionStorage {
    /* ThreadLocal - способ сделать SessionStorage потокобезопасным
    Каждый лог, обращаясь к INSTANCE.get() получают свою КОПИЮ
     */
    private static final ThreadLocal<SessionStorage> INSTANCE = ThreadLocal.withInitial(SessionStorage::new);

    private final LinkedHashMap<CreateUserRequest, UserSteps> userStepsMap = new LinkedHashMap<>();

    //Добавлено: Хранилище ID пользователей для будущего удаления после теста
    private static final ThreadLocal<List<Long>> USER_IDS =
            ThreadLocal.withInitial(ArrayList::new);

    private SessionStorage() {
    }

    //Добавлено: Методы для добавления и получения айди юзера
    public static void addUserId(Long id) {
        USER_IDS.get().add(id);
    }

    public static List<Long> getUserIds() {
        return new ArrayList<>(USER_IDS.get());
    }

    ;

    public static void addUsers(List<CreateUserRequest> users) {
        for (CreateUserRequest user : users) {
            INSTANCE.get().userStepsMap.put(
                    user,
                    new UserSteps(user.getUsername(), user.getPassword())
            );
        }
    }

    /**
     * Возвращаем объект CreateUserRequest по его порядковому номеру в списке созданных пользователей.
     *
     * @param number Порядковый номер начинается с 1 (а не с 0).
     * @return Объект CreateUserRequest, соответствующий указанному порядковому номеру
     */
    //Получение юзера по индексу
    public static CreateUserRequest getUser(int number) {
        return new ArrayList<>(INSTANCE.get().userStepsMap.keySet()).get(number - 1);
    }

    //Получение первого пользователя
    public static CreateUserRequest getUser() {
        return getUser(1);
    }

    //Получение степов
    public static UserSteps getSteps(int number) {
        return new ArrayList<>(INSTANCE.get().userStepsMap.values()).get(number - 1);
    }

    //Получение первого пользователя
    public static UserSteps getSteps() {
        return getSteps(1);
    }

    public static void clear() {
        INSTANCE.get().userStepsMap.clear();
        //
        USER_IDS.get().clear();
    }

    // AI правки: новый метод — сброс ThreadLocal после теста, чтобы не утекала память в пуле потоков
    public static void remove() {
        INSTANCE.remove();
        //очищаем список пользователей
        USER_IDS.remove();
    }
}
