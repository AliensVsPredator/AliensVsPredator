package org.avp.api.game.item.tool;

import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Tier;

public class CustomHoeItem extends HoeItem {

    public CustomHoeItem(Tier tier, int attackDamage, float attackSpeed, Properties properties) {
        super(tier, attackDamage, attackSpeed, properties);
    }
}
