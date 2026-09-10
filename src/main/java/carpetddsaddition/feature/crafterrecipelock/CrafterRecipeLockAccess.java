/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.crafterrecipelock;

public interface CrafterRecipeLockAccess {
    boolean dds$isRecipeLocked();
    int dds$getRecipeTemplateMask();
    String dds$getRecipeId();
    void dds$setRecipeLock(boolean locked, int templateMask, String recipeId);
}
