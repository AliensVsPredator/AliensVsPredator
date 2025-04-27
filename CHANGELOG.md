# v0.1.1

## ☢️ Breaking Changes
- N/A

## ✨ What's New
- Added tooltip for ammo chest item.
- Added tooltip for lead chest item.
- Added tooltip for sentry turret item.
- Implemented new queen sound effects:
  - Idle
  - Hurt
  - Death
- Added tooltips for armor items that have full set bonuses.
  - Full set buffs are colored green.
  - Full set debuffs are colored red.
  - The following armor sets are affected by this change:
    - MK50 Suit
    - Pressure Suit

## ♻️ Changes
- N/A

## 🐞 Fixes
- Fixed radiation damaging regular armor on mobs and players (armor is now ignored).
- Fixed radiation damaging wolf armor on wolves (armor is now ignored).
- Fixed radiation damage being reduced by protection 4.
- Fixed radiation damage being reduced by resistance.
- Fixed aberrant xenomorphs becoming nether afflicted when the world reloads.
- Fixed irradiated xenomorphs becoming nether afflicted when the world reloads.
- Fixed sentry turret's pitch being slightly offset from where it should be.
- Fixed yautja's pitch being slightly offset from where it should be.

## 🛠 Data Pack
- Added the following chests to the `#c:chests` block tag:
  - Ammo Chest
  - Lead Chest
- Added the following ores to the `#c:ores` block tag:
  - Autunite Ore
  - Bauxite Ore
  - Deepslate Titanium Ore
  - Deepslate Zinc Ore
  - Galena Ore
  - Lithium Ore
  - Monazite Ore
  - Zinc Ore
- Added the following ingots to the `#c:ingots` item tag:
  - Aluminum Ingot
  - Brass Ingot
  - Ferroaluminum Ingot
  - Lead Ingot
  - Steel Ingot
  - Titanium Ingot
  - Uranium Ingot
  - Zinc Ingot
- Added the following ingots to their respective individual ingot item tags:
  - Aluminum Ingot to `#c:ingots/aluminum` item tag.
  - Brass Ingot to `#c:ingots/brass` item tag.
  - Lead Ingot to `#c:ingots/lead` item tag.
  - Steel Ingot to `#c:ingots/steel` item tag.
  - Titanium Ingot to `#c:ingots/titanium` item tag.
  - Zinc Ingot to `#c:ingots/zinc` item tag.
- Added the following nuggets to the `#c:nuggets` item tag:
  - Aluminum Nugget
  - Brass Nugget
  - Ferroaluminum Nugget
  - Lead Nugget
  - Steel Nugget
  - Titanium Nugget
  - Uranium Nugget
  - Zinc Nugget
- Added the following nuggets to their respective individual nugget item tags:
  - Aluminum Nugget to `#c:nuggets/aluminum` item tag.
  - Brass Nugget to `#c:nuggets/brass` item tag.
  - Lead Nugget to `#c:nuggets/lead` item tag.
  - Steel Nugget to `#c:nuggets/steel` item tag.
  - Titanium Nugget to `#c:nuggets/titanium` item tag.
  - Zinc Nugget to `#c:nuggets/zinc` item tag.
- Added the following ores to the `#c:ores` item tag:
  - Autunite Ore
  - Bauxite Ore
  - Lithium Ore
  - Deepslate Titanium Ore
  - Galena Ore
  - Deepslate Zinc Ore
  - Monazite Ore
  - Zinc Ore

## 🔬 Technical Changes
- Moved Stellaris compatibility tags to datagen.