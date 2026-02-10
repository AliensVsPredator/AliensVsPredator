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