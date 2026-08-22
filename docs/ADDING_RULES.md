# Adding rules to Carpet DDS Addition

The framework is designed so a normal rule is declared once, not once per
Minecraft version.

## 1. Declare metadata once

Add an entry to `src/main/rules/rules.json`:

```json
{
  "name": "someNewRule",
  "type": "boolean",
  "default": false,
  "categories": ["feature", "survival"],
  "description": {
    "en_us": "Short Carpet rule description.",
    "zh_cn": "规则的中文说明。"
  }
}
```

Supported cross-version scalar types are currently `boolean`, `int`, `double`,
and `String`. `en_us` is mandatory; additional language keys are optional and
fall back to English when absent.

The Gradle generator creates the Carpet-facing settings class, typed `DDSRules`
facade, and modern Carpet translation data.

Feature code reads:

```java
DDSRules.someNewRule()
```

and does not import the generated settings class directly.

## 2. Add a feature module

Create:

```text
src/main/java/carpetddsaddition/feature/<rule>/
```

Keep ordinary feature/business logic free of Minecraft-version conditionals
where practical.

```java
public final class SomeNewRule {
    private SomeNewRule() {
    }

    public static boolean enabled() {
        return DDSRules.someNewRule();
    }
}
```

Do not create empty handler/compat/manager classes merely to satisfy a template.
Simple rules should stay simple.

## 3. Add only the Mixin hook the feature needs

If the feature changes vanilla behavior, add a narrowly scoped Mixin under:

```text
src/main/java/carpetddsaddition/mixin/feature/<rule>/
```

and register it in `carpet-dds-addition.mixins.json`.

Do not put unrelated rules into one global Mixin.

## 4. Put version drift at the correct boundary

Feature-specific Minecraft API drift belongs in:

```text
feature/<rule>/compat/
```

Shared Minecraft compatibility belongs in:

```text
platform/minecraft/
```

Cross-generation Carpet API compatibility belongs in:

```text
platform/carpet/
```

Example:

```java
//#if MC >= 12100
//$$ newer API
//#else
older API
//#endif
```

Do not copy an entire feature into every `versions/<mc>/` directory. A
per-version source override is reserved for a genuinely different implementation
that cannot be isolated cleanly at a compatibility boundary.

## 5. Third-party integrations are not generic compatibility code

If a feature cooperates with another mod/subsystem, place that integration
under:

```text
integration/<mod>/
```

Do not put third-party integration code into a generic top-level `compat/`
directory.

## 6. Networked features

Do not add feature business logic directly to the shared transport layer unless
it is required for protocol/session operation.

Shared `network/` owns transport, handshake, capability negotiation, rate
limiting and routing. Feature packet handlers should be feature-owned.

Do not change the DDS protocol wire format merely to reorganize code.

## 7. Verification before release

At minimum run:

```bash
python3 scripts/verify-framework.py
python3 scripts/verify-preprocessor-roundtrip.py --skip-build
```

Smoke-build the oldest, reference and newest nodes:

```bash
./gradlew :1.14.4:build :1.21.11:build :26.2:build --stacktrace
```

On Windows:

```bat
gradlew.bat :1.14.4:build :1.21.11:build :26.2:build --stacktrace
```

Before release, run the full build:

```bash
./gradlew buildAndGather --stacktrace
```

For a node that claims one JAR supports several Minecraft releases, launch-test
every declared release before publishing that range.
