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
