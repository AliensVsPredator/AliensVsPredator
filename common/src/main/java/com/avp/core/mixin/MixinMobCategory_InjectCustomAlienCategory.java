package com.avp.core.mixin;

import net.minecraft.world.entity.MobCategory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Arrays;

import com.avp.core.AVP;
import com.avp.core.common.config.ConfigProperties;
import com.avp.core.common.entity.AVPMobCategories;

@Mixin(MobCategory.class)
public abstract class MixinMobCategory_InjectCustomAlienCategory {

    // This comes from a dependency in fabric related to asm (org.objectweb.asm.Opcodes).
    @Unique
    private static final int PUTSTATIC = 179;

    @SuppressWarnings("InvokerTarget")
    @Invoker("<init>")
    private static MobCategory newMobCategory(
        String internalName,
        int internalId,
        String name,
        final int j,
        final boolean bl,
        final boolean bl2,
        final int k
    ) {
        throw new AssertionError();
    }

    @SuppressWarnings("ShadowTarget")
    private static @Final @Mutable MobCategory[] VALUES;

    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(
        method = "<clinit>", at = @At(
            value = "FIELD", opcode = PUTSTATIC,
            target = "net/minecraft/world/entity/MobCategory.$VALUES:[Lnet/minecraft/world/entity/MobCategory;", shift = At.Shift.AFTER
        )
    )
    private static void addCustomMobCategory(CallbackInfo ci) {
        var properties = AVP.SPAWNING_CONFIG.properties();

        if (!properties.getOrThrow(ConfigProperties.ALIEN_CUSTOM_MOB_CATEGORY_ENABLED)) {
            return;
        }

        var alienSpawnLimit = properties.getOrThrow(ConfigProperties.ALIEN_CUSTOM_MOB_CATEGORY_SPAWN_LIMIT);
        var categories = new ArrayList<>(Arrays.asList(VALUES));
        var last = categories.get(categories.size() - 1);
        var alien = newMobCategory("ALIENS", last.ordinal() + 1, "alien", alienSpawnLimit, false, false, 128);
        AVPMobCategories.ALIENS = alien;
        categories.add(alien);
        VALUES = categories.toArray(new MobCategory[0]);
    }
}
