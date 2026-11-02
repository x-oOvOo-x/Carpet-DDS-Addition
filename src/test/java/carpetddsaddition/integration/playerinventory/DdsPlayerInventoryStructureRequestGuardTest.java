package carpetddsaddition.integration.playerinventory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class DdsPlayerInventoryStructureRequestGuardTest {
    @Test void requestFieldsMustBeCompleteAndPositive() {
        Object target = new Object();
        assertFalse(DdsPlayerInventoryStructureRequestGuard.requestFieldsValid(-1, target, 1L, 1L));
        assertFalse(DdsPlayerInventoryStructureRequestGuard.requestFieldsValid(0, null, 1L, 1L));
        assertFalse(DdsPlayerInventoryStructureRequestGuard.requestFieldsValid(0, target, 0L, 1L));
        assertFalse(DdsPlayerInventoryStructureRequestGuard.requestFieldsValid(0, target, 1L, 0L));
        assertTrue(DdsPlayerInventoryStructureRequestGuard.requestFieldsValid(0, target, 1L, 1L));
    }

    @Test void strictGenerationMustBePositiveAndExact() {
        assertFalse(DdsPlayerInventoryStructureRequestGuard.generationMatches(0L, 1L));
        assertFalse(DdsPlayerInventoryStructureRequestGuard.generationMatches(1L, 0L));
        assertFalse(DdsPlayerInventoryStructureRequestGuard.generationMatches(1L, 2L));
        assertFalse(DdsPlayerInventoryStructureRequestGuard.generationMatches(2L, 1L));
        assertTrue(DdsPlayerInventoryStructureRequestGuard.generationMatches(2L, 2L));
    }

    @Test void bindingRequiresSameViewerConnectionMenuAndContainer() {
        Object viewer = new Object(), connection = new Object(), menu = new Object();
        assertTrue(DdsPlayerInventoryStructureRequestGuard.bindingMatches(
                viewer, viewer, connection, connection, menu, menu, 7, 7));
        assertFalse(DdsPlayerInventoryStructureRequestGuard.bindingMatches(
                viewer, new Object(), connection, connection, menu, menu, 7, 7));
        assertFalse(DdsPlayerInventoryStructureRequestGuard.bindingMatches(
                viewer, viewer, connection, new Object(), menu, menu, 7, 7));
        assertFalse(DdsPlayerInventoryStructureRequestGuard.bindingMatches(
                viewer, viewer, connection, connection, menu, new Object(), 7, 7));
        assertFalse(DdsPlayerInventoryStructureRequestGuard.bindingMatches(
                viewer, viewer, connection, connection, menu, menu, 7, 8));
    }

    @Test void liveTargetRejectsSameUuidReplacementByObjectIdentity() {
        Object oldTarget = new Object(), reconnectedTarget = new Object();
        assertTrue(DdsPlayerInventoryStructureRequestGuard.liveTargetMatches(oldTarget, oldTarget));
        assertFalse(DdsPlayerInventoryStructureRequestGuard.liveTargetMatches(reconnectedTarget, oldTarget));
        assertFalse(DdsPlayerInventoryStructureRequestGuard.liveTargetMatches(null, oldTarget));
    }

    @Test void offlineSessionIsInvalidatedWhenTheUuidComesOnline() {
        Object live = new Object();
        assertTrue(DdsPlayerInventoryStructureRequestGuard.targetPresenceMatches(true, null, null));
        assertFalse(DdsPlayerInventoryStructureRequestGuard.targetPresenceMatches(true, live, null));
        assertTrue(DdsPlayerInventoryStructureRequestGuard.targetPresenceMatches(false, live, live));
        assertFalse(DdsPlayerInventoryStructureRequestGuard.targetPresenceMatches(false, new Object(), live));
    }

    @Test void genericCanonicalSizeOnlyRequiresExactEquality() {
        assertTrue(DdsPlayerInventoryStructureRequestGuard.canonicalSizeMatches(41, 41));
        assertTrue(DdsPlayerInventoryStructureRequestGuard.canonicalSizeMatches(36, 36));
        assertFalse(DdsPlayerInventoryStructureRequestGuard.canonicalSizeMatches(41, 40));
        assertFalse(DdsPlayerInventoryStructureRequestGuard.canonicalSizeMatches(-1, -1));
    }

    @Test void canonicalCapacityMustRemainExactTwentySevenOrFiftyFour() {
        assertTrue(DdsPlayerInventoryStructureRequestGuard.canonicalEnderCapacityMatches(27, 27));
        assertTrue(DdsPlayerInventoryStructureRequestGuard.canonicalEnderCapacityMatches(54, 54));
        assertFalse(DdsPlayerInventoryStructureRequestGuard.canonicalEnderCapacityMatches(54, 27));
        assertFalse(DdsPlayerInventoryStructureRequestGuard.canonicalEnderCapacityMatches(27, 54));
        assertFalse(DdsPlayerInventoryStructureRequestGuard.canonicalEnderCapacityMatches(36, 36));
    }

    @Test void transitionGenerationRejectCleanupMatchesOnlyTheImmediateNextGeneration() {
        assertTrue(DdsPlayerInventoryStructureRequestGuard.nextGenerationMatches(4L, 5L));
        assertFalse(DdsPlayerInventoryStructureRequestGuard.nextGenerationMatches(4L, 4L));
        assertFalse(DdsPlayerInventoryStructureRequestGuard.nextGenerationMatches(4L, 6L));
        assertFalse(DdsPlayerInventoryStructureRequestGuard.nextGenerationMatches(0L, 1L));
        assertFalse(DdsPlayerInventoryStructureRequestGuard.nextGenerationMatches(Long.MAX_VALUE, Long.MIN_VALUE));
    }

    @Test void parentTokenRequiresSameConnectionProviderTargetAndNextGeneration() {
        Object connection = new Object(), target = new Object();
        assertTrue(DdsPlayerInventoryStructureRequestGuard.parentMatches(
                connection, connection, "gca", "gca", target, target, 4L, 5L, true));
        assertFalse(DdsPlayerInventoryStructureRequestGuard.parentMatches(
                connection, new Object(), "gca", "gca", target, target, 4L, 5L, true));
        assertFalse(DdsPlayerInventoryStructureRequestGuard.parentMatches(
                connection, connection, "gca", "org", target, target, 4L, 5L, true));
        assertFalse(DdsPlayerInventoryStructureRequestGuard.parentMatches(
                connection, connection, "gca", "gca", target, new Object(), 4L, 5L, true));
        assertFalse(DdsPlayerInventoryStructureRequestGuard.parentMatches(
                connection, connection, "gca", "gca", target, target, 4L, 6L, true));
        assertFalse(DdsPlayerInventoryStructureRequestGuard.parentMatches(
                connection, connection, "gca", "gca", target, target, 4L, 5L, false));
    }

    @Test void requestIdsArePositiveMonotonicPerConnection() {
        DdsPlayerInventoryStructureRequestGuard.Sequence sequence = new DdsPlayerInventoryStructureRequestGuard.Sequence();
        Object first = new Object(), second = new Object();

        assertFalse(sequence.accept(first, 0L));
        assertTrue(sequence.accept(first, 1L));
        assertFalse(sequence.accept(first, 1L));
        assertFalse(sequence.accept(first, -1L));
        assertTrue(sequence.accept(first, 2L));

        assertTrue(sequence.accept(second, 1L));
        assertFalse(sequence.accept(second, 1L));
        assertTrue(sequence.accept(second, 2L));

        sequence.clear(first);
        assertTrue(sequence.accept(first, 1L));
        assertTrue(sequence.accept(second, 3L));
        sequence.clear();
        assertTrue(sequence.accept(second, 1L));
    }
}
