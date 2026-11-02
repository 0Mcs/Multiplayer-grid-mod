# Multiplayer grid

Compact grid view for the Minecraft Multiplayer server list. Built with Fabric.

## Supported versions

Jars for each supported Minecraft version are in `libs/<version>/`.

| Line | Versions |
| --- | --- |
| 26.x | 26.1, 26.1.2, 26.2, 26.3 |
| 1.21.x | 1.21.11 |

## Build

Requires JDK 25 (the `legacy/` subproject needs JDK 21 or newer) and Fabric Loom, included through the Gradle wrapper.

```
./build-all.sh
```

The script builds every supported version and copies the jars into `libs/<version>/`. To build a single 26.x version:

```
./gradlew -Pmc=26.3 -Pmc_dep='~26.3' build
```

To build the 1.21.x line:

```
cd legacy && ./gradlew build
```

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

MIT. See [LICENSE](LICENSE).
