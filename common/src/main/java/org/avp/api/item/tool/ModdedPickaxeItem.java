package org.avp.api.item.tool;

import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;

public class ModdedPickaxeItem extends PickaxeItem {

    public ModdedPickaxeItem(Tier tier, int attackDamage, float attackSpeed, Properties properties) {
        super(tier, attackDamage, attackSpeed, properties);
    }
}
