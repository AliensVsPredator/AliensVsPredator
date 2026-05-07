package com.blib.internal.mixin;

import com.blib.api.common.tag.v1.BLibMobEffectTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MilkBucketItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

/**
 * Lets a downstream mod opt an effect out of milk's effect-wipe by tagging it into
 * {@link BLibMobEffectTags#MILK_IMMUNE}. Vanilla {@code MilkBucketItem.finishUsingItem} indiscriminately
 * removes every active effect; we want stealth cloaks, curses, and other lore-bound effects to survive
 * an unrelated drink.
 * <p>
 * Implementation strategy: snapshot every active effect whose holder is in the tag at HEAD, let
 * {@code finishUsingItem} run its wipe (whatever form that takes), then re-apply the snapshot at RETURN.
 * The re-applied instances preserve their original duration, amplifier, ambient flag, and visibility flags,
 * so the user-visible behavior is "milk no-ops against tagged effects" rather than "milk refreshes their
 * timer."
 * <p>
 * Loader-agnostic by design — vanilla calls {@code LivingEntity.removeAllEffects()} from
 * {@code finishUsingItem}; NeoForge patches the same method to call
 * {@code LivingEntity.removeEffectsCuredBy(EffectCures.MILK)} instead. Either internal call wipes the
 * tagged effects, but the HEAD-snapshot / RETURN-restore pair survives both implementations because both
 * injection points exist on both loaders. The earlier {@code @Redirect}-based version targeted only the
 * vanilla call site and crashed NeoForge with a "Scanned 0 target(s)" injection failure.
 * <p>
 * Snapshot state lives in a {@link ThreadLocal} stack so nested {@code finishUsingItem} calls (vanishingly
 * unlikely, but cheap to defend against) don't interfere with each other. Each HEAD inject pushes a fresh
 * list and the matching RETURN inject pops it.
 */
@Mixin(MilkBucketItem.class)
public abstract class MixinMilkBucketItem_PreserveTaggedEffects {

    private static final ThreadLocal<List<List<MobEffectInstance>>> BLIB_SNAPSHOT_STACK = ThreadLocal.withInitial(ArrayList::new);

    @Inject(method = "finishUsingItem", at = @At("HEAD"))
    private void blib$snapshotMilkImmuneEffects(ItemStack stack, Level level, LivingEntity entity, CallbackInfoReturnable<ItemStack> cir) {
        if (level.isClientSide()) return;

        var preserved = new ArrayList<MobEffectInstance>();
        for (var instance : entity.getActiveEffects()) {
            if (instance.getEffect().is(BLibMobEffectTags.MILK_IMMUNE)) {
                preserved.add(new MobEffectInstance(instance));
            }
        }
        BLIB_SNAPSHOT_STACK.get().add(preserved);
    }

    @Inject(method = "finishUsingItem", at = @At("RETURN"))
    private void blib$restoreMilkImmuneEffects(ItemStack stack, Level level, LivingEntity entity, CallbackInfoReturnable<ItemStack> cir) {
        if (level.isClientSide()) return;

        var stack2 = BLIB_SNAPSHOT_STACK.get();
        if (stack2.isEmpty()) return;

        var preserved = stack2.remove(stack2.size() - 1);
        for (var snapshot : preserved) {
            entity.addEffect(snapshot);
        }
    }
}
