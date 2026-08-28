# Rules

[中文](rules-zh_cn.md) | **English**

This document lists the Carpet rules currently provided by Carpet DDS Addition and explains their behavior in detail.

---

## Place Water with Ice (icePlaceWater)

When holding normal ice and right-clicking without sneaking, performs a water-bucket-like placement action and consumes one ice block.

When used on a cauldron, the cauldron is filled directly to the maximum water level.

Sneak-right-clicking does not trigger this rule and keeps the vanilla ice placement behavior.

- Type: `boolean`
- Default: `false`
- Suggested options: `true`, `false`
- Categories: `feature`, `survival`

---

## No Normal Campfire Damage (noCampfireDamage)

Prevents lit normal campfires from damaging living entities.

Soul campfires are not affected by this rule and continue to deal damage as in vanilla.

Other campfire behavior, including lighting, extinguishing, cooking, and smoke, remains vanilla.

- Type: `boolean`
- Default: `false`
- Suggested options: `true`, `false`
- Categories: `feature`, `survival`

---

## No Sweet Berry Bush Damage or Slowdown (noSweetBerryBushEffects)

Prevents sweet berry bushes from damaging or slowing living entities.

Growth, harvesting, and other vanilla sweet berry bush behavior remain unchanged.

- Type: `boolean`
- Default: `false`
- Suggested options: `true`, `false`
- Categories: `feature`, `survival`

---

## Baby Piglins Do Not Pick Up Items (noBabyPiglinItemPickup)

Prevents baby piglins from targeting dropped items and from picking them up.

Adult piglin item pickup behavior is unaffected, and vanilla bartering remains unchanged.

- Type: `boolean`
- Default: `false`
- Suggested options: `true`, `false`
- Supported versions: `1.16.1+`
- Categories: `feature`, `survival`

---

## Sneak Toggle Iron Trapdoors (sneakToggleIronTrapdoor)

Allows players to manually open or close an iron trapdoor by sneak-right-clicking it with an empty main hand.

Redstone-powered state handling and all other vanilla behavior remain unchanged.

- Type: `boolean`
- Default: `false`
- Suggested options: `true`, `false`
- Categories: `feature`, `survival`

---

## Natural Passive Spawning Control (naturalPassiveSpawning)

Controls natural spawning for different groups of passive mobs.

The value can be set to `all`, `none`, or a comma-separated combination of allowed spawning groups.

Available groups:

- `land`: land passive mobs
- `fish`: fish
- `squid`: squid
- `axolotl`: axolotls
- `bat`: bats
- `dolphin`: dolphins

When `land` is disabled, initial land-creature spawning in newly generated chunks is also disabled.

Examples:

```text
/carpet naturalPassiveSpawning all
```

Allows all controlled passive mobs to spawn naturally.

```text
/carpet naturalPassiveSpawning none
```

Disables all controlled passive mob natural spawning.

```text
/carpet naturalPassiveSpawning land,fish
```

Allows only land passive mobs and fish to spawn naturally.

```text
/carpet naturalPassiveSpawning squid,dolphin
```

Allows only squid and dolphins to spawn naturally.

- Type: `String`
- Default: `all`
- Suggested options: `all`, `none`, `land`, `fish`, `squid`, `axolotl`, `bat`, `dolphin`
- Categories: `optimization`, `survival`

---

## Pet Regeneration (petRegeneration)

Allows owned tamed pets that do not normally regenerate health passively to recover health slowly.

The regeneration rate matches the vanilla passive regeneration rate used by horse-type mobs.

Only tamed pets with an owner are affected.

- Type: `boolean`
- Default: `false`
- Suggested options: `true`, `false`
- Categories: `feature`, `survival`

---

## Enhanced Name Tags (enhancedNameTag)

Enhances vanilla name tags with formatted entity names and persistent floating labels for blocks.

### Entity Naming

When using a renamed name tag on an entity, extended formatting can be used.

Legacy color codes are supported:

```text
&0 &1 &2 &3 &4 &5 &6 &7
&8 &9 &a &b &c &d &e &f
```

Text styles are supported:

```text
&l  Bold
&o  Italic
&n  Underline
&m  Strikethrough
&r  Reset formatting
```

RGB hexadecimal colors are supported:

```text
&#RRGGBB
```

Example:

```text
&#FF5555Red Text
```

Line breaks are supported:

```text
\n
```

Several symbol aliases are supported:

```text
:warning:  → ⚠
:check:    → ✓
:cross:    → ✕
:star:     → ★
:gear:     → ⚙
:right:    → →
:left:     → ←
:up:       → ↑
:down:     → ↓
:heart:    → ❤
```

To insert a literal `&`, use:

```text
&&
```

### Block Naming

Sneak-right-click a block with a renamed name tag to add a floating label to that block.

Block labels have the following behavior:

- Label data is saved persistently with the world.
- Labels remain after a server restart.
- A label is shown only while a player is looking at the corresponding block.
- The floating text disappears automatically when the player looks away.
- No persistent real Text Display entity is kept in the server world.
- When a player destroys the block, its label is removed.
- If the block is replaced with a different block type, the stale label is cleaned up during later validation.

When the rule is disabled:

- New labels cannot be added.
- Existing labels cannot be modified.
- Existing labels cannot be deleted through the rule.
- Existing entity names and block labels remain stored and continue to display normally.

- Type: `boolean`
- Default: `false`
- Suggested options: `true`, `false`
- Supported versions: `1.19.4+`
- Categories: `feature`, `survival`

---

## Right-Click Crop Harvesting (rightClickHarvest)

Right-click a fully mature crop to harvest it and automatically replant it using one suitable seed or crop item from the generated drops.

Currently supported:

- Wheat
- Carrots
- Potatoes
- Beetroot
- Nether wart
- Cocoa

Harvest drops are still generated using the vanilla loot logic.

If the generated drops contain the appropriate replanting item, one item is consumed and the crop is replanted.

For example:

- Wheat consumes one wheat seed.
- Carrots consume one carrot.
- Potatoes consume one potato.
- Beetroot consumes one beetroot seed.
- Nether wart consumes one nether wart.
- Cocoa consumes one cocoa bean.

If the generated drops do not contain the required replanting item, the harvest still succeeds, but no free replanting item is created and the crop position becomes air.

This rule does not generate additional experience.

- Type: `boolean`
- Default: `false`
- Suggested options: `true`, `false`
- Categories: `feature`, `survival`

---

## Renewable Calcite (renewableCalcite)

Adds a renewable calcite mechanic modeled after vanilla basalt generation.

Lava turns into calcite when both of the following conditions are met:

1. Smooth basalt is directly below the lava.
2. An amethyst block exists above the lava or on one of its four horizontal sides.

Example layout:

```text
     Amethyst Block
           │
Amethyst ─ Lava ─ Amethyst
           │
     Amethyst Block

       Smooth Basalt
```

Only one amethyst block is required among the five valid catalyst directions.

The block below the lava is reserved for smooth basalt and is not checked as an amethyst catalyst position.

The generation behavior is designed to mirror vanilla basalt-style conversion.

- Type: `boolean`
- Default: `false`
- Suggested options: `true`, `false`
- Supported versions: `1.17+`
- Categories: `feature`, `survival`

---

## Renewable Tuff (renewableTuff)

Adds a renewable tuff mechanic modeled after vanilla basalt generation.

Lava turns into tuff when both of the following conditions are met:

1. Smooth basalt is directly below the lava.
2. A magma block exists above the lava or on one of its four horizontal sides.

Example layout:

```text
       Magma Block
           │
Magma ─── Lava ─── Magma
           │
       Magma Block

       Smooth Basalt
```

Only one magma block is required among the five valid catalyst directions.

The block below the lava is reserved for smooth basalt and is not checked as a magma catalyst position.

The generation behavior is designed to mirror vanilla basalt-style conversion.

- Type: `boolean`
- Default: `false`
- Suggested options: `true`, `false`
- Supported versions: `1.17+`
- Categories: `feature`, `survival`

---

## No Bee Anger (noBeeAnger)

Prevents bees from entering an angry state and attacking entities.

When enabled, newly triggered bee anger is suppressed.

Other vanilla bee behavior remains intact, including:

- Collecting nectar
- Pollinating
- Returning to a bee nest or beehive
- Flying
- Searching for flowers
- Other non-anger-related behavior

This rule does not merely cancel bee damage; it suppresses the anger state itself.

- Type: `boolean`
- Default: `false`
- Suggested options: `true`, `false`
- Supported versions: `1.15+`
- Categories: `feature`, `survival`

---

## Infinite Vault Opening (infiniteVaultOpening)

Allows the same player to use keys to repeatedly open the same vault.

While the rule is enabled, vault eligibility checks temporarily treat the player as if they have not previously received a reward.

The rule does not delete or clear the vault's original rewarded-player records.

Therefore:

```text
Rule disabled
    ↓
Player opens the vault for the first time
    ↓
Player UUID is recorded normally

Rule enabled
    ↓
Reward history is ignored for eligibility checks
    ↓
The same player can open the vault repeatedly

Rule disabled again
    ↓
The original reward record still exists
    ↓
Vanilla one-time reward restriction is restored
```

The following vault mechanics remain vanilla:

- Key type validation
- Key consumption
- Loot tables
- Reward generation
- Reward ejection
- Vault animations
- Other normal and ominous vault mechanics

The vault must still satisfy its normal vanilla activation conditions.

Players must be close enough for the vault to become active before inserting a key.

- Type: `boolean`
- Default: `false`
- Suggested options: `true`, `false`
- Supported versions: `1.21+`
- Categories: `feature`, `survival`

---

## DDS Network Protocol (ddsNetworkProtocol)

Controls the optional DDS client-server network protocol.

This protocol provides a dedicated communication channel for DDS features that support negotiated client-side enhancements.

A DDS client installation is not required to connect to a server running DDS.

Players without DDS installed on the client can still connect normally and use DDS functionality that is implemented entirely on the server.

For example, most `quickContainerAccess` item operations are implemented authoritatively on the server. Installing the same DDS version on the client additionally reduces incorrect vanilla prediction flicker and enables negotiated QCA slot interactions on the Creative survival-inventory tab.

It is generally recommended to keep this rule enabled.

- Type: `boolean`
- Default: `true`
- Suggested options: `true`, `false`
- Supported versions: `1.14.4–26.2`
- Categories: `feature`

---

## Undo and Redo (undoRedo)

Allows Creative-mode players to undo or redo their own recorded world changes.

When enabled, the following commands are available:

```text
/undo
/redo
```

`/undo` reverts the current player's most recent undoable action, while `/redo` reapplies the most recently undone action.

The feature is restricted to Creative-mode players. Survival-mode and other non-Creative players cannot perform Undo or Redo.

The DDS client is optional. A server-only installation provides the complete `/undo` and `/redo` workflow.

When compatible DDS builds are installed on both the client and server and `ddsNetworkProtocol` is enabled, the following shortcuts are also available:

```text
Ctrl+Z       Undo
Ctrl+Alt+Z   Redo
```

Undo/Redo tracks not only immediate block changes caused by a player action, but also delayed consequences that can still be attributed to that action, including block entities, entity changes, container interactions, Scheduled Ticks, Block Events, delayed redstone chains, Sculk Sensor vibration propagation, and TNT or other consequences triggered by those chains.

For example:

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

Delayed propagation recreated by Redo remains associated with the original action and can therefore be undone again.

Before applying Undo or Redo, DDS checks whether the affected world state has been changed by later operations so that old history does not forcibly overwrite unrelated subsequent changes.

Undo/Redo history is stored only in server runtime memory and is not persisted into the world save. Each player has a bounded history budget, and older records are automatically evicted when the limit is reached.

- Type: `boolean`
- Default: `false`
- Suggested options: `true`, `false`
- Supported versions: `1.21.9+`
- Categories: `feature`, `creative`

---

## Quick Container Access (quickContainerAccess)

Adds portable access for vanilla shulker boxes, Ender Chests and several workstation menus, together with quick storage interactions for shulker boxes and Ender Chests.

Portable targets include:

- Shulker boxes (all colors)
- Ender Chest
- Crafting Table
- Stonecutter
- Anvil, Chipped Anvil and Damaged Anvil
- Loom
- Cartography Table
- Grindstone
- Smithing Table
- Enchanting Table

Main interactions:

- Right-click the air while holding a supported item to open its menu; shulker boxes and anvils must be single items.
- With an empty cursor, right-click a single supported item in a validated container slot to open it directly.
- Stacked inventory targets keep vanilla right-click splitting behavior.
- Carry an ordinary item on the cursor and right-click a single shulker box or Ender Chest to insert it quickly.
- Carry a single shulker box or Ender Chest and right-click an ordinary stack to absorb it, or right-click an empty compatible slot to extract the last non-empty internal stack.
- While a shulker box is open through QCA, carry another single shulker box and right-click the opened shulker's protected source slot to merge the carried shulker's contents into the currently open shulker. Compatible stacks are merged first, then empty slots are filled, and any remainder stays in the carried shulker.

In Creative mode, QCA operates on real player-inventory slots only while the survival-inventory tab is selected. Creative item-picker tabs, including the player hotbar row rendered at the bottom of those tabs, are intentionally left entirely to vanilla: QCA does not perform portable opening, quick storage, right-drag storage, or local prediction suppression there. QCA slot interactions on the Creative survival-inventory tab require a compatible DDS client and an available DDS network protocol.

Nested opening is supported from validated slots in persistent vanilla container menus and from the GCA/DDS fake-player interfaces when GCA grants access. This allows chains such as:

```text
Player inventory → Shulker Box → Workstation
Chest → Shulker Box → Workstation
Ender Chest → Shulker Box → Workstation
Fake-player inventory → Shulker Box → Workstation
```

Ender Chests always resolve to the current inventory owner's canonical Ender Chest. Re-entering an Ender Chest already present in the active path navigates back to that existing node instead of recursively nesting another copy. GCA control slots, invalid/result-only slots, and slots that do not permit normal pickup or placement are excluded from direct QCA mutation.

Mutable shulker access always requires a stack count of exactly `1`. Invalid sessions are terminated rather than repaired by splitting stacked shulkers or giving replacement items. Active item-backed sources are identity-leased so another QCA writer cannot mutate the same source concurrently. The server remains authoritative for all item changes.

A DDS client is not required for the main server-recognizable QCA gameplay paths. Installing the same DDS build additionally enables the dedicated Creative survival-inventory slot interactions and suppresses incorrect vanilla local prediction for DDS quick right-clicks, reducing item and shulker-tooltip flicker.

Soft compatibility is provided for Carpet AMS Addition `largeShulkerBox` and `largeEnderChest`. AMS `largeEnderChest` requires the player to rejoin after enabling the rule before an already-online player's Ender Chest is actually resized to 54 slots.

- Type: `boolean`
- Default: `false`
- Suggested options: `true`, `false`
- Note: Available portable workstation targets depend on which blocks and menus exist in the corresponding vanilla Minecraft version.
- Supported versions: `1.14.4–26.2`
- Categories: `feature`, `survival`

---

## Disable QCA Shulker Sounds (disableQuickContainerAccessSounds)

When enabled, Quick Container Access does not play the shulker-box open or close sounds generated by QCA shulker interactions.

Ender Chest, anvil, and other QCA sounds and behavior remain unchanged.

- Type: `boolean`
- Default: `false`
- Suggested options: `true`, `false`
- Supported versions: `1.14.4–26.2`
- Categories: `feature`, `survival`

---

## DDS Fake-Player Inventory (fakePlayerInventory)

Enhances GCA fake-player inventory and Ender Chest interfaces for players using a DDS client. This rule does not grant fake-player inventory access by itself: GCA remains responsible for whether the viewer may open the fake player's inventory or Ender Chest.

When the rule is enabled and the viewer has a compatible DDS client, DDS replaces the corresponding GCA screen with its enhanced interface. When the rule is disabled, or when the viewer does not have DDS installed, GCA's original interface and behavior remain unchanged.

The enhanced fake-player inventory provides action controls, editable attack/use intervals, number-key and on-screen hotbar selection, and separate main-inventory/hotbar container grouping so supported sorters can operate on those regions independently.

The enhanced fake-player Ender Chest uses the fake player's real Ender Chest inventory, supports both the vanilla 27-slot size and Carpet AMS Addition's 54-slot `largeEnderChest`, and provides bulk store, take, drop, sneak-toggle and disconnect controls.

DDS Quick Container Access can operate on the real fake-player inventory and Ender Chest slots while preserving GCA control slots and server-authoritative item validation.

- Type: `boolean`
- Default: `false`
- Suggested options: `true`, `false`
- Supported versions: `1.19.2–26.2`
- Categories: `feature`, `survival`

---

## Direct Block Drops (directBlockDrops)

Sends item drops produced by player block breaking and DDS right-click harvesting directly into the player's inventory whenever space is available.

The rule handles drops that belong to the same synchronous drop chain as the current player action, including:

- Drops produced directly by player block breaking;
- Synchronous chain drops caused by bamboo, cactus, sugar cane, scaffolding, and similar blocks breaking or losing support;
- Items released from broken container blocks;
- Synchronous harvest drops produced by `rightClickHarvest`.

Items are still generated according to vanilla drop and loot logic. This rule only changes where the resulting item entities are delivered.

When the player's inventory has enough space, the items are inserted directly into the inventory.

When the inventory is full or cannot accept all items, the remaining items continue to drop normally into the world.

The rule only tracks synchronous drops associated with the current player action. It does not continue collecting unrelated items produced during later game ticks.

- Type: `boolean`
- Default: `false`
- Suggested options: `true`, `false`
- Categories: `feature`, `survival`

---

## Safe Spectator Camera (commandCamera)

Allows eligible Survival-mode players to temporarily enter a protected spectator Camera mode.

### Basic Commands

```text
/c
```

Enters Camera mode.

When already in Camera mode, executing `/c` again exits Camera mode.

When leaving Camera mode, the player is first returned to the position saved when Camera mode was entered, and is then restored to Survival mode.

```text
/c back
```

Returns the player to the position originally saved when Camera mode was entered while keeping the player in spectator mode.

This is useful for quickly returning to the original location after flying away without immediately leaving Camera mode.

```text
/c follow <player>
```

Spectates the specified player.

If Camera mode is not currently active, Camera mode is entered first and the specified player is then spectated.

If Camera mode is already active, the current spectating target is switched directly.

### Entry Requirements

The player's state is checked before Camera mode can be entered.

The player must:

- Be alive;
- Be in Survival mode;
- Be on the ground;
- Not be in a suffocating or air-depleted state;
- Not be on fire.

If the required conditions are not satisfied, Camera mode is not entered.

### State Protection

The player's original position, dimension, and relevant state are saved when Camera mode is entered.

When Camera mode is exited, the player is first returned to the originally saved position and is then restored to Survival mode.

This prevents spectator movement from being used to permanently relocate a Survival-mode player.

If the player disconnects or the Camera session becomes invalid, recovery behavior prioritizes restoring the player's state instead of leaving the player in an invalid spectator session.

- Type: `boolean`
- Default: `false`
- Suggested options: `true`, `false`
- Supported versions: `1.16.5–26.2`
- Categories: `feature`, `survival`

---

## No Villager Witch Conversion (noVillagerWitchConversion)

Prevents villagers from transforming into witches when struck by lightning.

- Type: `boolean`
- Default: `false`
- Suggested options: `true`, `false`
- Categories: `feature`, `survival`

---

## Explosion No Block Damage (explosionNoBlockDamage)

Controls whether selected explosion types can destroy blocks.

When an explosion type is enabled in this rule, the explosion still occurs normally but no longer destroys blocks in the world.

Supported explosion types:

- `creeper`: Creeper explosions;
- `tnt`: TNT explosions;
- `tnt_minecart`: TNT Minecart explosions;
- `end_crystal`: End Crystal explosions;
- `ghast`: Ghast fireball explosions;
- `wither`: Wither-related explosions;
- `wither_skull`: Wither Skull explosions.

The rule can be set to:

```text
none
```

All explosions retain vanilla block-destruction behavior.

```text
all
```

Prevents all explosion types supported by this rule from destroying blocks.

Multiple types can also be combined using commas, for example:

```text
tnt,creeper
```

Only TNT and Creeper explosions are prevented from destroying blocks.

```text
tnt,tnt_minecart,end_crystal
```

Only TNT, TNT Minecart, and End Crystal explosions are prevented from destroying blocks.

Explosion sources not listed by this rule retain their vanilla behavior.

- Type: `String`
- Default: `none`
- Suggested options: `none`, `all`, `creeper`, `tnt`, `tnt_minecart`, `end_crystal`, `ghast`, `wither`, `wither_skull`
- Categories: `feature`, `survival`

---

## No Respawn Block Explosion (noRespawnBlockExplosion)

Prevents beds and respawn anchors from exploding in dimensions where they would normally explode.

- Type: `boolean`
- Default: `false`
- Suggested options: `true`, `false`
- Categories: `feature`, `survival`

---

## Disable Zombie Sieges (disableZombieSieges)

Prevents village zombie sieges from spawning.

Normal mob spawning, zombie reinforcements, raids, and other spawning mechanics remain vanilla.

- Type: `boolean`
- Default: `false`
- Suggested options: `true`, `false`
- Categories: `feature`, `survival`

---

## No Airborne Mining Penalty (noAirborneMiningPenalty)

Removes the mining-speed penalty applied while the player is not on the ground.

Underwater mining and all other mining-speed modifiers remain vanilla.

- Type: `boolean`
- Default: `false`
- Suggested options: `true`, `false`
- Categories: `feature`, `survival`

---

## Stonecut Deepslate (stonecutterCutsDeepslate)

Allows deepslate to use every stonecutting recipe available to cobbled deepslate.

The rule only expands valid stonecutter inputs for deepslate and does not change the vanilla output counts of those recipes.

- Type: `boolean`
- Default: `false`
- Suggested options: `true`, `false`
- Supported versions: `1.17+`
- Categories: `feature`, `survival`

---

## Stonecut Wood (stonecutterCutsWood)

Adds DDS-generated stonecutter recipes for supported vanilla wood families.

These recipes exist only while the rule is enabled. When the rule is disabled, DDS removes its generated wood stonecutting recipes from the server recipe manager.

Standard wood families support logs, wood, stripped logs and stripped wood. Nether wood families use stems, hyphae and their stripped variants. Bamboo uses bamboo blocks and stripped bamboo blocks.

For example, oak supports:

```text
1 Oak Log -> 4 Oak Planks
1 Oak Log -> 4 Oak Buttons
1 Oak Log -> 4 Oak Stairs
1 Oak Log -> 8 Oak Slabs
1 Oak Log -> 2 Oak Doors
1 Oak Log -> 4 Oak Fences
1 Oak Log -> 1 Oak Fence Gate
1 Oak Log -> 2 Oak Pressure Plates
1 Oak Log -> 1 Oak Shelf
1 Oak Log -> 2 Oak Signs
1 Oak Log -> 4 Oak Trapdoors
1 Oak Log -> 1 Stripped Oak Log

1 Oak Plank -> 1 Oak Button
1 Oak Plank -> 1 Oak Fence
1 Oak Plank -> 2 Oak Slabs
1 Oak Plank -> 1 Oak Stair
1 Oak Plank -> 1 Oak Trapdoor

1 Oak Stair -> 1 Oak Slab
```

Wood, stripped logs and stripped wood use the same direct-processing outputs as their corresponding log family.

Equivalent recipes are generated for other supported vanilla wood families when the corresponding vanilla items exist.

- Type: `boolean`
- Default: `false`
- Suggested options: `true`, `false`
- Supported versions: `1.21.3–26.2`
- Categories: `feature`, `survival`

---

## Smelt Concrete Powder to Glass (smeltConcretePowderToGlass)

Allows all 16 colors of concrete powder to be smelted in a normal furnace into the corresponding stained glass.

Only the normal furnace is affected; blast furnaces and smokers remain vanilla.

- Type: `boolean`
- Default: `false`
- Suggested options: `true`, `false`
- Categories: `feature`, `survival`

---

## No Bee Water Damage (noBeeWaterDamage)

Prevents bees from taking drowning damage from water.

Water physics, bee AI, and all other vanilla behavior remain unchanged.

- Type: `boolean`
- Default: `false`
- Suggested options: `true`, `false`
- Supported versions: `1.15+`
- Categories: `feature`, `survival`

---

## Mending Absorbs Stored XP (mendingAbsorbXp)

When a Survival player holds a damaged Mending item in the offhand, the item consumes stored player experience to repair itself.

During normal repair, XP is deducted smoothly each game tick while durability is settled once every 10 ticks. At 20 TPS, the maximum repair rate is 100 durability per second, with each XP repairing up to 2 durability.

While sneaking, available XP is consumed immediately to repair the current offhand item as much as possible in a single operation.

Experience-orb pickup sounds play periodically during continuous repair, and the player level-up sound plays when the item becomes fully repaired.

- Type: `boolean`
- Default: `false`
- Suggested options: `true`, `false`
- Categories: `feature`, `survival`
