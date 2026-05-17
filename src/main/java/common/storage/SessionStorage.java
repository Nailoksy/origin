package common.storage;

import api.models.CreateUserRequest;
import api.requests.steps.UserSteps;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

//Где хранятся созданные пользователи и шаги юзера. Паттерн Синглтон.
public class SessionStorage {
    private static final  SessionStorage INSTANCE = new SessionStorage();

    private final LinkedHashMap<CreateUserRequest, UserSteps> userStepsMap = new LinkedHashMap<>();

    private SessionStorage() {};

    public static void addUsers(List<CreateUserRequest> users) {
        for (CreateUserRequest user : users){
            INSTANCE.userStepsMap.put(user, new UserSteps(user.getUsername(), user.getPassword()));
        }
    }

    /**
     * Возвращаем объект CreateUserRequest по его порядковому номеру в списке созданных пользователей.
     * @param number Порядковый номер начинается с 1 (а не с 0).
     * @return Объект CreateUserRequest, соответствующий указанному порядковому номеру
     */
    //Получение юзера по индексу
    public static CreateUserRequest getUser(int number) {
        return new ArrayList<>(INSTANCE.userStepsMap.keySet()).get(number-1);
    }

    //Получение первого пользователя
    public static CreateUserRequest getUser() {
        return getUser(1);
    }

    //Получение степов
    public static UserSteps getSteps(int number) {
        return new ArrayList<>(INSTANCE.userStepsMap.values()).get(number-1);
    }

    //Получение первого пользователя
    public static UserSteps getSteps() {
        return getSteps(1);
    }

    public static void clear(){
        INSTANCE.userStepsMap.clear();
    }
}
