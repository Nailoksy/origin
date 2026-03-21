package generators;

import org.apache.commons.lang3.RandomStringUtils;

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

    public static long getRandomId() {
        return Long.MAX_VALUE;
    }

    public static int getRandomAmount(){
        return ThreadLocalRandom.current().nextInt(1, 5001);
    }
}
