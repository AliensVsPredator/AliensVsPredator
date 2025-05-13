# v0.1.7

## ☢️ Breaking Changes
- N/A

## ✨ What's New
- N/A

## ♻️ Changes
- Updated female marine model + `hair_5` texture.
- Irradiated queens now have a chance of spawning naturally in irradiated biomes.
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
  - `IRRADIATED_DRONE_SPAWN`
  - `IRRADIATED_PRAETORIAN_SPAWN`
  - `IRRADIATED_QUEEN_SPAWN`
  - `IRRADIATED_WARRIOR_SPAWN`
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
- Fixed nether resin nodes having incorrect block properties.
- Fixed irradiated resin, irradiated resin nodes, irradiated resin veins and irradiated resin webs having incorrect block properties.
- Fixed resin, resin nodes, resin veins and resin webs having incorrect colors on maps.
- Fixed variant aliens being unable to replace enemy variant resin.
- Fixed variant aliens not creating hives near enemy variant hives.
- Fixed variant aliens spawning on resin near enemy variant hives without having a nearby hive of their own to spawn in.
- Fixed variant hives not balancing drone and warrior numbers correctly.
- Fixed variant hives not balancing praetorian numbers correctly.
- Fixed variant hives not balancing queen numbers correctly.
- Fixed nether resin not being flame-resistant and turning into basalt (NeoForge-only).
- Fixed nether resin veins being destroyed when ignited by flamethrowers.
- Fixed aliens spreading resin that belonged to other strains (ex. nether xenomorphs spreading aberrant resin).
- Fixed broken textures appearing when an aberrant queen became irradiated.
- Fixed broken textures appearing when a nether queen became irradiated.
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
- Added `#avp:is_irradiated` biome tag.
- Added `#avp:resin_blocks` block tag.
- Added `#avp:resin_nodes` block tag.
- Added `#avp:aberrant_resin_replaceable` block tag.
- Added `#avp:irradiated_resin_replaceable` block tag.
- Added `#avp:nether_resin_replaceable` block tag.
- Added `#avp:normal_resin_replaceable` block tag.
- Added `#avp:resin_replaceable` block tag.
  - Allows for controlling which blocks aliens can fully replace with resin.
  - Serves as the base for the other resin replaceable tags.

## 🔬 Technical Changes
- Entity spawn handling code is now shared between Fabric and NeoForge.
- Rewrote alien variant type handling from the ground up.
  - Aliens can no longer have multiple variants.
  - This rewrite makes handling alien variants far less bug-prone.