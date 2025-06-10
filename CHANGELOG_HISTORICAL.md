# v0.1.9

## ☢️ Breaking Changes
- Some (previously uncraftable) resin blocks have had their registry names changed:
  - `resin_ribbed` -> `ribbed_resin`
  - `resin_o` -> `resin_vent`
  - `resin_smooth` -> `smooth_resin`

## ✨ What's New
- [Fabric] Now requires Fabric API `0.116.0+1.21.1`.
- [Fabric] Now requires Fabric Loader `0.16.14`.
- [NeoForge] Now requires NeoForge `21.1.173`.
- Now requires AzureLib 3.0.20.
- Added new tooltip hints to armor case item.
- Added new tooltip hints to predator armor items.
- Added new tooltip hints to nether chitin armor items.
- Added new tooltip hints to plated nether chitin armor items.
- Added a new "event" for when queens spawn naturally in the world.
  - When a queen spawns, nearby players will get an ominous message.
  - A sound effect cue will also play when the queen spawns.
  - This something we're testing for future features, feedback is (always) appreciated <3.
- Added new alien blocks:
  - Aberrant Chitin Block
  - Aberrant Chitin Block Slab
  - Aberrant Chitin Block Stairs
  - Aberrant Chitin Block Wall
  - Aberrant Chitin Bricks
  - Aberrant Chitin Brick Slab
  - Aberrant Chitin Brick Stairs
  - Aberrant Chitin Brick Wall
  - Aberrant Resin Bricks
  - Aberrant Resin Brick Slab
  - Aberrant Resin Brick Stairs
  - Aberrant Resin Brick Wall
  - Aberrant Resin Slab
  - Aberrant Resin Stairs
  - Aberrant Resin Vent
  - Chiseled Aberrant Chitin
  - Chiseled Aberrant Chitin (Embryo)
  - Chiseled Chitin
  - Chiseled Chitin (Embryo)
  - Chiseled Nether Chitin
  - Chiseled Nether Chitin (Embryo)
  - Irradiated Chitin Block
  - Irradiated Chitin Block Slab
  - Irradiated Chitin Block Stairs
  - Irradiated Chitin Block Wall
  - Irradiated Chitin Bricks
  - Irradiated Chitin Brick Slab
  - Irradiated Chitin Brick Stairs
  - Irradiated Chitin Brick Wall
  - Irradiated Resin Bricks
  - Irradiated Resin Brick Slab
  - Irradiated Resin Brick Stairs
  - Irradiated Resin Brick Wall
  - Irradiated Resin Slab
  - Irradiated Resin Stairs
  - Irradiated Resin Vent
  - Nether Chitin Block
  - Nether Chitin Block Slab
  - Nether Chitin Block Stairs
  - Nether Chitin Block Wall
  - Nether Chitin Bricks
  - Nether Chitin Brick Slab
  - Nether Chitin Brick Stairs
  - Nether Chitin Brick Wall
  - Nether Resin Bricks
  - Nether Resin Brick Slab
  - Nether Resin Brick Stairs
  - Nether Resin Brick Wall
  - Nether Resin Slab
  - Nether Resin Stairs
  - Nether Resin Vent
  - Polished Aberrant Chitin
  - Polished Aberrant Chitin Slab
  - Polished Aberrant Chitin Stairs
  - Polished Aberrant Chitin Wall
  - Polished Chitin
  - Polished Chitin Slab
  - Polished Chitin Stairs
  - Polished Chitin Wall
  - Polished Nether Chitin
  - Polished Nether Chitin Slab
  - Polished Nether Chitin Stairs
  - Polished Nether Chitin Wall
  - Resin Brick Slab
  - Resin Brick Stairs
  - Resin Brick Wall
  - Resin Slab
  - Resin Stairs
  - Ribbed Aberrant Resin
  - Ribbed Irradiated Resin
  - Ribbed Nether Resin
  - Smooth Aberrant Resin
  - Smooth Aberrant Resin Slab
  - Smooth Aberrant Resin Stairs
  - Smooth Aberrant Resin Wall
  - Smooth Irradiated Resin
  - Smooth Irradiated Resin Slab
  - Smooth Irradiated Resin Stairs
  - Smooth Irradiated Resin Wall
  - Smooth Nether Resin
  - Smooth Nether Resin Slab
  - Smooth Nether Resin Stairs
  - Smooth Nether Resin Wall
  - Smooth Resin Slab
  - Smooth Resin Stairs
  - Smooth Resin Wall
- Added keybind to allow players to crawl on demand.
  - Player crawling already exists in vanilla, but there is no key to activate it on demand.
  - This update adds a keybind (left alt) to crawl on demand. Player crawling will be important for future AVP updates.

## ♻️ Changes
- Disabled armor case menu (for now, until it is fixed).
- Iron-like ingots can now be used to craft industrial furnaces.
- Iron-like ingots can now be used to craft razor wire.
- Increased the damage dealt by old painless by 6x, making it the most lethal weapon in the game.
- Old painless now consumes 6 bullets per shot instead of 1.
- Reduced queen resin generation per tick (10 -> 1).
- Removed queen spawn chunk blacklisting system.
  - With further testing we've found that it hurt gameplay more than helped, so we've gutted it.

## 🐞 Fixes
- Fixed old painless ammo consumption logic not accounting for consuming multiple bullets per shot.
- Fixed chestbursters spreading resin like adult xenomorphs.
- Fixed aberrant resin blocks not burning in fire or lava.
- Fixed players eating chorus fruit while infected causing facehuggers to spawn instead of the correct embryo type.
- Fixed aberrant resin being mineable with axes.
- Fixed irradiated resin being mineable with axes.
- Fixed the following blocks not dropping anything when mined:
  - Ferroaluminum Plating Slab
  - Ferroaluminum Plating Stairs
  - Ferroaluminum Slab
  - Ferroaluminum Stairs
  - Ferroaluminum Tread Slab
  - Ferroaluminum Tread Stairs
  - Steel Plating Slab
  - Steel Plating Stairs
  - Steel Slab
  - Steel Stairs
  - Steel Tread Slab
  - Steel Tread Stairs
  - Titanium Plating Slab
  - Titanium Plating Stairs
  - Titanium Slab
  - Titanium Stairs
  - Titanium Tread Slab
  - Titanium Tread Stairs

## 🛠 Data Pack
- Renamed `#avp:facehugger_protection_helmet` item tag to `#avp:facehugger_resistant_helmets`.
- Renamed `#avp:fire_resistant_armor` item tag to `#avp:fire_resistant_armors`.
- Renamed `#avp:hostile_weapon` item tag to `#avp:hostile_weapons`.
- Renamed `#avp:radiation_items` item tag to `#avp:radioactive_items`.
- Renamed `#avp:radiation_resistant_armor` item tag to `#avp:radiation_resistant_armors`.
- Renamed `#avp:predator_armor` item tag to `#avp:predator_armors`.
- Added `#avp:aberrant_chitin_armor` item tag.
- Added `#avp:irradiated_chitin_armor` item tag.
- Added `#avp:normal_chitin_armor` item tag.
- Added `#avp:plated_aberrant_chitin_armor` item tag.
- Added `#avp:plated_irradiated_chitin_armor` item tag.
- Added `#avp:plated_normal_chitin_armor` item tag.
- Added `#avp:chitin_armors` item tag.
- Added `#avp:plated_chitin_armors` item tag.

## 🔬 Technical Changes
- Added translations for all tags provided by the AVP mod.

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

# v0.1.7

## ☢️ Breaking Changes
- N/A

## ✨ What's New
- Added a new debug command `/avp debug hive layer current` to check which layer of a hive the player is currently in.

## ♻️ Changes
- Updated female marine model + `hair_5` texture.
- Irradiated queens now have a chance of spawning naturally in irradiated biomes.
- Hives are now broken up into layers:
  - Every hive is now represented as a "sphere" of influence.
  - The sphere consists of different layers. From closest to furthest away from the hive center, the layers are as follows:
    - Center Layer (Core). The queen resides here.
    - Praetorian Layer. Where Praetorians can naturally spawn.
    - Drone Layer. Where drones can naturally spawn. In the future, ovomorphs will be moved here by drones.
    - Warrior Layer. Where warriors can naturally spawn.
    - Edge Layer. Nothing spawns here, this is the border of the hive.
    - Leash Layer. Aliens can exit the core hive (resin area), but still be members of the hive beyond it if they are within this layer.
    - Buffer Layer. This is a layer double the width of the core layers + leash layer. No other hives (of the same variant) can form here.
  - Currently, this hive layering is only used for natural spawning. Later on the alien AI will have a bias towards resting in their respective layers.
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
  - `MINIMUM_DISTANCE_BETWEEN_HIVES_IN_BLOCKS`
    - This has now been replaced with `MINIMUM_DISTANCE_BETWEEN_NATURAL_QUEEN_SPAWNS_IN_CHUNKS`, which more accurately describes what the original config option was used for.

## 🐞 Fixes
- Fixed uprooted ovomorphs not despawning even if they have already hatched.
- Fixed nether resin nodes having incorrect block properties.
- Fixed irradiated resin, irradiated resin nodes, irradiated resin veins and irradiated resin webs having incorrect block properties.
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
  - Irradiated Resin
  - Irradiated Resin Node
  - Irradiated Resin Vein
  - Irradiated Resin Web
  - Resin
  - Resin Node
  - Resin Vein
  - Resin Web
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

# v0.1.6

## ☢️ Breaking Changes
- N/A

## ✨ What's New
- N/A

## ♻️ Changes
- Queens no longer forcibly create their own hives regardless of nearby hives.
- Queens may now join an existing hive even if that hive already has a leader.
  - This means a hive can have multiple queens. This is intended behavior for a future mechanic.

## 🐞 Fixes
- Fixed ovomorphs hatching and releasing facehuggers even while they are dead or dying.
- Fixed queens having issues creating new hives.
- Fixed hives not creating praetorians if a queen is present.
- Fixed hives sometimes breaking out into civil wars due to interference from multiple queens and/or hive leaders.
  - Please note that this only applies to newer hives and does not apply to existing hives.
  - If you have hives undergoing civil wars, one of the hives needs to lose (all xenomorphs in that hive are dead) so that the hive dies.

## 🛠 Data Pack
- N/A

## 🔬 Technical Changes
- N/A

# v0.1.5

## ☢️ Breaking Changes
- NOTE: If you have an existing world you must delete your old avp config for some changes in this update to take effect.

## ✨ What's New
- N/A

## ♻️ Changes
- Updated deepslate titanium ore texture to be more consistent with other deepslate ores.
- Xenomorphs now only move 10% faster instead of 20% faster when chasing a target.
- Significantly improved hit registration for all hitscan-based weaponry.
- Queens no longer despawn under any circumstances.
- Queens no longer immediately place a resin block down beneath wherever they are standing.
- Chestbursters, facehuggers and ovomorphs no longer count towards the hive's overall health.
  - These aliens will no longer count towards the hive boss bar.
  - They also will not keep the hive alive. If you kill all xenomorphs in the hive, the hive dies, even if there were chestbursters, facehuggers or ovomorphs within the hive.
  - If they are left alive under the right conditions, however, they can form an entirely new hive.
- Rebalanced queen resin spread stats to allow queens to spread resin faster and more frequently than drones.
  - We want queens to be able to "jump-start" a hive with this change. Previously they were too slow to spread any resin at all.
- All aliens (except queens) will now only naturally spawn in hives.
  - This means if there is resin outside a hive, aliens will no longer naturally spawn there.
  - Hives must be alive and must not be in an angered state in order for aliens to spawn in them.
- Aliens with a hive no longer despawn if their hive is in an angered state.
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
  - These are only display-facing, non-breaking changes. Ovomorphs will have their registry name changed starting with v0.2.0.
- Renamed "Raw Silica" item to "Silicon".
  - These are only display-facing, non-breaking changes. Raw Silica will have its registry name changed starting with v0.2.0.
- Renamed "Block of Raw Silica" block to "Block of Silicon".
  - These are only display-facing, non-breaking changes. Block of Raw Silica will have its registry name changed starting with v0.2.0.
- Reduced overall block damage dealt by acid by 80%.
  - Acid's block damage still scales with the "strength" of acid, which increases when acid entities overlap.

## 🐞 Fixes
- Fixed the inner jaws of both drones and warriors sticking out of their necks.
- Fixed aliens not checking their surroundings to see if there is enough space to grow.
- Fixed hatched ovomorphs closing up again after re-logging.
- Fixed hatched ovomorphs not closing up again after royal jelly is used on them.
- Fixed 'Imperfect Organism' advancement not including alternative strains of aliens or royal aliens.
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
- Fixed ovomorphs spawned from queens not persisting.
- Fixed facehuggers and xenomorphs lunging towards targets even if they aren't looking towards the target.
- Fixed irradiated queens laying eggs when they shouldn't.
- Fixed persistence not carrying over when an alien grows into the next stage.
- Fixed royal aliens not being acid immune.
- Fixed royal aliens not being tagged as aliens.
- Fixed missing name translations for aberrant, irradiated, nether and royal aliens.
- Fixed hive leaders being stuck in warrior form within small hives and not growing into queens.
- Fixed queen spawn checks only checking for nearby queens.
  - This created a scenario where a queen could spawn in a loaded chunk next to an unloaded queen in an unloaded chunk.
  - Instead, queen spawn attempts will now check for nearby *hives* instead of nearby queens, as hives are loaded regardless of chunk load state.
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

# v0.1.4

## ☢️ Breaking Changes
- Ported the mod to NeoForge! :)
  - Please note that this also required a substantial change to the Fabric version of the mod. We haven't found any issues in our testing, but please back up your worlds just to be safe!

## ✨ What's New
- N/A

## ♻️ Changes
- N/A

## 🐞 Fixes
- Fixed ModernFix crash (for good this time).

## 🛠 Data Pack
- Added NeoForge-related data pack files for worldgen and entity spawns.
- Added `#avp:razor_wire` block tag.
  - This tag is now used internally for shear item logic.
  - This change fixes the crash with ModernFix.

## 🔬 Technical Changes
- Rewrote the project to support multiple mod loaders.
- Added NeoForge modloader support.

# v0.1.3

## ☢️ Breaking Changes
- N/A

## ✨ What's New
- N/A

## ♻️ Changes
- N/A

## 🐞 Fixes
- Fixed block corruption occurring in servers caused by the previous attempt to fix a world crash caused by ModernFix.
  - Please note that this means ModernFix will cease to work with AVP, again - we're looking into why exactly ModernFix isn't playing nice with AVP and hope to have the issue fixed soon.

## 🛠 Data Pack
- N/A

## 🔬 Technical Changes
- N/A

# v0.1.2

## ☢️ Breaking Changes
- N/A

## ✨ What's New
- N/A

## ♻️ Changes
- Doubled range of M37-12 Shotgun (6 blocks -> 12 blocks).
- Doubled range of ZX-76 Shotgun (6 blocks -> 12 blocks).

## 🐞 Fixes
- Fixed hitboxes for the following entities being incorrectly sized:
  - Aberrant Chestburster
  - Aberrant Drone
  - Aberrant Facehugger
  - Aberrant Ovamorph
  - Aberrant Praetorian
  - Aberrant Queen
  - Aberrant Warrior
  - Irradiated Drone
  - Irradiated Praetorian
  - Irradiated Queen
  - Irradiated Warrior
  - Nether Chestburster
  - Nether Drone
  - Nether Facehugger
  - Nether Ovamorph
  - Nether Praetorian
  - Nether Queen
  - Nether Warrior
  - Royal Aberrant Chestburster
  - Royal Aberrant Facehugger
  - Royal Aberrant Ovamorph
  - Royal Nether Chestburster
  - Royal Nether Facehugger
  - Royal Nether Ovamorph
  - Royal Chestburster
  - Royal Facehugger
  - Royal Ovamorph

## 🛠 Data Pack
- N/A

## 🔬 Technical Changes
- N/A

# v0.1.1

## ☢️ Breaking Changes
- N/A

## ✨ What's New
- Added tooltip for ammo chest item.
- Added tooltip for lead chest item.
- Added tooltip for sentry turret item.
- Added new sound effects:
  - Ovamorph hatching sfx
  - Ovamorph rooting sfx
  - Ovamorph shearing sfx
  - Queen egg-laying sfx
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
- Added a sound effect for when royal jelly is used on ovamorphs.
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
- Fixed incorrect resin spreading subtitles.
- Fixed shearing ovamorphs causing damage to shears while in creative mode.
- Fixed rooting ovamorphs consuming resin balls while in creative mode.
- Fixed royal jellying ovamorphs consuming royal jelly while in creative mode.
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
- Added a log for when an ovamorph is unable to spawn a facehugger.

# v0.1.0

## ☢️ Breaking Changes
- N/A

## ✨ What's New
- Marines now wander around.

## ♻️ Changes
- Rebalanced radiation effect levels:
  - Level 1 Radiation now deals 0.5 damage (in half-hearts) every 4 seconds (was previously 0.1 damage every 4 seconds).
  - Level 2 Radiation now deals 1 damage (in half-hearts) every 2 seconds (was previously 2.1 damage every 2 seconds).
  - Level 3 Radiation now deals 2 damage (in half-hearts) every 1 second (was previously 5 damage every 1 second).

## 🐞 Fixes
- Fixed radiation effect applying forever.

## 🛠 Data Pack
- N/A

## 🔬 Technical Changes
- N/A

# v0.0.23-beta

## ☢️ Breaking Changes
- Renamed `shotgun_bullet` item ID to `shotgun_shell`.
  - Shotgun bullet items will disappear in pre-existing worlds.
- Renamed `caseless_casing` item ID to `caseless_cartridge`.
  - Caseless casing items will disappear in pre-existing worlds.
- Rewrote turret to be an entity instead of a block entity.
  - Works mostly the same as when it was a block entity regarding ammo chest usage, redstone power, etc.
  - This change allows mobs to target and attack the turret.
  - Sentry turrets are unaffected by all mob effects, positive or negative.
  - Sentry turrets now fire at a much faster rate.
  - Sentry turret damage has been reduced 50% (4 half-hearts -> 2 half-hearts).
  - Since they are a machine entity, sentry turrets are immune to most forms of environmental damage, including but not limited to:
    - Burning (Camp Fire blocks, Fire blocks, Magma Blocks, etc.)
    - Cactus (Cactus is currently too OP and needed a nerf)
    - Drowning
    - Falling
    - Fireworks
    - Freezing
    - Poison
    - Radiation
    - Razor Wire
    - Suffocation (No breathing)
    - Withering
    - And more like Bee stings, Starvation, etc.
- Updated registry names for the following blocks:
  - Ammo Chest (`avp:ammo_chest_be` -> `avp:ammo_chest`)
  - Desk Terminal (`avp:desk_terminal_be` -> `avp:desk_terminal`)
  - Industrial Furnace (`avp:industrial_furnace_be` -> `avp:industrial_furnace`)
  - Lead Chest (`avp:lead_chest_be` -> `avp:lead_chest`)
  - Resonator (`avp:resonator_be` -> `avp:resonator`)
  - Sentry Turret (`avp:sentry_turret_be` -> `avp:sentry_turret`)
  - Trip Mine (`avp:trip_mine_be` -> `avp:trip_mine`)
- Changed alien variants to have their own standalone entity types.
  - This didn't seem to break anything in our dev environment, but it's a significant change, so we've marked it as breaking to be safe.

## ✨ What's New
- Added alien music disc 1 "Silver Smile" by Rotch Gwylt.
  - Find all 9 fragments across all marine structures to craft the disc!
- Added predator music disc 1 "Hunter" by Rotch Gwylt.
  - Find all 9 fragments across all marine structures to craft the disc!
- Added alien music disc 1 fragment item.
- Added predator music disc 1 fragment item.
- Added mobile lab structure.
- Added commissary house to all vanilla villages.
- Added outpost munition structure.
- Added ammunition indicator mechanic:
  - A message will show to the player when they are low on or out of ammunition.
  - The low ammunition threshold is less than 20% ammunition remaining.
  - The ammunition indicator only goes based on ammunition already loaded into the gun.
- Added new industrial glass blocks:
  - Industrial Glass Slab
  - Industrial Glass Stairs
  - Industrial Glass Door
  - Industrial Glass Trap Door
- Queens now lay eggs:
  - No eggsack yet, that's still a work in progress.
  - There is a 5% chance the egg will be a royal ovamorph every time she lays and egg.
  - Queens can lay eggs under the following conditions:
    - It's been more than 1 minute since she last laid an egg.
    - There are no other FRIENDLY eggs near her within a 4 block radius. Enemy eggs do not count.
    - She is not aggro'd towards any mob.
    - She has a hive.
    - Her hive is alive.
    - Her hive is chunk loaded.
    - She is within her hive boundaries.
- Overhauled marines:
  - Introduced new AI system for marines (GOAP - Goal-Oriented Action Planning).
    - Marines will plan on-the-fly picking the best goal to accomplish, and will pick from a pool of actions to satisfy that goal.
  - Marines now have a dedicated inventory (technically 2, 1 primary inventory and 1 hidden inventory).
    - Items that the marines pick up go in their primary inventory.
    - Marines drop all items in their primary inventory on death.
    - Items that the marine spawns in with go in their hidden inventory.
    - Marines do NOT drop items in their hidden inventory.
  - Marine gun attacks are now consistent with player gun attacks (making them far more deadly).
  - Marines shooting sound effects now match their held gun type.
  - Marines now start off with their weapon unequipped, and equip it when they spot a target.
  - Marines now pick up food items that are on the ground.
  - Marines eat food items in their inventory if their health is < 50%.
  - Marines can now spawn with iron axes or iron swords.
  - Fixed incorrect positioning of items for both male and female marine models.
- Added new item textures for the following armor sets:
  - Aberrant Chitin Armor
  - Irradiated Chitin Armor
  - Plated Aberrant Chitin Armor
  - Plated Irradiated Chitin Armor

## ♻️ Changes
- Removed gun reload cooldown in creative mode.
- Irradiated chitin can now be blast smelted into regular chitin.
- Updated ash block texture.
- Removed item recipes and creative mode tab entries for both irradiated chitin armor sets.
  - They will be added back once they've been more refined.
- Removed resonator recipe.
  - It will be added back once the block is more refined.
- Changed radiation mechanics:
  - Container blocks and block items will no longer emit radiation passively.
    - After further testing, we found that this functionality hurt game performance too much for it to be worthwhile.
    - We also found that it wasn't intuitive where radiation effects were coming from.
    - The following containers are affected:
      - Block Item containers (Chest item entities with radioactive items inside of them)
      - Boat Chests
      - Chests
      - Minecart Chests
      - Shulker Boxes
- Updated the following textures for male marines:
  - Beard 0
  - Beard 1
  - Beard 2
  - Hair 0
  - Hair 1
  - Hair 3

## 🐞 Fixes
- Fixed more than 1 predator spawning within jungles.
- Fixed players consuming ammunition while reloading guns in creative.
- Fixed old painless showing bogus ammo capacity tooltip values.
- Fixed the mod not working with Sinytra Connector on NeoForge.
- Fixed facehuggers immediately dying when becoming infertile while attached to a host.
- Fixed facehugger infertile animation not playing correctly.
- Fixed `/avp nuke nuke` command, it is now `/avp test nuke`.
- Fixed facehuggers staying on invalid hosts (thanks, carry-on mod).
- Fixed the drone's inner jaw clipping through their head.
- Fixed the warrior's inner jaw clipping through their head.
- Fixed aliens not targeting marines correctly.
- Fixed marines swinging their arms too far when running.
- Fixed aliens variants not rendering correctly in monster spawners.
- Fixed aliens losing their variants when being picked up with the Carry-On mod.
- Fixed JEI crash, no longer supported. Use REI for recipe viewing.
- Fixed irradiated queen rendering with a black texture due to missing glow layer texture.
- Fixes ammo chest not loading loot tables.
- Fixed royal ovamorph spawns not being royal for all royal altar structures.
- Fixed industrial furnace blocks not dropping themselves as an item when mined.
- Fixed parasites removed via eating chorus fruit being teleported into walls.
- Fixed parasites removed via eating chorus fruit always producing a regular chestburster.
- Fixed parasites removed via eating chorus fruit having host data incorrectly attached to them.
- Fixed aberrant ovamorph texture having regular resin instead of aberrant resin.
- Fixed chestbursters not running (slithering?) away from irradiated xenomorphs.
- Fixed chestbursters not moving due to their follow range defaulting to 0 in the config.
  - NOTE: This is not a backwards-compatible fix because once attributes are applied to an entity, they're stuck like that. This fix only applies to new chestbursters.
- Fixed commissary villagers throwing wheat seeds at players with the Hero of the Village effect.
  - They now instead throw either 1 small or 1 medium bullet at the player.
- Fixed Industrial Recipes including resin nodes.
- Fixed marines not storing the following features to their entity data:
  - Beard color
  - Beard variant
  - Eye color
  - Gender
  - Hair color
  - Hair variant
  - Skin color

## 🛠 Data Pack
- Rename tag `#avp:mobile_lab` to `#avp:has_mobile_lab`.
- Rename tag `#avp:marine_camp_grass` to `#avp:has_marine_camp_grass`.
- Added `#avp:melee_weapons` and `#avp:ranged_weapons` item tags.
- Added `#avp:ovamorphs` entity type tag.
- Added `#avp:does_not_hurt_sentry_turrets` damage type tag.

## 🔬 Technical Changes
- Bumped gradle version from `8.12` to `8.13`.
- Updated Just version from `a7d374ee33` to `7edae6d449`.
- Rewrote marine rendering code to use dynamic coloring, reducing the number of asset files considerably.

# v0.0.22-beta

## ☢️ Breaking Changes
- Removed Royal Jelly Canister item.
  - May be re-added at some point in the future.
- Removed minY/maxY spawn level properties from config.
  - After further consideration, these properties only ended up confusing players as to why certain aliens weren't spawning in hives.
  - The default values were too strict, and there's no reason a Praetorian can't spawn in a hive that's on the surface.
  - The queen and predator max/min Y level limits remain unchanged, but are currently hardcoded. We may make them configurable in a future update.

## ✨ What's New
- Added aberrant chitin armor set.
- Added irradiated chitin armor set.
- Added plated aberrant chitin armor set.
- Added plated irradiated chitin armor set.
- Added irradiated queens.
- Added new structure in badlands, `badlands_royal_altar`
- Added Chest loot tables for Marine structures.
- Queen sounds registered.
- Added Desert, deepslate, jungle and nether Royal Altars.
- Added Crafted Resin Blocks.
- Added new textures for the queen's following forms:
  - Aberrant
  - Base
  - Irradiated
  - Nether
- Added new, fully-functional canister items:
    - Function like buckets, but have a capacity of 8 instead of 1.
    - Shift-right-click fluids to pick fluids up.
    - Right-click replaceable blocks (air, vines, tall grass, etc.) to place fluids down.
    - Canisters support the following fluids:
        - Lava
        - Milk
        - Powder Snow
        - Water
    - Credits to [Cerbon](https://github.com/CerbonXD) for implementation.


## ♻️ Changes
- Marines can now spawn on grass blocks.
  - This only affects their spawns within camp structures, at the moment.
- Reduced spawn weight of marines in marine camps (25 -> 15).
- Reduced spawn weight of marines in communication outposts (25 -> 15).
- Cut nuke's default block sample count in half to improve performance.
- Increased royal chestburster's growth time into a praetorian 3x (600 seconds -> 1800 seconds).
- Villagers now only run away from adult xenomorphs instead of all aliens.
- Updated marine camp loot tables.
- Xenomorphs will now continue to pursue targets that move off of resin.

## 🐞 Fixes
- Fixed irradiated xenomorph spawn egg colors.
- Fixed xenomorphs lunging even when they do not have line-of-sight to their target.
- Fixed xenomorph, marine and predator AI freezing up when their health is below 50%.
- Fixed irradiated xenomorphs turning blocks at incorrect positions into blue ice blocks when hurt.
- Fixed xenomorphs attacking targets off of resin when they shouldn't.
- Fixed irradiated xenomorphs not attacking xenomorphs that were the irradiated xenomorph's original variant type.
- Fixed drone being considered as a royal alien.
- Fixed irradiated praetorian not being considered a royal alien.
- Fixed boats spawning underneath marine camps.
- Fixed ovamorphs spawning with altar structures despawning when they shouldn't.
- Fixed grenade explosions leaving ghost blocks behind.
- Fixed damage source instances being allocated every radiation effect tick.
- Fixed marine's head being slightly tilted.
- Fixed trinitite blocks having broken transparency.
- Fixed irradiated praetorians having incorrect textures.
- Fixed Smart Disc duplication for creative players.
- Fixed irradiated grenade items passively irradiating entities.
- Fixed z-fighting on predator's mandible membranes.
- Fixed royal chestbursters not growing up directly into their respective praetorian types.
- Fixed the following royal facehuggers not producing their respective royal chestbursters:
  - Royal Facehugger
  - Royal Aberrant Facehugger
  - Royal Nether Facehugger
- Fixed the following royal ovamorphs not producing royal facehuggers:
  - Royal Aberrant Ovamorph
  - Royal Nether Ovamorph
  - Royal Ovamorph
- Fixed artificial spawning issues with AVP entities:
  - Fixed `/summon` command summoning AVP entities with incorrect data.
  - Fixed spawner blocks spawning AVP entities with incorrect data.
  - This affected entities in multiple ways. Such as:
    - Royal chestbursters, facehuggers and ovamorphs not spawning as royal.
    - Ovamorphs spawning with no facehugger inside.
    - Ovamorphs spawning with a tiny size.
    - Facehuggers spawning infertile.
    - ...and potentially other entity data bugs that were fixed but not discovered prior to the fix.
- Fixed the following alien entity types being inverted:
  - Royal Chestburster (inverted with Aberrant Royal Chestburster).
  - Royal Facehugger (inverted with Aberrant Royal Facehugger).
  - Royal Ovamorph (inverted with Aberrant Royal Ovamorph).
- Fixed the following aliens being improperly tagged as `#avp:xenomorphs`:
  - Royal Aberrant Chestburster
  - Royal Aberrant Facehugger
  - Royal Aberrant Ovamorph
  - Royal Nether Chestburster
  - Royal Nether Facehugger
  - Royal Nether Ovamorph
  - Royal Chestburster
  - Royal Facehugger
  - Royal Ovamorph
- Fixed irradiated acid replacing the following blocks:
  - Air
  - Barrier Block
  - Bedrock
  - Chain Command Block
  - Chest
  - End Gateway
  - End Portal
  - End Portal Frame
  - Jigsaw Block
  - Light Block
  - Moving Piston Block
  - Reinforced Deepslate
  - Repeating Command Block
  - Spawner Block
  - Structure Block
  - Trial Spawner Block
  - Vault Block
- Fixed the following blocks showing in creative tabs when they shouldn't:
  - Aberrant Resin Node
  - Irradiated Resin Node
  - Nether Resin Node
  - Resin Node
- Fixed the following blocks missing the `#minecraft:mineable_with_axe` tag:
  - Aberrant Resin Vein
  - Irradiated Resin Vein
- Fixed the following aberrant aliens being incorrectly marked as irradiated aliens:
  - Aberrant Ovamorph
  - Aberrant Praetorian
  - Aberrant Queen
  - Aberrant Warrior
- Fixed the following entities giving the wrong picked spawn egg result in creative mode:
  - Royal Ovamorph
  - Royal Facehugger
  - Royal Chestburster

## 🛠 Data Pack
- Added `has_badlands_altar` tag for biomes that the badlands altar can spawn in.
- Removed aberrant aliens from `#avp:irradiated_aliens` tag.
- Added `#minecraft:undead` tag to `#avp:radiation_resistant` tag.
- Added `#avp:has_desert_altar` biome tag.
- `#avp:has_altar` biome tag is now composed of `#avp:has_badlands_altar` and `#avp:has_desert_altar`.
- Added `#avp:jungle_predator_armor` item tag.
- Added `#avp:mk50_armor` item tag.
- Added `#avp:nether_chitin_armor` item tag.
- Added `#avp:plated_nether_chitin_armor` item tag.
- Added `#avp:pressure_armor` item tag.
- Added `#avp:fire_resistant_armor` item tag.
  - Composed of `#avp:nether_chitin_armor` and `#avp:plated_nether_chitin_armor` item tags.
- Added `#avp:predator_armor` item tag (future-proofing).
  - Composed of `#avp:jungle_predator_armor` item tag.
- Corrected multiple data pack tagging mistakes:
  - Drones are no longer tagged as `#avp:royal_aliens`.
  - Irradiated praetorians are now tagged as `#avp:royal_aliens`.
  - Fixed the following aliens being improperly tagged as `#avp:xenomorphs`:
    - Royal Aberrant Chestburster
    - Royal Aberrant Facehugger
    - Royal Aberrant Ovamorph
    - Royal Nether Chestburster
    - Royal Nether Facehugger
    - Royal Nether Ovamorph
    - Royal Chestburster
    - Royal Facehugger
    - Royal Ovamorph

## 🔬 Technical Changes
- Refactored smart disc return to owner code.
- Refactored REI support to new `com.avp.client.compat.rei` location.
- Refactored Trades to dedicated class.
- Removed unnecessary code in RadiatedBlock.java.
- Cleaned up marine animation code, slightly more optimal.
- Alphabetically sorted block tag contents.
- Cleaned up acid damage code.
- Added Just library dependency.
- Refactored hive code to use Just types.
- Refactored embryo growth code (cleanup / code-splitting).
- Refactored alien targeting checks to make the code more readable and easier to maintain.
- Removed Chestburster Queen entity type.
  - Was never fully implemented as an entity.

# v0.0.21-beta

## ☢️ Breaking Changes
- Corrected irradiated xenomorph registry names:
  - `irraiated_drone` -> `irradiated_drone`
  - `irraiated_warrior` -> `irradiated_warrior`
  - `irraiated_praetorian` -> `irradiated_praetorian`
- Corrected irradiated aliens tag naming:
  - `irraiated_aliens` -> `irradiated_aliens`

## ✨️ What's New
- N/A

## ♻️ Changes
- Flamethrower now spreads fire in a more natural shape.
  - Previously it spread fire in a box, now it spreads fire in a sphere.

## 🐞 Fixes
- Fixed commissary villager's texture layer not respecting custom villager textures.
- Fixed commissary villager's zombie form missing a texture.
- Fixed MK50 rendering brown instead of white when undyed.
- Fixed MK50 armor not blocking radiation effect from chests.
- Fixed gun side effects hurting players when pvp is off.
- Fixed gun side effects not happening in the correct order.
- Fixed random holes in flamethrower flames.
- Fixed dyed industrial glass blocks not changing beacon colors.
- Fixed flamethrower spreading fire through walls.
- Fixed pulse rifle recipe not requiring a stock as an ingredient.
- Fixed numerous bugs with radiation effects being applied:
  - Fixed irradiated boat chests irradiating radiation-resistant entities.
  - Fixed irradiated minecart chests irradiating radiation-resistant entities.
  - Fixed irradiated resin block irradiating dead entities.
  - Fixed irradiated resin block irradiating radiation-resistant entities.
  - Fixed irradiated resin node block irradiating dead entities.
  - Fixed irradiated resin node block irradiating radiation-resistant entities.
  - Fixed irradiated resin vein irradiating dead entities.
  - Fixed irradiated resin vein irradiating radiation-resistant entities.
  - Fixed irradiated resin web irradiating dead entities.
  - Fixed irradiated resin web irradiating radiation-resistant entities.
- Fixed the following slabs not dropping their items correctly:
  - Ferroaluminum Fastened Siding Slab
  - Ferroaluminum Fastened Standing Slab
  - Ferroaluminum Grate Slab
  - Ferroaluminum Siding Slab
  - Ferroaluminum Standing Slab
  - Steel Fastened Siding Slab
  - Steel Fastened Standing Slab
  - Steel Slab
  - Steel Siding Slab
  - Steel Standing Slab
  - Titanium Fastened Siding Slab
  - Titanium Fastened Standing Slab
  - Titanium Slab
  - Titanium Siding Slab
  - Titanium Standing Slab
- Fixed the following stairs not dropping at all:
  - Ferroaluminum Fastened Siding Stairs
  - Ferroaluminum Fastened Standing Stairs
  - Ferroaluminum Grate Stairs
  - Ferroaluminum Siding Stairs
  - Ferroaluminum Standing Stairs
  - Steel Fastened Siding Stairs
  - Steel Fastened Standing Stairs
  - Steel Stairs
  - Steel Siding Stairs
  - Steel Standing Stairs
  - Titanium Fastened Siding Stairs
  - Titanium Fastened Standing Stairs
  - Titanium Stairs
  - Titanium Siding Stairs
  - Titanium Standing Stairs
- Fixed the following doors duplicating when dropped:
  - Ferroaluminum Door
  - Steel Door
  - Titanium Door
- Fixed the following entities giving the wrong picked spawn egg result in creative mode:
  - Irradiated Drone
  - Irradiated Praetorian
  - Irradiated Warrior
- Fixed the following entities dropping the wrong items on death:
  - Aberrant Drone
  - Aberrant Praetorian
  - Aberrant Queen
  - Aberrant Warrior
  - Irradiated Drone
  - Irradiated Praetorian
  - Irradiated Warrior
  - Nether Queen
- Fixed typos:
  - Aberant -> Aberrant
  - Irraiated -> Irradiated

## 🛠 Data Pack
- Renamed `irraiated_aliens` entity type tag to `irradiated_aliens`.

## 🔬 Technical Changes
- Rewrote alien variant handling to prevent future bugs with drops and spawn egg picking.
- Cleaned up radiation effect code.
- Fixed class typo (`MixinLivingEntity_NukedRadation` -> `MixinLivingEntity_NukedRadiation`).

# v0.0.20-beta

## What's New
- Full set of Predator armor gives jump boost now.
- Full set of Predator armor gives damage boost now.
- Resonator is powered by Redstone now.
- Resonator when powered will remove 1 resin block in a 25 block radius.
  - It will then store in itself 1 resin ball of the type of resin it removed.
  - Resin veins are replaced with air.
  - All other resin blocks are replace with stone or deepslate depending on the y value (0 and lower deepslate, higher stone)
  - Can be right-clicked to get all resin balls but will also drop them if the block is broken.
- Adds start of Nuke cloud that playes when a nuke block is used.

## Changes
- Desk Terminal now has facing values set.
- Adds light source back to guns, this can cause lag.
- Refactored Turret and Trip mine configs to new Block Config section.
- Redid how Xenos should float in 1 block deep water, meaning they just walk in it now.
- Queens now can go up 2 blocks.
- Get rid of shitty item texture on Sentry Turret
- Adjusts voxel shape of Desk Terminal.
- Block entities data fix spam fixed.

## Fixes
- Removes all but predator armor from `FREEZE_IMMUNE_WEARABLES` tag.
- Fixes walking up blocks issue.
- Fixes Nether acid breaking bedrock with its fire.
- Fixes normal Praetorian loot table.
- Fixes Irradiated Warriors loot table.
- Fixes Queens not respecting the do not replace tag.
- Fixes jelly item not being used up.
- Fully fixes nuke not rendering at a far distances.

## Data Pack
- Adds missing `acid_immune` tag to `nether_acid_immune`
- Adds `resin_veins` tag to track all resin veins.

# v0.0.19-beta

## What's New
- Nukes will now turn any aliens that have an Irradiated form into that form.
- Adds ash block that appears when a nuke goes off and spreads over time.
- Makes MK50 dyeable.
- Queens now place a resin node under them when first spawned.
- Added Gun flashes to most guns.
- Golden Apples (normal/enchanted) can now be used to cure the Radiation effect.
- Adds basic Sentry Turret.
  - Currently, requires ammo chest within a configurable 5 block radius with Medium bullets.
  - Currently, requires Redstone power. (Will need its GUI implemented)
  - Currently only targets monsters. (Will need its GUI implemented for different targeting) 
  - Will break blocks to damage monsters in it's FOV (45 degrees in front of itself where it's facing). (Will need it's GUI implemented for different FOV range)
  - Currently, it does not rotate to target the monster it's trying to get.
  - Currently, has a range of 32 blocks, configurable.
  - Turrets now require an ammo chest with Medium Bullets
- Adds in Marine Patrols in the same biomes that the camps can spawn in.
- Ash blocks will generate in Nuked biomes if an active player is in one.
- Chorus Fruit can now remove embryos instead of teleporting entity, leaving the host alive.
- Mob Effects now are inherited.
- Witches that generate a burster will now pass 1 random effect.
- Adds Outpost Comms structure, needs tags.
- Adds Irradiated acid that is blue/white and turns blocks to blue ice instead of eating them.
- Adds Lead Chest
  - Blocks radation effect from items in it.
  - When in inventory, picks up radiation giving items until full.
- Chests, Shulkers, Minecarts with Chests, Boats with Chests that have radiation giving items now give off radiation in a 3 block radius.
- Adds Ammo Chest
  - Only takes ammo tagged items.
  - Guns will pull from it's inventory first.
- Industrial furnaces will now melt resin full blocks into green plastic blocks.
- Add Royal Ovamorph.
- Add Royal Facehugger.
- Add Royal Burster.
- Poison jelly on Royal eggs turn them into an Aberrant Royal Egg.
- Add Commissary Villager type
- Adds in REI support
- Adds Redstone Crystal dummy item.
- Adds Servo item. 
- Adds Speaker item.
- Adds Redstone Generator dummy block.
- Adds Desk Terminal dummy block.
- Adds Trip Mine dummy block.
  - Explodes when a non Yautja entity is near it for more than 5 seconds.
  - Plays a sound every second as a count down.
- Adds Resonator dummy block.
- Adds Nuclear Battery dummy item.
- Add Blueprint Block
  - Villager now uses it
- Adds Queen animations.
- Yautja now lose mask at 50% health.
- Yautja blades now show when not holding an item and is aggressive.
- Yautja can be facehugged when mask less.
- Yautja can not regain mask if healed over half heath again.
- Players wearing helmets tagged in `facehugger_protection_helmet` can't be facehugged now.

## Changes
- Adjusts how Marine Camps spawn in the world, will only apply to new ones.
- Adjusts how many Marines spawn at Camps.
- Marines now target all monsters, not just aliens.
- Updates Trinitite block texture.
- Nuked Biomes are no longer cold.
- Nuked Biome monster spawns adjusted.
- Marine Camp spawns adjusted again.
- Tweaks done to marine firing.
- Tweaks done to Predator throwing items.
- Predators will call for help from nearby predators if attacked.
- Predators will now target all aliens, any mob holding a `hostile_weapon` tagged item, or any entity that hurts them.
- Adds reload animations to all guns.
- Nuked Biomes temperature increased.
- Disables fleeing goal for now.
- Reimplements attacking only on resin.
- MK50 no longer removes the Radiation effect.
- Kills Parasites when no longer fertile.
- More tweaks to Marine spawn camp spawning limits.
- Predators no longer do a light a check to spawn.
- Aberrant is now a 10% stat decrease.
- Irradiated is a 20% stat boost.
- Xenos now jump out of 1 block deep water.
- Marines, Yautja, and Xenos can open doors now.
- Xenos can walk over fences.
- Nether acid now sets blocks on fire.
- Made fire placement from flamethrower a bit more random.
- Adjusts Yautja head yaw offset

## Fixes
- Fixes crash related to custom mob category, due to how early it loads, configuration not possible, so removed and enabled by default.
- Fixes Irradiated Resin node crash.
- Fixes Nether Queen not requiring resin by default.
- Fixes rockets leaving water holes.
- Fixes Nuke block entity render distance.
- Fixes Death rotation of Facehuggers.
- Fixes Marines being a valid target at all for other Marines.
- Fixes Aberrant/Nether Queens don't drop chitin of their type.
- Fixes Marine projectile is on fire.
- Fixes Marine Facehugger placement broke with model change.
- Fixes block outline on Trinitite blocks.
- Fixes Nether aliens spawning everywhere instead of the nether.
- Fixes Marines spawning on roofs.
- Fixes Aliens only prioritizing one thing until it's killed rather than killing stuff near it.
- Fixes gun firing animation playing when empty.
- Fixes grenades ghost blocks and such.
- Fixes all guns firing animations.
- Fixes doors not giving 3 in the recipe.
- Fixes trapdoors not giving 2 in the recipe.
- Fixes doors see through issue when against a block.
- Fixes Female Marines texture hole.
- Fixes Female Marines left cuff being grouped wrong.
- Fixes missing Radiation effect icon.
- Fixes Aberrant/Irradiated resin not mineable with axes.
- Fixes hives somes time spreading the wrong resin.
- Fixes Old Painless spin animation not playing.
- Fixes ZX76 Shotgun not animating properly.
- Fixes lang issues with Irradiated.
- Fixes missing drops for doors, trapdoors, buttons, and slabs.
- Fixes Yautja not swimming/floating.
- Fixes Marines not swimming/floating.
- Fixes gun flashes appearing when not firing.
- Fixes Industrial Furnace display name.
- Fixes Queens, Facehuggers, Ovamorphs, and Chestbursters from be coming Irradiated.

## Data Pack
- Adds Undead tag to `radiation_resistant` entity tag.
- Added block tag `marine_spawn_blocks` to limit what blocks in a structure marines can spawn on
- Added item tag `hostile_weapon` to tag items that Predators find hostile to them.
- Fixes missing tags for items of `fences`, `doors`, `trapdoors`, `slabs`, `buttons`, `stairs`, `walls`, `freeze_immune_wearables`
- Added item tag `radiation_cure_items` to tag items that when used can cure the Radiation effect.
- Added item tag `radiation_items` to tag items that will give off radiation.
- Added item tag `ammo_items` to tag items that the ammo block will accept and use.
- Added item tag `facehugger_protection_helmet` to tag helmets that will block facehuggers, default to only `jungle_predator_helmet`.

# v0.0.18-beta

## What's New
- Chestbursters will now avoid non-aliens and enemy hive members.
- Added dynamic gene inheritance:
  - Xenomorphs will now inherit attributes proportionate to their host's attributes.
  - For example, hosts with high attack damage will give the parasite higher base attack damage.
  - This change applies to the following attributes:
    - Armor
    - Armor Toughness
    - Attack Damage
    - Move Speed
- Added Ferroaluminum, Steel, Titanium Doors, Trapdoors, buttons, pressure plates, slabs, and stairs  (Credit to Small_chubby)
- Added in Royal Jelly block.
- Added in Royal Jelly block recipes. 9 raw Royal Jelly to 1 block and 1 block is 9 raw Royal Jelly.
- Added in Royal Jelly can now evolve the aliens.
- Added in Poison Jelly, this can stop aliens from evolving.
- Added base nuke block with config to enable TNT like system.
- Added Trinitite block.
- Added Grenades
  - Normal
  - Incendiary
  - Irradiated
- Added Marines
  - Can spawn with Iron Armor
  - Can spawn with Weapons and grenades (grenades not yet useable by Marines)
- Added in Marine camp that generates in grassy biomes and spawns marines.
- Marines added as vaild Humonaid host.
- Added lifecycle growth timer configs.
- Predators now can get their own Mob Category/spawn cap.
- Added camo variant of Tactical Camo.
- Added in Industrial Furance, that is 2x faster and has extra recipes.
- Added in Nether and Aberrant Queen variants.
- Added in Radiation effect that damages and affects in stages, getting worse the higher the stage.
- Added in Nuked Biome.
  - Creepers spawn in powered.
  - 10% chance Aliens (not Queens) can be mutated if in nuked biomes, turning irradiated.
  - Can spawn Husks and Strays.
- Radiation now added to nuke biomes.
- Added in Shurkien.
- Added in Smart disc.
- Added in mostly finished Russian/Ukrainian langs (Credit to GræyBMW)
- Added in templates for Chinese, French, Korean, and Spanish, with PRs welcomes for those.
- Irradiated aliens don't evolve.
- Added in default tag for aliens to not need oxygen with the mod Stellaris
- Added in Aberrant and Irradiated resin blocks and items.
- Irradiated items and blocks give off radiation now.

## Changes
- Acid's maximum multiplier has been reduced from 10 to 5.
- MK50 suit helmets now only accept industrial glass panes and not industrial glass blocks in recipe.
- Pressure suit helmets now only accept industrial glass panes and not industrial glass blocks in recipe.
- LEDs now only accept industrial glass panes and not industrial glass blocks in recipe.
- LED Displays now only accept industrial glass panes and not industrial glass blocks in recipe.
- Transistors now only accept industrial glass panes and not industrial glass blocks in recipe.
- Ammo now takes nuggets instead of ingots.
- Facehugger death animation changed.
- Configuration system rewritten, now supports Mod Menu for changing in game and syncing from servers.
- Casing textures updated.
- Hives can only generate 1 Queen
- Queens that aren't hive leaders make a new hive.
- Weapon animations started (Heavy WIP).
- Now requires latest Azurelib 3.0.9.
- Nukes now make an area a Nuked Biome.
- Aliens, Yautja, and Marines flee a fight at half health.
- Yautja now spawn with a Shurkien or Smart disc and use them.
- Hives/Aliens now ignore creepers.
- Nuked biomes now extend 2 chunks past the crater.
- New textures/models for Drones, Warriors, and Praetorians.
- Renamed nuke command from `test` to `nuke`.
- Removed config command, no longer needed.
- Aliens no longer require resin for targeting mobs.

## Fixes
- Steel bars have proper block properties.
- Chain fences have proper block properties.
- Razor wire missing tool (Shears).
- Facehuggers jumping between hosts.
- 3rd person view fixed on Flamethrower, Smartgun, Old Painless.
- Xenos can't spawn between Aberrant and Irraiated now.

## Data Pack
- Added `industrial_glass_block` block tag.
- Added `industrial_glass_pane` block tag.
- The `industrial_glass` block tag is now composed of `industrial_glass_block` and `industrial_glass_pane` block tags.
- Added `industrial_glass_block` item tag.
- Added `industrial_glass_pane` item tag.
- The `industrial_glass` item tag is now composed of `industrial_glass_block` and `industrial_glass_pane` item tags.
- Added `radiation_resistant` entity tag.
- Added `radiation_resistant_armor` item tag for armor that can keep you from getting radiation effect. Currently just MK50.

# v0.0.17-beta

## What's New
- Added new spawning behaviors for normal aliens and nether aliens:
  - Nethermorphs are now able to spawn on nether resin in the overworld or end.
  - Normal xenomorphs are now able to spawn on normal resin in the nether or end.
- Aluminum, ferroaluminum, steel and zinc are now treated as "iron-like" metals. These metals can now be used as substitutes for iron in the following recipes:
  - activator rail
  - anvil
  - blast furnace
  - bucket
  - cauldron
  - crafter
  - crossbow
  - detector rail
  - flint and steel
  - hopper
  - minecart
  - piston
  - rail
  - shears
  - shield
  - smithing table
  - stonecutter
  - tripwire hook
- AVP's pottery sherds now generate in cold ruins and ocean ruins:
  - Parasite and vector sherds can be found in the warm ocean ruins.
  - Ovoid and royalty sherds can be found in the cold ocean ruins.
- Wearing nether chitin (regular or plated, full or mixed set) now grants permanent fire resistance.
- The following items are now fire-resistant (do not burn in fire or lava like netherite items):
  - jungle predator helmet
  - jungle predator chestplate
  - jungle predator leggings
  - jungle predator boots
  - nether chitin
  - nether resin (block)
  - nether resin ball
  - nether resin vein (block)
  - nether web (block)
  - plated nether chitin
  - veritanium shard
  - veritanium axe
  - veritanium hoe
  - veritanium pickaxe
  - veritanium shovel
  - veritanium sword
- Added new config-related commands:
  - `/avp config check {property}` Allows for viewing a config property's current value.
  - `/avp config reset {property}` Allows for resetting a property in a config file to its original value.
  - `/avp config set {property} {value}` Allows for setting a property in a config file to the specified value.
- Added `hive.debug.highlight_all_members` config property.
- Added spawning config options for all mobs:
  - `weight` controls how frequently the mob spawns.
  - `min_group_size` The minimum count of the mob that spawns together.
  - `max_group_size` The maximum count of the mob that spawns together.
  - The following mobs support these new spawning properties:
    - Ovamorph
    - Chestburster
    - Drone
    - Warrior
    - Praetorian
    - Queen
    - Nether Ovamorph
    - Nether Chestburster
    - Nether Drone
    - Nether Warrior
    - Nether Praetorian
    - Yautja

## Changes
- Xenomorphs will now target entities that are targeting fellow hive members.
  - An example scenario is if xenomorph A attacks a zombie piglin, and other nearby zombie piglins now target xenomorph A. Xenomorphs B and C will preemptively attack the zombie piglins for targeting their fellow hive members.
- Nether-afflicted acid will no longer burn through infiniburn nether blocks. These blocks include:
  - Netherrack
  - Magma
- Rebalanced block break times for the following block sets (for reference, an iron block is 5s):
  - Aluminum (2s -> 3s)
  - Brass (5s -> 4s)
  - Ferroaluminum (7s -> 5.5s)
  - Industrial Glass (10s -> 5s)
  - Lead (1s -> 2s)
  - Plastic (9s -> 4s)
  - Steel (10s -> 6s)
  - Titanium (15s -> 7s)
  - Uranium (20s -> 8s)
- Rebalanced block explosion resistance for the following block sets (for reference, an iron block is 6):
  - Aluminum (2 -> 4)
  - Brass (6 -> 4)
  - Ferroaluminum (9 -> 5.5)
  - Plastic (10 -> 4)
  - Steel (12 -> 7)
  - Titanium (12 -> 8)
  - Uranium (15 -> 9)
  - Zinc (6 -> 4)
- Increased predator follow range (16 blocks -> 35 blocks).
- Armor points, armor toughness points have been adjusted for the following mobs:
  - Drone (0 -> 4 armor)
  - Praetorian (0 -> 12 armor, 0 -> 12 armor toughness)
  - Queen (0 -> 16 armor, 0 -> 16 armor toughness)
  - Warrior (0 -> 8 armor)
  - Yautja (0 -> 16 armor, 0 -> 16 armor toughness)

## Fixes
- Fixed normal xenomorphs spawning on nether resin.
- Fixed nethermorphs spawning on normal resin.
- Fixed xenomorphs not attacking predators on sight.
- Fixed acid and nether acid putting out fires.
- Fixed nether resin web having an incorrect item model.
- Fixed razor wire having an incorrect item model.
- Fixed resin web having an incorrect item model.
- Fixed pulse rifle burst fire mode consuming 1 bullet instead of 4 bullets per shot.
- Fixed xenomorphs being able to break blocks while lunging or falling.
- Fixed predators not spawning correctly.
- Fixed some z-fighting issues with the predator's texture.

## Data Pack
- Added `aberrant_aliens` entity type tag.
- Added `nether_aliens` entity type tag.
- Added `normal_aliens` entity type tag.
- `aliens` entity type tag is now composed of `aberrant_aliens`, `nether_aliens` and `normal_aliens` tags.
- `hive_aliens` entity type tag is now only composed of the `xenomorphs` tag.
- Added missing aberrant/nether facehugger variants to `parasites` entity type tag.
- Added missing aberrant/nether praetorian variants to `royal_aliens` entity type tag.
- Added missing aberrant/nether drone, warrior and praetorian entity types to `xenomorphs` entity type tag.
- `acid_immune` block tag now includes air, fire and soul fire blocks.
- Added `iron_block_like` item tag.
- Added `iron_ingot_like` item tag.
- Added `nether_resin` block tag.
- Added `normal_resin` block tag.
- The `resin` block tag is now composed of the new `nether_resin` and `normal_resin` block tags.

# v0.0.16-beta

## What's New
- Added new avp commands
  - `/avp debug count`
    - Counts how many hives or entities there are.
  - `/avp debug hive nearest`
    - Gives the coordinates for the nearest hive.
  - `/avp test nuke`
    - EXPERIMENTAL. Detonates a nuke at the user's location. Feedback is appreciated on blast radius, performance, etc.
- Added uranium ingot item.
- Added uranium block.
- Hives now have a boss bar.
  - The boss bar only appears if you aggro any alien that is a part of the hive.
  - If you leave the hive (go beyond the hive's radius), the boss bar disappears.
  - The boss bar is represented as a count of the total number of aliens in the hive.
  - Killing all aliens will drop the boss bar down to 0.
- Hives now have an "angry" state.
  - This angry state is based on if the hive is aggressive towards any player (basically if any player has the boss bar showing).
  - When angered, ALL hive members will stop spreading resin.
  - Once there are no more players angering the hive, the aliens will start placing resin as normal.
- Added jungle predator armor.
  - Stronger than netherite. 
  - Dropped by predators, repaired with veritanium shards.
- Added veritanium shard item.
  - Dropped by predators, can be used to repair predator armor.
- Added veritanium toolset (axe, hoe, pickaxe, shovel and sword).
  - Dropped by predators, repaired with veritanium shards.
- Added new min-y config properties for the following mobs:
  - Chestburster
  - Drone
  - Ovamorph
  - Praetorian
  - Queen
  - Warrior
  - Yautja
- Added new config property `spawning.custom_mob_category.alien.limit`.
  - Allows for changing the spawn limit of aliens in the custom mob category.
  - Only works if the custom mob category for aliens is enabled.
- Added new config property `hive.max_praetorian_count`.
  - Allows for changing the maximum number of praetorians that can appear in a hive.
- Added new config property `hive.member_count_required_for_praetorian`.
  - Allows for changing the number of hive members required per praetorian.

## Changes
- Baby mobs are no longer considered valid hosts.
- Reworked M6B Rocket Launcher explosion.
  - Now uses custom-made explosion logic based on nuclear explosion testing.
- Rebalanced base gun durability values:
  - F903WE Rifle (1792 -> 2048)
  - M37-12 Shotgun (512 -> 1024)
  - M41 Pulse Rifle (2048 -> 3072)
  - M42A3 Sniper Rifle (512 -> 1024)
  - M4RA Battle Rifle (1024 -> 2048)
  - M56 Smartgun (2560 -> 4096)
  - M6B Rocket Launcher (256 -> 512)
  - M88 Mod 4 Combat Pistol (512 -> 1024)
  - Old Painless (2560 -> 4096)
  - ZX-76 Shotgun (768 -> 1024)
- Pressure armor now requires aluminum ingots as a repair ingredient (was previously iron ingots).
- MK50 armor now requires lead ingots as a repair ingredient (was previously iron ingots).
- Tactical armor now requires steel ingots as a repair ingredient (was previously iron ingots).
- The defense points provided by plated chitin boots has been slightly reduced (4 -> 3).
  - Now inline with diamond boots.
  - The same change also applies to plated nether chitin boots.
- Plated chitin armor now has +1 toughness.
  - This change also applies to plated nether chitin.
- Titanium armor now has +1 toughness.
- Aliens now bleed acid in an increased range around them (+1 larger than their x or z hitbox sizes).
- Renamed hives config property `hive_debugging.enabled` to `hive.debug.enabled`.
- Renamed hives config property `hive_debugging.highlight_leader` to `hive.debug.highlight_leader`.
- Renamed hives config property `hive_debugging.mark_hive_center` to `hive.debug.mark_hive_center`.
- Renamed hives config property `hive_leash_radius_in_blocks` to `hive.leash_radius_in_blocks`.

## Fixes
- Fixed Yautja spawning below Y 32 by default.
  - Their default maximum Y is now 100.
- Fixed industrial glass panes missing top and bottom textures.
- Fixed missing subtitles for the following sound effect groups:
  - Xenomorph sounds (attacking, dying, hissing, hurting, idling and lunging).
  - Weapons (shooting + reloading sound effects for all weapons).
- Fixed aliens targeting creative/spectator players.
- Fixed incorrect hive distance checking resulting in hives forming closer than they're allowed to.
- Fixed variant facehuggers briefly appearing as normal facehuggers before switching to the correct variant when hatching from an ovamorph.
  - Specifically applies to nether and aberrant facehuggers.
- Fixed variant chestbursters briefly appearing as normal chestbursters before switching to the correct variant when bursting from a host.
  - Specifically applies to nether and aberrant chestbursters.

## Data Packs
- N/A

# v0.0.15-beta

## What's New
- [IMPORTANT] Fabric loader v0.16.10 is now required.
- [IMPORTANT] FabricAPI v0.115.0 is now required.
- [IMPORTANT] AzureLib v3.0.8 is now required.
- Added industrial glass pane blocks.
- Added slabs for all concrete blocks.
- Added stairs for all concrete blocks.
- Resin blocks will now turn to basalt when burned.
  - Mining resin blocks not on fire will still drop the block.
  - Xenomorphs can still replace basalt blocks with resin.
- Added new sound effects for when xenomorphs:
  - Aggro
  - Attack
  - Die
  - Hurt
  - Idle
  - Lunge/Pounce
- Added new advancements:
  - "Eggsploration Time" - Shear a rooted ovamorph.
  - "Imperfect Organism" - Kill an alien.
  - "Regicide" - Kill a royal alien.
  - "Xenocide" - Kill one of each type of alien.
  - "Cover Me in... Uh..." - Wear a full set of chitin armor.
  - "Kneel to the Crown" - Wear a full set of plated chitin armor.

## Changes
- Reworked blueprint loot table spawning.
  - Gun blueprints are no longer unique to certain structures.
  - Instead, gun blueprints are now grouped into tiers, and certain structures contain certain tiers of guns.
  - For example, pistols and shotguns are considered "early-game" tier and can be found in dungeons, jungle temples, desert pyramids and pillager outposts.
  - Submachine guns and rifles **in addition to previous tier guns** can be found in ancient cities, strongholds, woodland mansions and mineshafts.
  - Rocket launchers, smartguns, pulse rifles, miniguns and sniper rifles can be found in end cities.
  - Flamethrower blueprints are unique and have a chance to be found in nether fortresses, and are guaranteed in bastion treasure chests.
- Increased flamethrower ammunition capacity (500 -> 1000, 100% increase).
  - During playtesting we found that the flamethrower eats through ammunition quickly. Rather than increasing the flamethrower cooldown (which made the gun not as fun to shoot since it no longer shot a steady stream), we've increased the provided ammunition amount to compensate.
- Increased flamethrower durability (2048 -> 4096, 100% increase).
  - Complements the increased ammunition capacity.
- Flamethrower projectiles now cause indirect player damage.
  - This prevents an exploit where entities could be killed by the flamethrower's flames without aggro'ing the entity.

## Fixes
- Potentially fixed hives forming close together due to a bad leadership check.
- Fixed flamethrowers removing liquids.
- Fixed industrial glass not being mineable by pickaxes.
- Fixed industrial glass not requiring the correct tool to drop itself.
- Fixed guardian's thorns applying damage to players that shoot them with a gun.
- Fixed flamethrower projectiles not being classified as fire damage.
  - This means nether mobs will no longer be hurt by flamethrower projectiles.
- Fixed bullets and flamethrow damage types not being classified as projectiles.
  - This means that you can no longer hurt endermen with bullets or flamethrower projectiles.

## Data Pack
- Added concrete block tag.

# v0.0.14-beta

## What's New
- N/A

## Changes
- Decreased minimum lunge range for facehuggers to 1 block.
- Decreased minimum lunge range for drone to 1 block.
- Decreased minimum lunge range for warrior to 1 block.
- Decreased lunge cooldown for drone.
- Decreased lunge cooldown for warrior.

## Fixes
- Fixed facehuggers hugging in situations that they shouldn't.
- Fixed a crash caused by a null target in xenomorph digging code.

## Data Pack
- N/A

# v0.0.13-beta

## What's New
- Added cut plastic variant blocks (full blocks, slabs and stairs).

## Changes
- Resin blocks now convert to 4 resin balls instead of 9.
- It now takes 4 resin balls to create 1 resin block.
- Resin vein recipe now produces 5 resin veins instead of 1.
- Resin block/vein/web recipe changes also apply to nether resin blocks/veins/webs.
- Plastic is now dyed by surrounding a single dye with 8 plastic blocks.
  - This reduces the dye cost for plastic by 8x.

## Fixes
- N/A

## Data Pack
- N/A

# v0.0.12-beta

## What's New
- Added hive burning mechanics:
  - Full resin blocks are now flammable.
  - Resin veins are *not* flammable. This means the veins need to be removed first before the hive can be ignited.
  - Flamethrowers can now be used to ignite hives, bypassing the resin vein fire immunity.
  - Nether resin blocks will *not* burn.
- Xenomorphs can now dig towards targets.
  - Xenomorphs can dig through blocks up to steel in terms of hardness.
  - Industrial concrete, industrial glass and plastic can not be dug through no matter what.
- The following aliens will now lunge towards targets from certain distances:
  - Facehuggers
  - Drones
  - Warriors
- Aliens that lunge have new lunge animation(s).
- Added fuel tank item, replaces magma cream when reloading the flamethrower.

## Changes
- Xenomorphs will not place resin for 10 seconds after being hurt.
- Parasites (facehuggers) now attach more reliably if colliding with host.
- Rockets now render further away.
- Flamethrowers now require fuel tank items for ammunition.

## Fixes
- Fixed xenomorphs spreading resin while targeting players.
- Fixed industrial glass not having the same hardness of strength.
- Fixed the flamethrower not shooting flame projectiles.
- Fixed rockets not despawning after a certain amount of time.
- Fixed rockets slowing down when they shouldn't.
- Fixed rockets freezing in midair.
- Fixed enchantments not always applying to guns.
- Fixed guns not being enchantable in the enchantment table.
- Fixed guns not being enchantable with the `/enchant` command.
- Fixed missing raw silica block texture.

## Data Pack
- Removed guns from vanilla enchantment item tags.
- Added `gun_enchantments` enchantment tag.
- Added `xenomorph_immune` block tag.

# v0.0.11-beta

## What's New
- Added razor wire block:
  - Deals considerable damage to any creatures that pass through it.
  - Crafted using 5 iron ingots and 4 iron nuggets (produce 16 razor wire).
- Carbon can now be used as a fuel source (half the smelting power of coal/charcoal).
- Guns now show an attack indicator similar to swords when a target is in range of a gun.
- Added recoil to guns.
- Guns now emit light when fired (thanks, AzureDoom!).
- Guns can now be enchanted with the following enchantments:
  - Curse of Vanishing
  - Flame
  - Infinity
  - Mending
  - Multi-Shot
  - Piercing
  - Power
  - Punch
  - Quick Charge
  - Unbreaking
- Hives can now die.
  - Hives will die when all of their members are missing or dead.
- Added two new debug properties for hives:
  - `hive_debugging.highlight_leader`, which controls leader highlighting during debug.
  - `hive_debugging.mark_hive_center`, which controls hive center marking during debug.

## Changes
- Autunite ore now drops 2 to 4 autunite dust items instead of 4 to 5.
- Lithium ore now drops 2 to 4 lithium dust items instead of 4 to 5.
- Xenomorphs now attack players regardless of if they are standing on resin.
- Ovamorphs that are de-rooted will no longer despawn.
- Renamed `hive_debugging_enabled` config property to `hive_debugging.enabled`.

## Fixes
- Fixed the following aliens becoming hive leaders when they shouldn't:
  - Ovamorphs
  - Facehuggers
  - Chestbursters
- Fixed chestbursters not growing up.
- Fixed zinc ore not dropping more than 1 raw zinc item when mined.
- Fixed alien attack animations playing out of sync with actual attack damage being dealt (Thanks, AzureDoom!).
- Fixed old painless minigun consuming ammunition while player is in creative mode.
- Fixed pressure and mk50 suits providing full air immediately on equip.
- Fixed resin web and nether resin web not slowing down non-alien entities.
- Fixed xenomorphs attacking hosts with parasites attached.
- Fixed xenomorphs attacking hosts with embryos inside of them.
- Fixed nethermorphs not spreading nether resin.
- Fixed naturally spawning nethermorphs sometimes attacking each other.
- Fixed an error that caused hive data loading to partially fail.
- Fixed nether praetorians dropping regular chitin and plated chitin.
- Fixed F903WE rifle using medium bullets when it should be using small bullets.
- Fixed M4RA battle rifle using small bullets when it should be using medium bullets.
- Fixed ferroaluminum grates not rendering correctly.
- Fixed steel grates not rendering correctly.
- Fixed missing steel bars texture.
- Fixed missing raw crude iron texture.
- Fixed missing raw brass texture.
- Fixed the following smelt/blast recipes not giving experience:
  - resin ball -> slime ball
  - nether resin ball -> slime ball
  - slime ball -> polymer
  - all glass blocks -> respective industrial glass block
  - all ore blocks -> respective materials
  - all raw ore items -> respective materials

## Data Pack
- Added gun item tag.

# v0.0.10-beta

## What's New
- Added hive debugging mode.
  - False by default, and can be enabled through the hives config.
- Hives now keep track of hive members.
  - Hive members that haven't been heard from in a while will be revoked hive membership.
  - If the leader's membership is revoked, then the hive will try and find a new leader.
- Hive members now ping hives.
  - Pings from hive members help prevent the hive from revoking membership. Think of it as a "Hey, I'm still alive" reminder for hives.
- Aliens now maintain an equilibrium with their hive in terms of roles.
  - The hive AI will always try to create an equal amount of drones and warriors. If there are too many drones, some of those drones will be selected to turn into warriors.
  - The hive AI will create 1 praetorian for every 8 hive members, up to a maximum of 6 praetorians.
  - If the leader is a praetorian and there is no queen, the leader will turn into a queen.

## Changes
- Adjusted mod author credits.
- Queens no longer always persist and are now subject to hive leader despawn conditions.
- Improved hive config "minimum distance between hives" comments.
- Improved hive config "hive radius" comments.
- Aliens no longer grow over time into different forms.
  - Instead, aliens grow into different forms when the hive AI commands them to.
- Reverted resin veins, nether resin veins being climbable.
  - After further testing, we found that making resin veins climbable made xeno pathing bug out and negatively impacted player movement through a hive in a way that wasn't enjoyable.
- Ferroaluminum, steel and titanium chain fences now use chain block sound effects.
- Aliens no longer drop acid when killed using /kill command (thanks AzureDoom!).

## Fixes
- Fixed alien hive leaders despawning (they should never despawn).
- Fixed deepslate titanium ore not being mineable with pickaxes.
- Fixed acid eating through industrial glass when it shouldn't.
- Fixed acid damaging acid-immune boots when it shouldn't.

## Data Pack
- Added industrial_glass block tag.

# v0.0.9-beta

## What's New
- Added mod icon.
- Added mod author credits.
- Implemented hive mechanics
  - Queens are no longer the center of the hive.
  - Hives are "ethereal". They are not a single entity, but the composition of many aliens acting as a single force.
  - Only grown aliens (xenomorphs) may contest for hive leadership.
  - Aliens will create a hive if no hive exists around them.
  - If a hive already exists, aliens will attempt to join the hive.
  - Different variant aliens (nethermorphs, aberrants) will never form hives together.
  - Aliens from different hives will fight each other.
  - Xenomorphs within a hive continuously "contest" for leadership.
  - The "best" leader for the hive wins (strongest, oldest, etc.).
- Implemented natural nethermorph spawning. The following Xenomorphs now spawn in Nether Waste and Crimson Forest biomes:
  - Nether Ovamorph
  - Nether Chestburster
  - Nether Drone
  - Nether Warrior
  - Nether Praetorian
- The following resin blocks/items can now be composted:
  - Nether Resin
  - Nether Resin Ball
  - Nether Resin Veins
  - Nether Resin Web
  - Resin
  - Resin Ball
  - Resin Veins
  - Resin Web
- Added titanium toolset (Axe, Hoe, Pickaxe, Shovel, Sword).
- Added new ferroaluminum blocks:
  - cut slab
  - cut stairs
  - grate
  - siding
  - standing
  - fastened siding
  - fastened standing
- Added new steel blocks:
  - cut slab
  - cut stairs
  - grate
  - siding
  - standing
  - fastened siding
  - fastened standing
- Added new titanium blocks:
  - cut slab
  - cut stairs
  - grate
  - siding
  - standing
  - fastened siding
  - fastened standing
- Added a recipe for vanilla name tags (need them to label specimens, after all).
- Added raw ferrobauxite item.
- Added raw brass item.

## Changes
- Resin and nether resin blocks can now be converted back to their respective resin balls.
- Parasites now apply the following effects while attached to a host:
  - Blindness 4 (immediate)
  - Mining Fatigue 4 (immediate)
  - Slowness 4 (immediate)
  - Weakness 4 (after 20 seconds)
- Parasites now fall off of hosts after 2.5 minutes (1.5 minutes for players).
- Nethermorphs now bleed blue acidic blood instead of green acidic blood.
  - Blue acidic blood ignites hurt entities.
  - Normal aliens are still immune to blue acidic blood.
- Improved gene mechanics:
  - Nethermorphs now also require cold resistance to be minimized to be considered nethermorphs.
  - Piglins and piglin brutes now produce nether chestbursters when facehugged.
  - The following hosts now grant gene bonuses to chestbursters:
    - Piglin
    - Piglin Brute
- Ovamorph rooting changes:
  - Only 1 resin ball is dropped from shearing.
  - Ovamorphs can now be right-clicked with their respective resin ball to be re-rooted.
- Buffed combat pistol:
  - Increased damage (2 half-hearts -> 5 half-hearts, 150% increase).
  - Reduced cooldown between shots (7 ticks -> 6 ticks, ~14% decrease).
  - Reduced reload time (40 ticks -> 30 ticks, 25% decrease).
- Completely overhauled sounds of the following weapons (volume adjustment + fix sound dropoff over distance):
  - 88 Mod 4 Combat Pistol
  - F903WE Rifle
  - Flamethrower (Sevastopol)
  - M37-12 Shotgun
  - M41A Pulse Rifle
  - M42A3 Sniper Rifle
  - M4RA Battle Rifle
  - M56 Smartgun
  - M6B Rocket Launcher
  - Old Painless
  - ZX-76 Shotgun
- Reworked armor durability values. For the following durability magic values, 15 = iron and 33 = diamond as a reference:
  - Chitin: 12 -> 21
  - MK50: 12 -> 14
  - Nether Chitin: 12 -> 21
  - Plated Chitin: 12 -> 27
  - Plated Nether Chitin: 12 -> 27
  - Pressure: 12 -> 12 (unchanged)
  - Steel: 12 -> 21
  - Tactical: 12 -> 18
  - Titanium: 12 -> 27
- Reworked titanium block stonecutting recipes:
  - 1 chiseled titanium -> 4 chiseled titanium
  - 1 cut titanium -> 4 cut titanium
  - 1 titanium chain fence -> 16 titanium chain fence
  - 1 titanium column -> 4 titanium column
  - 1 titanium plating -> 4 titanium plating
  - 1 titanium tread -> 4 titanium tread
- Updated titanium block set textures:
  - Chiseled titanium
  - Cut titanium
  - Titanium block
  - Titanium chain fence
  - Titanium column
  - Titanium plating
  - Titanium tread
- Updated all weapon part textures (barrel, grip, receiver, smart barrel, smart receiver, stock).
- Titanium chain fence is now climbable.
- Slight quality of life change to resin block models.
- Slightly increased pulse rifle blueprint spawn rate.
- Slightly decreased chestburster hitbox size (0.5 -> 0.35 width/height).
- Industrial concrete blocks now require iron (or better) tools to mine.
- Steel blocks now require iron (or better) tools to mine.
- Titanium blocks now require iron (or better) tools to mine.
- Shotgun shots now have knockback.
- Resin veins and nether resin veins are now climbable.
- Guns now deal 10x less damage to blocks.
- Modified ferroaluminum ingot item recipe to use raw ferrobauxite.
- Modified brass ingot item recipe to use raw brass.

## Fixes
- Fixed guns requiring ammunition in creative mode.
- Fixed armor case texture dimensions not being powers of 2.
- Fixed m41a pulse rifle texture dimensions not being powers of 2.
- Fixed nethermorphs taking damage from magma blocks (hot floor damage type).
- Fixed industrial glass not being fully transparent.
- Fixed English translations of light blue and light gray avp blocks.
- Fixed missing English translations for acid entity.
- Fixed missing English translations for when entities die from acid.
- Fixed hitscan weapon attacks dealing acid damage instead of bullet damage.
- Fixed hitscan weapon attacks not including shooter as source of damage.
- Fixed players not being "hurt" by attached parasites.
- Fixed embryos not disappearing when players turn on creative or spectator mode or are otherwise invulnerable.
- Fixed missing ferroaluminum ingot texture.

## Data Pack
- Added ferroaluminum block tag.
- Added industrial concrete block tag.
- Added padding block tag.
- Added steel block tag.
- Added titanium block tag.
- Added xenomorphs entity tag.

# v0.0.8-beta
- [DISCLAIMER] Some aspects of the weapons are unfinished (flamethrower, old painless rendering), this is expected. This update is a two-parter because I'm tired lol. Also because guns are now ~90%, this is good enough to put us out of alpha and (officially fr fr no cap) into beta. Huzzah!
- [BREAKING] Renamed SADAR weapon + blueprint (now called M6B Rocket Launcher).
- Implemented weapon mechanics
  - All weapons now consume ammunition and fire projectiles.
  - Implemented reloading. Default keybind is R.
  - Added firing sound effects to all weapons.
  - Old Painless is now animated when firing.
- Added rooting mechanic for ovamorphs
  - Ovamorphs now have a "rooted" state, defaulting to true.
  - While rooted, ovamorphs can not be pushed by entities or fluids.
  - When sheared, ovamorphs will drop resin and be de-rooted.
  - When de-rooted, ovamorphs can be freely pushed by entities and/or fluids.
- Added hives configuration file
  - Can now configure how far away (in blocks) hives start.
- The following xenomorphs have had their detection range increased to 35 (now configurable):
  - Drone
  - Warrior
  - Praetorian
  - Queen
- The following xenomorphs no longer lose targets due to broken line-of-sight:
  - Drone
  - Warrior
  - Praetorian
  - Queen
- Added gunpowder recipe (8 carbon surrounding 1 blaze powder in the center).
- Infected hosts no longer despawn.
- Ovamorphs can now be restored when right-clicked with royal jelly.
- Facehuggers can now be restored when right-clicked with royal jelly.
- MK50 armor now slows down the player when wearing the full set.
- Fixed facehuggers not injecting embryos into certain hosts (including players).
- Fixed aliens other than xenomorphs being resistant to water flow.
- Fixed acid applying knockback to hurt entities.
- Fixed facehuggers despawning while attached to a host.
- Fixed the following armor sets not being enchantable:
  - Chitin
  - MK50
  - Nether Chitin
  - Plated Chitin
  - Plated Nether Chitin
  - Pressure
  - Steel
  - Tactical
  - Titanium
- Fixed the following entities not giving a spawn egg when picked in creative mode:
    - Ovamorph
    - Facehugger
    - Chestburster
    - Drone
    - Warrior
    - Praetorian
- Fixed chestburster texture.
- Fixed aberrant chestburster texture.
- Fixed missing aluminum block texture.
- Fixed missing lead block texture.

# v0.0.7-alpha
- [BREAKING] Changed bullet casing registry names.
- Updated required AzureLib version to 3.0.4.
- Added padding block recipes.
- Added panel padding block recipes.
- Added pipe padding block recipes.
- Added raw silica blast furnace recipe.
- Added autunite block.
- Autunite ore block now glows.
- Autunite block also glows (like ore).
- Updated autunite dust item texture.
- Updated autunite ore block texture.
- Lithium dust item now explodes in water.
- Lithium ore block now explodes in water.
- Lithium block now explodes in water.
- Lithium ore block now explodes if water touches it.
- Lithium block now explodes if water touches it.
- MK50 armor now grants permanent water breathing if wearing full set.
- Pressure suit armor now grants permanent water breathing if wearing full set.
- Aliens now only place resin when they are less than 64 blocks away from a queen.
- Resin balls can now be smelted into slime balls.
- Nether resin balls can now be smelted into slime balls.
- Xenomorphs now only replace certain blocks with resin node blocks.
- Xenomorphs no longer spawn on resin veins (only resin blocks).
- Ovamorphs now open when hurt by an entity.
- Fixed missing nether chestburster texture.

# v0.0.6-alpha
- Drones now have a chance to drop chitin on death.
- Nether drones now have a chance to drop nether chitin on death.
- Warriors now drop chitin on death (guaranteed).
- Nether warriors now drop nether chitin on death (guaranteed).
- Praetorians now drop chitin on death (guaranteed).
- Praetorians now drop plated chitin on death (guaranteed).
- Nether praetorians now drop nether chitin on death (guaranteed).
- Nether praetorians now drop plated nether chitin on death (guaranteed).
- Queens now drop chitin on death (guaranteed).
- Queens now drop plated chitin on death (guaranteed).
- Queens now drop royal jelly on death (guaranteed).
- Aliens now only regenerate health when out of combat for 10 or more seconds.
- Aliens now only place resin when they are less than 256 blocks away from a queen.
- Queens now spawn 1024 blocks apart instead of 512 blocks apart.
- Fixed facehuggers not attaching to vindicators properly.

# v0.0.5-alpha
- Now using a new name format for mod jar file(s).
- Alien health regen rate is now configurable.
- Added titanium armor set textures.
- Added raw bauxite block texture.
- Reduced drone damage (10 half-hearts -> 5 half-hearts).
- Reduced drone health regen (1 half-hearts / second -> 0.5 half-hearts / second).
- Reduced warrior damage (15 half-hearts -> 10 half-hearts).
- Reduced warrior health regen (1 half-hearts / second -> 0.5 half-hearts / second).
- Reduced praetorian damage (20 half-hearts -> 15 half-hearts).
- Reduced praetorian health regen (1 half-hearts / second -> 0.5 half-hearts / second).
- Reduced queen damage (100 half-hearts -> 50 half-hearts).
- Reduced queen health regen (1 half-hearts / second -> 0.5 half-hearts / second).
- Reduced yautja damage (20 half-hearts -> 15 half-hearts).
- Fixed aberrant ovamorph spawn egg colors.
- Fixed aberrant facehugger spawn egg colors.
- Fixed aberrant chestburster spawn egg colors.
- Fixed aberrant drone spawn egg colors.
- Fixed aberrant warrior spawn egg colors.
- Fixed aberrant praetorian spawn egg colors.
- Fixed nether facehugger spawn egg colors.
- Fixed nether ovamorph spawn egg colors.
- Fixed spawn egg picked from ovamorphs not yielding nether/aberrant spawn egg variants.
- Fixed spawn egg picked from facehuggers not yielding nether/aberrant spawn egg variants.
- Fixed spawn egg picked from chestbursters not yielding nether/aberrant spawn egg variants.
- Fixed spawn egg picked from drones not yielding nether/aberrant spawn egg variants.
- Fixed spawn egg picked from warriors not yielding nether/aberrant spawn egg variants.
- Fixed spawn egg picked from praetorians not yielding nether/aberrant spawn egg variants.
- Fixed industrial glass block not rendering with transparency.
- Fixed steel tools not being enchantable.
- Fixed steel tools not being tagged.
- Fixed yautja getting stuck in boats and mine carts.

# v0.0.4-alpha
- Added industrial glass block set.
- Added armor case item recipe.
- Added electronic item recipes.
- Added steel tool recipes.
- Added mk50 armor set recipes.
- Added pressure armor set recipes.
- Added tactical armor set recipes.
- Added bauxite ore -> aluminum ingot recipes.
- Added raw bauxite -> aluminum ingot recipes.
- Added galena ore -> lead ingot recipes.
- Added raw galena -> lead ingot recipes.
- Added monazite ore -> neodymium magnet recipes.
- Added raw monazite -> neodymium magnet recipes.
- Added deepslate titanium ore -> titanium ingot recipes.
- Added raw titanium -> titanium ingot recipes.
- Added zinc ore -> zinc ingot recipes.
- Added deepslate zinc ore -> zinc ingot recipes.
- Added raw zinc -> zinc ingot recipes.
- Added caseless casing recipe.
- Added caseless bullet recipe.
- Added heavy casing recipe.
- Added heavy bullet recipe.
- Added pistol casing recipe.
- Added pistol bullet recipe.
- Added rifle casing recipe.
- Added rifle bullet recipe.
- Added shotgun casing recipe.
- Added shotgun bullet recipe.
- Added rocket recipe.
- Added resistor recipe.
- Added diode recipe.
- Added transistor recipe.
- Added regulator recipe.
- Added capacitor recipe.
- Added integrated circuit recipe.
- Added led recipe.
- Added led display recipe.
- Added cpu recipe.
- Added battery pack recipe.
- Added smart receiver recipe.
- Updated plated chitin armor set recipes.
- Updated plated nether chitin armor set recipes.

# v0.0.3-alpha
- Fixed black plastic block texture.
- Fixed host-borne aliens despawning.
- Fixed queens despawning.
- Fixed acid emitting smoke particles when it shouldn't.
- Fixed infertile facehuggers bleeding acid.
- Fixed hatched ovamorphs bleeding acid.
- Fixed parasites targeting already-infected hosts.
- Updated titanium block textures.
- Updated titanium ingot textures.
- Added titanium tool textures (unused).

# v0.0.2-alpha
- Fixed a crash with Carry On occurring when AvP entities are picked up.

# v0.0.1-alpha
- Initial release.
