# v0.1.7

## ☢️ Breaking Changes
- N/A

## ✨ What's New
- N/A

## ♻️ Changes
- Aliens can now place resin nodes above blocks they can't replace.
  - Previously, aliens could only replace certain blocks with resin nodes. This lead to the alien being unable to put resin nodes down in areas with irreplaceable blocks.
  - Now, aliens can place resin nodes in open air blocks above blocks they can't replace, allowing them to spread resin veins in nearly all places.

## 🐞 Fixes
- Fixed nether queens not spawning in the nether.
- Fixed the following resin blocks not being flammable (Fabric-only):
  - Aberrant Resin
  - Aberrant Resin Node
  - Irradiated Resin
  - Irradiated Resin Node

## 🛠 Data Pack
- Added `#avp:resin_blocks` block tag.
- Added `#avp:resin_nodes` block tag.
- Added `#avp:resin_replaceable` block tag.
  - Allows for controlling which blocks aliens can fully replace with resin.

## 🔬 Technical Changes
- N/A