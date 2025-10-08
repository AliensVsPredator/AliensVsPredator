# v0.2.6

## ✍️ Developer Notes
- For help or other questions, concerns, etc. check out our Discord server: https://discord.gg/wp7mvmbkVb

## ☢️ Breaking Changes
- N/A

## ✨ What's New
- AVP now requires AzureLib 3.0.38.

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
  - Armor cases now accept any aluminum ingots.
  - Battery packs now accept any aluminum ingots.
  - Brass blocks now accept any brass ingots.
  - Canisters now accept any titanium ingots.
  - Ferroaluminum blocks now accept any ferroaluminum ingots.
  - Ferroaluminum button block now accept any ferroaluminum ingots.
  - Ferroaluminum door block now accept any ferroaluminum ingots.
  - Ferroaluminum pressure plate block now accept any ferroaluminum ingots.
  - Ferroaluminum trapdoor block now accept any ferroaluminum ingots.
  - Gene readers now accept any aluminum ingots.
  - Gun parts now accept any steel ingots.
  - Integrated circuits now accept any lead ingots.
  - Lead blocks now accept any lead ingots.
  - Lead chests now accept any lead ingots.
  - MK50 armor set now accepts any lead ingots and any aluminum ingots.
  - Nuke blocks now accept any lead ingots.
  - Pressure armor set now accepts any aluminum ingots.
  - Raw bauxite blocks now accept any raw aluminum.
  - Raw galena blocks now accept any raw lead.
  - Raw titanium blocks now accept any raw titanium.
  - Raw zinc blocks now accept any raw zinc.
  - Regulators now accept any lead ingots.
  - Speakers now accept any aluminum ingots.
  - Steel armor set now accepts any steel ingots.
  - Steel bars block now accept any steel ingots.
  - Steel blocks now accept any steel ingots.
  - Steel button block now accept any steel ingots.
  - Steel door block now accept any steel ingots.
  - Steel pressure plate block now accept any steel ingots.
  - Steel tool set now accepts any steel ingots.
  - Steel trapdoor block now accept any steel ingots.
  - Tactical armor set now accepts any steel ingots.
  - Titanium armor set now accepts any titanium ingots.
  - Titanium blocks now accept any titanium ingots.
  - Titanium button block now accept any titanium ingots.
  - Titanium door block now accept any titanium ingots.
  - Titanium pressure plate block now accept any titanium ingots.
  - Titanium tool set now accepts any titanium ingots.
  - Titanium trapdoor block now accept any titanium ingots.
  - Uranium blocks now accept any uranium ingots.
  - Uranium ingots now accept any titanium ingots.
  - Zinc blocks now accept any zinc ingots.
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
- Added `#c:nuggets/ferroaluminum` item tag.
- Added `#c:nuggets/uranium` item tag.
- Added `avp:ferroaluminum_ingot` to `#c:ingots/ferroaluminum` item tag.
- Added `avp:raw_aluminum` to `#c:raw_materials/aluminum` item tag.
- Added `avp:raw_galena` to `#c:raw_materials/lead` item tag.
- Added `avp:raw_titanium` to `#c:raw_materials/titanium` item tag.
- Added `avp:raw_zinc` to `#c:raw_materials/zinc` item tag.

## 🔬 Technical Changes
- N/A