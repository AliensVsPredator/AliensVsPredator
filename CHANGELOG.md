# v0.2.6

## ✍️ Developer Notes
- For help or other questions, concerns, etc. check out our Discord server: https://discord.gg/wp7mvmbkVb
- Shoutout to Moobien on our Github issue tracker for raising awareness on compatibility issues with the mod's blocks/items!

## ☢️ Breaking Changes
- N/A

## ✨ What's New
- AVP now requires AzureLib 3.0.38.
- Added a new recipe for raw ferrobauxite (1 raw crude iron + 1 raw bauxite).
  - Existing raw crude iron and raw ferrobauxite recipes have 2 overlapping ingredients, so it made sense to have another recipe where the overlapping ingredients are substituted with raw crude iron.
  - Shoutout to Davianortis in our Discord server for this suggestion!

## ♻️ Changes
- Guns can now be repaired with steel ingots from other mods.
- MK50 armor can now be repaired with any lead ingots.
- Pressure armor can now be repaired with any aluminum ingots.
- Steel armor can now be repaired with any steel ingots.
- Steel tools can now be repaired with any steel ingots.
- Tactical armor can now be repaired with any steel ingots.
- Titanium armor can now be repaired with any titanium ingots.
- Titanium tools can now be repaired with any titanium ingots.
- Modified numerous recipes to support tagged items from other mods:
  - Aluminum blocks now accept any aluminum ingots.
  - Aluminum ingot now accepts any aluminum nuggets.
  - Ammo chests now accept any steel ingots.
  - Armor cases now accept any aluminum ingots.
  - Battery packs now accept any aluminum ingots.
  - Brass blocks now accept any brass ingots.
  - Brass ingot now accepts any brass nuggets.
  - Bullets now accept any aluminum, brass, lead or steel nuggets (where applicable).
  - Canisters now accept any titanium ingots.
  - Electronic parts now accept any brass nuggets or gold nuggets (where applicable).
  - Electronic parts now accept any redstone dust (where applicable).
  - Electronic parts now accept any silicon (where applicable).
  - Ferroaluminum blocks now accept any ferroaluminum ingots.
  - Ferroaluminum button block now accept any ferroaluminum ingots.
  - Ferroaluminum door block now accept any ferroaluminum ingots.
  - Ferroaluminum ingot now accepts any ferroaluminum nuggets.
  - Ferroaluminum pressure plate block now accept any ferroaluminum ingots.
  - Ferroaluminum trapdoor block now accept any ferroaluminum ingots.
  - Gene readers now accept any aluminum ingots.
  - Gun parts now accept any steel ingots.
  - Gunpowder now accepts any coal dust.
  - Integrated circuits now accept any lead ingots.
  - Lead blocks now accept any lead ingots.
  - Lead chests now accept any lead ingots.
  - Lead ingot now accepts any lead nuggets.
  - MK50 armor set now accepts any lead ingots and any aluminum ingots.
  - MK50 chestplate now accepts any leather.
  - MK50 helmet now accepts any coal dust.
  - MK50 leggings now accepts any leather.
  - Nuke blocks now accept any lead ingots.
  - Padding blocks now accept any leather.
  - Pressure armor set now accepts any aluminum ingots.
  - Pressure helmet now accepts any coal dust.
  - Raw bauxite blocks now accept any raw aluminum.
  - Raw brass now accepts any raw copper.
  - Raw brass now accepts any raw zinc.
  - Raw crude iron now accepts any coal dust.
  - Raw crude iron now accepts any raw iron.
  - Raw ferrobauxite now accepts any coal dust.
  - Raw ferrobauxite now accepts any raw aluminum/bauxite.
  - Raw ferrobauxite now accepts any raw iron.
  - Raw ferrobauxite now accepts any raw steel.
  - Raw galena blocks now accept any raw lead.
  - Raw titanium blocks now accept any raw titanium.
  - Raw zinc blocks now accept any raw zinc.
  - Razor wire now accepts any iron nuggets.
  - Regulators now accept any lead ingots.
  - Speakers now accept any aluminum ingots.
  - Steel armor set now accepts any steel ingots.
  - Steel bars block now accept any steel ingots.
  - Steel blocks now accept any steel ingots.
  - Steel button block now accept any steel ingots.
  - Steel door block now accept any steel ingots.
  - Steel ingot now accepts any steel nuggets.
  - Steel pressure plate block now accept any steel ingots.
  - Steel tool set now accepts any steel ingots.
  - Steel trapdoor block now accept any steel ingots.
  - Syringes now accept any iron nuggets.
  - Tactical armor set now accepts any steel ingots.
  - Titanium armor set now accepts any titanium ingots.
  - Titanium blocks now accept any titanium ingots.
  - Titanium button block now accept any titanium ingots.
  - Titanium door block now accept any titanium ingots.
  - Titanium ingot now accepts any titanium nuggets.
  - Titanium pressure plate block now accept any titanium ingots.
  - Titanium tool set now accepts any titanium ingots.
  - Titanium trapdoor block now accept any titanium ingots.
  - Uranium blocks now accept any uranium ingots.
  - Uranium ingot now accepts any uranium nuggets.
  - Uranium ingots now accept any titanium ingots.
  - Zinc blocks now accept any zinc ingots.
  - Zinc ingot now accepts any zinc nuggets.
- Eating a chorus fruit while infected with a chestburster now only grants 10% warp gene strength to the chestburster instead of 100%.
  - Warp genes now start off at 10% strength and gain 10% additional strength each generation, up to 100%.
  - Warp gene strength will determine the chance the warp xeno has to teleport.
    - For projectiles, the warp xeno with 10% warp gene strength will have a 10% chance to dodge projectiles, 20% for 20%, and so on up until 100% dodge rate.
    - For any attack, warp xenos previously had a fixed 10% chance to teleport. This is now 1% at 10% warp gene strength, 2% at 20%, and so on up until 10% chance to teleport.
  - Based on player feedback, we decided that the warp gene providing full strength within the first generation was too punishing. We want to allow people to still make warp xenos at full strength, but we don't want warp xenos to be impossible to kill after only a single generation (they are immune to projectiles like bullets at 100% strength!). This patch should be a fair compromise by not punishing players severely for simple chorus fruit usage to save their lives, while also not preventing them from achieving warp xenos with full strength (with a bit more effort and intent).

## 🐞 Fixes
- Fixed predators rendering incorrectly.
- Fixed uranium ingot recipe data name.

## 🧪 Experimental
- N/A

## 🛠 Data Pack
- Too many changes to record here, but mostly just adding AVP blocks/items to pre-existing common block/item tags.

## 🔬 Technical Changes
- N/A