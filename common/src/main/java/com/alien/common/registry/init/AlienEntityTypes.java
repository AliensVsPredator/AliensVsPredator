package com.alien.common.registry.init;

import com.alien.common.gameplay.entity.acid.Acid;
import com.alien.common.gameplay.entity.living.alien.adolescent.Adolescent;
import com.alien.common.gameplay.entity.living.alien.chestburster.Chestburster;
import com.alien.common.gameplay.entity.living.alien.ovipositor.Ovipositor;
import com.alien.common.gameplay.entity.living.alien.ovomorph.Ovomorph;
import com.alien.common.gameplay.entity.living.alien.parasite.facehugger.Facehugger;
import com.alien.common.gameplay.entity.living.alien.xenomorph.boiler.Boiler;
import com.alien.common.gameplay.entity.living.alien.xenomorph.crusher.Crusher;
import com.alien.common.gameplay.entity.living.alien.xenomorph.drone.Drone;
import com.alien.common.gameplay.entity.living.alien.xenomorph.praetorian.Praetorian;
import com.alien.common.gameplay.entity.living.alien.xenomorph.predalien.Predalien;
import com.alien.common.gameplay.entity.living.alien.xenomorph.prowler.Prowler;
import com.alien.common.gameplay.entity.living.alien.xenomorph.queen.Queen;
import com.alien.common.gameplay.entity.living.alien.xenomorph.runner.Runner;
import com.alien.common.gameplay.entity.living.alien.xenomorph.spitter.Spitter;
import com.alien.common.gameplay.entity.living.alien.xenomorph.warrior.Warrior;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import com.avp.common.registry.AVPDeferredHolder;
import com.avp.common.registry.init.entity_type.AVPEntityTypes;
import com.avp.service.Services;

public class AlienEntityTypes {

    public static final AVPDeferredHolder<EntityType<Adolescent>> ABERRANT_ADOLESCENT = AVPEntityTypes.register(
        "aberrant_adolescent",
        EntityType.Builder.of(Adolescent::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(0.5f, 0.5f)
    );

    public static final AVPDeferredHolder<EntityType<Boiler>> ABERRANT_BOILER = AVPEntityTypes.register(
        "aberrant_boiler",
        EntityType.Builder.of(Boiler::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(0.8f, 1.98f)
    );

    public static final AVPDeferredHolder<EntityType<Chestburster>> ABERRANT_CHESTBURSTER = AVPEntityTypes.register(
        "aberrant_chestburster",
        EntityType.Builder.of(Chestburster::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(0.35f, 0.35f)
    );

    public static final AVPDeferredHolder<EntityType<Crusher>> ABERRANT_CRUSHER = AVPEntityTypes.register(
        "aberrant_crusher",
        EntityType.Builder.of(Crusher::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(1.8f, 1.98f)
    );

    public static final AVPDeferredHolder<EntityType<Drone>> ABERRANT_DRONE = AVPEntityTypes.register(
        "aberrant_drone",
        EntityType.Builder.of(Drone::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(0.8f, 1.98f)
    );

    public static final AVPDeferredHolder<EntityType<Facehugger>> ABERRANT_FACEHUGGER = AVPEntityTypes.register(
        "aberrant_facehugger",
        EntityType.Builder.of(Facehugger::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(0.8f, 0.25f)
    );

    public static final AVPDeferredHolder<EntityType<Ovomorph>> ABERRANT_OVOMORPH = AVPEntityTypes.register(
        "aberrant_ovomorph",
        EntityType.Builder.of(Ovomorph::new, AVPEntityTypes.OVOMORPH_CATEGORY)
            .sized(0.65f, 0.8f)
    );

    public static final AVPDeferredHolder<EntityType<Praetorian>> ABERRANT_PRAETORIAN = AVPEntityTypes.register(
        "aberrant_praetorian",
        EntityType.Builder.of(Praetorian::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(0.98f, 3.98f)
    );

    public static final AVPDeferredHolder<EntityType<Predalien>> ABERRANT_PREDALIEN = AVPEntityTypes.register(
        "aberrant_predalien",
        EntityType.Builder.of(Predalien::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(0.98f, 3.98f)
    );

    public static final AVPDeferredHolder<EntityType<Prowler>> ABERRANT_PROWLER = AVPEntityTypes.register(
        "aberrant_prowler",
        EntityType.Builder.of(Prowler::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(0.8f, 0.98f)
    );

    public static final AVPDeferredHolder<EntityType<Queen>> ABERRANT_QUEEN = AVPEntityTypes.register(
        "aberrant_queen",
        EntityType.Builder.of(Queen::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(1.98f, 3.98f)
    );

    public static final AVPDeferredHolder<EntityType<Runner>> ABERRANT_RUNNER = AVPEntityTypes.register(
        "aberrant_runner",
        EntityType.Builder.of(Runner::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(0.8f, 0.98f)
    );

    public static final AVPDeferredHolder<EntityType<Spitter>> ABERRANT_SPITTER = AVPEntityTypes.register(
        "aberrant_spitter",
        EntityType.Builder.of(Spitter::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(0.8f, 2.5f)
    );

    public static final AVPDeferredHolder<EntityType<Warrior>> ABERRANT_WARRIOR = AVPEntityTypes.register(
        "aberrant_warrior",
        EntityType.Builder.of(Warrior::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(0.8f, 1.98f)
    );

    public static final AVPDeferredHolder<EntityType<Acid>> ACID = AVPEntityTypes.register(
        "acid",
        EntityType.Builder.of(Acid::new, MobCategory.MISC)
            .sized(0.66F, 0.05F)
    );

    public static final AVPDeferredHolder<EntityType<Adolescent>> ADOLESCENT = AVPEntityTypes.register(
        "adolescent",
        EntityType.Builder.of(Adolescent::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(0.5f, 0.5f)
    );

    public static final AVPDeferredHolder<EntityType<Boiler>> BOILER = AVPEntityTypes.register(
        "boiler",
        EntityType.Builder.of(Boiler::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(0.8f, 1.98f)
    );

    public static final AVPDeferredHolder<EntityType<Chestburster>> CHESTBURSTER = AVPEntityTypes.register(
        "chestburster",
        EntityType.Builder.of(Chestburster::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(0.35f, 0.35f)
    );

    public static final AVPDeferredHolder<EntityType<Crusher>> CRUSHER = AVPEntityTypes.register(
        "crusher",
        EntityType.Builder.of(Crusher::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(1.8f, 1.98f)
    );

    public static final AVPDeferredHolder<EntityType<Drone>> DRONE = AVPEntityTypes.register(
        "drone",
        EntityType.Builder.of(Drone::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(0.8f, 1.98f)
    );

    public static final AVPDeferredHolder<EntityType<Facehugger>> FACEHUGGER = AVPEntityTypes.register(
        "facehugger",
        EntityType.Builder.of(Facehugger::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(0.8f, 0.25f)
    );

    public static final AVPDeferredHolder<EntityType<Crusher>> IRRADIATED_CRUSHER = AVPEntityTypes.register(
        "irradiated_crusher",
        EntityType.Builder.of(Crusher::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(1.8f, 1.98f)
    );

    public static final AVPDeferredHolder<EntityType<Drone>> IRRADIATED_DRONE = AVPEntityTypes.register(
        "irradiated_drone",
        EntityType.Builder.of(Drone::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(0.8f, 1.98f)
    );

    public static final AVPDeferredHolder<EntityType<Praetorian>> IRRADIATED_PRAETORIAN = AVPEntityTypes.register(
        "irradiated_praetorian",
        EntityType.Builder.of(Praetorian::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(0.98f, 3.98f)
    );

    public static final AVPDeferredHolder<EntityType<Predalien>> IRRADIATED_PREDALIEN = AVPEntityTypes.register(
        "irradiated_predalien",
        EntityType.Builder.of(Predalien::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(0.98f, 3.98f)
    );

    public static final AVPDeferredHolder<EntityType<Prowler>> IRRADIATED_PROWLER = AVPEntityTypes.register(
        "irradiated_prowler",
        EntityType.Builder.of(Prowler::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(0.8f, 0.98f)
    );

    public static final AVPDeferredHolder<EntityType<Queen>> IRRADIATED_QUEEN = AVPEntityTypes.register(
        "irradiated_queen",
        EntityType.Builder.of(Queen::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(1.98f, 3.98f)
    );

    public static final AVPDeferredHolder<EntityType<Runner>> IRRADIATED_RUNNER = AVPEntityTypes.register(
        "irradiated_runner",
        EntityType.Builder.of(Runner::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(0.8f, 0.98f)
    );

    public static final AVPDeferredHolder<EntityType<Warrior>> IRRADIATED_WARRIOR = AVPEntityTypes.register(
        "irradiated_warrior",
        EntityType.Builder.of(Warrior::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(0.8f, 1.98f)
    );

    public static final AVPDeferredHolder<EntityType<Adolescent>> NETHER_ADOLESCENT = AVPEntityTypes.register(
        "nether_adolescent",
        EntityType.Builder.of(Adolescent::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(0.5f, 0.5f)
    );

    public static final AVPDeferredHolder<EntityType<Boiler>> NETHER_BOILER = AVPEntityTypes.register(
        "nether_boiler",
        EntityType.Builder.of(Boiler::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(0.8f, 1.98f)
    );

    public static final AVPDeferredHolder<EntityType<Chestburster>> NETHER_CHESTBURSTER = AVPEntityTypes.register(
        "nether_chestburster",
        EntityType.Builder.of(Chestburster::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(0.35f, 0.35f)
    );

    public static final AVPDeferredHolder<EntityType<Crusher>> NETHER_CRUSHER = AVPEntityTypes.register(
        "nether_crusher",
        EntityType.Builder.of(Crusher::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(1.8f, 1.98f)
    );

    public static final AVPDeferredHolder<EntityType<Drone>> NETHER_DRONE = AVPEntityTypes.register(
        "nether_drone",
        EntityType.Builder.of(Drone::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(0.8f, 1.98f)
    );

    public static final AVPDeferredHolder<EntityType<Facehugger>> NETHER_FACEHUGGER = AVPEntityTypes.register(
        "nether_facehugger",
        EntityType.Builder.of(Facehugger::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(0.8f, 0.25f)
    );

    public static final AVPDeferredHolder<EntityType<Ovomorph>> NETHER_OVOMORPH = AVPEntityTypes.register(
        "nether_ovomorph",
        EntityType.Builder.of(Ovomorph::new, AVPEntityTypes.OVOMORPH_CATEGORY)
            .sized(0.65f, 0.8f)
    );

    public static final AVPDeferredHolder<EntityType<Praetorian>> NETHER_PRAETORIAN = AVPEntityTypes.register(
        "nether_praetorian",
        EntityType.Builder.of(Praetorian::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(0.98f, 3.98f)
    );

    public static final AVPDeferredHolder<EntityType<Predalien>> NETHER_PREDALIEN = AVPEntityTypes.register(
        "nether_predalien",
        EntityType.Builder.of(Predalien::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(0.98f, 3.98f)
    );

    public static final AVPDeferredHolder<EntityType<Prowler>> NETHER_PROWLER = AVPEntityTypes.register(
        "nether_prowler",
        EntityType.Builder.of(Prowler::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(0.8f, 0.98f)
    );

    public static final AVPDeferredHolder<EntityType<Queen>> NETHER_QUEEN = AVPEntityTypes.register(
        "nether_queen",
        EntityType.Builder.of(Queen::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(1.98f, 3.98f)
    );

    public static final AVPDeferredHolder<EntityType<Runner>> NETHER_RUNNER = AVPEntityTypes.register(
        "nether_runner",
        EntityType.Builder.of(Runner::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(0.8f, 0.98f)
    );

    public static final AVPDeferredHolder<EntityType<Spitter>> NETHER_SPITTER = AVPEntityTypes.register(
        "nether_spitter",
        EntityType.Builder.of(Spitter::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(0.8f, 2.5f)
    );

    public static final AVPDeferredHolder<EntityType<Warrior>> NETHER_WARRIOR = AVPEntityTypes.register(
        "nether_warrior",
        EntityType.Builder.of(Warrior::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(0.8f, 1.98f)
    );

    public static final AVPDeferredHolder<EntityType<Ovipositor>> OVIPOSITOR = AVPEntityTypes.register(
        "ovipositor",
        EntityType.Builder.of(Ovipositor::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(5.0f, 3.25f)
    );

    public static final AVPDeferredHolder<EntityType<Ovomorph>> OVOMORPH = AVPEntityTypes.register(
        "ovomorph",
        EntityType.Builder.of(Ovomorph::new, AVPEntityTypes.OVOMORPH_CATEGORY)
            .sized(0.65f, 0.8f)
    );

    public static final AVPDeferredHolder<EntityType<Praetorian>> PRAETORIAN = AVPEntityTypes.register(
        "praetorian",
        EntityType.Builder.of(Praetorian::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(0.98f, 3.98f)
    );

    public static final AVPDeferredHolder<EntityType<Predalien>> PREDALIEN = AVPEntityTypes.register(
        "predalien",
        EntityType.Builder.of(Predalien::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(0.98f, 3.98f)
    );

    public static final AVPDeferredHolder<EntityType<Prowler>> PROWLER = AVPEntityTypes.register(
        "prowler",
        EntityType.Builder.of(Prowler::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(0.8f, 0.98f)
    );

    public static final AVPDeferredHolder<EntityType<Queen>> QUEEN = AVPEntityTypes.register(
        "queen",
        EntityType.Builder.of(Queen::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(1.98f, 3.98f)
    );

    public static final AVPDeferredHolder<EntityType<Adolescent>> ROYAL_ABERRANT_ADOLESCENT = AVPEntityTypes.register(
        "royal_aberrant_adolescent",
        EntityType.Builder.of(Adolescent::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(0.5f, 0.5f)
    );

    public static final AVPDeferredHolder<EntityType<Chestburster>> ROYAL_ABERRANT_CHESTBURSTER = AVPEntityTypes.register(
        "royal_aberrant_chestburster",
        EntityType.Builder.of(Chestburster::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(0.35f, 0.35f)
    );

    public static final AVPDeferredHolder<EntityType<Facehugger>> ROYAL_ABERRANT_FACEHUGGER = AVPEntityTypes.register(
        "royal_aberrant_facehugger",
        EntityType.Builder.of(Facehugger::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(0.8f, 0.25f)
    );

    public static final AVPDeferredHolder<EntityType<Ovomorph>> ROYAL_ABERRANT_OVOMORPH = AVPEntityTypes.register(
        "royal_aberrant_ovomorph",
        EntityType.Builder.of(Ovomorph::new, AVPEntityTypes.OVOMORPH_CATEGORY)
            .sized(0.65f, 0.8f)
    );

    public static final AVPDeferredHolder<EntityType<Adolescent>> ROYAL_ADOLESCENT = AVPEntityTypes.register(
        "royal_adolescent",
        EntityType.Builder.of(Adolescent::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(0.5f, 0.5f)
    );

    public static final AVPDeferredHolder<EntityType<Chestburster>> ROYAL_CHESTBURSTER = AVPEntityTypes.register(
        "royal_chestburster",
        EntityType.Builder.<Chestburster>of(Chestburster::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(0.35f, 0.35f)
    );

    public static final AVPDeferredHolder<EntityType<Facehugger>> ROYAL_FACEHUGGER = AVPEntityTypes.register(
        "royal_facehugger",
        EntityType.Builder.of(Facehugger::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(0.8f, 0.25f)
    );

    public static final AVPDeferredHolder<EntityType<Ovomorph>> ROYAL_OVOMORPH = AVPEntityTypes.register(
        "royal_ovomorph",
        EntityType.Builder.of(Ovomorph::new, AVPEntityTypes.OVOMORPH_CATEGORY)
            .sized(0.65f, 0.8f)
    );

    public static final AVPDeferredHolder<EntityType<Adolescent>> ROYAL_NETHER_ADOLESCENT = AVPEntityTypes.register(
        "royal_nether_adolescent",
        EntityType.Builder.of(Adolescent::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(0.5f, 0.5f)
    );

    public static final AVPDeferredHolder<EntityType<Chestburster>> ROYAL_NETHER_CHESTBURSTER = AVPEntityTypes.register(
        "royal_nether_chestburster",
        EntityType.Builder.of(Chestburster::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(0.35f, 0.35f)
    );

    public static final AVPDeferredHolder<EntityType<Facehugger>> ROYAL_NETHER_FACEHUGGER = AVPEntityTypes.register(
        "royal_nether_facehugger",
        EntityType.Builder.of(Facehugger::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(0.8f, 0.25f)
    );

    public static final AVPDeferredHolder<EntityType<Ovomorph>> ROYAL_NETHER_OVOMORPH = AVPEntityTypes.register(
        "royal_nether_ovomorph",
        EntityType.Builder.of(Ovomorph::new, AVPEntityTypes.OVOMORPH_CATEGORY)
            .sized(0.65f, 0.8f)
    );

    public static final AVPDeferredHolder<EntityType<Runner>> RUNNER = AVPEntityTypes.register(
        "runner",
        EntityType.Builder.of(Runner::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(0.8f, 0.98f)
    );

    public static final AVPDeferredHolder<EntityType<Spitter>> SPITTER = AVPEntityTypes.register(
        "spitter",
        EntityType.Builder.of(Spitter::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(0.8f, 2.5f)
    );

    public static final AVPDeferredHolder<EntityType<Warrior>> WARRIOR = AVPEntityTypes.register(
        "warrior",
        EntityType.Builder.of(Warrior::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(0.8f, 1.98f)
    );

    public static void initialize() {
        Services.REGISTRY.registerEntityAttributes(ABERRANT_ADOLESCENT, Adolescent::createAdolescentAttributes);
        Services.REGISTRY.registerEntityAttributes(ABERRANT_BOILER, Boiler::createBoilerAttributes);
        Services.REGISTRY.registerEntityAttributes(ABERRANT_CHESTBURSTER, Chestburster::createChestbursterAttributes);
        Services.REGISTRY.registerEntityAttributes(ABERRANT_CRUSHER, Crusher::createCrusherAttributes);
        Services.REGISTRY.registerEntityAttributes(ABERRANT_DRONE, Drone::createDroneAttributes);
        Services.REGISTRY.registerEntityAttributes(ABERRANT_FACEHUGGER, Facehugger::createFacehuggerAttributes);
        Services.REGISTRY.registerEntityAttributes(ABERRANT_OVOMORPH, Ovomorph::createOvomorphAttributes);
        Services.REGISTRY.registerEntityAttributes(ABERRANT_PRAETORIAN, Praetorian::createPraetorianAttributes);
        Services.REGISTRY.registerEntityAttributes(ABERRANT_PREDALIEN, Predalien::createPredalienAttributes);
        Services.REGISTRY.registerEntityAttributes(ABERRANT_PROWLER, Prowler::createProwlerAttributes);
        Services.REGISTRY.registerEntityAttributes(ABERRANT_QUEEN, Queen::createQueenAttributes);
        Services.REGISTRY.registerEntityAttributes(ABERRANT_RUNNER, Runner::createRunnerAttributes);
        Services.REGISTRY.registerEntityAttributes(ABERRANT_SPITTER, Spitter::createSpitterAttributes);
        Services.REGISTRY.registerEntityAttributes(ABERRANT_WARRIOR, Warrior::createWarriorAttributes);
        Services.REGISTRY.registerEntityAttributes(CHESTBURSTER, Chestburster::createChestbursterAttributes);
        Services.REGISTRY.registerEntityAttributes(ADOLESCENT, Adolescent::createAdolescentAttributes);
        Services.REGISTRY.registerEntityAttributes(BOILER, Boiler::createBoilerAttributes);
        Services.REGISTRY.registerEntityAttributes(CRUSHER, Crusher::createCrusherAttributes);
        Services.REGISTRY.registerEntityAttributes(DRONE, Drone::createDroneAttributes);
        Services.REGISTRY.registerEntityAttributes(FACEHUGGER, Facehugger::createFacehuggerAttributes);
        Services.REGISTRY.registerEntityAttributes(IRRADIATED_CRUSHER, Crusher::createCrusherAttributes);
        Services.REGISTRY.registerEntityAttributes(IRRADIATED_DRONE, Drone::createDroneAttributes);
        Services.REGISTRY.registerEntityAttributes(IRRADIATED_PRAETORIAN, Praetorian::createPraetorianAttributes);
        Services.REGISTRY.registerEntityAttributes(IRRADIATED_PREDALIEN, Predalien::createPredalienAttributes);
        Services.REGISTRY.registerEntityAttributes(IRRADIATED_PROWLER, Prowler::createProwlerAttributes);
        Services.REGISTRY.registerEntityAttributes(IRRADIATED_QUEEN, Queen::createQueenAttributes);
        Services.REGISTRY.registerEntityAttributes(IRRADIATED_RUNNER, Runner::createRunnerAttributes);
        Services.REGISTRY.registerEntityAttributes(IRRADIATED_WARRIOR, Warrior::createWarriorAttributes);
        Services.REGISTRY.registerEntityAttributes(NETHER_ADOLESCENT, Adolescent::createAdolescentAttributes);
        Services.REGISTRY.registerEntityAttributes(NETHER_BOILER, Boiler::createBoilerAttributes);
        Services.REGISTRY.registerEntityAttributes(NETHER_CHESTBURSTER, Chestburster::createChestbursterAttributes);
        Services.REGISTRY.registerEntityAttributes(NETHER_CRUSHER, Crusher::createCrusherAttributes);
        Services.REGISTRY.registerEntityAttributes(NETHER_DRONE, Drone::createDroneAttributes);
        Services.REGISTRY.registerEntityAttributes(NETHER_FACEHUGGER, Facehugger::createFacehuggerAttributes);
        Services.REGISTRY.registerEntityAttributes(NETHER_OVOMORPH, Ovomorph::createOvomorphAttributes);
        Services.REGISTRY.registerEntityAttributes(NETHER_PRAETORIAN, Praetorian::createPraetorianAttributes);
        Services.REGISTRY.registerEntityAttributes(NETHER_PREDALIEN, Predalien::createPredalienAttributes);
        Services.REGISTRY.registerEntityAttributes(NETHER_PROWLER, Prowler::createProwlerAttributes);
        Services.REGISTRY.registerEntityAttributes(NETHER_QUEEN, Queen::createQueenAttributes);
        Services.REGISTRY.registerEntityAttributes(NETHER_RUNNER, Runner::createRunnerAttributes);
        Services.REGISTRY.registerEntityAttributes(NETHER_SPITTER, Spitter::createSpitterAttributes);
        Services.REGISTRY.registerEntityAttributes(NETHER_WARRIOR, Warrior::createWarriorAttributes);
        Services.REGISTRY.registerEntityAttributes(OVIPOSITOR, Ovipositor::createOvipositorAttributes);
        Services.REGISTRY.registerEntityAttributes(OVOMORPH, Ovomorph::createOvomorphAttributes);
        Services.REGISTRY.registerEntityAttributes(PRAETORIAN, Praetorian::createPraetorianAttributes);
        Services.REGISTRY.registerEntityAttributes(PREDALIEN, Predalien::createPredalienAttributes);
        Services.REGISTRY.registerEntityAttributes(PROWLER, Prowler::createProwlerAttributes);
        Services.REGISTRY.registerEntityAttributes(QUEEN, Queen::createQueenAttributes);
        Services.REGISTRY.registerEntityAttributes(ROYAL_ABERRANT_ADOLESCENT, Adolescent::createAdolescentAttributes);
        Services.REGISTRY.registerEntityAttributes(ROYAL_ABERRANT_CHESTBURSTER, Chestburster::createChestbursterAttributes);
        Services.REGISTRY.registerEntityAttributes(ROYAL_ABERRANT_FACEHUGGER, Facehugger::createFacehuggerAttributes);
        Services.REGISTRY.registerEntityAttributes(ROYAL_ABERRANT_OVOMORPH, Ovomorph::createOvomorphAttributes);
        Services.REGISTRY.registerEntityAttributes(ROYAL_ADOLESCENT, Adolescent::createAdolescentAttributes);
        Services.REGISTRY.registerEntityAttributes(ROYAL_CHESTBURSTER, Chestburster::createChestbursterAttributes);
        Services.REGISTRY.registerEntityAttributes(ROYAL_FACEHUGGER, Facehugger::createFacehuggerAttributes);
        Services.REGISTRY.registerEntityAttributes(ROYAL_NETHER_ADOLESCENT, Adolescent::createAdolescentAttributes);
        Services.REGISTRY.registerEntityAttributes(ROYAL_NETHER_CHESTBURSTER, Chestburster::createChestbursterAttributes);
        Services.REGISTRY.registerEntityAttributes(ROYAL_NETHER_FACEHUGGER, Facehugger::createFacehuggerAttributes);
        Services.REGISTRY.registerEntityAttributes(ROYAL_NETHER_OVOMORPH, Ovomorph::createOvomorphAttributes);
        Services.REGISTRY.registerEntityAttributes(ROYAL_OVOMORPH, Ovomorph::createOvomorphAttributes);
        Services.REGISTRY.registerEntityAttributes(RUNNER, Runner::createRunnerAttributes);
        Services.REGISTRY.registerEntityAttributes(SPITTER, Spitter::createSpitterAttributes);
        Services.REGISTRY.registerEntityAttributes(WARRIOR, Warrior::createWarriorAttributes);
    }
}
