package com.avp.fabric.mixin;

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

import com.avp.common.registry.init.AVPMobCategoryData;
import com.avp.fabric.service.FabricBridgeService;
import com.avp.service.Services;

// TODO: Rename this.
@Mixin(MobCategory.class)
public abstract class MixinMobCategory_InjectCustomMobCategory {

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

    private static MobCategory newMobCategory(
        String internalName,
        int internalId,
        AVPMobCategoryData.Data data
    ) {
        return newMobCategory(
            internalName,
            internalId,
            data.name(),
            data.max(),
            data.isFriendly(),
            data.isPersistent(),
            data.despawnDistance()
        );
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
        var nextOrdinal = last.ordinal() + 1;

        var alien = newMobCategory("AVP_ALIEN", nextOrdinal++, AVPMobCategoryData.ALIEN);
        var ovomorph = newMobCategory("AVP_OVOMORPH", nextOrdinal++, AVPMobCategoryData.OVOMORPH);
        var predator = newMobCategory("AVP_PREDATOR", nextOrdinal++, AVPMobCategoryData.PREDATOR);

        var fabricBridgeService = (FabricBridgeService) Services.BRIDGE;

        fabricBridgeService.setAlienMobCategory(alien);
        fabricBridgeService.setOvomorphMobCategory(ovomorph);
        fabricBridgeService.setPredatorMobCategory(predator);

        categories.add(alien);
        categories.add(ovomorph);
        categories.add(predator);

        $VALUES = categories.toArray(new MobCategory[0]);
    }
}
