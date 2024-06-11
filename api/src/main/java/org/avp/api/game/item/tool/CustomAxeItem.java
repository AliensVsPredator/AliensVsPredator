package org.avp.api.game.item.tool;

import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Tier;

public class CustomAxeItem extends AxeItem {

    public CustomAxeItem(Tier tier, float attackDamage, float attackSpeed, Properties properties) {
        super(tier, attackDamage, attackSpeed, properties);
    }
}
