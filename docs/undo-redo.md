# Undo and Redo (undoRedo)

[中文](undo-redo-zh_cn.md) | **English**

`undoRedo` provides server-authoritative Undo and Redo for Creative-mode players.

- Supported versions: `1.21.9+`
- Type: `boolean`
- Default: `false`
- Categories: `feature`, `creative`

## Enable the rule

```text
/carpet undoRedo true
```

When the rule is disabled, Undo/Redo commands and client shortcuts do not perform any undo or redo operation.

## Commands

```text
/undo
/redo
```

`/undo` reverts the current player's most recent undoable action. `/redo` reapplies the most recently undone action.

The feature is restricted to Creative-mode players. Survival-mode and other non-Creative players cannot execute Undo or Redo.

## Client shortcuts

The DDS client is optional. A server-only installation still provides the full `/undo` and `/redo` command workflow.

When compatible DDS builds are installed on both the client and server and `ddsNetworkProtocol` is enabled, the following shortcuts are available:

```text
Ctrl+Z       Undo
Ctrl+Alt+Z   Redo
```

The shortcuts are only client-side entry points. The server still enforces the `undoRedo` rule and the player's game mode.

## Tracked changes

Undo/Redo tracks world changes directly produced by a player action together with delayed consequences that can still be attributed to that action, including:

- block-state changes;
- block entities and their data;
- entity creation, removal, and related state changes;
- item transfers between containers and the player's inventory;
- delayed work such as Scheduled Ticks and Block Events;
- delayed redstone chains;
- Sculk Sensor vibration propagation;
- TNT and other later consequences triggered by those chains.

A validated example is:

```text
Sculk Sensor A
→ Sculk Sensor B
→ Repeater
→ TNT
→ /undo
→ /redo
→ TNT is activated again
→ /undo
```

Delayed redstone and vibration work recreated by Redo remains associated with the original action, so it can be undone again.

## Conflict protection

Before applying Undo or Redo, DDS checks whether affected world state has been changed by later actions.

Conflicting portions are not force-restored when doing so would overwrite unrelated later changes.

Autonomous movement, countdowns, and other lifecycle changes that belong to the same originating action are distinguished from actual later player conflicts where possible.

## History

Undo/Redo history is kept in server runtime memory and is not persisted into the world save.

Each player has a bounded history budget; older records are evicted when the history reaches its memory limit.
