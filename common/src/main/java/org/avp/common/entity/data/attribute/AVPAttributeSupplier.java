package org.avp.common.entity.data.attribute;

import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;

public class AVPAttributeSupplier {

    public static AttributeSupplier.Builder builder() {
        return Monster.createMonsterAttributes()
            .add(Attributes.ARMOR, 0)
            .add(Attributes.FOLLOW_RANGE, 16)
            .add(Attributes.MAX_HEALTH, 20)
            .add(Attributes.MOVEMENT_SPEED, 1);
    }

    private AVPAttributeSupplier() {
        throw new UnsupportedOperationException();
    }
}
