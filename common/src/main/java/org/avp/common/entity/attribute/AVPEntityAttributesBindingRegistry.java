package org.avp.common.entity.attribute;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.avp.api.GameObject;

public class AVPEntityAttributesBindingRegistry {

    private static final List<Map.Entry<GameObject<? extends EntityType<? extends Mob>>, AttributeSupplier>> BINDINGS =
        new ArrayList<>();

    public static List<Map.Entry<GameObject<? extends EntityType<? extends Mob>>, AttributeSupplier>> getBindings() {
        return BINDINGS;
    }

    protected static <T extends Mob> void addBinding(
        GameObject<EntityType<T>> gameObject,
        AttributeSupplier attributeSupplier
    ) {
        BINDINGS.add(new AbstractMap.SimpleEntry<>(gameObject, attributeSupplier));
    }

    protected static AttributeSupplier.Builder builder() {
        return Monster.createMonsterAttributes()
            .add(Attributes.ARMOR, 0)
            .add(Attributes.FOLLOW_RANGE, 16)
            .add(Attributes.MAX_HEALTH, 20)
            .add(Attributes.MOVEMENT_SPEED, 1);
    }

    static {
        AVPBaseAlienEntityAttributes.addBindings();
        AVPEntityAttributes.addBindings();
        AVPExoticAlienEntityAttributes.addBindings();
        AVPPrometheusAlienEntityAttributes.addBindings();
        AVPPrometheusEngineerEntityAttributes.addBindings();
        AVPRunnerAlienEntityAttributes.addBindings();
        AVPYautjaEntityAttributes.addBindings();
    }

    private AVPEntityAttributesBindingRegistry() {
        throw new UnsupportedOperationException();
    }
}
