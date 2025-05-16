# v0.1.8

## ☢️ Breaking Changes
- N/A

## ✨ What's New
- N/A

## ♻️ Changes
- Updated royal facehugger texture.
- Hive boss bars are now stylized based on the hive's variant.
  - For example, a nether xenomorph hive will show "Nether Hive" for the boss bar title, and the boss bar color will be red instead of green.

## 🐞 Fixes
- Fixed an error occurring when attempting to join a server with the AVP mod installed (NeoForge-only).
- Fixed marines not shooting properly after player hit accuracy changes.
- Fixed weapons with piercing having excessive recoil.
- Fixed weapons with piercing not piercing through multiple blocks.
- Fixed aliens not spawning in their respective hive layers.
  - Last update we had commented that the hive layering was only effective for natural alien spawns. However, this wasn't actually the case and aliens were not spawning in their correct layers.
  - As a recap, the hive is now organized into spherical layers, with:
    - warriors spawning on the exterior parts (or deeper) of the hive.
    - drones/ovomorphs in the interior middle parts (or deeper) of the hive.
    - and then praetorians + the queen in the center of the hive.

## 🛠 Data Pack
- Added `#avp:chestbursters` entity type tag.
- Added `#avp:spawns_in_hive_drone_layer` entity type tag.
- Added `#avp:spawns_in_hive_praetorian_layer` entity type tag.
- Added `#avp:spawns_in_hive_queen_layer` entity type tag.
- Added `#avp:spawns_in_hive_warrior_layer` entity type tag.

## 🔬 Technical Changes
- Improved bullet piercing accuracy for weapons.