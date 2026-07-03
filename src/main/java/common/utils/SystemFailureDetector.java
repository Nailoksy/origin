package common.utils;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.regex.Pattern;

public final class SystemFailureDetector {

    private static final Pattern SERVER_ERROR_STATUS = Pattern.compile("but was <5\\d{2}>");

    private SystemFailureDetector() {
    }

    public static boolean isRetryable(Throwable throwable) {
        Throwable current = throwable;
        while (current != null) {
            if (isInfrastructureException(current) || isServerErrorAssertion(current)) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

    private static boolean isInfrastructureException(Throwable throwable) {
        if (throwable instanceof ConnectException || throwable instanceof SocketTimeoutException) {
            return true;
        }

        String className = throwable.getClass().getName();
        return className.contains("TimeoutException")
                || className.contains("WebDriverException")
                || className.contains("SessionNotCreatedException")
                || className.contains("UnreachableBrowserException");
    }

    private static boolean isServerErrorAssertion(Throwable throwable) {
        String message = throwable.getMessage();
        return message != null && SERVER_ERROR_STATUS.matcher(message).find();
    }
}
