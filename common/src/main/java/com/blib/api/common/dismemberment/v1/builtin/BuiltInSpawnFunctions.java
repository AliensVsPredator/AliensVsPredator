package com.blib.api.common.dismemberment.v1.builtin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.function.Function;

import com.blib.api.common.dismemberment.v1.SpawnFunctionRegistry;
import com.blib.mod.BLib;

/**
 * Registers spawn-offset functions for BLib's built-in vanilla-mob limbs. Structural fields (id/category/fatal) and
 * visual fields (root bone / render offsets / scale / rotation) live in {@code data/minecraft/blib_limbs/*.json} and
 * {@code assets/minecraft/blib_limb_visuals/*.json} respectively; the function-typed spawn offset can't be serialized,
 * so it lives here and is keyed by limb id.
 * <p>
 * Spawn-offset variety in the original Java registrations covers exactly two patterns:
 * <ul>
 * <li>{@link #EYE_HEIGHT} — heads anchor at {@code entity.getEyeHeight()}.</li>
 * <li>{@code bbHeight × fraction} — every other body part, with fractions in {@code {0.3, 0.4, 0.5, 0.6, 0.7,
 * 0.75}}.</li>
 * </ul>
 * Called once at BLib mod init from {@code BLib.initialize()}.
 */
public final class BuiltInSpawnFunctions {

    private static final Function<LivingEntity, Vec3> EYE_HEIGHT =
        entity -> new Vec3(0.0, entity.getEyeHeight(), 0.0);

    private BuiltInSpawnFunctions() {}

    public static void register() {
        registerHumanoids(
            "zombie",
            "zombie_villager",
            "husk",
            "drowned",
            "skeleton",
            "stray",
            "wither_skeleton",
            "piglin",
            "piglin_brute",
            "zombified_piglin",
            "pillager",
            "vindicator",
            "evoker",
            "illusioner",
            "enderman",
            "bogged",
            "iron_golem",
            "warden"
        );

        registerVillagers("villager", "witch", "wandering_trader");

        registerQuadrupeds("cow", "pig", "creeper", "sheep", "mooshroom", "panda", "polar_bear", "goat", "turtle");

        registerHorses("horse", "donkey", "mule", "skeleton_horse", "zombie_horse");

        registerSpiders("spider", "cave_spider");

        registerWolfOrFox("wolf");
        registerWolfOrFox("fox");
        registerChicken("chicken");
        registerRabbit("rabbit");
        registerSnowGolem("snow_golem");
        registerAllay("allay");
    }

    // === helpers ===

    private static void eye(String limbPath) {
        SpawnFunctionRegistry.register(BLib.MOD.resources().createLocation(limbPath), EYE_HEIGHT);
    }

    private static void bb(String limbPath, double fraction) {
        SpawnFunctionRegistry.register(
            BLib.MOD.resources().createLocation(limbPath),
            entity -> new Vec3(0.0, entity.getBbHeight() * fraction, 0.0)
        );
    }

    // === per-shape registrations ===

    private static void registerHumanoids(String... prefixes) {
        for (var p : prefixes) {
            eye(p + "_head");
            bb(p + "_body", 0.5);
            bb(p + "_right_arm", 0.75);
            bb(p + "_left_arm", 0.75);
            bb(p + "_right_leg", 0.3);
            bb(p + "_left_leg", 0.3);
        }
    }

    private static void registerVillagers(String... prefixes) {
        for (var p : prefixes) {
            eye(p + "_head");
            bb(p + "_body", 0.5);
            bb(p + "_arms", 0.75);
            bb(p + "_right_leg", 0.3);
            bb(p + "_left_leg", 0.3);
        }
    }

    private static void registerQuadrupeds(String... prefixes) {
        for (var p : prefixes) {
            eye(p + "_head");
            bb(p + "_body", 0.5);
            bb(p + "_right_hind_leg", 0.3);
            bb(p + "_left_hind_leg", 0.3);
            bb(p + "_right_front_leg", 0.3);
            bb(p + "_left_front_leg", 0.3);
        }
    }

    private static void registerHorses(String... prefixes) {
        for (var p : prefixes) {
            eye(p + "_head");
            bb(p + "_body", 0.5);
            bb(p + "_right_hind_leg", 0.5);
            bb(p + "_left_hind_leg", 0.5);
            bb(p + "_right_front_leg", 0.5);
            bb(p + "_left_front_leg", 0.5);
            bb(p + "_tail", 0.5);
        }
    }

    private static void registerSpiders(String... prefixes) {
        for (var p : prefixes) {
            eye(p + "_head");
            bb(p + "_right_hind_leg", 0.5);
            bb(p + "_left_hind_leg", 0.5);
            bb(p + "_right_middle_hind_leg", 0.5);
            bb(p + "_left_middle_hind_leg", 0.5);
            bb(p + "_right_middle_front_leg", 0.5);
            bb(p + "_left_middle_front_leg", 0.5);
            bb(p + "_right_front_leg", 0.5);
            bb(p + "_left_front_leg", 0.5);
        }
    }

    private static void registerWolfOrFox(String prefix) {
        eye(prefix + "_head");
        bb(prefix + "_body", 0.5);
        bb(prefix + "_right_hind_leg", 0.4);
        bb(prefix + "_left_hind_leg", 0.4);
        bb(prefix + "_right_front_leg", 0.4);
        bb(prefix + "_left_front_leg", 0.4);
        bb(prefix + "_tail", 0.5);
    }

    private static void registerChicken(String prefix) {
        eye(prefix + "_head");
        bb(prefix + "_body", 0.5);
        bb(prefix + "_right_leg", 0.3);
        bb(prefix + "_left_leg", 0.3);
        bb(prefix + "_right_wing", 0.6);
        bb(prefix + "_left_wing", 0.6);
    }

    private static void registerRabbit(String prefix) {
        eye(prefix + "_head");
        bb(prefix + "_body", 0.5);
        bb(prefix + "_right_haunch", 0.3);
        bb(prefix + "_left_haunch", 0.3);
        bb(prefix + "_right_front_leg", 0.3);
        bb(prefix + "_left_front_leg", 0.3);
        bb(prefix + "_tail", 0.5);
    }

    private static void registerSnowGolem(String prefix) {
        eye(prefix + "_head");
        bb(prefix + "_upper_body", 0.5);
        bb(prefix + "_right_arm", 0.75);
        bb(prefix + "_left_arm", 0.75);
    }

    private static void registerAllay(String prefix) {
        eye(prefix + "_head");
        bb(prefix + "_body", 0.5);
        bb(prefix + "_right_arm", 0.7);
        bb(prefix + "_left_arm", 0.7);
    }
}
