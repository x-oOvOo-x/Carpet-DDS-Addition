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

A DDS client installation is **not required** to connect to a server running DDS.

Players without DDS installed on the client can still connect normally and use DDS functionality that is implemented entirely on the server.

For example, actual item operations performed by `quickContainerAccess` are always authoritative on the server. Installing the same DDS version on the client only improves client-side behavior by reducing incorrect vanilla prediction, such as temporary item or shulker-content preview flicker.

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

The feature is restricted to **Creative-mode players**. Survival-mode and other non-Creative players cannot perform Undo or Redo.

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

Adds portable access for vanilla shulker boxes, Ender Chests and several workstation menus directly from the real player's own inventory, together with quick storage interactions for shulker boxes and Ender Chests.

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
- With an empty cursor, right-click a single supported item in the player's own inventory to open it directly.
- Stacked inventory targets keep vanilla right-click splitting behavior.
- Carry an ordinary item on the cursor and right-click a single shulker box or Ender Chest in the player's inventory to insert it quickly.
- Carry a single shulker box or Ender Chest and right-click an ordinary player-inventory stack to absorb it, or right-click an empty player slot to extract the last non-empty internal stack.

There is exactly one nested-opening exception: **a single shulker box inside the current player's own Ender Chest can be right-clicked and opened directly.** World containers such as chests, barrels, hoppers and container minecarts keep vanilla behavior, as do workstations stored inside shulker boxes. Carpet fake-player inventories and other players' inventories are not trusted sources.

Mutable shulker access always requires a stack count of exactly `1`. Invalid sessions are terminated rather than repaired by splitting stacked shulkers or giving replacement items. The server remains authoritative for all item changes.

The DDS client is optional. A server-only installation provides all gameplay functionality; installing the same DDS build on the client additionally suppresses incorrect vanilla local prediction for DDS quick right-clicks, reducing item and shulker-tooltip flicker.

Soft compatibility is provided for Carpet AMS Addition `largeShulkerBox` and `largeEnderChest`. AMS `largeEnderChest` requires the player to rejoin after enabling the rule before an already-online player's Ender Chest is actually resized to 54 slots.


- Type: `boolean`
- Default: `false`
- Suggested options: `true`, `false`
- Supported versions: `1.14.4–26.2`
- Note: Available portable workstation targets depend on which blocks and menus exist in the corresponding vanilla Minecraft version.
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
- Supported versions: `1.14.4–26.2`
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
- Supported versions: `1.14.4–26.2`
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
- Supported versions: `1.14.4–26.2`
- Categories: `feature`, `survival`

---

## No Respawn Block Explosion (noRespawnBlockExplosion)

Prevents beds and respawn anchors from exploding in dimensions where they would normally explode.

- Type: `boolean`
- Default: `false`
- Suggested options: `true`, `false`
- Supported versions: `1.14.4–26.2`
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

When a Survival player holds a damaged Mending item in the offhand, the item periodically consumes stored player experience to repair itself.

At most 1 XP is consumed every 5 ticks, and each XP repairs up to 2 durability.

A feedback sound is played when the item becomes fully repaired, with periodic audible feedback during continuous repair.

- Type: `boolean`
- Default: `false`
- Suggested options: `true`, `false`
- Categories: `feature`, `survival`
