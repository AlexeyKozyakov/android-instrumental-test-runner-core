package com.github.grishberg.tests.common;

/**
 * Logger interface
 */
public interface RunnerLogger {
    void w(String tag, String message);

    void i(String tag, String message);

    void i(String tag, String msgFormat, Object... args);

    void d(String tag, String message);

    void d(String tag, String msgFormat, Object... args);

    void e(String tag, String message);

    void e(String tag, String message, Throwable throwable);

    void w(String tag, String msgFormat, Object... args);
}
