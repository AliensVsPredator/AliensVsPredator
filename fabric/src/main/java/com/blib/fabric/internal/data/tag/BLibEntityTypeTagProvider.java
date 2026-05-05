package com.blib.fabric.internal.data.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.ApiStatus;

import java.util.concurrent.CompletableFuture;

import com.blib.api.common.tag.v1.BLibEntityTypeTags;

@ApiStatus.Internal
public final class BLibEntityTypeTagProvider extends FabricTagProvider.EntityTypeTagProvider {

    public BLibEntityTypeTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        addHumanoids();
        addNetherCreatures();
        addThermalVisible();
        addThermalHot();
    }

    private void addHumanoids() {
        getOrCreateTagBuilder(BLibEntityTypeTags.HUMANOIDS)
            .addOptionalTag(EntityTypeTags.ILLAGER)
            .add(
                EntityType.PIGLIN,
                EntityType.PIGLIN_BRUTE,
                EntityType.PLAYER,
                EntityType.VILLAGER,
                EntityType.WANDERING_TRADER,
                EntityType.WITCH
            );
    }

    private void addNetherCreatures() {
        getOrCreateTagBuilder(BLibEntityTypeTags.NETHER_CREATURES)
            .add(
                EntityType.HOGLIN,
                EntityType.PIGLIN,
                EntityType.PIGLIN_BRUTE,
                EntityType.STRIDER
            );
    }

    /**
     * Default thermal-vision visibility for vanilla mobs. Decided per-entity from a "would IR detect a meaningful
     * heat signature" lens:
     * <ul>
     * <li><b>Included:</b> warm-blooded mammals, birds, arthropods (have biological mass), undead (game convention),
     * nether mobs (often hot), most hostile biological mobs, players. Boss mobs (Wither, Ender Dragon, Warden) too.
     * Slime is gelatinous biological mass — included.</li>
     * <li><b>Excluded:</b> cold-blooded creatures (fish, axolotl, frog, turtle, squid), purely
     * mechanical/inanimate entities (iron golem, snow golem, armor stand, shulker, breeze).</li>
     * </ul>
     * Modders/datapacks override either way by re-declaring the tag.
     */
    private void addThermalVisible() {
        getOrCreateTagBuilder(BLibEntityTypeTags.THERMAL_VISIBLE)
            // Vanilla aggregate tags pick up zombie/skeleton families + wither + phantom + the four illagers.
            .addOptionalTag(EntityTypeTags.UNDEAD)
            .addOptionalTag(EntityTypeTags.ILLAGER)
            .add(
                // Players
                EntityType.PLAYER,
                // Land mammals
                EntityType.ARMADILLO,
                EntityType.BAT,
                EntityType.CAMEL,
                EntityType.CAT,
                EntityType.COW,
                EntityType.DONKEY,
                EntityType.FOX,
                EntityType.GOAT,
                EntityType.HORSE,
                EntityType.LLAMA,
                EntityType.MOOSHROOM,
                EntityType.MULE,
                EntityType.OCELOT,
                EntityType.PANDA,
                EntityType.PIG,
                EntityType.POLAR_BEAR,
                EntityType.RABBIT,
                EntityType.SHEEP,
                EntityType.SNIFFER,
                EntityType.TRADER_LLAMA,
                EntityType.WOLF,
                // Birds + small flyers
                EntityType.CHICKEN,
                EntityType.PARROT,
                EntityType.ALLAY,
                EntityType.VEX,
                // Aquatic mammals (warm-blooded)
                EntityType.DOLPHIN,
                // Aquatic hostile biological — alien sea creatures, included for gameplay over biology
                EntityType.GUARDIAN,
                EntityType.ELDER_GUARDIAN,
                // Arthropods — small but biological
                EntityType.BEE,
                EntityType.CAVE_SPIDER,
                EntityType.SILVERFISH,
                EntityType.SPIDER,
                // Hostile biological non-undead
                EntityType.CREEPER,
                EntityType.RAVAGER,
                EntityType.VILLAGER,
                EntityType.WANDERING_TRADER,
                EntityType.WARDEN,
                EntityType.WITCH,
                // Nether warm/hot
                EntityType.BLAZE,
                EntityType.GHAST,
                EntityType.HOGLIN,
                EntityType.MAGMA_CUBE,
                EntityType.PIGLIN,
                EntityType.PIGLIN_BRUTE,
                EntityType.STRIDER,
                // End-realm creatures
                EntityType.ENDERMAN,
                EntityType.ENDERMITE,
                EntityType.ENDER_DRAGON,
                // Gelatinous biological
                EntityType.SLIME
            );
    }

    /**
     * Default thermally-hot entities — they read as if fully lit + emissive in IR regardless of ambient
     * lighting. Includes fire creatures (blaze, magma cube), lava-dwellers (strider), and projectile-fire mobs
     * (ghast). Modders can override or extend.
     */
    private void addThermalHot() {
        getOrCreateTagBuilder(BLibEntityTypeTags.THERMAL_HOT)
            .add(
                EntityType.BLAZE,
                EntityType.GHAST,
                EntityType.MAGMA_CUBE,
                EntityType.STRIDER
            );
    }
}
