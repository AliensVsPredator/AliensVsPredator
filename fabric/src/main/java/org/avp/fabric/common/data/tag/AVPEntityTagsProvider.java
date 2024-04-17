package org.avp.fabric.common.data.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;

import java.util.concurrent.CompletableFuture;

import org.avp.common.entity.type.AVPBaseAlienEntityTypes;
import org.avp.common.entity.type.AVPEngineerEntityTypes;
import org.avp.common.entity.type.AVPEntityTypes;
import org.avp.common.entity.type.AVPExoticAlienEntityTypes;
import org.avp.common.entity.type.AVPPrometheusAlienEntityTypes;
import org.avp.common.entity.type.AVPRunnerAlienEntityTypes;
import org.avp.common.entity.type.AVPYautjaEntityTypes;
import org.avp.common.tag.AVPEntityTags;

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
        getOrCreateTagBuilder(AVPEntityTags.PARASITES).add(
            AVPBaseAlienEntityTypes.INSTANCE.FACEHUGGER.get(),
            AVPBaseAlienEntityTypes.INSTANCE.FACEHUGGER_ROYAL.get(),
            AVPExoticAlienEntityTypes.INSTANCE.OCTOHUGGER.get(),
            AVPPrometheusAlienEntityTypes.INSTANCE.TRILOBITE.get(),
            AVPPrometheusAlienEntityTypes.INSTANCE.TRILOBITE_BABY.get()
        );

        getOrCreateTagBuilder(AVPEntityTags.EGGS).add(
            AVPBaseAlienEntityTypes.INSTANCE.OVAMORPH.get(),
            AVPExoticAlienEntityTypes.INSTANCE.OVAMORPH_DRACO.get()
        );

        getOrCreateTagBuilder(AVPEntityTags.ROYAL_ALIENS).add(
            AVPBaseAlienEntityTypes.INSTANCE.FACEHUGGER_ROYAL.get(),
            AVPBaseAlienEntityTypes.INSTANCE.PRAETORIAN.get(),
            AVPBaseAlienEntityTypes.INSTANCE.QUEEN.get(),
            AVPRunnerAlienEntityTypes.INSTANCE.CRUSHER.get()
        );

        getOrCreateTagBuilder(AVPEntityTags.ALIENS)
            .addTag(AVPEntityTags.EGGS)
            .addTag(AVPEntityTags.PARASITES)
            .addTag(AVPEntityTags.ROYAL_ALIENS)
            .add(
                AVPEntityTypes.INSTANCE.BELUGABURSTER.get(),
                AVPEntityTypes.INSTANCE.BELUGAMORPH.get(),

                AVPBaseAlienEntityTypes.INSTANCE.BOILER.get(),
                AVPBaseAlienEntityTypes.INSTANCE.CHESTBURSTER.get(),
                AVPBaseAlienEntityTypes.INSTANCE.DRONE.get(),
                AVPBaseAlienEntityTypes.INSTANCE.SPITTER.get(),
                AVPBaseAlienEntityTypes.INSTANCE.WARRIOR.get(),
                AVPExoticAlienEntityTypes.INSTANCE.DEACON_ADULT_ENGINEER.get(),
                AVPExoticAlienEntityTypes.INSTANCE.DRACOBURSTER.get(),
                AVPExoticAlienEntityTypes.INSTANCE.DRACOMORPH.get(),
                AVPExoticAlienEntityTypes.INSTANCE.ULTRAMORPH.get(),

                AVPPrometheusAlienEntityTypes.INSTANCE.DEACON.get(),
                AVPPrometheusAlienEntityTypes.INSTANCE.DEACON_ADULT.get(),

                AVPRunnerAlienEntityTypes.INSTANCE.CHESTBURSTER_RUNNER.get(),
                AVPRunnerAlienEntityTypes.INSTANCE.DRONE_RUNNER.get(),
                AVPRunnerAlienEntityTypes.INSTANCE.WARRIOR_RUNNER.get()
            );

        getOrCreateTagBuilder(AVPEntityTags.ACID_BLEEDERS)
            .addTag(AVPEntityTags.ALIENS);

        getOrCreateTagBuilder(AVPEntityTags.ACID_IMMUNE)
            .addTag(AVPEntityTags.ACID_BLEEDERS);

        getOrCreateTagBuilder(AVPEntityTags.ENGINEERS).add(
            AVPEngineerEntityTypes.INSTANCE.ENGINEER.get()
        );

        getOrCreateTagBuilder(AVPEntityTags.NOT_WORTH_KILLING)
            .add(EntityType.ALLAY)
            .add(EntityType.AXOLOTL)
            .add(EntityType.BAT)
            .add(EntityType.BEE)
            .add(EntityType.CAT)
            .add(EntityType.CHICKEN)
            .add(EntityType.COD)
            .add(EntityType.CREEPER)
            .add(EntityType.GLOW_SQUID)
            .add(EntityType.PARROT)
            .add(EntityType.PUFFERFISH)
            .add(EntityType.SALMON)
            .add(EntityType.SQUID)
            .add(EntityType.TADPOLE)
            .add(EntityType.TROPICAL_FISH);

        getOrCreateTagBuilder(AVPEntityTags.NON_HOSTS)
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

        getOrCreateTagBuilder(AVPEntityTags.PREDATORS)
            .add(AVPYautjaEntityTypes.INSTANCE.YAUTJA.get());
    }

    private void modifyMinecraftTags() {
        getOrCreateTagBuilder(EntityTypeTags.CAN_BREATHE_UNDER_WATER)
            .addTag(AVPEntityTags.ALIENS);
    }
}
