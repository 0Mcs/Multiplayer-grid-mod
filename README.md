# Multiplayer grid

Compact grid view for the Minecraft Multiplayer server list. Built for Minecraft 26.2 with Fabric.

## Build

Requires JDK 25 and Fabric Loom (included through the Gradle wrapper).

```
./gradlew build
```

The jar is written to `build/libs/`.

## Run the dev client

```
./gradlew runClient
```

## Layout

- `src/client/java/com/multiplayergrid/`: mod code (client only)
- `src/client/java/com/multiplayergrid/mixin/`: mixins into the Multiplayer screen
- `src/main/resources/`: `fabric.mod.json`, Mixin config, language files and icon
- `docs/modrinth-description.md`: text used for the Modrinth page
- `branding/`: icon files for Modrinth and the 16x16 mod icon

## License

All rights reserved. (Change this if you want to publish under another license.)
