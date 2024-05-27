package org.avp.fabric.common.data.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;

import java.util.concurrent.CompletableFuture;

import org.avp.common.entity.data.AVPEntityDataRegistry;
import org.avp.common.tag.AVPEntityTypeTags;

public class AVPEntityTagsProvider extends FabricTagProvider.EntityTypeTagProvider {

    public AVPEntityTagsProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        createAVPTags();
        modifyMinecraftTags();
    }

    private void createAVPTags() {
        AVPEntityDataRegistry.INSTANCE.getEntries().forEach(entityData -> {
            var entityType = entityData.getHolder().get();
            var tags = entityData.getTags();

            tags.forEach(tag -> getOrCreateTagBuilder(tag).add(entityType));
        });

        getOrCreateTagBuilder(AVPEntityTypeTags.HIVE_ALIENS)
            .addTag(AVPEntityTypeTags.EGGS)
            .addTag(AVPEntityTypeTags.ROYAL_ALIENS);

        getOrCreateTagBuilder(AVPEntityTypeTags.ALIENS)
            .addTag(AVPEntityTypeTags.HIVE_ALIENS)
            .addTag(AVPEntityTypeTags.EGGS)
            .addTag(AVPEntityTypeTags.PARASITES)
            .addTag(AVPEntityTypeTags.ROYAL_ALIENS);

        getOrCreateTagBuilder(AVPEntityTypeTags.ACID_IMMUNE)
            .addTag(AVPEntityTypeTags.ACID_BLEEDERS);

        getOrCreateTagBuilder(AVPEntityTypeTags.SMALL_GUNS_IMMUNE)
            .addOptionalTag(AVPEntityTypeTags.MEDIUM_GUNS_IMMUNE)
            .addOptionalTag(AVPEntityTypeTags.HEAVY_GUNS_IMMUNE)
            .addOptionalTag(AVPEntityTypeTags.UBER_GUNS_IMMUNE);

        getOrCreateTagBuilder(AVPEntityTypeTags.MEDIUM_GUNS_IMMUNE)
            .addOptionalTag(AVPEntityTypeTags.HEAVY_GUNS_IMMUNE)
            .addOptionalTag(AVPEntityTypeTags.UBER_GUNS_IMMUNE);

        getOrCreateTagBuilder(AVPEntityTypeTags.HEAVY_GUNS_IMMUNE)
            .addOptionalTag(AVPEntityTypeTags.UBER_GUNS_IMMUNE);

        getOrCreateTagBuilder(AVPEntityTypeTags.NOT_WORTH_KILLING)
            .add(
                EntityType.ALLAY,
                EntityType.AXOLOTL,
                EntityType.BAT,
                EntityType.BEE,
                EntityType.CAT,
                EntityType.CHICKEN,
                EntityType.COD,
                EntityType.CREEPER,
                EntityType.GLOW_SQUID,
                EntityType.PARROT,
                EntityType.PUFFERFISH,
                EntityType.SALMON,
                EntityType.SQUID,
                EntityType.TADPOLE,
                EntityType.TROPICAL_FISH
            );

        getOrCreateTagBuilder(AVPEntityTypeTags.NON_HOSTS)
            .addOptionalTag(EntityTypeTags.UNDEAD)
            .add(
                EntityType.ALLAY,
                EntityType.AXOLOTL,
                EntityType.BAT,
                EntityType.BEE,
                EntityType.BLAZE,
                EntityType.BREEZE,
                EntityType.CAVE_SPIDER,
                EntityType.CHICKEN,
                EntityType.AXOLOTL,
                EntityType.COD,
                EntityType.ELDER_GUARDIAN,
                EntityType.AXOLOTL,
                EntityType.ENDERMAN,
                EntityType.ENDERMITE,
                EntityType.AXOLOTL,
                EntityType.FROG,
                EntityType.GHAST,
                EntityType.GLOW_SQUID,
                EntityType.GUARDIAN,
                EntityType.IRON_GOLEM,
                EntityType.MAGMA_CUBE,
                EntityType.PARROT,
                EntityType.PUFFERFISH,
                EntityType.SALMON,
                EntityType.SHULKER,
                EntityType.SHULKER_BULLET,
                EntityType.SILVERFISH,
                EntityType.SLIME,
                EntityType.SNOW_GOLEM,
                EntityType.SPIDER,
                EntityType.SQUID,
                EntityType.STRIDER,
                EntityType.TADPOLE,
                EntityType.TROPICAL_FISH,
                EntityType.VEX,
                EntityType.WARDEN
            );

        getOrCreateTagBuilder(AVPEntityTypeTags.MONSTERS)
            .addTag(AVPEntityTypeTags.ALIENS)
            .addTag(AVPEntityTypeTags.PREDATORS);
    }

    private void modifyMinecraftTags() {
        getOrCreateTagBuilder(EntityTypeTags.CAN_BREATHE_UNDER_WATER)
            .addTag(AVPEntityTypeTags.ALIENS);
    }
}
