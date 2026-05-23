# v0.2.2

## ✍️ Developer Notes
- For help or other questions, concerns, etc. check out our Discord server: https://discord.gg/wp7mvmbkVb

## ✨ What's New
- Added Storage API (`api.common.storage.v1`) for persistent data storage at three scopes:
  - `DataStore` interface (extends `NBTSerializable`) for storable data
  - `DataStoreType<T>` record with factory, registered to the `DATA_STORE_TYPES` registry
  - `DataStoreManager` interface with `getGlobal()`, `getLevel()`, and `getChunk()` methods
  - Global scope: server-wide data, saved on `MinecraftServer#saveEverything`
  - Level scope: per-dimension data, saved on `ServerLevel#save`
  - Chunk scope: per-chunk data with region-based file persistence (32x32 chunks per region)
  - Data is stored per-mod under `{world}/blib/data_storage/{namespace}/`:
    - Global: `global/{path}.nbt`
    - Level: `levels/{dimension}/{path}.nbt`
    - Chunk: `levels/{dimension}/chunks/r32.{rx}.{rz}.nbt`
  - Region files encode the region size in both the filename and NBT data for forward compatibility
  - Lazy loading: data is loaded from disk on first access
  - Region reference counting: cached region data is released when the last tracked chunk in a region unloads
- Added `DATA_STORE_TYPES` registry to `BLibRegistries` and `BLibBuiltInRegistries`
- Added `BLibMod#storage()` method to access storage through the mod facade
- Added `BLibStorageAccess` for type-safe data store access
- Added `BLibServerSaveEvent`, `BLibLevelSaveEvent`, `BLibChunkSaveEvent`, and `BLibChunkUnloadEvent`
- Added `BLibEventAccess#onServerSave()`, `onLevelSave()`, `onChunkSave()`, and `onChunkUnload()` event accessors
- Added Faction API (`api.common.faction.v1`) for persistent, type-safe faction management:
  - `FactionManager` interface for creating, accessing, and removing factions
  - `FactionType<T>` for defining custom faction data types, registered to the `FACTION_TYPES` registry
  - `FactionKey<T>` record for type-safe faction identification (couples faction ID with its type)
  - `FactionData` base class for custom per-faction data
  - `FactionRelationships` for tracking faction members and parent-faction relationships
  - `FactionMember` sealed interface with `Entity` and `SubFaction` variants
  - `FactionDataError` sealed interface with `UnknownType` and `TypeMismatch` error variants
  - Type-validated data access returning `Result<T, FactionDataError>` to prevent type mismatches
  - Shard-based persistence (1000 entries per shard, dirty-tracking for incremental saves)
  - Reverse entity-to-faction index for efficient UUID-to-faction lookups via `FactionManager#getFactionIds(UUID)`
  - Automatic faction membership cleanup when non-player entities are killed or discarded
- Added `FACTION_TYPES` registry to `BLibRegistries` and `BLibBuiltInRegistries`
- Added `BLibMod#factions()` method to access factions through the mod facade
- Added `BLibFactionAccess` for type-safe faction access
- Added `BLibEntityRemoveEvent` for entity removal events (provides `Entity` and `RemovalReason`)
- Added `BLibEventAccess#onEntityRemove()` event accessor
- Added `BLibFactionRemoveEvent` for faction removal events
- Added `BLibEventAccess#onFactionRemove()` event accessor
- Added `Dirty` interface (`api.common.util.v1`) for reusable dirty-tracking
- Added Reputation API (`api.common.reputation.v1`) for asymmetric reputation tracking:
  - `ReputationManager` interface for reputation operations
  - `ReputationKey` sealed interface with `Faction(ResourceLocation)` and `Entity(UUID)` variants
  - `ReputationData` class for holding a subject's outgoing reputation map (implements `Dirty`)
  - Supports faction-to-faction, faction-to-entity, entity-to-faction, and entity-to-entity relationships
  - Asymmetric design: A's reputation toward B is independent of B's reputation toward A
  - Shard-based persistence (1000 entries per shard, dirty-tracking for incremental saves)
  - Reverse incoming index for efficient cleanup when subjects are removed
- Added `BLibMod#reputation()` method to access reputation through the mod facade
- Added `BLibReputationAccess` for reputation access
- Added `/blib` operator commands (permission level 2) for debugging and management:
  - `/blib factions create|remove|add-member|remove-member|list` — faction management
  - `/blib reputation get|set|remove` — reputation management between factions and entities

## ♻️ Changes
- Renamed server lifecycle event methods for consistency (prefixed with "on"):
  - `onServerStarted()` (replaces `serverStarted()`)
  - `onServerStarting()` (replaces `serverStarting()`)
  - `onServerStopped()` (replaces `serverStopped()`)
  - `onServerStopping()` (replaces `serverStopping()`)
- Deprecated `serverStarted()`, `serverStarting()`, `serverStopped()`, `serverStopping()` for removal

## 🔬 Technical Changes
- Split `BLibDataStoreManager` into dedicated sub-managers:
  - `BLibGlobalDataStoreManager` for server-wide stores
  - `BLibLevelDataStoreManager` for per-dimension stores
  - `BLibChunkDataStoreManager` for region-based chunk stores
  - `DataStoreIO` for shared file I/O utilities
- Added mixins for save and chunk event dispatch:
  - `MixinMinecraftServer_SaveGlobalData` hooks into `saveEverything()`
  - `MixinServerLevel_Events` hooks into `save()` and `unload()`
  - `MixinChunkMap_SaveChunkData` hooks into `ChunkMap#save()`
- Added `BLibGlobalEvents.SERVER_SAVE`, `LEVEL_SAVE`, `CHUNK_SAVE`, and `CHUNK_UNLOAD` global event handles
- Added `ShardUtil` utility for shard index calculations and dirty shard detection
- Added `BLibFactionManager` internal implementation with shard-based IO and reverse entity index
- Added `FactionDataIO`, `FactionRelationshipsIO`, and `FactionIO` for faction persistence
- Added `FactionDataSerializer` and `FactionRelationshipsSerializer` for faction NBT serialization
- Added `BLibGlobalEvents.ENTITY_REMOVE` global event handle
- Renamed mixin `MixinEntity_TickEvent` to `MixinEntity_Events` (now handles both tick and remove events)
- Added `BLibReputationManager` internal implementation with shard-based IO and reverse incoming index
- Added `ReputationDataIO` and `ReputationIO` for reputation persistence
- Added `ReputationDataSerializer` for reputation NBT serialization
- Extracted `FactionMemberIndex` from `BLibFactionManager` for cleaner separation of concerns
- Added `ShardManager` to generalize shard-based persistence across faction and reputation systems
- Added `BLibCommandSuggestions` utility with suggestion providers for faction IDs and faction type IDs
- Added `BLibFactionCommands`, `BLibReputationCommands`, and `BLibGOAPCommands` command implementations
- Added `BLibCommands` init class for registering the `/blib` command tree

# v0.2.1

## ✍️ Developer Notes
- For help or other questions, concerns, etc. check out our Discord server: https://discord.gg/wp7mvmbkVb

## ☢️ Breaking Changes
- Removed `MixinLivingEntity_ApplyArmorEffects` - the automatic fire resistance armor behavior is no longer built-in. Use the new `onEntityTick()` event to implement custom armor set effects.

## ✨ What's New
- Added `BLibEntityTickEvent` - a new event fired on every entity tick.
- Added `onEntityTick()` to `BLibEventAccess` for registering entity tick listeners.
- Added `KeyedAccess` interface for key-value access patterns (moved to `com.blib.api.common.util.v1`).

## ♻️ Changes
- Moved `KeyedAccess` from `com.blib.api.common` to `com.blib.api.common.util.v1`.

## 🐞 Fixes
- Fixed potential crash during decorated pot sherd pattern rendering.

## 🔬 Technical Changes
- Added global event dispatch system via `BLibGlobalEventHandle` and `BLibGlobalEvents` for efficient cross-mod event invocation.
- Added `BLibGlobalOnlyEventHandle` for BLib-specific events that don't have platform (Fabric/NeoForge) equivalents.


# v0.2.0

## ✍️ Developer Notes
- For help or other questions, concerns, etc. check out our Discord server: https://discord.gg/wp7mvmbkVb

## ☢️ Breaking Changes
- Merged AzureLib into BLib (with permission from AzureLib's maintainer).
    - This means BLib (and its dependents) no longer require AzureLib.
- Restructured entire project into api/internal packaging.
    - Previously there was only an internal package, with an unofficial API, now there is a dedicated API package.
    - Any classes in the API package are guaranteed to maintain backwards compatibility according to the mod's semantic version.
- Numerous breaking changes to all previously existing classes.
    - Many classes have been renamed or moved. If you can no longer find a BLib class in your project, it likely has a different name. Please reach out for migration assistance in our Discord if you get stuck!
- Updated `just-goap` to `0.3.0`.
    - `just-goap` was mostly rewritten. Please consult that project's changelog for migration guidance.

## ✨ What's New
- Added ShapedRecipeBuilder#into(ItemStack).
- Added ShapelessRecipeBuilder#into(ItemStack).
- Added a new "property" API for managing simple property files.

# v0.1.2

## ✍️ Developer Notes
- For help or other questions, concerns, etc. check out our Discord server: https://discord.gg/wp7mvmbkVb

## ✨ What's New
- [Fabric] Fabric Loader 0.18.4 is now required.

# v0.1.1

## ✍️ Developer Notes
- For help or other questions, concerns, etc. check out our Discord server: https://discord.gg/wp7mvmbkVb

## ✨ What's New
- AzureLib 3.1.3 is now required.
- [NeoForge] NeoForge 21.1.217 is now required.

## 🐞 Fixes
- Fixed a potential crash at startup related to BLib's custom datafix migration registry.

# v0.1.0

## ✍️ Developer Notes
- For help or other questions, concerns, etc. check out our Discord server: https://discord.gg/wp7mvmbkVb

## ✨ What's New
- Initial release.