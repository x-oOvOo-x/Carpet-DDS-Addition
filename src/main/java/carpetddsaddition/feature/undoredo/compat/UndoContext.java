/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.undoredo.compat;

//#if MC >= 12109
//$$ import java.util.ArrayDeque;
//$$ import java.util.Deque;
//#endif

public final class UndoContext {
    private UndoContext() {
    }

    //#if MC >= 12109
    //$$ private static final ThreadLocal<Deque<UndoRecord>> STACK = new ThreadLocal<>();
    //$$
    //$$ public static UndoRecord current() {
    //$$     Deque<UndoRecord> stack = STACK.get();
    //$$     return stack == null ? null : stack.peekLast();
    //$$ }
    //$$
    //$$ static void push(UndoRecord record) {
    //$$     Deque<UndoRecord> stack = STACK.get();
    //$$     if (stack == null) {
    //$$         stack = new ArrayDeque<>();
    //$$         STACK.set(stack);
    //$$     }
    //$$     stack.addLast(record);
    //$$ }
    //$$
    //$$ static void pop(UndoRecord expected) {
    //$$     Deque<UndoRecord> stack = STACK.get();
    //$$     if (stack == null) return;
    //$$
    //$$     UndoRecord actual = stack.pollLast();
    //$$     if (actual != expected) {
    //$$         stack.clear();
    //$$     }
    //$$
    //$$     if (stack.isEmpty()) {
    //$$         STACK.remove();
    //$$     }
    //$$ }
    //$$
    //$$ static void clear() {
    //$$     STACK.remove();
    //$$ }
    //#else
    static void clear() {
    }
    //#endif
}
