package com.blib.api.common.dismemberment.v1.builtin;

import net.minecraft.world.entity.EntityType;

/**
 * Registers limb definitions for the vanilla mobs BLib supports out of the box. Called once during BLib's mod init so
 * consumers don't need to enumerate vanilla mobs themselves — registering an alien xenomorph or a custom mob is purely
 * additive on top of these defaults.
 */
public final class BuiltInLimbDefinitions {

    private BuiltInLimbDefinitions() {}

    public static void register() {
        registerHumanoids();
        registerVillagers();
        registerArmless();
        registerSpiders();
        registerQuadrupeds();
        registerEquines();
        registerCanidsAndChickens();
    }

    private static void registerHumanoids() {
        HumanoidLimbs.register(EntityType.ZOMBIE, "zombie");
        HumanoidLimbs.register(EntityType.ZOMBIE_VILLAGER, "zombie_villager");
        HumanoidLimbs.register(EntityType.HUSK, "husk");
        HumanoidLimbs.register(EntityType.DROWNED, "drowned");
        HumanoidLimbs.register(EntityType.SKELETON, "skeleton");
        HumanoidLimbs.register(EntityType.STRAY, "stray");
        HumanoidLimbs.register(EntityType.WITHER_SKELETON, "wither_skeleton");
        HumanoidLimbs.register(EntityType.PIGLIN, "piglin");
        HumanoidLimbs.register(EntityType.PIGLIN_BRUTE, "piglin_brute");
        HumanoidLimbs.register(EntityType.ZOMBIFIED_PIGLIN, "zombified_piglin");
        HumanoidLimbs.register(EntityType.PILLAGER, "pillager");
        HumanoidLimbs.register(EntityType.VINDICATOR, "vindicator");
        HumanoidLimbs.register(EntityType.EVOKER, "evoker");
        HumanoidLimbs.register(EntityType.ILLUSIONER, "illusioner");
        HumanoidLimbs.register(EntityType.ENDERMAN, "enderman");
        HumanoidLimbs.register(EntityType.BOGGED, "bogged");

        // IronGolemModel exposes head/body/arms/legs as direct children of the model root, so HumanoidLimbs works out
        // of the box. WardenModel exposes the same part names but nested under bone/body, so it relies on the custom
        // resolver registered in BuiltInModelPartResolvers.
        HumanoidLimbs.register(EntityType.IRON_GOLEM, "iron_golem");
        HumanoidLimbs.register(EntityType.WARDEN, "warden");
    }

    private static void registerVillagers() {
        VillagerLimbs.register(EntityType.VILLAGER, "villager");
        VillagerLimbs.register(EntityType.WITCH, "witch");
        VillagerLimbs.register(EntityType.WANDERING_TRADER, "wandering_trader");
    }

    private static void registerArmless() {
        SnowGolemLimbs.register();
        AllayLimbs.register();
    }

    private static void registerSpiders() {
        SpiderLimbs.register(EntityType.SPIDER, "spider");
        SpiderLimbs.register(EntityType.CAVE_SPIDER, "cave_spider");
    }

    private static void registerQuadrupeds() {
        QuadrupedLimbs.register(EntityType.COW, "cow");
        QuadrupedLimbs.register(EntityType.PIG, "pig");
        QuadrupedLimbs.register(EntityType.CREEPER, "creeper");
        QuadrupedLimbs.register(EntityType.SHEEP, "sheep");
        QuadrupedLimbs.register(EntityType.MOOSHROOM, "mooshroom");
        QuadrupedLimbs.register(EntityType.PANDA, "panda");
        QuadrupedLimbs.register(EntityType.POLAR_BEAR, "polar_bear");
        QuadrupedLimbs.register(EntityType.GOAT, "goat");
        QuadrupedLimbs.register(EntityType.TURTLE, "turtle");
    }

    private static void registerEquines() {
        HorseLimbs.register(EntityType.HORSE, "horse");
        HorseLimbs.register(EntityType.DONKEY, "donkey");
        HorseLimbs.register(EntityType.MULE, "mule");
        HorseLimbs.register(EntityType.SKELETON_HORSE, "skeleton_horse");
        HorseLimbs.register(EntityType.ZOMBIE_HORSE, "zombie_horse");
    }

    private static void registerCanidsAndChickens() {
        WolfLimbs.register();
        FoxLimbs.register();
        ChickenLimbs.register();
        RabbitLimbs.register();
    }
}
