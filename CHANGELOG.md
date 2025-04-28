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
- Facehuggers now spawn on top of ovamorphs if no obstructing block present.
- Facehuggers now jump towards a random direction when leaving ovamorphs.
- Facehuggers now move 10% faster after targets.
- Xenomorphs now move 20% faster after targets.
- Improved xenomorph(and their variants) digging capabilities:
  - Xenomorphs now dig 2x as many blocks at a single time.
  - Queens now dig 4x as many blocks at a single time.
  - Xenomorphs now dig a single block faster:
    - Drones dig a single block 10x faster (5 -> 50 block damage).
    - Warriors dig a single block 5x faster (10 -> 50 block damage).
    - Praetorians dig a single block 3.33x faster (15 -> 50 block damage).

## 🐞 Fixes
- Fixed world crash caused by ModernFix.
- Fixed xenomorphs periodically getting stuck while digging.
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
- Added a logger in the event a ovamorph is unable to spawn a facehugger.