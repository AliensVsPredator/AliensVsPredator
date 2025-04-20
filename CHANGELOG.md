# v0.0.23-beta

## ☢️ Breaking Changes
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
- Added predator music disc 1 "Hunter" by Rotch Gwylt.
- Added mobile lab structure.
- Added commissary house to all vanilla villages.
- Added outpost munition structure.
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
- Added new item textures for the following armor sets:
  - Aberrant Chitin Armor
  - Irradiated Chitin Armor
  - Plated Aberrant Chitin Armor
  - Plated Irradiated Chitin Armor

## ♻️ Changes
- Updated ash block texture.

## 🐞 Fixes
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
