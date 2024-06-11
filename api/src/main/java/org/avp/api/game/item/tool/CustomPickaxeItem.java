package org.avp.api.game.item.tool;

import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;

public class CustomPickaxeItem extends PickaxeItem {

    public CustomPickaxeItem(Tier tier, int attackDamage, float attackSpeed, Properties properties) {
        super(tier, attackDamage, attackSpeed, properties);
    }
}
