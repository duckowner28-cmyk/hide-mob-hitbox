# Hide Mob Hitboxes

A tiny client-side Fabric mod for Minecraft **1.21.11**. It hides the debug
hitbox outlines (the boxes you get from `F3+B`) for every entity **except
players** — mobs, item frames, boats, minecarts, etc. all stop drawing their
outline, but player hitboxes still show.

## How it works

Every hitbox outline, no matter what turned hitbox-rendering on, is drawn
through one choke point: `EntityRenderDispatcher#renderHitboxes(...)`. This
mod injects at the very start of that method (`EntityRenderDispatcherMixin`)
and cancels it unless the entity being drawn is a player
(`PlayerEntityRenderState`). Because it hooks the draw call itself rather
than the on/off toggle, it works regardless of *why* hitboxes were turned on
— vanilla `F3+B`, a keybind mod, or another mod that force-enables hitboxes
for PvP/ESP purposes.

To make it win out over other mods that might also try to draw mob
hitboxes, the mixin is registered with a low priority (`priority = 100` in
`EntityRenderDispatcherMixin`, default is 1000) so it merges — and runs —
before most other mods' injectors at the same spot. **Important honesty
note:** there's no way to guarantee it beats literally every other mod. A
mod with an even lower priority, or one that hooks a completely different
part of the pipeline (e.g. draws its own boxes via raw GL calls instead of
going through vanilla's method), could still show mob hitboxes. If you find
a specific mod that still slips through, tell me which one and I can add a
more targeted mixin for it.

## Project layout

```
hide-mob-hitboxes/
├── build.gradle
├── settings.gradle
├── gradle.properties
└── src/main/
    ├── java/com/example/hidemobhitboxes/
    │   ├── HideMobHitboxesClient.java      (client entrypoint, just logs on load)
    │   └── mixin/EntityRenderDispatcherMixin.java   (the actual logic)
    └── resources/
        ├── fabric.mod.json
        └── hidemobhitboxes.mixins.json
```

## Building it

This folder has the Loom build script and sources, but **not** the Gradle
wrapper binary (`gradlew` / `gradle-wrapper.jar`), since that's a binary
file I can't generate for you here. Easiest way to get a working build:

1. Go to <https://fabricmc.net/develop/template/> and generate a fresh
   template for Minecraft `1.21.11` (Fabric API + Mixins, no other options
   needed). Download/unzip it.
2. From that template, copy over its `gradlew`, `gradlew.bat`,
   `gradle/wrapper/gradle-wrapper.jar`, and `gradle/wrapper/gradle-wrapper.properties`
   into this folder (overwriting the placeholder properties file here is
   fine — they should match).
3. Copy this project's `build.gradle`, `settings.gradle`, `gradle.properties`,
   and everything under `src/` into the template folder, overwriting its
   defaults.
4. From a terminal in that folder:
   ```
   ./gradlew build
   ```
   (Windows: `gradlew.bat build`)
5. The finished jar shows up in `build/libs/hide-mob-hitboxes-1.0.0.jar`.
   Drop that into your `.minecraft/mods` folder alongside Fabric Loader and
   Fabric API for 1.21.11.

## Before you build — double check versions

Modding-relevant versions move fast. In `gradle.properties`, verify against
<https://fabricmc.net/develop/> that these are still current for 1.21.11:

- `yarn_mappings` (currently set to `1.21.11+build.4`)
- `loader_version` (currently set to `0.16.14`)
- `fabric_version` / Fabric API (currently set to `0.141.1+1.21.11`)

If Gradle complains a dependency can't be resolved, it's almost always
because one of these three needs bumping to a newer build.

## Load order with other mods

Fabric doesn't have a formal "always wins" mechanism for rendering mixins,
so "override any other mod" is best-effort here (explained above), not an
absolute guarantee. If it's not winning against a specific mod you use, the
fabric.mod.json `depends` list is a place you could add an explicit `loadAfter`
on that mod's ID once you know it, which sometimes helps mixin ordering.
