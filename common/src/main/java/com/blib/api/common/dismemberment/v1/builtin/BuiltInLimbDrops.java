package com.blib.api.common.dismemberment.v1.builtin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import com.blib.api.common.dismemberment.v1.LimbCategories;
import com.blib.api.common.dismemberment.v1.LimbInteractionRegistry;
import com.blib.api.common.dismemberment.v1.entity.DismemberedLimbEntity;

/**
 * Right-click drops for HEAD limbs of vanilla mobs whose dismemberment definitions BLib provides out of the box. Maps
 * each mob to the closest silhouette-matching vanilla skull/head item — mobs without a corresponding head item
 * (illagers, villagers, livestock, etc.) fall through and produce no drop.
 * <p>
 * Registered automatically during BLib's mod init. Consumers wanting to add their own right-click drops should register
 * additional entries through {@link LimbInteractionRegistry} — registration order is priority order so more-specific
 * rules (or overrides) should be registered after this default.
 */
public final class BuiltInLimbDrops {

    private BuiltInLimbDrops() {}

    public static void register() {
        LimbInteractionRegistry.register(BuiltInLimbDrops::isVanillaMobHeadLimb, BuiltInLimbDrops::headForLimb);
    }

    private static boolean isVanillaMobHeadLimb(DismemberedLimbEntity limb) {
        var sourceType = limb.getSourceEntityType();

        if (sourceType == null || vanillaHeadItemFor(sourceType) == null) {
            return false;
        }

        var definition = limb.resolveLimbDefinition();
        return definition != null && definition.category().equals(LimbCategories.HEAD);
    }

    private static ItemStack headForLimb(DismemberedLimbEntity limb) {
        var item = vanillaHeadItemFor(limb.getSourceEntityType());
        return item == null ? ItemStack.EMPTY : new ItemStack(item);
    }

    private static @Nullable Item vanillaHeadItemFor(@Nullable EntityType<?> sourceType) {
        if (sourceType == null) {
            return null;
        }

        // Zombie family — all share the zombie head silhouette.
        if (
            sourceType == EntityType.ZOMBIE
                || sourceType == EntityType.HUSK
                || sourceType == EntityType.DROWNED
                || sourceType == EntityType.ZOMBIE_VILLAGER
        ) {
            return Items.ZOMBIE_HEAD;
        }

        // Skeletons share the skeleton skull.
        if (sourceType == EntityType.SKELETON || sourceType == EntityType.STRAY) {
            return Items.SKELETON_SKULL;
        }

        if (sourceType == EntityType.WITHER_SKELETON) {
            return Items.WITHER_SKELETON_SKULL;
        }

        if (sourceType == EntityType.CREEPER) {
            return Items.CREEPER_HEAD;
        }

        // Piglins (incl. brutes and zombified) share the piglin head silhouette.
        if (
            sourceType == EntityType.PIGLIN
                || sourceType == EntityType.PIGLIN_BRUTE
                || sourceType == EntityType.ZOMBIFIED_PIGLIN
        ) {
            return Items.PIGLIN_HEAD;
        }

        // Illagers, villagers, livestock — no matching skull item, fall through.
        return null;
    }
}
