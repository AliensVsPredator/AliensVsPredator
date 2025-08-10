# v0.2.1

## ☢️ Breaking Changes
- N/A

## ✨ What's New
- [Fabric] Now requires Fabric API `0.116.4+1.21.1`.
- [NeoForge] Now requires NeoForge `21.1.197`.

## ♻️ Changes
- Aliens will now kill hosts that have embryos from other strains.
- Nuke blocks are now enabled by default in singleplayer.
- Praetorians now have a chance to drop a single royal jelly item (affected by looting).
- Queens now drop multiple royal jelly items instead of exactly one royal jelly item. (affected by looting).
- Ovipositors now drop multiple royal jelly items (affected by looting).
- Hives now still tick if aggro'd, even if their center chunk is unloaded.
  - This should fix an issue where hive boss bars remain present after players teleport away from the hive.
- Queens no longer require hive chunk center to be loaded to lay eggs.
  - This was changed since it may result in queens not laying eggs if they are part of a hive with multiple queens.
- Reduced radiation status effect duration:
  - Level 1 radiation reduced from 16 minutes to 4 minutes.
  - Level 2 radiation reduced from 8 minutes to 2 minutes.
  - Level 3 radiation reduced from 4 minutes to 1 minute.

## 🐞 Fixes
- Fixed nether aliens setting attackers on fire from afar when attacked with projectiles.
- Fixed mobs with poison barb genes poisoning attackers from afar when attacked with projectiles.
- Fixed mobs with thorn genes hurting attackers from afar when attacked with projectiles.
- Fixed aliens growing up in small spaces and clipping out of said small spaces into larger areas.
  - Aliens will no longer attempt to grow up in small spaces and will need more room to grow into their next growth stage.

## 🛠 Data Pack
- N/A

## 🔬 Technical Changes
- N/A