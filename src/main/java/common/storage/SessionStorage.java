package common.storage;

import api.models.CreateUserRequest;
import api.requests.steps.AdminSteps;
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

    private SessionStorage() {
    }

    public static void addUsers(List<CreateUserRequest> users) {
        for (CreateUserRequest user : users) {
            INSTANCE.get().userStepsMap.put(
                    user,
                    new UserSteps(user.getUsername(), user.getPassword())
            );
        }
    }

    public static void addUser(CreateUserRequest user) {
        INSTANCE.get().userStepsMap.put(
                user,
                new UserSteps(user.getUsername(), user.getPassword())
        );
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
    }

    public static void deleteAllUsers() {
        System.out.println(
                "Thread = " + Thread.currentThread().getName()
                        + ", users in storage = "
                        + INSTANCE.get().userStepsMap.size()
        );
        INSTANCE.get().userStepsMap.keySet().stream()
                .map(CreateUserRequest::getId)
                .forEach(id -> {
                    try {
                        AdminSteps.deleteUserById(id);
                    } catch (Exception e) {
                        System.out.println("Пользователь с  " + id + " уже удален или не найден");
                    }
                });
//        INSTANCE.get().userStepsMap.keySet().stream()
//                .map(CreateUserRequest::getId)
//                .forEach(AdminSteps::deleteUserById);


//        GetAllUsersResponse[] users = AdminSteps.getAllUsers();
//
//        for (CreateUserRequest user : INSTANCE.get().userStepsMap.keySet()) {
//
//            Long id = Arrays.stream(users)
//                    .filter(u -> u.getUsername().equals(user.getUsername()))
//                    .findFirst()
//                    .orElseThrow()
//                    .getId();
//
//            AdminSteps.deleteUserById(id);
//        }
    }
    public static boolean isEmpty() {
        return INSTANCE.get().userStepsMap.isEmpty();
    }
}
