# Architecture decisions

## 1. Build model

Carpet DDS Addition uses a ReplayMod/Fallen-Breath preprocessor graph with version subprojects.

`settings.json` defines the Minecraft target graph and the DDS development reference version. The historical preprocessor main project remains `1.15.2`; this is a mapping/source anchor, not the feature-development target. `1.21.11` is the current DDS development/reference version.

The multi-version model is shared-source first: ordinary feature code lives in `src/main`, version drift is isolated at compatibility boundaries, and `versions/<mc>/` remains metadata-only unless an irreducible per-version source override is required.

## 2. Rule model

`src/main/rules/rules.json` is the single source of truth for rule names, scalar types, defaults, categories, version availability and localized descriptions.

Gradle generates three classes per target:

- `CarpetDDSAdditionSettings`: Carpet-facing annotated fields;
- `DDSRules`: typed read-only facade for feature modules;
- `DDSRuleTranslations`: Carpet translation map for settings API targets that support it.

Feature code reads `DDSRules`; it does not import Carpet rule annotations or the generated settings class directly.

## 3. Source ownership model

DDS uses feature-oriented ownership with a small shared core.

```text
carpetddsaddition/
├─ CarpetDDSAddition.java  # Fabric/Carpet entry point only
├─ core/                   # generic dispatchers plus composition/bootstrap roots
├─ feature/                # DDS features; one vertical slice per feature
├─ platform/               # genuinely shared Carpet/Minecraft compatibility boundaries
├─ integration/            # third-party integration owned by DDS
├─ network/                # shared DDS transport/session/protocol infrastructure
└─ mixin/                  # narrow hooks grouped by feature/integration/network
```

A feature owns its business logic and feature-specific compatibility code. For example:

```text
feature/commandcamera/
├─ CommandCamera.java
├─ CameraState.java
├─ CommandCameraLifecycle.java
└─ compat/
   ├─ CommandCameraCompat.java
   ├─ CommandCameraCommandCompat.java
   └─ CommandCameraMapTeleportCompat.java
```

Feature-specific packet identifiers and handlers are owned by the corresponding feature `network/` package rather than the shared transport dispatcher.

## 4. Dependency direction

The intended high-level direction is:

```text
Fabric/Carpet entry point
        ↓
 core/bootstrap
   ↙    ↓    ↘
feature integration shared network
   ↓       ↓       ↓
feature compat   platform
        ↘   ↓   ↙
      Minecraft / Carpet API

platform callback → generic core dispatcher
Mixin hook → feature/integration/network entry point
feature → generated DDSRules
```

Shared infrastructure must not become a reverse dependency on individual features. In particular:

- `core/lifecycle` and `core/rules` are generic dispatch mechanisms and must not import individual feature/integration implementations;
- cross-feature wiring belongs in `core/bootstrap`;
- `network/` must remain feature-agnostic;
- `platform/` must not contain feature business logic or single-feature adapters merely because they contain version macros;
- feature code must not depend on `mixin/` implementation classes;
- `mixin/` contains hooks/accessors, not reusable business logic;
- features must not depend back on `core/bootstrap` composition classes.

## 5. Carpet API boundary

Use Carpet's public extension/settings APIs wherever the supported Carpet generation provides them.

Cross-generation Carpet API differences belong in:

```text
platform/carpet/
```

Feature code must not reproduce compatibility work already centralized there.

The framework supports both the historical Carpet settings API and modern `carpet.api.settings` generations while keeping those differences behind the platform boundary.

## 6. Minecraft compatibility boundary

Shared Minecraft API drift used by multiple vertical slices may belong in:

```text
platform/minecraft/
```

Feature-specific version drift belongs beside its feature:

```text
feature/<feature>/compat/
```

A helper is not promoted to `platform/minecraft` merely because it wraps Minecraft API differences. If its semantics are owned by one rule—for example consuming ice, filling a water cauldron and emitting the matching feedback for `icePlaceWater`—it remains feature-local.

Version macros are boundary mechanics. `//#if MC ...` is allowed only in `platform/`, feature-local `compat/`, `mixin/`, `network/`, or `integration/` code. Ordinary feature/business sources remain version-agnostic.

A `compat/` package is not a generic dumping ground: stable domain state and business logic should remain in the feature package unless the class genuinely exists to isolate API/version drift. Existing frozen subsystems may retain established layouts until a concrete compatibility or maintenance reason justifies moving them.

## 7. Third-party integration boundary

Code whose purpose is to cooperate with another mod or subsystem belongs in:

```text
integration/
```

DDS-owned GCA fake-player enhancements therefore live under `integration/gca/` rather than a generic compatibility package. GCA class probing, reflective rule access and Carpet fake-player API drift stay inside that integration slice.

Minecraft-version compatibility and third-party integration are separate concerns. Integration code may contain version boundaries when the integrated API itself varies, but shared feature code should not gain a dependency on an optional integration merely for convenience. Shared network may be consumed by an integration, but shared network must not import the integration implementation.

## 8. Network policy

The DDS v1 protocol is transport infrastructure, not a feature container.

Shared network code owns:

- channel and wire format;
- handshake/capability negotiation;
- connection state;
- rate limiting;
- server-thread dispatch;
- packet-route registration infrastructure;
- fail-closed protocol state.

Feature and integration slices own their packet identifiers and business handlers. Shared network code routes registered packets but does not import feature or integration implementations.

Server-initiated protocol state broadcasts are control-plane operations and must not be routed through client-originated packet rate limiting.

## 9. Lifecycle and composition policy

Carpet lifecycle callbacks are platform entry points, not the home of feature lifecycle logic.

The current direction is:

```text
Carpet callback
    ↓
DdsLifecycle                 generic dispatcher
    ↑
DdsRuntimeBootstrap          registers ordered feature/network components
```

`DdsLifecycle` and `DdsRuleEvents` deliberately know nothing about concrete features. `DdsRuntimeBootstrap` is the runtime composition root and owns the explicit ordering of lifecycle components and rule-change observers because changing that order can change observable behavior.

One-time feature/integration packet registration is composed separately by `DdsNetworkBootstrap`; the bootstrap owns registration order while packet definitions remain inside their vertical slices.

## 10. Mixin policy

Mixins are narrowly scoped hooks and accessors. They should modify Minecraft behavior only where an ordinary API is insufficient.

```text
mixin/feature/<feature>/
mixin/integration/<integration>/
mixin/network/
```

A normal feature Mixin should be limited to injection/signature mechanics, argument capture, delegation and return/cancellation bridging. Multi-step rule decisions, entity classification, probability logic, persistence changes and reusable state mutation belong in the owning feature or feature-local compatibility layer.

Injection descriptors, `@At`, ordinal, slice, locals, cancellation behavior and priority are compatibility contracts and must not be changed as incidental cleanup.

`@Overwrite` is prohibited. `injectors.defaultRequire` remains `1`, and the Mixin audit is an explicit validation gate rather than part of ordinary builds.

For versions where a shared Mixin source is intentionally inactive, use `@Pseudo` with a non-existent target under:

```text
carpetddsaddition.disabled.*
```

Do not create production placeholder classes solely to satisfy an inactive Mixin target. A disabled-version target must remain inert and must not introduce a real runtime class dependency.

## 11. Performance policy

Optimize measured or structurally obvious hot paths, not cold configuration code for cosmetic gains.

Priority paths are per-tick entity/player hooks, packet handling, inventory operations, spawning, block scheduled ticks and repeated world/entity scans. Hot paths should use cheap early exits before expensive lookups, raycasts, reflection, allocation or NBT work.

Examples of the intended pattern include category-level passive-spawn rejection before position search, empty follow-session rejection before Camera player work, and dimension-index rejection before enhanced-name-tag raycasts.

Established Undo/Redo and QCA state machines are not rewritten during a general performance pass when their existing hot-path invariants already avoid unnecessary NBT, chunk access or packet work.

## 12. Refactor policy

Architecture cleanup is performed in checkpoints. A structural change must preserve feature behavior and established version boundaries unless the checkpoint explicitly targets a behavior or compatibility defect.

Before merging a structural checkpoint:

1. run `py -3 scripts/verify-framework.py`;
2. build the affected compatibility boundary versions;
3. run the relevant Mixin audits when Mixin sources/configuration changed;
4. use a full supported-version build plus `auditMixinsAll` before final merge of a broad refactor.

Frozen subsystems such as established QCA and Undo/Redo state machines are not reorganized merely for package aesthetics. Changes there require a concrete correctness, compatibility, performance or maintenance benefit.

## 13. Publishing policy

Compilation of an anchor and runtime compatibility of every Minecraft release claimed by an artifact are separate release gates.

A node may claim several Minecraft releases only after runtime smoke tests pass for the declared range.
