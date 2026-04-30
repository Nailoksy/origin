package api.generators;

import api.models.GetAllUsersResponse;
import org.apache.commons.lang3.RandomStringUtils;
import api.requests.steps.AdminSteps;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import static org.apache.commons.lang3.RandomStringUtils.*;

public class RandomData {
    private RandomData() {}

    public static String getUsername(){
        return RandomStringUtils.randomAlphabetic(10);
    }

    public static String getPassword(){
        return randomAlphabetic(3).toUpperCase() +
                randomAlphabetic(5).toLowerCase() +
                randomNumeric(3) + "%$#";
    }

    public static long getNonExistingId() {
    GetAllUsersResponse[] allUsers = AdminSteps.getAllUsers();

        long maxId = Arrays.stream(allUsers)
                .mapToLong(GetAllUsersResponse::getId)
                .max()
                .orElseThrow();

        return maxId + 1;
    }

    public static int getRandomAmount(){
        return ThreadLocalRandom.current().nextInt(1, 5001);
    }
}
