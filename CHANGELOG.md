# v0.2.0-beta3

## ☢️ Breaking Changes
- Renamed `avp:ovamorph` to `avp:ovomorph`.
- Parasites no longer implant a parasite type into hosts, now implant correct embryo type.
  - Previously, parasites would store their own entity type to a host. At the time of birth, the embryo type was then computed. This has been changed such that the embryo type is computed immediately and then stored.
  - This means that any currently-infected hosts in your game *will not burst* and will need to be infected again.

## ✨ What's New
- Aliens now remember what host type they came from.
- Added two new advancements:
  - "Eviction" - Infected? Eat a chorus fruit to remove the embryo, saving yourself... and dooming the world.
  - "Hive Buster" - Defeat an alien hive.
- Revamped genetics system:
  - Gene bonuses are now datapacked.
  - All hosts now provide gene bonuses.
  - Gene bonuses now have a more noticeable impact to xenomorphs.
  - Added a new "syringe" item:
    - Right-click any mob to get their genes.
    - Right-click an ovomorph or a host to add the genes to them.
      - Added genes are "dormant" (do not apply immediately).
      - Dormant genes only manifest on chestbursters born from hosts.
- Ovomorphs now have hatching AI
  - Ovomorphs now have a "desire" meter for hatching. 
  - Changes in the environment (such as light) will increase the desire meter.
  - Movement of nearby hosts will also increase the desire meter.
- Added the queen's ovipositor/eggsack:
  - Queens will create an ovipositor when they are in a hive and are "safe" (they aren't the only hive member).
  - The queen will play a new animation when she is mounted on her ovipositor.
  - Ovipositors are an entity and extend off of the queen. Other mobs will not attack the ovipositor.
  - The queen requires a considerable amount of space as well as stable ground to create her ovipositor.
  - Eggs no longer drop at the queen's feet, but instead at the end of the ovipositor.
  - Egglaying has otherwise remained unchanged.
- Ovomorphs no longer spawn naturally.
  - Ovipositor is complete, natural ovomorph spawning is no longer necessary.
- Added new entities:
  - Aberrant Adolescent
  - Aberrant Crusher
  - Aberrant Prowler
  - Aberrant Runner
  - Adolescent
  - Crusher
  - Irradiated Crusher
  - Irradiated Prowler
  - Irradiated Runner
  - Nether Adolescent
  - Nether Crusher
  - Nether Prowler
  - Nether Runner
  - Ovipositor
  - Prowler
  - Royal Aberrant Adolescent
  - Royal Adolescent
  - Royal Nether Adolescent
  - Runner

## ♻️ Changes
- Predators no longer spawn naturally in jungles.
- All aliens can now detect vibrations (instead of just xenomorphs).
- Chestbursters can no longer be trapped in boats and minecarts.
- Facehuggers can no longer be trapped in boats and minecarts.
- Improved razor wire death message.
- Moved the following config options to datapack:
  - `CHESTBURSTER_MAX_GROWTH_TIMER_SECONDS`
  - `DRONE_MAX_GROWTH_TIMER_SECONDS`
  - `PRAETORIAN_MAX_GROWTH_TIMER_SECONDS`
  - `PRAETORIAN_SHORTCUT_TIMER_SECONDS`
  - `WARRIOR_MAX_GROWTH_TIMER_SECONDS`
- Removed the following config options:
  - `ABERRANT_OVOMORPH_SPAWN`
  - `NETHER_OVOMORPH_SPAWN`
  - `OVOMORPH_SPAWN`

## 🐞 Fixes
- Fixed queens not transferring their genes to laid eggs.
- Fixed potion effects carried over from host to embryo not being permanent.
- Fixed entities being able to ride aliens (disabling their AI in the process).
- Fixed embryos born from a host eating chorus fruit not persisting.
- Fixed inconsistent potion effect behavior with embryos born from hosts eating chorus fruit.
- Fixed aberrant chitin armors not counting towards chitin armor advancements.

## 🛠 Data Pack
- Added `#avp:runner_hosts` entity tag.
- Added `#avp:runners` entity tag.
- Added `#avp:prowlers` entity tag.
- Added `#avp:crushers` entity tag.
- Updated `#avp:hosts` to be composed of `#avp:runner_hosts` entity tag.
- Updated `#avp:xenomorphs` tag to include new runner alien line entity tags.
- Updated hive layer entity tags to include new runner alien line entity tags.
- Updated variant entity tags to include new runner alien entity types.

## 🔬 Technical Changes
- Upgraded alien lifecycles to support host entity tags.
- Added full datapack support for alien growth stages.
- Alien lifecycle host entity tags have been migrated to individual alien growth stages.
- Upgraded alien infections to support host entity tags.
- Added full datapack support for alien infections.
- Removed redundant royal data from aliens.
- Added full datapack support for gene bonuses.
- Alien gene manager now managed through a mixin (same as host gene managers).
- Added `#avp:does_not_hurt_aliens` damage type tag.