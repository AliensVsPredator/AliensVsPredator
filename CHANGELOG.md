# v0.2.3

## ✍️ Developer Notes
- For help or other questions, concerns, etc. check out our Discord server: https://discord.gg/wp7mvmbkVb
- This update introduces an overhaul to how weapon ammunition is crafted. We felt that the bullet recipes were a bit overcomplicated. We also felt that the existing bullet recipes didn't quite mesh well with how bullets are structured in real life. Our goal with these changes is neither to nerf or buff ammunition cost, although either may be a consequence of our bullet crafting rework. All of the costs/yields here are subject to change based on feedback (which you can give in our Discord server linked above! :) )
- This update also changes most bullet yields. Smaller, cheaper ammunition is now more plentiful, while heavier, costly ammunition has kept the same yield amount. The goal with this change is to introduce a new factor for "weaker" guns - cost efficiency. The weaker guns have not had much use, and so we want to encourage more usage of them by making their bullets more plentiful. Please note that the yield for heavy bullets *has not changed*, only the weaker ammunition types have been given more generous yields.
- Some of the ammunition types now require more of certain resources, such as lead or steel. For example, heavy bullets now make use of steel nuggets in their recipe. We want heavy bullet consuming weapons to be powerful, but we want there to be a cost associated with that power. Brass is very plentiful (as copper and zinc are very plentiful), so we opted for steel as an ingredient to make steel more of a focus for players that want to play with heavy weaponry. As a reminder, guns work with most enchantments that work on bows/crossbows, so if you want to avoid the high cost of ammunition, the infinity enchantment is a good choice (at the cost of no mending, as usual).

## ☢️ Breaking Changes
- Removed bullet tip item.
  - If you had stockpiles of bullet tip items, they will be replaced with lead nuggets. Considering 4 bullet tips were crafted from 1 lead nugget beforehand, this will give back excess lead nuggets. :)
- Removed small casing item.
  - If you had stockpiles of small casing items, they will be replaced with 1 gunpowder. This means the brass nuggets used to craft them will be lost. Considering how common copper and zinc are, we saw it as more important to give back the gunpowder.
- Removed medium casing item.
  - If you had stockpiles of medium casing items, they will be replaced with 1 gunpowder.
- Removed heavy casing item.
  - If you had stockpiles of heavy casing items, they will be replaced with 1 gunpowder.
- Removed shotgun casing item.
  - If you had stockpiles of shotgun casing items, they will be replaced with 1 gunpowder.
- Removed caseless cartridge item.
  - If you had stockpiles of medium casing items, they will be replaced with 1 gunpowder. This means the clay balls used to craft them will be lost. It was a tough choice between giving back gunpowder and clay balls, but we determined gunpowder to be the resource worth giving back.

## ✨ What's New
- N/A

## ♻️ Changes
- Barrels in AVP structures that would originally be full of bullet casings will now instead be full of brass and steel nuggets.
- Overhauled small bullet recipe.
  - Small bullets no longer require bullet tips or small casings, and instead require 5 brass nuggets, 1 lead nugget and 1 gunpowder.
  - The small bullet recipe now yields 24 small bullets.
- Overhauled medium bullet recipe. 
  - Medium bullets no longer require bullet tips or medium casings, and instead require 6 brass nuggets, 1 lead nugget, 1 steel nugget and 1 gunpowder.
  - The medium bullet recipe now yields 16 medium bullets.
- Overhauled heavy bullet recipe. 
  - Heavy bullets no longer require bullet tips or heavy casings, and instead require 1 brass nugget, 3 lead nuggets, 4 steel nuggets and 1 gunpowder.
  - The heavy bullet recipe now yields 8 heavy bullets (no change).
- Overhauled shotgun shell recipe.
  - Shotgun shells no longer require bullet tips or shotgun casings, and instead require 1 brass nugget, 3 lead nuggets, 4 polymer and 1 gunpowder.
  - The shotgun shell recipe now yields 12 shotgun shells.
- Overhauled caseless bullet recipe.
  - Caseless bullets no longer require caseless cartridges, and instead require 1 brass nugget, 1 lead nugget, 4 clay balls and 1 gunpowder.
  - The caseless bullet recipe now yields 16 caseless bullets.

## 🐞 Fixes
- N/A

## 🧪 Experimental
- N/A

## 🛠 Data Pack
- N/A

## 🔬 Technical Changes
- Refactored data fixer entries to support avp -> minecraft namespace mappings.