# v0.1.7

## ☢️ Breaking Changes
- N/A

## ✨ What's New
- N/A

## ♻️ Changes
- Aliens can now place resin nodes above blocks they can't replace.
  - Previously, aliens could only replace certain blocks with resin nodes. This lead to the alien being unable to put resin nodes down in areas with irreplaceable blocks.
  - Now, aliens can place resin nodes in open air blocks above blocks they can't replace, allowing them to spread resin veins in nearly all places.
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
- Fixed nether aliens not spawning on nether resin in the overworld or the end.
- Fixed normal aliens not spawning on regular resin in the nether or the end.
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