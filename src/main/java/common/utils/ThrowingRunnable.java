package common.utils;

@FunctionalInterface
public interface ThrowingRunnable {
    void run() throws Throwable;
}
