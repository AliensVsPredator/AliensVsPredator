package org.avp.common.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import org.avp.api.GameObject;
import org.avp.common.entity.living.Yautja;
import org.avp.common.registry.AVPRegistry;

public class AVPYautjaEntityTypes implements AVPRegistry {

    private static final AVPYautjaEntityTypes INSTANCE = new AVPYautjaEntityTypes();

    public static AVPYautjaEntityTypes getInstance() {
        return INSTANCE;
    }

    public static final GameObject<EntityType<Yautja>> YAUTJA = AVPEntityTypes.registerLiving(
        "yautja",
        0xB9A86C,
        0x5A4728,
        EntityType.Builder.of(Yautja::new, MobCategory.MONSTER)
            .sized(0.98F, 2.48F)
    );

    @Override
    public void register() {}
}
