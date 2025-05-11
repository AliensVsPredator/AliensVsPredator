# v0.1.7

## ☢️ Breaking Changes
- N/A

## ✨ What's New
- N/A

## ♻️ Changes
- Aliens can now place resin nodes above blocks they can't replace.
  - Previously, aliens could only replace certain blocks with resin nodes. This lead to the alien being unable to put resin nodes down in areas with irreplaceable blocks.
  - Now, aliens can place resin nodes in open air blocks above blocks they can't replace, allowing them to spread resin veins in nearly all places.
- Added the following configuration options:
  - `ABERRANT_CHESTBURSTER_SPAWN`
  - `ABERRANT_DRONE_SPAWN`
  - `ABERRANT_OVAMORPH_SPAWN`
  - `ABERRANT_PRAETORIAN_SPAWN`
  - `ABERRANT_QUEEN_SPAWN`
  - `ABERRANT_WARRIOR_SPAWN`
- Removed the following configuration options:
  - `NATURAL_SPAWNING_ENABLED`
    - This config option wasn't actually used in the code.
  - `ADULT_SPAWNING_ENABLED`
    - Following the recent hive spawning changes, this config option is no longer all that useful.
  - `YOUNG_SPAWNING_ENABLED`
    - Following the recent hive spawning changes, this config option is no longer all that useful.
  - `REMOVE_VANILLA_SPAWNS`
    - Highly specific spawn configuration like this option provided is better suited for other mods to handle, not AVP.

## 🐞 Fixes
- Fixed nether queens not spawning in the nether.
- Fixed aberrant aliens not naturally spawning on aberrant resin.
- Fixed aberrant aliens not spawning on aberrant resin in the end, nether or overworld.
- Fixed nether aliens not spawning on nether resin in the end or the overworld.
- Fixed normal aliens not spawning on regular resin in the end or the nether.
- Fixed the following resin blocks having incorrect map colors:
  - Aberrant Resin
  - Aberrant Resin Node
  - Aberrant Resin Vein
  - Aberrant Resin Web
- Fixed the following resin blocks not being flammable (Fabric-only):
  - Aberrant Resin
  - Aberrant Resin Node
  - Irradiated Resin
  - Irradiated Resin Node

## 🛠 Data Pack
- Added `#avp:has_xenomorphs` biome tag.
- Added `#avp:resin_blocks` block tag.
- Added `#avp:resin_nodes` block tag.
- Added `#avp:resin_replaceable` block tag.
  - Allows for controlling which blocks aliens can fully replace with resin.

## 🔬 Technical Changes
- Entity spawn handling code is now shared between Fabric and NeoForge.