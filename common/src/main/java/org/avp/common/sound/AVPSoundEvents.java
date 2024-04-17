package org.avp.common.sound;

import net.minecraft.sounds.SoundEvent;

import org.avp.api.GameObject;
import org.avp.common.AVPResources;
import org.avp.common.service.Services;

public class AVPSoundEvents {

    public static final GameObject<SoundEvent> ENTITY_CHESTBURSTER_DEATH = register("entity.chestburster.death");

    public static final GameObject<SoundEvent> ENTITY_CHESTBURSTER_HURT = register("entity.chestburster.hurt");

    public static final GameObject<SoundEvent> ENTITY_PRAETORIAN_AMBIENT = register("entity.praetorian.ambient");

    public static final GameObject<SoundEvent> ENTITY_PRAETORIAN_DEATH = register("entity.praetorian.death");

    public static final GameObject<SoundEvent> ENTITY_PRAETORIAN_HURT = register("entity.praetorian.hurt");

    public static final GameObject<SoundEvent> ENTITY_QUEEN_AMBIENT = register("entity.queen.ambient");

    public static final GameObject<SoundEvent> ENTITY_QUEEN_AMBIENT_LOOP = register("entity.queen.ambient.loop");

    public static final GameObject<SoundEvent> ENTITY_QUEEN_DEATH = register("entity.queen.death");

    public static final GameObject<SoundEvent> ENTITY_QUEEN_HURT = register("entity.queen.hurt");

    public static final GameObject<SoundEvent> ENTITY_XENOMORPH_AMBIENT = register("entity.xenomorph.ambient");

    public static final GameObject<SoundEvent> ENTITY_XENOMORPH_ATTACK = register("entity.xenomorph.attack");

    public static final GameObject<SoundEvent> ENTITY_XENOMORPH_DEATH = register("entity.xenomorph.death");

    public static final GameObject<SoundEvent> ENTITY_XENOMORPH_EAT = register("entity.xenomorph.eat");

    public static final GameObject<SoundEvent> ENTITY_XENOMORPH_HURT = register("entity.xenomorph.hurt");

    public static final GameObject<SoundEvent> ENTITY_YAUTJA_AMBIENT = register("entity.yautja.ambient");

    public static final GameObject<SoundEvent> ENTITY_YAUTJA_CLOAK = register("entity.yautja.cloak");

    public static final GameObject<SoundEvent> ENTITY_YAUTJA_DEATH = register("entity.yautja.death");

    public static final GameObject<SoundEvent> ENTITY_YAUTJA_DECLOAK = register("entity.yautja.decloak");

    public static final GameObject<SoundEvent> ENTITY_YAUTJA_HURT = register("entity.yautja.hurt");

    public static final GameObject<SoundEvent> ITEM_WEAPON_AK_47_SHOOT = register("item.weapon.ak_47.shoot");

    public static final GameObject<SoundEvent> ITEM_WEAPON_COMBAT_PISTOL_RELOAD = register("item.weapon.combat_pistol.reload");

    public static final GameObject<SoundEvent> ITEM_WEAPON_COMBAT_PISTOL_SHOOT = register("item.weapon.combat_pistol.shoot");

    public static final GameObject<SoundEvent> ITEM_WEAPON_FLAMETHROWER_RELOAD_FINISH = register("item.weapon.flamethrower.reload_finish");

    public static final GameObject<SoundEvent> ITEM_WEAPON_FLAMETHROWER_RELOAD_START = register("item.weapon.flamethrower.reload_start");

    public static final GameObject<SoundEvent> ITEM_WEAPON_FLAMETHROWER_SHOOT = register("item.weapon.flamethrower.shoot");

    public static final GameObject<SoundEvent> ITEM_WEAPON_FX_RICOCHET_DIRT = register("item.weapon.fx.ricochet.dirt");

    public static final GameObject<SoundEvent> ITEM_WEAPON_FX_RICOCHET_GENERIC = register("item.weapon.fx.ricochet.generic");

    public static final GameObject<SoundEvent> ITEM_WEAPON_FX_RICOCHET_GLASS = register("item.weapon.fx.ricochet.glass");

    public static final GameObject<SoundEvent> ITEM_WEAPON_FX_RICOCHET_METAL = register("item.weapon.fx.ricochet.metal");

    public static final GameObject<SoundEvent> ITEM_WEAPON_FX_ROCKET_EXPLOSION = register("item.weapon.fx.rocket_explosion");

    public static final GameObject<SoundEvent> ITEM_WEAPON_GENERIC_RELOAD = register("item.weapon.generic.reload");

    public static final GameObject<SoundEvent> ITEM_WEAPON_GENERIC_SHOOT = register("item.weapon.generic.shoot");

    public static final GameObject<SoundEvent> ITEM_WEAPON_GENERIC_SHOOT_FAIL = register("item.weapon.generic.shoot_fail");

    public static final GameObject<SoundEvent> ITEM_WEAPON_OLD_PAINLESS_SHOOT_LOOP = register("item.weapon.old_painless.shoot_loop");

    public static final GameObject<SoundEvent> ITEM_WEAPON_OLD_PAINLESS_SHOOT_SPINNING = register("item.weapon.old_painless.shoot_spinning");

    public static final GameObject<SoundEvent> ITEM_WEAPON_OLD_PAINLESS_SHOOT_START = register("item.weapon.old_painless.shoot_start");

    public static final GameObject<SoundEvent> ITEM_WEAPON_OLD_PAINLESS_SHOOT_STOP = register("item.weapon.old_painless.shoot_stop");

    public static final GameObject<SoundEvent> ITEM_WEAPON_PULSE_RIFLE_SHOOT = register("item.weapon.pulse_rifle.shoot");

    public static final GameObject<SoundEvent> ITEM_WEAPON_ROCKET_LAUNCHER_RELOAD_FINISH = register("item.weapon.rocket_launcher.reload_finish");

    public static final GameObject<SoundEvent> ITEM_WEAPON_ROCKET_LAUNCHER_RELOAD_START = register("item.weapon.rocket_launcher.reload_start");

    public static final GameObject<SoundEvent> ITEM_WEAPON_ROCKET_LAUNCHER_SHOOT = register("item.weapon.rocket_launcher.shoot");

    public static final GameObject<SoundEvent> ITEM_WEAPON_SHOTGUN_SHOOT = register("item.weapon.shotgun.shoot");

    public static final GameObject<SoundEvent> ITEM_WEAPON_SNIPER_RIFLE_SHOOT = register("item.weapon.sniper_rifle.shoot");

    private static GameObject<SoundEvent> register(String registryName) {
        var resourceLocation = AVPResources.location(registryName);
        return Services.SOUND_EVENT_REGISTRY.register(
            registryName,
            () -> SoundEvent.createVariableRangeEvent(resourceLocation)
        );
    }

    private AVPSoundEvents() {
        throw new UnsupportedOperationException();
    }
}
