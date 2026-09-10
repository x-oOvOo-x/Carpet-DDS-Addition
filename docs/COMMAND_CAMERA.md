# commandCamera state and recovery contract

`commandCamera` stores a world-owned, immutable snapshot when a Survival player enters `/c`. The snapshot is intentionally smaller than full player NBT and exists only to restore the Camera origin and the Survival state that this feature explicitly pauses.

## Persistent Session v2

Every active player record stores:

- original game mode;
- dimension;
- position (`x/y/z`);
- view (`yaw/pitch`);
- velocity (`motionX/Y/Z`);
- `fallDistance`;
- `fallFlying`;
- `airSupply`;
- `remainingFireTicks`;
- `frozenTicks` (`1.17+`; zero/no-op before powder-snow freezing exists);
- `fallFlyingTicks`.

The old 10-field Session v1 remains readable. v1 records use safe defaults for fields they could not have captured and are rewritten as v2 on the next save. In particular, the legacy air sentinel means “restore the player's current maximum air” rather than pretending the old record knew a historical air value.

Malformed records are isolated per UUID. Unknown versions, invalid booleans, non-finite positions/motion, and invalid counters do not invalidate other Camera sessions.

## What is deliberately not snapshotted

DDS does not snapshot or roll back:

- swimming/on-ground/pose flags;
- vehicle identity, vehicle motion, or riding relationships;
- active item-use progress, Riptide/spin attacks, or other transient actions;
- potion/status effects;
- health, absorption, hunger, saturation, experience;
- inventory, Ender Chest, or advancements.

Potion/status effects continue their normal countdown while Camera mode is active. `/c` therefore does **not** freeze the complete Survival timeline.

Mounted players are rejected before entering Camera, including when `noCameraSurvivalRestrictions=true`.

## Two restore paths

### Camera-only return: `/c back` and void protection

This path remains Spectator-only. It stops follow, returns the camera to the player, teleports to the Camera return point, and clears spectator velocity. It never restores velocity or any Survival-only snapshot field and never changes/deletes the persistent Session.

For an origin below the dimension safety floor, only the Camera return Y is derived as:

```text
max(originY, minBuildHeight + 1)
```

The immutable original Y remains unchanged. Explicit `/c` exit still attempts to return to the exact original Y and original falling state.

Void damage is always consumed while a Camera Session is active. Auto-return failure keeps the Session and rate-limits feedback instead of allowing a death loop to destroy recoverability.

### Explicit final `/c` exit

The exit transaction is ordered as follows:

1. read the immutable Session;
2. clear follow and use the player as camera target;
3. teleport to the exact original dimension/position/view and verify it;
4. restore the original game mode;
5. restore velocity, fall distance, Elytra state/ticks, air, fire and freezing progress;
6. verify critical state (game mode and Elytra state);
7. force-save Minecraft playerdata;
8. remove the DDS Session and atomically rewrite the Session file;
9. report success.

Any failure before the final Session commit keeps the Session, clears follow, attempts to return the player to Spectator/self-camera, and leaves the operation retryable.

## Lifecycle and self-healing

Disconnect, client exit/crash, server stop, server switch, death/respawn, and external game-mode changes do not delete a Session. On player ticks, an active Session reasserts Spectator state for a living player. Process-local follow state and void-feedback cooldowns are cleared on disconnect and are never persisted.

Turning `commandCamera` off does not strand an existing Session: an active player can still use `/c` to perform the explicit final restore and exit.

## `noCameraSurvivalRestrictions`

`false` keeps the existing safety checks (grounded, full air, not on fire) in addition to the mandatory alive/Survival/not-mounted checks.

`true` only bypasses those environmental safety checks. It permits entry while falling, Elytra flying, underwater/low on air, burning, or near/inside the void, while the persistent Session v2 retains the supported Survival state needed for later recovery.
