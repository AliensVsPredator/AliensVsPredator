package org.avp.common.sound;

import net.minecraft.sounds.SoundEvent;

import org.avp.api.Holder;
import org.avp.common.AVPResources;
import org.avp.common.registry.AVPDeferredRegistry;
import org.avp.common.service.Services;

import java.util.function.Supplier;

public class AVPSoundEvents extends AVPDeferredRegistry<SoundEvent> {

    public static final AVPSoundEvents INSTANCE = new AVPSoundEvents();

    public final Holder<SoundEvent> ENTITY_CHESTBURSTER_DEATH = createHolder("entity.chestburster.death");

    public final Holder<SoundEvent> ENTITY_CHESTBURSTER_HURT = createHolder("entity.chestburster.hurt");

    public final Holder<SoundEvent> ENTITY_PRAETORIAN_AMBIENT = createHolder("entity.praetorian.ambient");

    public final Holder<SoundEvent> ENTITY_PRAETORIAN_DEATH = createHolder("entity.praetorian.death");

    public final Holder<SoundEvent> ENTITY_PRAETORIAN_HURT = createHolder("entity.praetorian.hurt");

    public final Holder<SoundEvent> ENTITY_QUEEN_AMBIENT = createHolder("entity.queen.ambient");

    public final Holder<SoundEvent> ENTITY_QUEEN_AMBIENT_LOOP = createHolder("entity.queen.ambient.loop");

    public final Holder<SoundEvent> ENTITY_QUEEN_DEATH = createHolder("entity.queen.death");

    public final Holder<SoundEvent> ENTITY_QUEEN_HURT = createHolder("entity.queen.hurt");

    public final Holder<SoundEvent> ENTITY_XENOMORPH_AMBIENT = createHolder("entity.xenomorph.ambient");

    public final Holder<SoundEvent> ENTITY_XENOMORPH_ATTACK = createHolder("entity.xenomorph.attack");

    public final Holder<SoundEvent> ENTITY_XENOMORPH_DEATH = createHolder("entity.xenomorph.death");

    public final Holder<SoundEvent> ENTITY_XENOMORPH_EAT = createHolder("entity.xenomorph.eat");

    public final Holder<SoundEvent> ENTITY_XENOMORPH_HURT = createHolder("entity.xenomorph.hurt");

    public final Holder<SoundEvent> ENTITY_YAUTJA_AMBIENT = createHolder("entity.yautja.ambient");

    public final Holder<SoundEvent> ENTITY_YAUTJA_CLOAK = createHolder("entity.yautja.cloak");

    public final Holder<SoundEvent> ENTITY_YAUTJA_DEATH = createHolder("entity.yautja.death");

    public final Holder<SoundEvent> ENTITY_YAUTJA_DECLOAK = createHolder("entity.yautja.decloak");

    public final Holder<SoundEvent> ENTITY_YAUTJA_HURT = createHolder("entity.yautja.hurt");

    public final Holder<SoundEvent> ITEM_WEAPON_AK_47_SHOOT = createHolder("item.weapon.ak_47.shoot");

    public final Holder<SoundEvent> ITEM_WEAPON_COMBAT_PISTOL_RELOAD = createHolder("item.weapon.combat_pistol.reload");

    public final Holder<SoundEvent> ITEM_WEAPON_COMBAT_PISTOL_SHOOT = createHolder("item.weapon.combat_pistol.shoot");

    public final Holder<SoundEvent> ITEM_WEAPON_FLAMETHROWER_RELOAD_FINISH = createHolder("item.weapon.flamethrower.reload_finish");

    public final Holder<SoundEvent> ITEM_WEAPON_FLAMETHROWER_RELOAD_START = createHolder("item.weapon.flamethrower.reload_start");

    public final Holder<SoundEvent> ITEM_WEAPON_FLAMETHROWER_SHOOT = createHolder("item.weapon.flamethrower.shoot");

    public final Holder<SoundEvent> ITEM_WEAPON_FX_RICOCHET_DIRT = createHolder("item.weapon.fx.ricochet.dirt");

    public final Holder<SoundEvent> ITEM_WEAPON_FX_RICOCHET_GENERIC = createHolder("item.weapon.fx.ricochet.generic");

    public final Holder<SoundEvent> ITEM_WEAPON_FX_RICOCHET_GLASS = createHolder("item.weapon.fx.ricochet.glass");

    public final Holder<SoundEvent> ITEM_WEAPON_FX_RICOCHET_METAL = createHolder("item.weapon.fx.ricochet.metal");

    public final Holder<SoundEvent> ITEM_WEAPON_FX_ROCKET_EXPLOSION = createHolder("item.weapon.fx.rocket_explosion");

    public final Holder<SoundEvent> ITEM_WEAPON_GENERIC_RELOAD = createHolder("item.weapon.generic.reload");

    public final Holder<SoundEvent> ITEM_WEAPON_GENERIC_SHOOT = createHolder("item.weapon.generic.shoot");

    public final Holder<SoundEvent> ITEM_WEAPON_GENERIC_SHOOT_FAIL = createHolder("item.weapon.generic.shoot_fail");

    public final Holder<SoundEvent> ITEM_WEAPON_OLD_PAINLESS_SHOOT_LOOP = createHolder("item.weapon.old_painless.shoot_loop");

    public final Holder<SoundEvent> ITEM_WEAPON_OLD_PAINLESS_SHOOT_SPINNING = createHolder("item.weapon.old_painless.shoot_spinning");

    public final Holder<SoundEvent> ITEM_WEAPON_OLD_PAINLESS_SHOOT_START = createHolder("item.weapon.old_painless.shoot_start");

    public final Holder<SoundEvent> ITEM_WEAPON_OLD_PAINLESS_SHOOT_STOP = createHolder("item.weapon.old_painless.shoot_stop");

    public final Holder<SoundEvent> ITEM_WEAPON_PULSE_RIFLE_SHOOT = createHolder("item.weapon.pulse_rifle.shoot");

    public final Holder<SoundEvent> ITEM_WEAPON_ROCKET_LAUNCHER_RELOAD_FINISH = createHolder("item.weapon.rocket_launcher.reload_finish");

    public final Holder<SoundEvent> ITEM_WEAPON_ROCKET_LAUNCHER_RELOAD_START = createHolder("item.weapon.rocket_launcher.reload_start");

    public final Holder<SoundEvent> ITEM_WEAPON_ROCKET_LAUNCHER_SHOOT = createHolder("item.weapon.rocket_launcher.shoot");

    public final Holder<SoundEvent> ITEM_WEAPON_SHOTGUN_SHOOT = createHolder("item.weapon.shotgun.shoot");

    public final Holder<SoundEvent> ITEM_WEAPON_SNIPER_RIFLE_SHOOT = createHolder("item.weapon.sniper_rifle.shoot");

    @Override
    protected Holder<SoundEvent> createHolder(String registryName, Supplier<SoundEvent> supplier) {
        var holder = Services.SOUND_EVENT_SERVICE.createHolder(registryName, supplier);
        entries.add(holder);
        return holder;
    }

    private Holder<SoundEvent> createHolder(String registryName) {
        return createHolder(registryName, () -> SoundEvent.createVariableRangeEvent(AVPResources.location(registryName)));
    }

    private AVPSoundEvents() {}

    @Override
    public void register() {
        entries.forEach(Services.SOUND_EVENT_SERVICE::register);
    }
}
