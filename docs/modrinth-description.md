# Multiplayer grid

**Multiplayer grid** turns the Multiplayer server list into a compact grid of tiles. Instead of one long list of wide rows, every server becomes a small card with its icon, name, ping and player count, so you can see your whole server list at a glance.

## What it changes

Vanilla shows each server as a wide row with its name, MOTD, status and the join and reorder controls. Multiplayer grid replaces that list with a grid:

- **Grid layout:** servers are arranged in columns, and the grid grows as you add servers. With one server you get a single tile, with two you get two tiles side by side, and so on.
- **Compact tiles:** each tile shows the server icon, the name above it, a signal indicator and the player count below it.
- **Long names scroll:** if a name is too wide for its tile, it slides from side to side instead of being cut off.
- **MOTD on hover:** the server description (MOTD) appears as a tooltip when you hover over the server icon, with its line breaks kept.
- **Player list on hover:** hover over the player count to see who is online.
- **Familiar controls:** double-click a tile to join, click to select it, and use Edit, Delete, Refresh and Back as usual.

## The Grid button

A **Grid** button sits next to **Refresh** on the Multiplayer screen. It opens the settings for the mod.

## Settings

The settings screen shows a live preview of a tile, so you can see every change before you leave.

- **Columns:** how many columns the grid can use, from 1 to 4.
- **Grid:** turns the grid on or off. When it is off, the screen goes back to the default Minecraft server list.
- **Name:** shows or hides the server name above the icon.
- **Ping:** shows or hides the signal indicator.
- **Players:** shows or hides the player count.
- **Motd:** shows or hides the MOTD tooltip on hover.

If you install [Mod Menu](https://modrinth.com/mod/modmenu), the same settings appear in its config screen, plus a **Grid button** option that hides the Grid button from the Multiplayer screen. Mod Menu is optional, and the mod works without it.

## Requirements

- Minecraft **1.21 to 1.21.11** or **26.1 to 26.3**
- [Fabric Loader](https://fabricmc.net/use/) 0.19.5 or later
- [Fabric API](https://modrinth.com/mod/fabric-api)
- Optional: [Mod Menu](https://modrinth.com/mod/modmenu)

## Notes

- This is a client-side mod. It does not need to be installed on servers.
- The mod changes only the Multiplayer server list. It does not change how you join servers or how servers behave.
- Settings are saved to `config/multiplayer-grid.properties`.
