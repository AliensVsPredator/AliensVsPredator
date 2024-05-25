package org.avp.api.item;

import net.minecraft.world.item.Item;
import org.avp.api.Holder;

public record ItemHolderArmorSet(
   Holder<Item> helmet,
   Holder<Item> body,
   Holder<Item> leggings,
   Holder<Item> boots
) {}
