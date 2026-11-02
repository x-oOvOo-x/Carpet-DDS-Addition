/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.directblockdrops.compat;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

/** Cross-version identities for naturally cascading vertical plants. */
public final class DirectDropVerticalPlantCompat {
    private DirectDropVerticalPlantCompat() {}

    public static boolean isKelp(Block block) {
        return block == Blocks.KELP || block == Blocks.KELP_PLANT;
    }

    public static boolean isDripleaf(Block block) {
        //#if MC >= 11700
        //$$ return block == Blocks.BIG_DRIPLEAF || block == Blocks.BIG_DRIPLEAF_STEM;
        //#else
        return false;
        //#endif
    }

    public static boolean isTwisting(Block block) {
        //#if MC >= 11600
        //$$ return block == Blocks.TWISTING_VINES || block == Blocks.TWISTING_VINES_PLANT;
        //#else
        return false;
        //#endif
    }

    public static boolean isWeeping(Block block) {
        //#if MC >= 11600
        //$$ return block == Blocks.WEEPING_VINES || block == Blocks.WEEPING_VINES_PLANT;
        //#else
        return false;
        //#endif
    }

    public static boolean growsUp(Block block) {
        return isKelp(block) || isTwisting(block) || isDripleaf(block);
    }

    public static boolean sameFamily(Block from, Block to) {
        return isKelp(from) && isKelp(to)
                || isTwisting(from) && isTwisting(to)
                || isWeeping(from) && isWeeping(to)
                || isDripleaf(from) && isDripleaf(to);
    }

    public static boolean matchesDrop(Block block, Item item) {
        if (isKelp(block) && item == Items.KELP) return true;
        //#if MC >= 11600
        //$$ if (isTwisting(block) && item == Items.TWISTING_VINES
        //$$         || isWeeping(block) && item == Items.WEEPING_VINES) return true;
        //#endif
        //#if MC >= 11700
        //$$ if (isDripleaf(block) && item == Items.BIG_DRIPLEAF) return true;
        //#endif
        return false;
    }
}
