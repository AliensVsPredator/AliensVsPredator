package com.avp.fabric.data.lang.en_us;

import com.alien.common.registry.init.AlienEntityTypes;
import com.predator.common.registry.init.PredatorEntityTypes;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.world.entity.EntityType;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.avp.common.registry.init.entity_type.HumanEntityTypes;

public class EnUsEntityProvider {

    public static final Consumer<FabricLanguageProvider.TranslationBuilder> CONSUMER = builder -> {
        addEntity(builder, AlienEntityTypes.ABERRANT_CHESTBURSTER, "Aberrant Chestburster");
        addEntity(builder, AlienEntityTypes.ABERRANT_DRONE, "Aberrant Drone");
        addEntity(builder, AlienEntityTypes.ABERRANT_FACEHUGGER, "Aberrant Facehugger");
        addEntity(builder, AlienEntityTypes.ABERRANT_OVOMORPH, "Aberrant Ovomorph");
        addEntity(builder, AlienEntityTypes.ABERRANT_PRAETORIAN, "Aberrant Praetorian");
        addEntity(builder, AlienEntityTypes.ABERRANT_QUEEN, "Aberrant Queen");
        addEntity(builder, AlienEntityTypes.ABERRANT_WARRIOR, "Aberrant Warrior");
        addEntity(builder, AlienEntityTypes.ACID, "Acid");
        addEntity(builder, AlienEntityTypes.CHESTBURSTER, "Chestburster");
        addEntity(builder, AlienEntityTypes.DRONE, "Drone");
        addEntity(builder, AlienEntityTypes.FACEHUGGER, "Facehugger");
        addEntity(builder, HumanEntityTypes.GRENADE_THROWN, "Grenade");
        addEntity(builder, AlienEntityTypes.IRRADIATED_DRONE, "Irradiated Drone");
        addEntity(builder, AlienEntityTypes.IRRADIATED_PRAETORIAN, "Irradiated Praetorian");
        addEntity(builder, AlienEntityTypes.IRRADIATED_QUEEN, "Irradiated Queen");
        addEntity(builder, AlienEntityTypes.IRRADIATED_WARRIOR, "Irradiated Warrior");
        addEntity(builder, HumanEntityTypes.MARINE, "Marine");
        addEntity(builder, AlienEntityTypes.NETHER_CHESTBURSTER, "Nether Chestburster");
        addEntity(builder, AlienEntityTypes.NETHER_DRONE, "Nether Drone");
        addEntity(builder, AlienEntityTypes.NETHER_FACEHUGGER, "Nether Facehugger");
        addEntity(builder, AlienEntityTypes.NETHER_OVOMORPH, "Nether Ovomorph");
        addEntity(builder, AlienEntityTypes.NETHER_PRAETORIAN, "Nether Praetorian");
        addEntity(builder, AlienEntityTypes.NETHER_QUEEN, "Nether Queen");
        addEntity(builder, AlienEntityTypes.NETHER_WARRIOR, "Nether Warrior");
        addEntity(builder, AlienEntityTypes.OVOMORPH, "Ovomorph");
        addEntity(builder, AlienEntityTypes.PRAETORIAN, "Praetorian");
        addEntity(builder, AlienEntityTypes.QUEEN, "Queen");
        addEntity(builder, HumanEntityTypes.ROCKET, "Rocket");
        addEntity(builder, AlienEntityTypes.ROYAL_ABERRANT_CHESTBURSTER, "Royal Aberrant Chestburster");
        addEntity(builder, AlienEntityTypes.ROYAL_ABERRANT_FACEHUGGER, "Royal Aberrant Facehugger");
        addEntity(builder, AlienEntityTypes.ROYAL_ABERRANT_OVOMORPH, "Royal Aberrant Ovomorph");
        addEntity(builder, AlienEntityTypes.ROYAL_CHESTBURSTER, "Royal Chestburster");
        addEntity(builder, AlienEntityTypes.ROYAL_FACEHUGGER, "Royal Facehugger");
        addEntity(builder, AlienEntityTypes.ROYAL_NETHER_CHESTBURSTER, "Royal Nether Chestburster");
        addEntity(builder, AlienEntityTypes.ROYAL_NETHER_FACEHUGGER, "Royal Nether Facehugger");
        addEntity(builder, AlienEntityTypes.ROYAL_NETHER_OVOMORPH, "Royal Nether Ovomorph");
        addEntity(builder, AlienEntityTypes.ROYAL_OVOMORPH, "Royal Ovomorph");
        addEntity(builder, PredatorEntityTypes.SHURIKEN, "Shuriken");
        addEntity(builder, PredatorEntityTypes.SMART_DISC, "Smart Disc");
        addEntity(builder, AlienEntityTypes.WARRIOR, "Warrior");
        addEntity(builder, PredatorEntityTypes.YAUTJA, "Yautja");
    };

    private static void addEntity(
        FabricLanguageProvider.TranslationBuilder translationBuilder,
        Supplier<? extends EntityType<?>> entityTypeSupplier,
        String value
    ) {
        addEntity(translationBuilder, entityTypeSupplier.get(), value);
    }

    private static void addEntity(FabricLanguageProvider.TranslationBuilder translationBuilder, EntityType<?> entityType, String value) {
        translationBuilder.add(entityType, value);
    }
}
