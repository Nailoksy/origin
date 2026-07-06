package common.storage;

import java.util.HashSet;
import java.util.Set;

public class UserDeleteRegistry {

    private static final ThreadLocal<UserDeleteRegistry> INSTANCE =
            ThreadLocal.withInitial(UserDeleteRegistry::new);

    private final Set<Long> idsToDelete = new HashSet<>();

    private UserDeleteRegistry() {}

    public static void add(Long id) {
        INSTANCE.get().idsToDelete.add(id);
    }

    public static Set<Long> getAll() {
        return new HashSet<>(INSTANCE.get().idsToDelete);
    }

    public static void clear() {
        INSTANCE.get().idsToDelete.clear();
    }

    public static void remove() {
        INSTANCE.remove();
    }
}