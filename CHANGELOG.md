# v0.1.5

## ☢️ Breaking Changes
- N/A

## ✨ What's New
- N/A

## ♻️ Changes
- Updated deepslate titanium ore texture to be more consistent with other deepslate ores.
- Xenomorphs now only move 10% faster instead of 20% faster when chasing a target.
- Significantly improved hit registration for all hitscan-based weaponry.
- Queens no longer despawn under any circumstances.
- Reduced `MINIMUM_DISTANCE_BETWEEN_HIVES_IN_BLOCKS` from 1024 blocks (64 chunks) to 256 blocks (16 chunks).
  - This change does not apply retroactively.
  - We're reducing the default value so that naturally spawning queens are more common as the default experience.
- Naturally spawning queens now only spawn once in a chunk region.
  - The size of the region currently depends on the value of `MINIMUM_DISTANCE_BETWEEN_HIVES_IN_BLOCKS` in the config.
  - Killing the queen will not allow more natural queens to spawn in that region. Once the region is cleansed, it's cleansed for good.
  - Artificial queens/hives can still be made in these regions even after naturally occurring queens have been eradicated.
- Removed `requiresResin` config options, xenomorphs (excluding queens) now always require resin in order to spawn.
  - We're making this change to allow for players to have consistent experiences, as well as to complement other spawn changes we've made this update.
  - If you want to add custom xenomorph spawns, we recommend using third-party mods designed with spawning customization in mind.
- Renamed "Ovamorph" to "Ovomorph".
  - These are only display-facing, non-breaking changes. Ovomorphs will have their registry names starting with v0.2.0.
- Reduced overall block damage dealt by acid by 80%.
  - Acid's block damage still scales with the "strength" of acid, which increases when acid entities overlap.

## 🐞 Fixes
- Fixed hatched ovomorphs closing up again after re-logging.
- Fixed hatched ovomorphs not closing up again after royal jelly is used on them.
- Fixed 'Imperfect Organism' not including alternative strains of aliens or royal aliens.
- Fixed 'Eggsploration Time' advancement not being granted when shearing royal or non-regular strain ovomorphs.
- Fixed 'Regicide' advancement not being granted when killing non-regular strain praetorians or queens.
- Fixed 'Xenocide' advancement not including alternative strains of aliens or royal aliens.
- Fixed mobs dying to chestbursting even after switching to peaceful difficulty.
- Fixed royal jelly item not triggering player hand swing animation when used.
- Fixed royal jelly item being consumed when right-clicking non-alien entities.
- Fixed royal jelly block item not triggering player hand swing animation when used.
- Fixed royal jelly block item being consumed when right-clicking non-alien entities.
- Fixed poison jelly item being consumed even if alien is already poisoned.
- Fixed villagers not becoming commissaries (Fabric-only).
- Fixed empty canisters being consumed when right-clicking non-cow entities.
- Fixed industrial furnaces not emitting light while cooking.
- Fixed shift-clicking not working with industrial furnace.
- Fixed marines always wandering around immediately when spawned in or when loading into the world.
- Fixed guns rendering a muzzle flash in inventories.
- Fixed multiple guns in inventory playing animations if the guns are copies of each other.
- Fixed trip mine block texture dimensions not being powers of 2.
- Fixed ovamorphs spawned from queens not persisting.
- Fixed facehuggers and xenomorphs lunging towards targets even if they aren't looking towards the target.
- Fixed irradiated queens laying eggs when they shouldn't.
- Fixed persistence not carrying over when an alien grows into the next stage.
- Fixed royal aliens not being acid immune.
- Fixed royal aliens not being tagged as aliens.
- Fixed missing name translations for aberrant, irradiated, nether and royal aliens.
- Fixed xenomorphs lunging in-place while attacking a target.
  - Fixed by increasing their minimum horizontal lunge distance check from 1 block to 6 blocks.

## 🛠 Data Pack
- Added `#avp:hated_by_xenomorphs` entity type tag. Includes players, marines and predators.
- Added `#avp:drones` entity type tag.
- Added `#avp:facehuggers` entity type tag.
- Added `#avp:praetorians` entity type tag.
- Added `#avp:queens` entity type tag.
- Added `#avp:royal_xenomorphs` entity type tag.
- Added `#avp:warriors` entity type tag.
- Updated `#avp:aliens` entity type tag to include missing `#avp:royal_aliens`.
- Updated `#avp:xenomorphs` to use `#avp:drones`, `#avp:queens`, `#avp:praetorians` and `#avp:warriors`.
- Updated `#avp:parasites` to use `#avp:facehuggers`.
- Updated `#avp:royal_aliens` to use `#avp:royal_xenomorphs`.
- Updated `#avp:normal_aliens` to include missing `avp:royal_chestburster`, `avp:royal_facehugger` and `avp:royal_ovamorph`.
- Updated `avp:aliens/shear_an_ovamorph` advancement to use `#avp:ovamorphs` entity type tag instead of just the single `avp:ovamorph` entity type.

## 🔬 Technical Changes
- N/A