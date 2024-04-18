package org.avp.common.sound;

import net.minecraft.sounds.SoundEvent;

import java.util.function.Supplier;

import org.avp.api.Holder;
import org.avp.common.AVPResources;
import org.avp.common.registry.AVPDeferredRegistry;
import org.avp.common.service.Services;

public class AVPSoundEvents extends AVPDeferredRegistry<SoundEvent> {

    public static final AVPSoundEvents INSTANCE = new AVPSoundEvents();

    public final Holder<SoundEvent> entityChestbursterDeath = createHolder("entity.chestburster.death");

    public final Holder<SoundEvent> entityChestbursterHurt = createHolder("entity.chestburster.hurt");

    public final Holder<SoundEvent> entityPraetorianAmbient = createHolder("entity.praetorian.ambient");

    public final Holder<SoundEvent> entityPraetorianDeath = createHolder("entity.praetorian.death");

    public final Holder<SoundEvent> entityPraetorianHurt = createHolder("entity.praetorian.hurt");

    public final Holder<SoundEvent> entityQueenAmbient = createHolder("entity.queen.ambient");

    public final Holder<SoundEvent> entityQueenAmbientLoop = createHolder("entity.queen.ambient.loop");

    public final Holder<SoundEvent> entityQueenDeath = createHolder("entity.queen.death");

    public final Holder<SoundEvent> entityQueenHurt = createHolder("entity.queen.hurt");

    public final Holder<SoundEvent> entityXenomorphAmbient = createHolder("entity.xenomorph.ambient");

    public final Holder<SoundEvent> entityXenomorphAttack = createHolder("entity.xenomorph.attack");

    public final Holder<SoundEvent> entityXenomorphDeath = createHolder("entity.xenomorph.death");

    public final Holder<SoundEvent> entityXenomorphEat = createHolder("entity.xenomorph.eat");

    public final Holder<SoundEvent> entityXenomorphHurt = createHolder("entity.xenomorph.hurt");

    public final Holder<SoundEvent> entityYautjaAmbient = createHolder("entity.yautja.ambient");

    public final Holder<SoundEvent> entityYautjaCloak = createHolder("entity.yautja.cloak");

    public final Holder<SoundEvent> entityYautjaDeath = createHolder("entity.yautja.death");

    public final Holder<SoundEvent> entityYautjaDecloak = createHolder("entity.yautja.decloak");

    public final Holder<SoundEvent> entityYautjaHurt = createHolder("entity.yautja.hurt");

    public final Holder<SoundEvent> itemWeaponAk47Shoot = createHolder("item.weapon.ak_47.shoot");

    public final Holder<SoundEvent> itemWeaponCombatPistolReload = createHolder("item.weapon.combat_pistol.reload");

    public final Holder<SoundEvent> itemWeaponCombatPistolShoot = createHolder("item.weapon.combat_pistol.shoot");

    public final Holder<SoundEvent> itemWeaponFlamethrowerReloadFinish = createHolder("item.weapon.flamethrower.reload_finish");

    public final Holder<SoundEvent> itemWeaponFlamethrowerReloadStart = createHolder("item.weapon.flamethrower.reload_start");

    public final Holder<SoundEvent> itemWeaponFlamethrowerShoot = createHolder("item.weapon.flamethrower.shoot");

    public final Holder<SoundEvent> itemWeaponFxRicochetDirt = createHolder("item.weapon.fx.ricochet.dirt");

    public final Holder<SoundEvent> itemWeaponFxRicochetGeneric = createHolder("item.weapon.fx.ricochet.generic");

    public final Holder<SoundEvent> itemWeaponFxRicochetGlass = createHolder("item.weapon.fx.ricochet.glass");

    public final Holder<SoundEvent> itemWeaponFxRicochetMetal = createHolder("item.weapon.fx.ricochet.metal");

    public final Holder<SoundEvent> itemWeaponFxRocketExplosion = createHolder("item.weapon.fx.rocket_explosion");

    public final Holder<SoundEvent> itemWeaponGenericReload = createHolder("item.weapon.generic.reload");

    public final Holder<SoundEvent> itemWeaponGenericShoot = createHolder("item.weapon.generic.shoot");

    public final Holder<SoundEvent> itemWeaponGenericShootFail = createHolder("item.weapon.generic.shoot_fail");

    public final Holder<SoundEvent> itemWeaponOldPainlessShootLoop = createHolder("item.weapon.old_painless.shoot_loop");

    public final Holder<SoundEvent> itemWeaponOldPainlessShootSpinning = createHolder("item.weapon.old_painless.shoot_spinning");

    public final Holder<SoundEvent> itemWeaponOldPainlessShootStart = createHolder("item.weapon.old_painless.shoot_start");

    public final Holder<SoundEvent> itemWeaponOldPainlessShootStop = createHolder("item.weapon.old_painless.shoot_stop");

    public final Holder<SoundEvent> itemWeaponPulseRifleShoot = createHolder("item.weapon.pulse_rifle.shoot");

    public final Holder<SoundEvent> itemWeaponRocketLauncherReloadFinish = createHolder("item.weapon.rocket_launcher.reload_finish");

    public final Holder<SoundEvent> itemWeaponRocketLauncherReloadStart = createHolder("item.weapon.rocket_launcher.reload_start");

    public final Holder<SoundEvent> itemWeaponRocketLauncherShoot = createHolder("item.weapon.rocket_launcher.shoot");

    public final Holder<SoundEvent> itemWeaponShotgunShoot = createHolder("item.weapon.shotgun.shoot");

    public final Holder<SoundEvent> itemWeaponSniperRifleShoot = createHolder("item.weapon.sniper_rifle.shoot");

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
