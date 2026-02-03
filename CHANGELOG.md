# vMAJOR.MINOR.PATCH-DIST

## ✍️ Developer Notes
- For help or other questions, concerns, etc. check out our Discord server: https://discord.gg/wp7mvmbkVb

## ☢️ Breaking Changes
- Removed `DataStoreKey<T>` in favor of registry-based `DataStoreType<T>`
- Removed `BLibResourceAccess#createDataStoreKey()`
- `DataStoreManager` methods now accept `BLibHolder<DataStoreType<T>>` instead of `DataStoreKey<T>`

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

## ♻️ Changes
- Renamed server lifecycle event methods for consistency (prefixed with "on"):
  - `onServerStarted()` (replaces `serverStarted()`)
  - `onServerStarting()` (replaces `serverStarting()`)
  - `onServerStopped()` (replaces `serverStopped()`)
  - `onServerStopping()` (replaces `serverStopping()`)
- Deprecated `serverStarted()`, `serverStarting()`, `serverStopped()`, `serverStopping()` for removal

## 🐞 Fixes
- N/A

## 🧪 Experimental
- N/A

## 🛠 Data Pack
- N/A

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