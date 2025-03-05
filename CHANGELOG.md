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

## Changes
- Acid's maximum multiplier has been reduced from 10 to 5.
- MK50 suit helmets now only accept industrial glass panes and not industrial glass blocks in recipe.
- Pressure suit helmets now only accept industrial glass panes and not industrial glass blocks in recipe.
- LEDs now only accept industrial glass panes and not industrial glass blocks in recipe.
- LED Displays now only accept industrial glass panes and not industrial glass blocks in recipe.
- Transistors now only accept industrial glass panes and not industrial glass blocks in recipe.

## Fixes
- N/A.

## Data Pack
- Added `industrial_glass_block` block tag.
- Added `industrial_glass_pane` block tag.
- The `industrial_glass` block tag is now composed of `industrial_glass_block` and `industrial_glass_pane` block tags.
- Added `industrial_glass_block` item tag.
- Added `industrial_glass_pane` item tag.
- The `industrial_glass` item tag is now composed of `industrial_glass_block` and `industrial_glass_pane` item tags.

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
