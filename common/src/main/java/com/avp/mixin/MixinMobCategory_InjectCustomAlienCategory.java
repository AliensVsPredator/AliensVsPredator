package com.avp.mixin;

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

import com.avp.common.registry.init.AVPMobCategories;

// TODO: Rename this.
@Mixin(MobCategory.class)
public abstract class MixinMobCategory_InjectCustomAlienCategory {

    @Unique
    private static final int OPCODE_PUTSTATIC = 179;

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
            value = "FIELD", opcode = OPCODE_PUTSTATIC,
            target = "net/minecraft/world/entity/MobCategory.$VALUES:[Lnet/minecraft/world/entity/MobCategory;", shift = At.Shift.AFTER
        )
    )
    private static void addCustomMobCategory(CallbackInfo ci) {
        var categories = new ArrayList<>(Arrays.asList($VALUES));
        var last = categories.get(categories.size() - 1);
        var alien = newMobCategory("ALIENS", last.ordinal() + 1, "alien", 75, false, false, 128);
        var predator = newMobCategory("PREDATOR", last.ordinal() + 1, "predator", 75, false, false, 128);
        AVPMobCategories.ALIENS = alien;
        AVPMobCategories.PREDATOR = predator;
        categories.add(alien);
        categories.add(predator);
        $VALUES = categories.toArray(new MobCategory[0]);
    }
}
