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
        getOrCreateTagBuilder(AVPEntityTypeTags.PARASITES).add(
            AVPBaseAlienEntityTypes.INSTANCE.facehugger.get(),
            AVPBaseAlienEntityTypes.INSTANCE.facehuggerRoyal.get(),
            AVPExoticAlienEntityTypes.INSTANCE.octohugger.get(),
            AVPPrometheusAlienEntityTypes.INSTANCE.trilobite.get(),
            AVPPrometheusAlienEntityTypes.INSTANCE.trilobiteBaby.get()
        );

        getOrCreateTagBuilder(AVPEntityTypeTags.EGGS).add(
            AVPBaseAlienEntityTypes.INSTANCE.ovamorph.get(),
            AVPExoticAlienEntityTypes.INSTANCE.ovamorphDraco.get()
        );

        getOrCreateTagBuilder(AVPEntityTypeTags.ROYAL_ALIENS).add(
            AVPBaseAlienEntityTypes.INSTANCE.facehuggerRoyal.get(),
            AVPBaseAlienEntityTypes.INSTANCE.praetorian.get(),
            AVPBaseAlienEntityTypes.INSTANCE.queen.get(),
            AVPRunnerAlienEntityTypes.INSTANCE.crusher.get()
        );

        getOrCreateTagBuilder(AVPEntityTypeTags.HIVE_ALIENS)
            .addTag(AVPEntityTypeTags.EGGS)
            .addTag(AVPEntityTypeTags.ROYAL_ALIENS)
            .add(
                AVPBaseAlienEntityTypes.INSTANCE.boiler.get(),

                AVPBaseAlienEntityTypes.INSTANCE.chestburster.get(),
                AVPBaseAlienEntityTypes.INSTANCE.chestbursterQueen.get(),
                AVPBaseAlienEntityTypes.INSTANCE.drone.get(),
                AVPBaseAlienEntityTypes.INSTANCE.nauticomorph.get(),
                AVPBaseAlienEntityTypes.INSTANCE.spitter.get(),
                AVPBaseAlienEntityTypes.INSTANCE.warrior.get(),

                AVPExoticAlienEntityTypes.INSTANCE.chestbursterDraco.get(),
                AVPExoticAlienEntityTypes.INSTANCE.dracomorph.get(),
                AVPExoticAlienEntityTypes.INSTANCE.ultramorph.get(),

                AVPRunnerAlienEntityTypes.INSTANCE.chestbursterRunner.get(),
                AVPRunnerAlienEntityTypes.INSTANCE.droneRunner.get(),
                AVPRunnerAlienEntityTypes.INSTANCE.warriorRunner.get()
            );

        getOrCreateTagBuilder(AVPEntityTypeTags.ALIENS)
            .addTag(AVPEntityTypeTags.EGGS)
            .addTag(AVPEntityTypeTags.PARASITES)
            .addTag(AVPEntityTypeTags.HIVE_ALIENS)
            .add(
                AVPEntityTypes.INSTANCE.belugaburster.get(),
                AVPEntityTypes.INSTANCE.belugamorph.get(),
                AVPExoticAlienEntityTypes.INSTANCE.deaconAdultEngineer.get(),

                AVPPrometheusAlienEntityTypes.INSTANCE.deacon.get(),
                AVPPrometheusAlienEntityTypes.INSTANCE.deaconAdult.get()
            );

        getOrCreateTagBuilder(AVPEntityTypeTags.ACID_BLEEDERS)
            .addTag(AVPEntityTypeTags.ALIENS);

        getOrCreateTagBuilder(AVPEntityTypeTags.ACID_IMMUNE)
            .addTag(AVPEntityTypeTags.ACID_BLEEDERS);

        getOrCreateTagBuilder(AVPEntityTypeTags.ENGINEERS).add(
            AVPEngineerEntityTypes.INSTANCE.engineer.get()
        );

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

        getOrCreateTagBuilder(AVPEntityTypeTags.PREDATORS)
            .add(AVPYautjaEntityTypes.INSTANCE.yautja.get());

        getOrCreateTagBuilder(AVPEntityTypeTags.PRODUCES_RESIN)
            .add(
                AVPBaseAlienEntityTypes.INSTANCE.drone.get(),
                AVPBaseAlienEntityTypes.INSTANCE.warrior.get(),
                AVPBaseAlienEntityTypes.INSTANCE.praetorian.get(),
                AVPBaseAlienEntityTypes.INSTANCE.queen.get(),
                AVPRunnerAlienEntityTypes.INSTANCE.droneRunner.get(),
                AVPRunnerAlienEntityTypes.INSTANCE.warriorRunner.get(),
                AVPRunnerAlienEntityTypes.INSTANCE.crusher.get()
            )
            .addTag(AVPEntityTypeTags.PREDATORS);

        getOrCreateTagBuilder(AVPEntityTypeTags.MONSTERS)
            .addTag(AVPEntityTypeTags.ALIENS)
            .addTag(AVPEntityTypeTags.PREDATORS);
    }

    private void modifyMinecraftTags() {
        getOrCreateTagBuilder(EntityTypeTags.CAN_BREATHE_UNDER_WATER)
            .addTag(AVPEntityTypeTags.ALIENS);
    }
}
