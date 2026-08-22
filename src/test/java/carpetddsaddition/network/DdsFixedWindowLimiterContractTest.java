/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.network;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class DdsFixedWindowLimiterContractTest {

    private static final String LIMITER_CLASS =
            "carpetddsaddition.network."
                    + "DdsServerNetwork$FixedWindowLimiter";

    @Test
    void limiterAllowsExactlyConfiguredCapacityPerWindow()
            throws Exception {

        Object limiter =
                newLimiter(3);

        Method allow =
                allowMethod(limiter);

        assertTrue(invokeAllow(limiter, allow));
        assertTrue(invokeAllow(limiter, allow));
        assertTrue(invokeAllow(limiter, allow));
        assertFalse(invokeAllow(limiter, allow));
    }

    @Test
    void limiterStartsANewWindowAfterOneSecond()
            throws Exception {

        Object limiter =
                newLimiter(1);

        Method allow =
                allowMethod(limiter);

        assertTrue(invokeAllow(limiter, allow));
        assertFalse(invokeAllow(limiter, allow));

        Field windowStartNanos =
                limiter.getClass()
                        .getDeclaredField(
                                "windowStartNanos"
                        );

        windowStartNanos.setAccessible(true);

        windowStartNanos.setLong(
                limiter,
                System.nanoTime()
                        - 2_000_000_000L
        );

        assertTrue(invokeAllow(limiter, allow));
    }

    @Test
    void limiterRejectsNonPositiveCapacity()
            throws Exception {

        Constructor<?> constructor =
                limiterConstructor();

        InvocationTargetException thrown =
                assertThrows(
                        InvocationTargetException.class,
                        () -> constructor.newInstance(0)
                );

        assertTrue(
                thrown.getCause()
                        instanceof IllegalArgumentException
        );
    }

    private static Object newLimiter(
            int maxRequests
    ) throws Exception {
        return limiterConstructor()
                .newInstance(maxRequests);
    }

    private static Constructor<?> limiterConstructor()
            throws Exception {

        Class<?> limiterClass =
                Class.forName(
                        LIMITER_CLASS
                );

        Constructor<?> constructor =
                limiterClass
                        .getDeclaredConstructor(
                                int.class
                        );

        constructor.setAccessible(true);

        return constructor;
    }

    private static Method allowMethod(
            Object limiter
    ) throws Exception {

        Method allow =
                limiter.getClass()
                        .getDeclaredMethod(
                                "allow"
                        );

        allow.setAccessible(true);

        return allow;
    }

    private static boolean invokeAllow(
            Object limiter,
            Method allow
    ) throws Exception {
        return (boolean) allow.invoke(
                limiter
        );
    }
}
