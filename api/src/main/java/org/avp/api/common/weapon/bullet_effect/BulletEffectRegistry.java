package org.avp.api.common.weapon.bullet_effect;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BulletEffectRegistry {

    private static final Map<ResourceLocation, Set<BulletEffect>> ITEM_RESOURCE_LOCATIONS_TO_BULLET_EFFECTS = new HashMap<>();

    public static void registerBulletEffect(ResourceLocation resourceLocation, BulletEffect bulletEffect) {
        ITEM_RESOURCE_LOCATIONS_TO_BULLET_EFFECTS.compute(resourceLocation, ($, bulletEffects) -> {
            var set = bulletEffects == null ? new HashSet<BulletEffect>() : bulletEffects;
            set.add(bulletEffect);
            return set;
        });
    }

    public static Set<BulletEffect> getBulletEffects(Item item) {
        return getBulletEffects(BuiltInRegistries.ITEM.getKey(item));
    }

    public static Set<BulletEffect> getBulletEffects(ResourceLocation resourceLocation) {
        return ITEM_RESOURCE_LOCATIONS_TO_BULLET_EFFECTS.get(resourceLocation);
    }

    private BulletEffectRegistry() {
        throw new UnsupportedOperationException();
    }
}
