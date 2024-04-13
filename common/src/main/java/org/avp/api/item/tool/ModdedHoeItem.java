package org.avp.api.item.tool;

import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Tier;

public class ModdedHoeItem extends HoeItem {

    public ModdedHoeItem(Tier tier, int attackDamage, float attackSpeed, Properties properties) {
        super(tier, attackDamage, attackSpeed, properties);
    }
}
