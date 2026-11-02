/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.integration.fabric.recipe;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

final class FabricRecipeSnapshotContractTest {
    private static final class Snapshot {}
    public static final class IterableSnapshot {
        private final List<?> values;

        public Iterable<?> recipes() {
            return values;
        }

        private IterableSnapshot(List<?> values) {
            this.values = values;
        }

        public static IterableSnapshot of(Iterable<?> recipes) {
            List<Object> values = new ArrayList<>();
            recipes.forEach(values::add);
            return new IterableSnapshot(values);
        }
    }

    public record TestRecipe(String id) {}

    public static final class SnapshotManager {
        private IterableSnapshot synchronizedRecipes = IterableSnapshot.of(List.of());
        public IterableSnapshot getSynchronizedRecipes() {
            return synchronizedRecipes;
        }
    }

    private static final class Manager {
        private Snapshot current = new Snapshot();
        private final Object unrelated = new Object();
        private static Snapshot global = new Snapshot();
    }

    @Test void snapshotFactoryAcceptsCollectionRatherThanRecipeMap() throws Exception {
        var recipeValues = Map.of("wood", "planks", "stone", "slabs").values();
        var snapshot = (IterableSnapshot) FabricRecipeHotSyncCompat.createSnapshot(
                IterableSnapshot.class, recipeValues);
        assertEquals(Set.copyOf(recipeValues), Set.copyOf(snapshot.values));
    }

    @Test void runtimeAuditDistinguishesLiveAndStaleRecipeIds() throws Exception {
        var vanilla = new TestRecipe("minecraft:stonecutting");
        var added = new TestRecipe("carpetddsaddition:wood");
        var off = List.of(vanilla);
        var on = List.of(vanilla, added);
        Object offIndex = FabricRecipeHotSyncCompat.createSnapshot(IterableSnapshot.class, off);
        Object onIndex = FabricRecipeHotSyncCompat.createSnapshot(IterableSnapshot.class, on);

        assertDoesNotThrow(() -> FabricRecipeHotSyncCompat.verifyRecipeIds(off, offIndex));
        assertThrows(IllegalStateException.class,
                () -> FabricRecipeHotSyncCompat.verifyRecipeIds(on, offIndex));
        assertDoesNotThrow(() -> FabricRecipeHotSyncCompat.verifyRecipeIds(on, onIndex));
        assertThrows(IllegalStateException.class,
                () -> FabricRecipeHotSyncCompat.verifyRecipeIds(off, onIndex));
        assertDoesNotThrow(() -> FabricRecipeHotSyncCompat.verifyRecipeIds(off, offIndex));
    }

    @Test void getterReflectsUpdatedFabricSnapshot() throws Exception {
        var manager = new SnapshotManager();
        var live = List.of(new TestRecipe("minecraft:oak"), new TestRecipe("carpetddsaddition:oak"));
        var stale = manager.getSynchronizedRecipes();
        var updated = FabricRecipeHotSyncCompat.createSnapshot(IterableSnapshot.class, live);
        assertTrue(FabricRecipeHotSyncCompat.replaceSnapshotField(manager, stale, updated));
        FabricRecipeHotSyncCompat.verifyRecipeIds(live, manager.getSynchronizedRecipes());
        assertThrows(IllegalStateException.class,
                () -> FabricRecipeHotSyncCompat.verifyRecipeIds(List.of(new TestRecipe("minecraft:oak")),
                        manager.getSynchronizedRecipes()));
    }

    @Test void replacesMatchingSnapshotByIdentity() throws Exception {
        Manager manager = new Manager();
        Snapshot original = manager.current;
        Snapshot next = new Snapshot();
        assertTrue(FabricRecipeHotSyncCompat.replaceSnapshotField(manager, original, next));
        assertSame(next, manager.current);
        assertNotSame(next, Manager.global);
        assertNotNull(manager.unrelated);
    }

    @Test void rejectsStaleSnapshotRatherThanMutatingUnrelatedField() throws Exception {
        Manager manager = new Manager();
        Snapshot first = manager.current;
        assertFalse(FabricRecipeHotSyncCompat.replaceSnapshotField(manager, new Snapshot(), new Snapshot()));
        assertSame(first, manager.current);
    }

    @Test void preservesUpdatedStateBeforeDispatchInCoordinator() throws Exception {
        // Static contract against accidental regression to the former
        // "notify first, mark applied last" order. Real Fabric failures still
        // require a separate runtime listener-fault test.
        var file = java.nio.file.Path.of("src/main/java/carpetddsaddition/feature/dynamicrecipes/compat/UnifiedRecipeCoordinatorCompat.java");
        java.nio.file.Path root = java.nio.file.Path.of("").toAbsolutePath();
        while (root != null && !java.nio.file.Files.exists(root.resolve(file))) root = root.getParent();
        assertNotNull(root, "repository source root");
        String source = java.nio.file.Files.readString(root.resolve(file));
        int apply = source.indexOf("UnifiedRecipeManagerCompat.applyAtRuntime(server, manager, target)");
        int state = source.indexOf("state.applied = next;");
        int notify = source.indexOf("FabricRecipeHotSyncCompat.send(server)");
        assertTrue(apply >= 0 && apply < state && state < notify);
    }
}
