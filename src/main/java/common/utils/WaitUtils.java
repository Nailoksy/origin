package common.utils;

public class WaitUtils {
    public static final long WAIT_FOR_UI = 1000;
    private WaitUtils() {
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Sleep was interrupted", e);
        }
    }

}