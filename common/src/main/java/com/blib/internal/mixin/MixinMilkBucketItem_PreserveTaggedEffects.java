package com.blib.internal.mixin;

import com.blib.api.common.tag.v1.BLibMobEffectTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.MilkBucketItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;

/**
 * Vanilla {@code MilkBucketItem.finishUsingItem} calls {@code LivingEntity.removeAllEffects()}, wiping every
 * active effect indiscriminately. Some downstream effects (stealth cloaks, curses, lore-bound debuffs) are
 * meaningful precisely because they shouldn't be undone by an unrelated drink. This redirect lets a downstream
 * mod opt out of the milk wipe by adding its effect to {@link BLibMobEffectTags#MILK_IMMUNE}.
 * <p>
 * Implementation: snapshot every active effect whose holder is in the tag, run the wipe, then re-add each
 * snapshot. The re-added instances preserve the original duration, amplifier, ambient flag, and visibility
 * flags — so milk effectively no-ops against tagged effects rather than refreshing their timer.
 */
@Mixin(MilkBucketItem.class)
public abstract class MixinMilkBucketItem_PreserveTaggedEffects {

    @Redirect(
        method = "finishUsingItem",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;removeAllEffects()Z")
    )
    private boolean blib$preserveMilkImmuneEffects(LivingEntity entity) {
        var preserved = new ArrayList<MobEffectInstance>();

        for (var instance : entity.getActiveEffects()) {
            if (instance.getEffect().is(BLibMobEffectTags.MILK_IMMUNE)) {
                preserved.add(new MobEffectInstance(instance));
            }
        }

        var removed = entity.removeAllEffects();

        for (var snapshot : preserved) {
            entity.addEffect(snapshot);
        }

        return removed;
    }
}
