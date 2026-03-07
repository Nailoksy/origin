package generators;

import org.apache.commons.lang3.RandomStringUtils;

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
}
