# v0.2.9

## ✍️ Developer Notes
- For help or other questions, concerns, etc. check out our Discord server: https://discord.gg/wp7mvmbkVb
- Starting now, this changelog and future changelogs will no longer include sections like "Features" or "Changes" if there were no notable changes in them.

## 🐞 Fixes
- [NeoForge] Fixed queens not spawning when AVP is used with Lithium. Thanks Halita Silverblood for catching this bug!

## 🔬 Technical Changes
- Custom mob categories for aliens, ovomorphs and predators have been reworked:
  - Changed alien enum name `ALIENS` -> `AVP_ALIEN`.
  - Changed ovomorph enum name `OVOMORPHS` -> `AVP_OVOMORPH`.
  - Changed predator enum name `PREDATOR` -> `AVP_PREDATOR`.
  - Custom mob category names are now prefixed with `avp:` in case of overlap with other mods.
  - [Fabric] Fixed custom mob categories using the same ordinal value.
  - [NeoForge] Custom mob categories in NeoForge now use NeoForge's enum extensions feature.