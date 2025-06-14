# v0.2.0-beta1

## ☢️ Breaking Changes
- Renamed `avp:ovamorph` to `avp:ovomorph`.

## ✨ What's New
- Aliens now remember what host type they came from.
- Ovomorphs now have hatching AI
  - Ovomorphs now have a "desire" meter for hatching. 
  - Changes in the environment (such as light) will increase the desire meter.
  - Movement of nearby hosts will also increase the desire meter.
- Added new aliens:
  - Aberrant Prowler
  - Aberrant Runner
  - Irradiated Prowler
  - Irradiated Runner
  - Nether Prowler
  - Nether Runner
  - Prowler
  - Runner

## ♻️ Changes
- All aliens can now detect vibrations (instead of just xenomorphs).
- Chestbursters can no longer be trapped in boats and minecarts.
- Facehuggers can no longer be trapped in boats and minecarts.

## 🐞 Fixes
- N/A

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
- Added full datagen support for alien growth stages. Alien lifecycle registry has been removed.
- Alien lifecycle host entity tags have been migrated to individual alien growth stages.
- Upgraded alien infections to support host entity tags.
- Added full datagen support for alien infections. Alien infection registry has been removed.