# v0.2.1

## ✍️ Developer Notes
- For help or other questions, concerns, etc. check out our Discord server: https://discord.gg/wp7mvmbkVb
- Changelogs will now have a new "✍️ Developer Notes" section in them for more "meta" changes about the mod.
- Changelogs will now have a new 🧪 Experimental section from now on with in-progress content. This content may or may not be accessible in survival. Players are encouraged to check these features out and give feedback on how they work (or don't work) so far. Please note that this content *can* change heavily and therefore shouldn't be used as part of long-term worlds.
- With that out of the way, hey guys! I know it's been awhile and this isn't the biggest update, the team has been taking a well-deserved break and enjoying things beyond the mod. Hopefully things will pick up again as holidays roll around. :)

## ☢️ Breaking Changes
- N/A

## ✨ What's New
- [Fabric] AVP now requires Fabric API `0.116.6+1.21.1`.
- [Fabric] AVP now requires Fabric Loader `0.17.2`.
- [NeoForge] AVP now requires NeoForge `21.1.209`.
- AVP now requires AzureLib `3.0.27`.

## ♻️ Changes
- Aliens will now kill hosts that have embryos from other strains.
- Nuke blocks are now enabled by default in singleplayer.
- Praetorians now have a chance to drop a single royal jelly item (affected by looting).
- Queens now drop multiple royal jelly items instead of exactly one royal jelly item. (affected by looting).
- Ovipositors now drop multiple royal jelly items (affected by looting).
- Irradiated queens can no longer create ovipositors.
- Poisoned queens can no longer create ovipositors.
- Hives now still tick if aggro'd, even if their center chunk is unloaded.
  - This should fix an issue where hive boss bars remain present after players teleport away from the hive.
- Queens no longer require hive chunk center to be loaded to lay eggs.
  - This was changed since it may result in queens not laying eggs if they are part of a hive with multiple queens.
- Reduced radiation status effect duration:
  - Level 1 radiation reduced from 16 minutes to 4 minutes.
  - Level 2 radiation reduced from 8 minutes to 2 minutes.
  - Level 3 radiation reduced from 4 minutes to 1 minute.
- Resonator blocks now require power (instead of a redstone signal).
- Desk Terminal blocks now require power.

## 🐞 Fixes
- Fixed nether aliens setting attackers on fire from afar when attacked with projectiles.
- Fixed mobs with poison barb genes poisoning attackers from afar when attacked with projectiles.
- Fixed mobs with thorn genes hurting attackers from afar when attacked with projectiles.
- Fixed aliens growing up in small spaces and clipping out of said small spaces into larger areas.
  - Aliens will no longer attempt to grow up in small spaces and will need more room to grow into their next growth stage.

## 🧪 Experimental
- Implemented power system:
  - Added Cable block + item.
  - Added Solar Panel block + item.
  - Added battery block + item.
  - Added wind turbine block + item.
  - Added thermal generator block + item.
  - Added an infinite power generator block + item (for testing purposes).

## 🛠 Data Pack
- N/A

## 🔬 Technical Changes
- N/A