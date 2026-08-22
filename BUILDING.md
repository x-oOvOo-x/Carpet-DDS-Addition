# Building Carpet DDS Addition

## Recommended environment

- JDK 25 installed and used by Gradle/IDEA.
- Gradle 9.x. The project is intentionally configured with `org.gradle.parallel=false` because ReplayMod/Fallen-Breath Preprocessor resolves adjacent version projects and parallel execution can trigger Gradle configuration-lock errors.

## Build Minecraft 1.21.11

From the repository root:

```powershell
gradle :1.21.11:clean :1.21.11:build --stacktrace --no-parallel
```

Do not write `:1.21.11\:build`.

After a complete build, inspect:

```powershell
Get-ChildItem .\versions\1.21.11\build\libs\
```

## Run a development client

```powershell
gradle :1.21.11:runClient --no-parallel
```

Create a single-player world and test:

```mcfunction
/carpet icePlaceWater true
/give @s minecraft:ice 64
```

## Build all anchors

```powershell
gradle buildAndGather --stacktrace --no-parallel
```

Gathered release jars are copied to:

```text
build/releases/
```
