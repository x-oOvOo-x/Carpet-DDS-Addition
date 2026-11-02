/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.commandcamera;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

final class CommandCameraFollowRecoveryTest {
    @AfterEach void clearRuntime() { CommandCamera.close(); }

    @Test void cameraResendsAreDelayedBoundedAndDoNotEndFollow() {
        UUID viewer = UUID.randomUUID(), target = UUID.randomUUID();
        CommandCamera.beginFollow(viewer, target, 2);
        assertFalse(CommandCamera.shouldResendFollowCamera(viewer));
        CommandCamera.armFollowCameraRecovery(viewer);

        for (int resend = 0; resend < 12; resend++) {
            for (int tick = 0; tick < 9; tick++)
                assertFalse(CommandCamera.shouldResendFollowCamera(viewer));
            assertTrue(CommandCamera.shouldResendFollowCamera(viewer));
        }

        for (int tick = 0; tick < 100; tick++)
            assertFalse(CommandCamera.shouldResendFollowCamera(viewer));
        assertEquals(target, CommandCamera.followTarget(viewer));
        assertTrue(CommandCamera.isFollowing(viewer));
    }

    @Test void retargetingAndCrossDimensionReattachRestartTheRetryWindow() {
        UUID viewer = UUID.randomUUID(), first = UUID.randomUUID(), second = UUID.randomUUID();
        CommandCamera.beginFollow(viewer, first, 2);
        CommandCamera.armFollowCameraRecovery(viewer);
        for (int tick = 0; tick < 9; tick++)
            assertFalse(CommandCamera.shouldResendFollowCamera(viewer));

        CommandCamera.beginFollow(viewer, second, 2);
        assertFalse(CommandCamera.shouldResendFollowCamera(viewer));
        CommandCamera.armFollowCameraRecovery(viewer);
        for (int tick = 0; tick < 9; tick++)
            assertFalse(CommandCamera.shouldResendFollowCamera(viewer));
        assertTrue(CommandCamera.shouldResendFollowCamera(viewer));

        CommandCamera.armFollowCameraRecovery(viewer);
        for (int tick = 0; tick < 9; tick++)
            assertFalse(CommandCamera.shouldResendFollowCamera(viewer));
        assertTrue(CommandCamera.shouldResendFollowCamera(viewer));
        assertEquals(second, CommandCamera.followTarget(viewer));
    }

    @Test void endCreditsSuspendsFollowUntilSameUuidReturns() {
        UUID viewer = UUID.randomUUID(), target = UUID.randomUUID();
        CommandCamera.beginFollow(viewer, target, 2);
        CommandCamera.armFollowCameraRecovery(viewer);

        // The player is still in PlayerList during End credits, but its entity is removed.
        for (int tick = 0; tick < 400; tick++)
            assertFalse(CommandCamera.suspendFollow(viewer, false));
        assertTrue(CommandCamera.isFollowSuspended(viewer));
        assertEquals(-1, CommandCamera.followAttachDelay(viewer));
        assertFalse(CommandCamera.shouldResendFollowCamera(viewer));
        assertEquals(target, CommandCamera.followTarget(viewer));

        assertTrue(CommandCamera.resumeFollow(viewer, 2));
        assertFalse(CommandCamera.isFollowSuspended(viewer));
        assertEquals(2, CommandCamera.followAttachDelay(viewer));
        assertFalse(CommandCamera.resumeFollow(viewer, 2));
    }

    @Test void missingTargetIsBoundedAndRecoveredTargetClearsMissingCounter() {
        UUID viewer = UUID.randomUUID(), target = UUID.randomUUID();
        CommandCamera.beginFollow(viewer, target, 2);
        for (int tick = 0; tick < 199; tick++)
            assertFalse(CommandCamera.suspendFollow(viewer, true));
        assertTrue(CommandCamera.suspendFollow(viewer, true));

        CommandCamera.beginFollow(viewer, target, 2);
        assertFalse(CommandCamera.suspendFollow(viewer, true));
        assertFalse(CommandCamera.suspendFollow(viewer, false));
        for (int tick = 0; tick < 199; tick++)
            assertFalse(CommandCamera.suspendFollow(viewer, true));
        assertTrue(CommandCamera.suspendFollow(viewer, true));
    }

    @Test void stoppingDisconnectingAndRestartingDiscardPendingRetries() {
        UUID viewer = UUID.randomUUID(), target = UUID.randomUUID();
        CommandCamera.armFollowCameraRecovery(viewer);
        assertFalse(CommandCamera.shouldResendFollowCamera(viewer));

        CommandCamera.beginFollow(viewer, target, 2);
        CommandCamera.armFollowCameraRecovery(viewer);
        CommandCamera.suspendFollow(viewer, false);
        assertTrue(CommandCamera.clearFollow(viewer));
        assertFalse(CommandCamera.isFollowSuspended(viewer));
        assertFalse(CommandCamera.shouldResendFollowCamera(viewer));
        assertFalse(CommandCamera.isFollowing(viewer));

        CommandCamera.beginFollow(viewer, target, 2);
        CommandCamera.armFollowCameraRecovery(viewer);
        CommandCamera.suspendFollow(viewer, false);
        CommandCamera.clearRuntime(viewer);
        assertFalse(CommandCamera.isFollowSuspended(viewer));
        assertFalse(CommandCamera.shouldResendFollowCamera(viewer));

        CommandCamera.beginFollow(viewer, target, 2);
        CommandCamera.armFollowCameraRecovery(viewer);
        CommandCamera.suspendFollow(viewer, false);
        CommandCamera.close();
        assertFalse(CommandCamera.isFollowSuspended(viewer));
        assertFalse(CommandCamera.shouldResendFollowCamera(viewer));
    }
}
