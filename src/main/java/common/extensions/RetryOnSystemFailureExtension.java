package common.extensions;

import api.models.CreateUserRequest;
import common.annotation.AdminSession;
import common.annotation.RetryOnSystemFailure;
import common.annotation.UserSession;
import common.storage.SessionStorage;
import common.utils.TestActionRetry;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;
import ui.pages.BasePage;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RetryOnSystemFailureExtension implements InvocationInterceptor {

    @Override
    public void interceptTestMethod(Invocation<Void> invocation,
                                    ReflectiveInvocationContext<Method> invocationContext,
                                    ExtensionContext extensionContext) throws Throwable {
        executeWithRetry(invocation, invocationContext, extensionContext);
    }

    @Override
    public void interceptTestTemplateMethod(Invocation<Void> invocation,
                                            ReflectiveInvocationContext<Method> invocationContext,
                                            ExtensionContext extensionContext) throws Throwable {
        executeWithRetry(invocation, invocationContext, extensionContext);
    }

    private void executeWithRetry(Invocation<Void> invocation,
                                  ReflectiveInvocationContext<Method> invocationContext,
                                  ExtensionContext extensionContext) throws Throwable {
        if (!isRetryEnabled(extensionContext)) {
            invocation.proceed();
            return;
        }

        TestActionRetry.executeThrowing(
                extensionContext.getDisplayName(),
                () -> invokeTestMethod(invocationContext, extensionContext),
                () -> {
                    TestActionRetry.prepareBrowserForRetry();
                    restoreSession(extensionContext);
                }
        );
    }

    private void invokeTestMethod(ReflectiveInvocationContext<Method> invocationContext,
                                  ExtensionContext extensionContext) throws Throwable {
        Method method = invocationContext.getExecutable();
        Object target = extensionContext.getRequiredTestInstance();
        Object[] arguments = invocationContext.getArguments().toArray();

        try {
            method.setAccessible(true);
            method.invoke(target, arguments);
        } catch (InvocationTargetException e) {
            throw e.getCause() != null ? e.getCause() : e;
        }
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
}
