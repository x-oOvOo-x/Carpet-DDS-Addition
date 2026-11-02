/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.playershulkerrestock;

import org.junit.jupiter.api.Test;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

/** Source-structure checks complement all-node build and runtime inventory tests. */
final class PlayerShulkerRestockSourceContractTest {
    private static Path root() {
        Path current = Path.of("").toAbsolutePath();
        while (current != null) {
            if (Files.isRegularFile(current.resolve("src/main/rules/rules.json"))) return current;
            current = current.getParent();
        }
        throw new IllegalStateException("Missing root rules.json");
    }

    private static String read(String relative) throws Exception {
        return Files.readString(root().resolve(relative));
    }

    @Test void oneRuleControlsAllFourSources() throws Exception {
        String rules = read("src/main/rules/rules.json");
        assertTrue(rules.contains("\"name\": \"autoRestock\""));
        assertFalse(rules.contains("\"name\": \"shulkerRestock\""));
        assertFalse(rules.contains("\"name\": \"playerShulkerRestock\""));
        assertTrue(read("src/main/java/carpetddsaddition/feature/playershulkerrestock/compat/PlayerShulkerRestockCompat.java")
                .contains("DDSRules.autoRestock()"));
    }

    @Test void allActiveTargetsUseLegacySafeRestockPaths() throws Exception {
        String rules = read("src/main/rules/rules.json");
        int position = rules.indexOf("\"name\": \"autoRestock\"");
        assertTrue(position >= 0);
        assertTrue(rules.substring(position).contains("\"minMc\": 11404"));
        String compat = read("src/main/java/carpetddsaddition/feature/playershulkerrestock/compat/PlayerShulkerRestockCompat.java");
        assertTrue(compat.contains("MC >= 11404 && MC <= 260300"));
        String storage = read("src/main/java/carpetddsaddition/feature/playershulkerrestock/compat/PlayerShulkerRestockStorage.java");
        assertTrue(storage.contains("MC >= 12005"));
        assertTrue(storage.contains("ItemContainerContents.fromItems(contents)"));
        assertTrue(storage.contains("ContainerHelper.loadAllItems(blockEntityTag, contents)"));
        assertTrue(storage.contains("ContainerHelper.saveAllItems(updatedTag, contents)"));
        assertTrue(storage.contains("MC >= 11903"));
        String network = read("src/main/java/carpetddsaddition/feature/playershulkerrestock/compat/PlayerShulkerRestockNetworkCompat.java");
        assertTrue(network.contains("MC >= 12111"));
        assertTrue(network.contains("MC >= 12101"));
        assertTrue(network.contains("MC >= 11903"));
        assertTrue(network.contains("MC >= 12105"));
        String hook = read("src/main/java/carpetddsaddition/mixin/feature/playershulkerrestock/ServerPlayerGameModeMixin.java");
        assertTrue(hook.contains("MC >= 11601"));
        assertTrue(hook.contains("InteractionResult.PASS"));
        assertTrue(hook.contains("result.wasItemInteraction()"));
    }

    @Test void bundleSourcesUseServerAuthorityAndPreserveExistingGuards() throws Exception {
        String storage = read("src/main/java/carpetddsaddition/feature/playershulkerrestock/compat/PlayerShulkerRestockStorage.java");
        String bundle = read("src/main/java/carpetddsaddition/feature/playershulkerrestock/compat/PlayerShulkerRestockBundleCompat.java");
        assertTrue(storage.contains("fromBundles(player, inv, inv, 36"));
        assertTrue(storage.contains("fromShulkerBundles(player, inv, inv, 36"));
        assertTrue(storage.contains("fromBundles(player, inv, ender"));
        assertTrue(storage.contains("fromShulkerBundles(player, inv, ender"));
        assertTrue(storage.contains("QuickContainerAccessLease.isHeldByAnyone(bundle)"));
        assertTrue(storage.contains("if (capacity != 27 && capacity != 54) return false"));
        assertTrue(storage.contains("overflow(contents, capacity)"));
        assertTrue(bundle.contains("MC >= 12005"));
        assertTrue(bundle.contains("instanceof BundleItem"));
        assertTrue(bundle.contains("DataComponents.BUNDLE_CONTENTS"));
        assertTrue(bundle.contains("new BundleContents(contents)"));
        assertTrue(bundle.contains("MAX_BUNDLE_DEPTH = 16"));
        assertTrue(bundle.contains("extract(source, expected, room, depth + 1)"));
        assertTrue(bundle.contains("MC >= 260102"));
        assertTrue(bundle.contains("ItemStackTemplate.fromNonEmptyStack(item)"));
        assertTrue(bundle.contains("template.create()"));
        assertTrue(bundle.contains("AutoRestockTransfer.commit(room, extracted.moved, valid"));
    }

    @Test void ams54SlotDetectionIncludes26_3AndRetainsOverflowGuard() throws Exception {
        String ams = read("src/main/java/carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessAmsCompat.java");
        assertTrue(ams.contains("MC <= 260300"));
        assertFalse(ams.contains("MC <= 260200"));
        assertTrue(ams.contains("LARGE_SHULKER_BOX"));
        assertTrue(ams.contains("club.mcams.carpet.AmsServerLazySettings"));
        assertTrue(ams.contains("if (amsLoaded && cached == null && setting == null) return 0"));
        assertTrue(ams.contains("return effectiveShulkerEnabled(cached, setting) ? 54 : 27"));
        String qcaStorage = read("src/main/java/carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessStorage.java");
        assertTrue(qcaStorage.contains("return QuickContainerAccessAmsCompat.shulkerSize()"));
        assertTrue(qcaStorage.contains("size != 27 && size != 54"));
        String storage = read("src/main/java/carpetddsaddition/feature/playershulkerrestock/compat/PlayerShulkerRestockStorage.java");
        assertTrue(storage.contains("QuickContainerAccessLease.shulkerCapacity()"));
        assertTrue(storage.contains("if (capacity != 27 && capacity != 54) return false"));
        assertTrue(storage.contains("overflow(contents, capacity)"));
    }

    @Test void sourcePriorityLeaseAndQuantityGuardsArePreserved() throws Exception {
        String code = read("src/main/java/carpetddsaddition/feature/playershulkerrestock/compat/PlayerShulkerRestockStorage.java");
        int carriedLoose = code.indexOf("fromLoose(player, inv, inv, 36");
        int carriedBox = code.indexOf("fromShulkers(player, inv, inv, 36");
        int ender = code.indexOf("player.getEnderChestInventory()");
        int enderLoose = code.indexOf("fromLoose(player, inv, ender");
        int enderBox = code.indexOf("fromShulkers(player, inv, ender");
        assertTrue(carriedLoose >= 0 && carriedLoose < carriedBox && carriedBox < ender
                && ender < enderLoose && enderLoose < enderBox);
        assertTrue(code.contains("(index + 9) % 36"));
        assertTrue(code.contains("storage == inv && slot == targetSlot"));
        assertTrue(code.contains("ender.getContainerSize()"));
        assertTrue(code.contains("QuickContainerAccessLease.isHeldByAnyone(source)"));
        assertTrue(code.contains("QuickContainerAccessLease.isHeldByAnyone(box)"));
        assertTrue(code.contains("QuickContainerAccessItems.sameItemAndData(source, expected)"));
        assertTrue(code.contains("AutoRestockTransfer.commit(room, source.getCount()"));
        assertTrue(code.contains("overflow(contents, capacity)"));
        assertTrue(code.contains("if (source != inv) source.setChanged()"));
        assertTrue(code.contains("targetMatches(inv, targetSlot, expected, observedCount)"));
    }
}
