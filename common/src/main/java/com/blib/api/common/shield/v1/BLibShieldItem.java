package com.blib.api.common.shield.v1;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Marker + behavior interface for items that participate in BLib's shield blocking pipeline. Independent of
 * vanilla {@link net.minecraft.world.item.ShieldItem} — implementing this on any {@link Item} subclass
 * (sword, axe, custom) opts that item into BLib's blocking flow without inheriting from vanilla's shield base
 * class or interacting with vanilla's shield-specific damage path.
 * <p>
 * The pipeline does the following on every incoming hit against a {@link LivingEntity} who is currently using
 * a {@code BLibShieldItem} (i.e. {@code entity.getUseItem().getItem() instanceof BLibShieldItem}):
 * <ol>
 *   <li>Check that the attacker is within {@link BLibShieldConfig#blockAngleDegrees} of the user's view
 *       direction. If not, the hit bypasses blocking entirely.</li>
 *   <li>Call {@link #onBlocked} to decide damage reduction and disable behavior.</li>
 *   <li>Play {@link BLibShieldConfig#blockSound} if configured.</li>
 *   <li>If the result calls for disable, put the item on the user's
 *       {@code ItemCooldowns} for the requested tick count (player only) and stop the use action.</li>
 *   <li>Reduce the incoming damage by {@link BlockResult#damageReduction()} before vanilla damage flow
 *       continues.</li>
 * </ol>
 * <p>
 * Vanilla shield damage path (cone check, durability damage, axe-disable) does not run for these items —
 * BLib's mixin into {@code LivingEntity.isDamageSourceBlocked} returns false for them so vanilla skips its
 * own shield handling. {@code Item.getUseAnimation} and {@code Item.getUseDuration} are also overridden via
 * mixin so the animator pose matches vanilla blocking and right-click starts the use action automatically.
 */
public interface BLibShieldItem {

    /**
     * Static configuration for this shield item. Should typically be a {@code static final} constant on the
     * implementing class — BLib calls this every time it needs use-duration, block-cone, or block-sound, so
     * cheap to return is worth more than per-stack flexibility.
     */
    BLibShieldConfig getShieldConfig();

    /**
     * Called when {@code user} is using this shield, the cone check has passed, and the hit is about to be
     * applied. Return a {@link BlockResult} describing how much damage to absorb and whether the shield
     * should be disabled afterward.
     *
     * @param user   The entity holding the shield. May be a player or any other living entity.
     * @param stack  The shield {@link ItemStack} being used. Mutate this if you want to apply durability damage.
     * @param source The damage source being blocked (attacker, projectile origin, magic, etc.).
     * @param damage The incoming damage amount before any reduction. The returned result's reduction is
     *               applied to this value.
     */
    BlockResult onBlocked(LivingEntity user, ItemStack stack, DamageSource source, float damage);
}
