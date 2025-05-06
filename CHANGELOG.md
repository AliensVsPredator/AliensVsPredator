# v0.1.5

## ☢️ Breaking Changes
- N/A

## ✨ What's New
- N/A

## ♻️ Changes
- Updated deepslate titanium ore texture to be more consistent with other deepslate ores.
- Xenomorphs now only move 10% faster instead of 20% faster when chasing a target.
- Reduced overall block damage dealt by acid by 80%.
  - Acid's block damage still scales with the "strength" of acid, which increases when acid entities overlap.

## 🐞 Fixes
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
- Updated `#avp:aliens` entity type tag to include missing `#avp:royal_aliens`.

## 🔬 Technical Changes
- N/A