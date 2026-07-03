package common.extensions;

import api.configs.Config;
import api.models.CreateUserRequest;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import common.annotation.AdminSession;
import common.annotation.RetryOnSystemFailure;
import common.annotation.UserSession;
import common.storage.SessionStorage;
import common.utils.SystemFailureDetector;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;
import ui.pages.BasePage;

import java.lang.reflect.Method;

public class RetryOnSystemFailureExtension implements InvocationInterceptor {

    private static final int DEFAULT_MAX_ATTEMPTS = 3;
    private static final long DEFAULT_DELAY_MS = 1_000;

    @Override
    public void interceptTestMethod(Invocation<Void> invocation,
                                    ReflectiveInvocationContext<Method> invocationContext,
                                    ExtensionContext extensionContext) throws Throwable {
        executeWithRetry(invocation, extensionContext);
    }

    @Override
    public void interceptTestTemplateMethod(Invocation<Void> invocation,
                                            ReflectiveInvocationContext<Method> invocationContext,
                                            ExtensionContext extensionContext) throws Throwable {
        executeWithRetry(invocation, extensionContext);
    }

    private void executeWithRetry(Invocation<Void> invocation, ExtensionContext extensionContext) throws Throwable {
        if (!isRetryEnabled(extensionContext)) {
            invocation.proceed();
            return;
        }

        int maxAttempts = resolveMaxAttempts(extensionContext);
        Throwable lastFailure = null;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                invocation.proceed();
                return;
            } catch (Throwable throwable) {
                lastFailure = throwable;
                if (!SystemFailureDetector.isRetryable(throwable) || attempt >= maxAttempts) {
                    throw throwable;
                }

                logRetry(extensionContext, attempt, maxAttempts, throwable);
                prepareForRetry(extensionContext);
                sleepBetweenRetries();
            }
        }

        throw lastFailure;
    }

    private boolean isRetryEnabled(ExtensionContext extensionContext) {
        RetryOnSystemFailure methodAnnotation = extensionContext.getTestMethod()
                .map(method -> method.getAnnotation(RetryOnSystemFailure.class))
                .orElse(null);
        if (methodAnnotation != null) {
            return methodAnnotation.enabled();
        }

        RetryOnSystemFailure classAnnotation = extensionContext.getTestClass()
                .map(testClass -> testClass.getAnnotation(RetryOnSystemFailure.class))
                .orElse(null);
        return classAnnotation == null || classAnnotation.enabled();
    }

    private int resolveMaxAttempts(ExtensionContext extensionContext) {
        RetryOnSystemFailure methodAnnotation = extensionContext.getTestMethod()
                .map(method -> method.getAnnotation(RetryOnSystemFailure.class))
                .orElse(null);
        if (methodAnnotation != null && methodAnnotation.maxAttempts() > 0) {
            return methodAnnotation.maxAttempts();
        }

        RetryOnSystemFailure classAnnotation = extensionContext.getTestClass()
                .map(testClass -> testClass.getAnnotation(RetryOnSystemFailure.class))
                .orElse(null);
        if (classAnnotation != null && classAnnotation.maxAttempts() > 0) {
            return classAnnotation.maxAttempts();
        }

        return readIntProperty("test.retry.maxAttempts", DEFAULT_MAX_ATTEMPTS);
    }

    private void prepareForRetry(ExtensionContext extensionContext) {
        if (WebDriverRunner.hasWebDriverStarted()) {
            Selenide.closeWebDriver();
        }
        restoreSession(extensionContext);
    }

    private void restoreSession(ExtensionContext extensionContext) {
        extensionContext.getTestMethod().ifPresent(method -> {
            UserSession userSession = method.getAnnotation(UserSession.class);
            if (userSession != null && !SessionStorage.isEmpty()) {
                BasePage.authAsUser(SessionStorage.getUser(userSession.auth()));
                return;
            }

            if (method.getAnnotation(AdminSession.class) != null) {
                BasePage.authAsUser(CreateUserRequest.getAdmin());
            }
        });
    }

    private void sleepBetweenRetries() {
        long delayMs = readIntProperty("test.retry.delayMs", (int) DEFAULT_DELAY_MS);
        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Retry interrupted", e);
        }
    }

    private void logRetry(ExtensionContext extensionContext, int attempt, int maxAttempts, Throwable throwable) {
        System.out.printf(
                "Тест '%s' упал из-за системной ошибки (%s). Повтор %d/%d%n",
                extensionContext.getDisplayName(),
                throwable.getClass().getSimpleName(),
                attempt,
                maxAttempts
        );
    }

    private int readIntProperty(String key, int defaultValue) {
        String value = Config.getProperty(key);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return Integer.parseInt(value);
    }
}
