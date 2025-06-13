package com.avp.common.registry.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;

import com.avp.AVPResources;
import com.avp.common.registry.AVPDeferredHolder;
import com.avp.service.Services;

public class AVPSoundEvents {

    public static final AVPDeferredHolder<SoundEvent> BLOCK_ACID_BURN = register("block.acid.burn");

    public static final AVPDeferredHolder<SoundEvent> BLOCK_RESIN_SPREAD = register("block.resin.spread");

    public static final AVPDeferredHolder<SoundEvent> ENTITY_OVOMORPH_HATCH = register("entity.ovomorph.hatch");

    public static final AVPDeferredHolder<SoundEvent> ENTITY_OVOMORPH_LAID = register("entity.ovomorph.laid");

    public static final AVPDeferredHolder<SoundEvent> ENTITY_OVOMORPH_ROOT = register("entity.ovomorph.root");

    public static final AVPDeferredHolder<SoundEvent> ENTITY_OVOMORPH_SHEAR = register("entity.ovomorph.shear");

    public static final AVPDeferredHolder<SoundEvent> ENTITY_QUEEN_ARM_ATTACK = register("entity.queen.arm_attack");

    public static final AVPDeferredHolder<SoundEvent> ENTITY_QUEEN_BACK_HAND_ATTACK = register("entity.queen.back_hand_attack");

    public static final AVPDeferredHolder<SoundEvent> ENTITY_QUEEN_DEATH = register("entity.queen.death");

    public static final AVPDeferredHolder<SoundEvent> ENTITY_QUEEN_HURT = register("entity.queen.hurt");

    public static final AVPDeferredHolder<SoundEvent> ENTITY_QUEEN_IDLE = register("entity.queen.idle");

    public static final AVPDeferredHolder<SoundEvent> ENTITY_QUEEN_RAM_ATTACK = register("entity.queen.ram_attack");

    public static final AVPDeferredHolder<SoundEvent> ENTITY_QUEEN_SCREAM = register("entity.queen.scream");

    public static final AVPDeferredHolder<SoundEvent> ENTITY_QUEEN_STEP_THUMP = register("entity.queen.step_thump");

    public static final AVPDeferredHolder<SoundEvent> ENTITY_QUEEN_STEP_THUMP_ROCK = register("entity.queen.step_thump_rock");

    public static final AVPDeferredHolder<SoundEvent> ENTITY_QUEEN_TAIL_ATTACK = register("entity.queen.tail_attack");

    public static final AVPDeferredHolder<SoundEvent> ENTITY_XENOMORPH_ATTACK = register("entity.xenomorph.attack");

    public static final AVPDeferredHolder<SoundEvent> ENTITY_XENOMORPH_DEATH = register("entity.xenomorph.death");

    public static final AVPDeferredHolder<SoundEvent> ENTITY_XENOMORPH_HISS = register("entity.xenomorph.hiss");

    public static final AVPDeferredHolder<SoundEvent> ENTITY_XENOMORPH_HURT = register("entity.xenomorph.hurt");

    public static final AVPDeferredHolder<SoundEvent> ENTITY_XENOMORPH_IDLE = register("entity.xenomorph.idle");

    public static final AVPDeferredHolder<SoundEvent> ENTITY_XENOMORPH_LUNGE = register("entity.xenomorph.lunge");

    public static final AVPDeferredHolder<SoundEvent> ITEM_ARMOR_EQUIP_CHITIN = register("item.armor.equip_chitin");

    public static final AVPDeferredHolder<SoundEvent> ITEM_ARMOR_EQUIP_MK50 = register("item.armor.equip_mk50");

    public static final AVPDeferredHolder<SoundEvent> ITEM_ARMOR_EQUIP_PRESSURE = register("item.armor.equip_pressure");

    public static final AVPDeferredHolder<SoundEvent> ITEM_ARMOR_EQUIP_STEEL = register("item.armor.equip_steel");

    public static final AVPDeferredHolder<SoundEvent> ITEM_ARMOR_EQUIP_TACTICAL = register("item.armor.equip_tactical");

    public static final AVPDeferredHolder<SoundEvent> ITEM_ARMOR_EQUIP_TITANIUM = register("item.armor.equip_titanium");

    public static final AVPDeferredHolder<SoundEvent> ITEM_ARMOR_EQUIP_VERITANIUM = register("item.armor.equip_veritanium");

    public static final AVPDeferredHolder<SoundEvent> WEAPON_FLAMETHROWER_SEVASTOPOL_RELOAD_FINISH = register(
        "item.weapon.flamethrower_sevastopol.reload_finish"
    );

    public static final AVPDeferredHolder<SoundEvent> WEAPON_FLAMETHROWER_SEVASTOPOL_RELOAD_START = register(
        "item.weapon.flamethrower_sevastopol.reload_start"
    );

    public static final AVPDeferredHolder<SoundEvent> WEAPON_FLAMETHROWER_SEVASTOPOL_SHOOT = register(
        "item.weapon.flamethrower_sevastopol.shoot"
    );

    public static final AVPDeferredHolder<SoundEvent> WEAPON_FX_RICOCHET_DIRT = register("item.weapon.fx.ricochet.dirt");

    public static final AVPDeferredHolder<SoundEvent> WEAPON_FX_RICOCHET_GENERIC = register("item.weapon.fx.ricochet.generic");

    public static final AVPDeferredHolder<SoundEvent> WEAPON_FX_RICOCHET_GLASS = register("item.weapon.fx.ricochet.glass");

    public static final AVPDeferredHolder<SoundEvent> WEAPON_FX_RICOCHET_METAL = register("item.weapon.fx.ricochet.metal");

    public static final AVPDeferredHolder<SoundEvent> WEAPON_GENERIC_RELOAD = register("item.weapon.generic.reload");

    public static final AVPDeferredHolder<SoundEvent> WEAPON_GENERIC_SHOOT = register("item.weapon.generic.shoot");

    public static final AVPDeferredHolder<SoundEvent> WEAPON_GENERIC_SHOOT_FAIL = register("item.weapon.generic.shoot_fail");

    public static final AVPDeferredHolder<SoundEvent> WEAPON_M37_12_SHOTGUN_SHOOT = register("item.weapon.m37_12_shotgun.shoot");

    public static final AVPDeferredHolder<SoundEvent> WEAPON_M41A_PULSE_RIFLE_SHOOT = register("item.weapon.m41a_pulse_rifle.shoot");

    public static final AVPDeferredHolder<SoundEvent> WEAPON_M42A3_SNIPER_RIFLE_SHOOT = register("item.weapon.m42a3_sniper_rifle.shoot");

    public static final AVPDeferredHolder<SoundEvent> WEAPON_M4RA_BATTLE_RIFLE_SHOOT = register("item.weapon.m4ra_battle_rifle.shoot");

    public static final AVPDeferredHolder<SoundEvent> WEAPON_M56_SMARTGUN_SHOOT = register("item.weapon.m56_smartgun.shoot");

    public static final AVPDeferredHolder<SoundEvent> WEAPON_M6B_ROCKET_LAUNCHER_RELOAD_FINISH = register(
        "item.weapon.m6b_rocket_launcher.reload_finish"
    );

    public static final AVPDeferredHolder<SoundEvent> WEAPON_M6B_ROCKET_LAUNCHER_RELOAD_START = register(
        "item.weapon.m6b_rocket_launcher.reload_start"
    );

    public static final AVPDeferredHolder<SoundEvent> WEAPON_M6B_ROCKET_LAUNCHER_SHOOT = register("item.weapon.m6b_rocket_launcher.shoot");

    public static final AVPDeferredHolder<SoundEvent> WEAPON_M88_MOD_4_COMBAT_PISTOL_RELOAD = register(
        "item.weapon.m88_mod_4_combat_pistol.reload"
    );

    public static final AVPDeferredHolder<SoundEvent> WEAPON_M88_MOD_4_COMBAT_PISTOL_SHOOT = register(
        "item.weapon.m88_mod_4_combat_pistol.shoot"
    );

    public static final AVPDeferredHolder<SoundEvent> WEAPON_OLD_PAINLESS_SHOOT = register("item.weapon.old_painless.shoot");

    public static final AVPDeferredHolder<SoundEvent> WEAPON_OLD_PAINLESS_SHOOT_FINISH = register("item.weapon.old_painless.shoot_finish");

    public static final AVPDeferredHolder<SoundEvent> WEAPON_OLD_PAINLESS_SHOOT_SPINNING = register(
        "item.weapon.old_painless.shoot_spinning"
    );

    public static final AVPDeferredHolder<SoundEvent> WEAPON_OLD_PAINLESS_SHOOT_START = register("item.weapon.old_painless.shoot_start");

    public static final AVPDeferredHolder<SoundEvent> WEAPON_ZX_76_SHOTGUN_SHOOT = register("item.weapon.zx_76_shotgun.shoot");

    public static final AVPDeferredHolder<SoundEvent> JUKEBOX_SOUNDS_ALIEN_MUSIC_1 = register("jukebox_sounds.alien_music_1");

    public static final AVPDeferredHolder<SoundEvent> JUKEBOX_SOUNDS_PREDATOR_MUSIC_1 = register("jukebox_sounds.predator_music_1");

    private static AVPDeferredHolder<SoundEvent> register(String id) {
        return Services.REGISTRY.register(
            BuiltInRegistries.SOUND_EVENT,
            id,
            () -> SoundEvent.createVariableRangeEvent(AVPResources.location(id))
        );
    }

    public static void initialize() {}
}
