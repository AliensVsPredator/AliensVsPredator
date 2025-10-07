# v0.2.6

## ✍️ Developer Notes
- For help or other questions, concerns, etc. check out our Discord server: https://discord.gg/wp7mvmbkVb

## ☢️ Breaking Changes
- N/A

## ✨ What's New
- AVP now requires AzureLib 3.0.38.

## ♻️ Changes
- Eating a chorus fruit while infected with a chestburster now only grants 10% warp gene strength to the chestburster instead of 100%.
  - Warp genes now start off at 10% strength and gain 10% additional strength each generation, up to 100%.
  - Warp gene strength will determine the chance the warp xeno has to teleport.
      - For projectiles, the warp xeno with 10% warp gene strength will have a 10% chance to dodge projectiles, 20% for 20%, and so on up until 100% dodge rate.
      - For any attack, warp xenos previously had a fixed 10% chance to teleport. This is now 1% at 10% warp gene strength, 2% at 20%, and so on up until 10% chance to teleport.
  - Based on player feedback, we decided that the warp gene providing full strength within the first generation was too punishing. We want to allow people to still make warp xenos at full strength, but we don't want warp xenos to be impossible to kill after only a single generation (they are immune to projectiles like bullets at 100% strength!). This patch should be a fair compromise by not punishing players severely for simple chorus fruit usage to save their lives, while also not preventing them from achieving warp xenos with full strength (with a bit more effort and intent).
- Modified numerous recipes to support tagged items:
  - MK50 armor set now accepts any lead ingots and any aluminum ingots.
  - Raw bauxite blocks now accept any kind of raw aluminum items.
  - Raw galena blocks now accept any kind of raw lead items.
  - Raw titanium blocks now accept any kind of raw titanium items.
  - Raw zinc blocks now accept any kind of raw zinc items.

## 🐞 Fixes
- Fixed predators rendering incorrectly.

## 🧪 Experimental
- N/A

## 🛠 Data Pack
- Added `avp:raw_aluminum` to `c:raw_materials/aluminum` tag.
- Added `avp:raw_galena` to `c:raw_materials/lead` tag.
- Added `avp:raw_titanium` to `c:raw_materials/titanium` tag.
- Added `avp:raw_zinc` to `c:raw_materials/zinc` tag.

## 🔬 Technical Changes
- N/A