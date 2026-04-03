# Horizon Pack Example

This repository serves as a fully functional source template for a Horizon pack. Horizon acts as the runtime environment, interpreting this directory structure, optionally compiling Java/C++ sources on the fly (only on 32bit for now).

## Core Concepts

In the Horizon ecosystem, a "Pack" is a completely isolated game environment. Unlike traditional add-ons, a Horizon pack grants deep access to the game engine through multiple layers:

1. **Native Layer (C/C++):** Direct memory hooking, vtable manipulation, and JNI bridging into `libminecraftpe.so`.
2. **Java Layer (Android/Dalvik):** UI manipulation, activity injection (e.g., custom main menus, loading screens), and core API implementation.
3. **Resource Patching (Assets):** Directly allows to modify any existing assets from Java and reloading them from additional directories.
4. (Optional) **Modding Layer (Inner Core):** High-level modding API for defining blocks, items, and game logic without recompiling the core environment.

Horizon reads the `manifest.json` and automatically manages the `LaunchSequence`, compiling required sources via its execution directories before starting the application context.

## Repository Structure

* `manifest.json`
The strict descriptor file for the pack. Defines the pack's ID, version, target InnerCore engine version, and entry points.
* `assets/`
Contains all static visual and data resources.
  * `images/`: Texture atlases, UI sprites, entity skins, and environment maps.
  * `materials/` & `shaders/`: Render controllers and GLSL shaders (`.vertex` / `.fragment`) for custom visual effects.
  * `innercore/`: Engine-specific visual assets (UI components, default mod icons, recipe panels).
  * `res/`: Android-level resources (layouts, localized strings, drawables) used by Horizon's UI injections.

---

* `java/`
The Dalvik/Android source code and prebuilt `.jar`/`.dex` libraries.
  * `src/com/zhekasmirnov/mcpe161/EnvironmentSetup.java`: **The primary Java entry point.** Modifying this allows you to hook into the Horizon app lifecycle, abort launches, override resource paths, inject custom UI (`buildCustomMenuLayout`), and configure Ad domains.
  * `src/io/nernar/instant/`: Classes handling the fast-launch "Instant" mechanics and specific prepatching.

---

* `native/`
C++ source code for native modification.
  * `instant/module.cpp`: Contains the JNI setup and symbol hooking (via `HookManager`). Modify this to intercept internal `libminecraftpe.so` calls (e.g., `Level::addEntity`, `MinecraftClient::setScreen`).
  * `callbacks/`: Native implementations for bridging C++ engine events back to the Java API (`JavaCallbacks::invokeCallback`).

---

* `so/`
Contains pre-compiled native shared libraries for the target architecture (e.g., `armeabi-v7a/libminecraftpe.so`).

## Development & Usage Workflow

1. **Deployment:** Place this repository folder directly into the Horizon `packs/` directory on your device or development environment.
2. **Java Modification:** To alter the pre-game launch sequence, UI, or library injection, edit the Java sources in `java/src/`. Horizon's `ExecutionDirectory` will rebuild these sequences upon pack launch.
3. **Native Modification:** To add new engine hooks, create or modify `.cpp` files in `native/`. Ensure symbol names match the target ABI. Horizon handles the NDK compilation based on the `manifest` and native directory structure.
4. **Resource Overrides:** Place custom assets directly into `assets/`. The `EnvironmentSetup.java` handles `ResourceManager` configuration, routing atlases and materials appropriately.
