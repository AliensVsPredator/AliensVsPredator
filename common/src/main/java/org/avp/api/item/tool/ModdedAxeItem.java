package org.avp.api.item.tool;

import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Tier;

public class ModdedAxeItem extends AxeItem {
    public ModdedAxeItem(Tier tier, float attackDamage, float attackSpeed, Properties properties) {
        super(tier, attackDamage, attackSpeed, properties);
    }
}
