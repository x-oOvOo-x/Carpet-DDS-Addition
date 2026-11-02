/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.playershulkerrestock;

import java.util.function.BooleanSupplier;
import java.util.function.IntConsumer;

/** Shared one-source quantity transfer, used by both loose and box-backed storage. */
public final class AutoRestockTransfer {
    private AutoRestockTransfer() {}

    public static boolean commit(int room, int available, BooleanSupplier valid, IntConsumer apply) {
        int amount = Math.min(room, available);
        if (amount <= 0 || !valid.getAsBoolean()) return false;
        apply.accept(amount);
        return true;
    }
}
