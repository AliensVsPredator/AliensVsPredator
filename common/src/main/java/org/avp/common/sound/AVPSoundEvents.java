package org.avp.common.sound;

import net.minecraft.sounds.SoundEvent;

import org.avp.common.AVPResources;
import org.avp.common.service.Services;
import org.avp.api.GameObject;

/**
 * @author Boston Vanseghi
 */
public class AVPSoundEvents {

    private static GameObject<SoundEvent> register(String registryName) {
        var resourceLocation = AVPResources.location(registryName);
        return Services.SOUND_EVENT_REGISTRY.register(
            registryName,
            () -> SoundEvent.createVariableRangeEvent(resourceLocation)
        );
    }

    public static final GameObject<SoundEvent> ENTITY_CHESTBURSTER_DEATH;

    public static final GameObject<SoundEvent> ENTITY_CHESTBURSTER_HURT;

    public static final GameObject<SoundEvent> ENTITY_PRAETORIAN_AMBIENT;

    public static final GameObject<SoundEvent> ENTITY_PRAETORIAN_DEATH;

    public static final GameObject<SoundEvent> ENTITY_PRAETORIAN_HURT;

    public static final GameObject<SoundEvent> ENTITY_QUEEN_AMBIENT;

    public static final GameObject<SoundEvent> ENTITY_QUEEN_AMBIENT_LOOP;

    public static final GameObject<SoundEvent> ENTITY_QUEEN_DEATH;

    public static final GameObject<SoundEvent> ENTITY_QUEEN_HURT;

    public static final GameObject<SoundEvent> ENTITY_XENOMORPH_AMBIENT;

    public static final GameObject<SoundEvent> ENTITY_XENOMORPH_ATTACK;

    public static final GameObject<SoundEvent> ENTITY_XENOMORPH_DEATH;

    public static final GameObject<SoundEvent> ENTITY_XENOMORPH_EAT;

    public static final GameObject<SoundEvent> ENTITY_XENOMORPH_HURT;

    public static final GameObject<SoundEvent> ITEM_WEAPON_AK_47_SHOOT;

    public static final GameObject<SoundEvent> ITEM_WEAPON_FLAMETHROWER_RELOAD_FINISH;

    public static final GameObject<SoundEvent> ITEM_WEAPON_FLAMETHROWER_RELOAD_START;

    public static final GameObject<SoundEvent> ITEM_WEAPON_FLAMETHROWER_SHOOT;

    public static final GameObject<SoundEvent> ITEM_WEAPON_FX_RICOCHET_DIRT;

    public static final GameObject<SoundEvent> ITEM_WEAPON_FX_RICOCHET_GENERIC;

    public static final GameObject<SoundEvent> ITEM_WEAPON_FX_RICOCHET_GLASS;

    public static final GameObject<SoundEvent> ITEM_WEAPON_FX_RICOCHET_METAL;

    public static final GameObject<SoundEvent> ITEM_WEAPON_GENERIC_RELOAD;

    public static final GameObject<SoundEvent> ITEM_WEAPON_GENERIC_SHOOT;

    public static final GameObject<SoundEvent> ITEM_WEAPON_GENERIC_SHOOT_FAIL;

    public static final GameObject<SoundEvent> ITEM_WEAPON_PULSE_RIFLE_SHOOT;

    public static final GameObject<SoundEvent> ITEM_WEAPON_SHOTGUN_SHOOT;

    public static final GameObject<SoundEvent> ITEM_WEAPON_SNIPER_RIFLE_SHOOT;

    public static void forceInitialization() {
        // This method doesn't need to do anything
    }

    private AVPSoundEvents() {}

    static {
        ENTITY_CHESTBURSTER_DEATH = register("entity.chestburster.death");
        ENTITY_CHESTBURSTER_HURT = register("entity.chestburster.hurt");

        ENTITY_PRAETORIAN_AMBIENT = register("entity.praetorian.ambient");
        ENTITY_PRAETORIAN_DEATH = register("entity.praetorian.death");
        ENTITY_PRAETORIAN_HURT = register("entity.praetorian.hurt");

        ENTITY_QUEEN_AMBIENT = register("entity.queen.ambient");
        ENTITY_QUEEN_AMBIENT_LOOP = register("entity.queen.ambient.loop");
        ENTITY_QUEEN_DEATH = register("entity.queen.death");
        ENTITY_QUEEN_HURT = register("entity.queen.hurt");

        ENTITY_XENOMORPH_AMBIENT = register("entity.xenomorph.ambient");
        ENTITY_XENOMORPH_ATTACK = register("entity.xenomorph.attack");
        ENTITY_XENOMORPH_DEATH = register("entity.xenomorph.death");
        ENTITY_XENOMORPH_EAT = register("entity.xenomorph.eat");
        ENTITY_XENOMORPH_HURT = register("entity.xenomorph.hurt");

        ITEM_WEAPON_AK_47_SHOOT = register("item.weapon.ak_47.shoot");

        ITEM_WEAPON_FLAMETHROWER_RELOAD_FINISH = register("item.weapon.flamethrower.reload_finish");
        ITEM_WEAPON_FLAMETHROWER_RELOAD_START = register("item.weapon.flamethrower.reload_start");
        ITEM_WEAPON_FLAMETHROWER_SHOOT = register("item.weapon.flamethrower.shoot");

        ITEM_WEAPON_FX_RICOCHET_DIRT = register("item.weapon.fx.ricochet.dirt");
        ITEM_WEAPON_FX_RICOCHET_GENERIC = register("item.weapon.fx.ricochet.generic");
        ITEM_WEAPON_FX_RICOCHET_GLASS = register("item.weapon.fx.ricochet.glass");
        ITEM_WEAPON_FX_RICOCHET_METAL = register("item.weapon.fx.ricochet.metal");

        ITEM_WEAPON_GENERIC_RELOAD = register("item.weapon.generic.reload");
        ITEM_WEAPON_GENERIC_SHOOT = register("item.weapon.generic.shoot");
        ITEM_WEAPON_GENERIC_SHOOT_FAIL = register("item.weapon.generic.shoot.fail");

        ITEM_WEAPON_PULSE_RIFLE_SHOOT = register("item.weapon.pulse_rifle.shoot");

        ITEM_WEAPON_SHOTGUN_SHOOT = register("item.weapon.shotgun.shoot");

        ITEM_WEAPON_SNIPER_RIFLE_SHOOT = register("item.weapon.sniper_rifle.shoot");
    }
}
