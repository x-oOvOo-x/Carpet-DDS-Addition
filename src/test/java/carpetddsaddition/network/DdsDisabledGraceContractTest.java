/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.network;

import org.junit.jupiter.api.Test;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import static org.junit.jupiter.api.Assertions.*;

final class DdsDisabledGraceContractTest {
    private static final String STATE = "carpetddsaddition.network.DdsServerNetwork$ClientState";

    @Test void graceIsUnavailableUntilArmedAndConsumedOnlyOnce() throws Exception {
        Object state = newState();
        assertFalse(call(state, "consumeDisabledGrace"));
        invoke(state, "armDisabledGrace");
        assertTrue(call(state, "consumeDisabledGrace"));
        assertFalse(call(state, "consumeDisabledGrace"));
    }

    @Test void clearingGracePreventsLaterConsumption() throws Exception {
        Object state = newState();
        invoke(state, "armDisabledGrace"); invoke(state, "clearDisabledGrace");
        assertFalse(call(state, "consumeDisabledGrace"));
    }

    private static Object newState() throws Exception { Constructor<?> c = Class.forName(STATE).getDeclaredConstructor(); c.setAccessible(true); return c.newInstance(); }
    private static void invoke(Object target, String name) throws Exception { method(target, name).invoke(target); }
    private static boolean call(Object target, String name) throws Exception { return (boolean) method(target, name).invoke(target); }
    private static Method method(Object target, String name) throws Exception { Method m = target.getClass().getDeclaredMethod(name); m.setAccessible(true); return m; }
}
