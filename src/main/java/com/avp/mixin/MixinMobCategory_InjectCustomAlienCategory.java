package com.avp.mixin;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.entity.MobCategory;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Arrays;

import com.avp.AVP;
import com.avp.common.entity.AVPMobCategories;

@Mixin(MobCategory.class)
public abstract class MixinMobCategory_InjectCustomAlienCategory {

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

    @Shadow
    private static @Final @Mutable MobCategory[] $VALUES;

    @Inject(
        method = "<clinit>", at = @At(
            value = "FIELD", opcode = Opcodes.PUTSTATIC,
            target = "net/minecraft/world/entity/MobCategory.$VALUES:[Lnet/minecraft/world/entity/MobCategory;", shift = At.Shift.AFTER
        )
    )
    private static void addCustomMobCategory(CallbackInfo ci) {
        if (FabricLoader.getInstance().isModLoaded("AzureLib")) {
            if (!AVP.config.spawnConfigs.ALIEN_CUSTOM_MOB_CATEGORY_ENABLED) {
                return;
            }

            var alienSpawnLimit = AVP.config.spawnConfigs.ALIEN_CUSTOM_MOB_CATEGORY_LIMIT;
            var predatorSpawnLimit = AVP.config.spawnConfigs.PREDATOR_CUSTOM_MOB_CATEGORY_LIMIT;
            var categories = new ArrayList<>(Arrays.asList($VALUES));
            var last = categories.get(categories.size() - 1);
            var alien = newMobCategory("ALIENS", last.ordinal() + 1, "alien", alienSpawnLimit, false, false, 128);
            var predator = newMobCategory("PREDATOR", last.ordinal() + 1, "predator", predatorSpawnLimit, false, false,
                    128);
            AVPMobCategories.ALIENS = alien;
            AVPMobCategories.PREDATOR = predator;
            categories.add(alien);
            categories.add(predator);
            $VALUES = categories.toArray(new MobCategory[0]);
        }
    }
}
