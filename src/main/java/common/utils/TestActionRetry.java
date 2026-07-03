package common.utils;

import api.configs.Config;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;

public final class TestActionRetry {

    private static final int DEFAULT_MAX_ATTEMPTS = 3;
    private static final long DEFAULT_DELAY_MS = 1_000;

    private TestActionRetry() {
    }

    public static void execute(String actionName, Runnable action) {
        execute(actionName, action, TestActionRetry::prepareBrowserForRetry);
    }

    public static void execute(String actionName, Runnable action, Runnable beforeRetry) {
        try {
            executeThrowing(actionName, action::run, beforeRetry);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static void executeThrowing(String actionName, ThrowingRunnable action) throws Throwable {
        executeThrowing(actionName, action, TestActionRetry::prepareBrowserForRetry);
    }

    public static void executeThrowing(String actionName, ThrowingRunnable action, Runnable beforeRetry) throws Throwable {
        int maxAttempts = readIntProperty("test.retry.maxAttempts", DEFAULT_MAX_ATTEMPTS);
        Throwable lastFailure = null;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                action.run();
                return;
            } catch (Throwable throwable) {
                lastFailure = throwable;
                if (!SystemFailureDetector.isRetryable(throwable) || attempt >= maxAttempts) {
                    throw throwable;
                }

                logRetry(actionName, attempt, maxAttempts, throwable);
                beforeRetry.run();
                sleepBetweenRetries();
            }
        }

        throw lastFailure;
    }

    public static void prepareBrowserForRetry() {
        dismissAlertIfPresent();
        if (WebDriverRunner.hasWebDriverStarted()) {
            Selenide.closeWebDriver();
        }
    }

    private static void dismissAlertIfPresent() {
        try {
            if (WebDriverRunner.hasWebDriverStarted()) {
                Selenide.switchTo().alert().accept();
            }
        } catch (Exception ignored) {
        }
    }

    private static void sleepBetweenRetries() {
        long delayMs = readIntProperty("test.retry.delayMs", (int) DEFAULT_DELAY_MS);
        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Retry interrupted", e);
        }
    }

    private static void logRetry(String actionName, int attempt, int maxAttempts, Throwable throwable) {
        System.out.printf(
                "Действие '%s' упало из-за системной ошибки (%s). Повтор %d/%d%n",
                actionName,
                throwable.getClass().getSimpleName(),
                attempt,
                maxAttempts
        );
    }

    private static int readIntProperty(String key, int defaultValue) {
        String value = Config.getProperty(key);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return Integer.parseInt(value);
    }
}
