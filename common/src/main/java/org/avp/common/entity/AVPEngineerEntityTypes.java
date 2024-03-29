package org.avp.common.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import org.avp.common.entity.living.Engineer;
import org.avp.common.registry.AVPRegistry;
import org.avp.api.GameObject;

/**
 * @author Boston Vanseghi
 */
public class AVPEngineerEntityTypes implements AVPRegistry {

    private static final AVPEngineerEntityTypes INSTANCE = new AVPEngineerEntityTypes();

    public static AVPEngineerEntityTypes getInstance() {
        return INSTANCE;
    }

    public static final GameObject<EntityType<Engineer>> ENGINEER = AVPEntityTypes.registerLiving(
        "engineer",
        0xB8B1B6,
        0x99AFBD,
        EntityType.Builder.of(Engineer::new, MobCategory.MONSTER)
    );

    @Override
    public void register() {}
}
