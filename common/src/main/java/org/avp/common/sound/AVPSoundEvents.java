package org.avp.common.sound;

import net.minecraft.sounds.SoundEvent;

import java.util.function.Supplier;

import org.avp.api.Holder;
import org.avp.common.AVPResources;
import org.avp.common.registry.AVPDeferredRegistry;
import org.avp.common.service.Services;

public class AVPSoundEvents extends AVPDeferredRegistry<SoundEvent> {

    public static final AVPSoundEvents INSTANCE = new AVPSoundEvents();

    public final Holder<SoundEvent> blockAcidBurn;

    public final EntitySoundHolderSet entityBoiler;

    public final Holder<SoundEvent> entityChestbursterDeath;

    public final Holder<SoundEvent> entityChestbursterHurt;

    public final EntitySoundHolderSet entityCrusher;

    public final EntitySoundHolderSet entityDracomorph;

    public final EntitySoundHolderSet entityDrone;

    public final EntitySoundHolderSet entityNauticomorph;

    public final EntitySoundHolderSet entityPraetorian;

    public final EntitySoundHolderSet entityQueen;

    public final Holder<SoundEvent> entityQueenAmbientLoop;

    public final EntitySoundHolderSet entitySpitter;

    public final EntitySoundHolderSet entityUltramorph;

    public final EntitySoundHolderSet entityWarrior;

    public final Holder<SoundEvent> entityXenomorphAmbient;

    public final Holder<SoundEvent> entityXenomorphAttack;

    public final Holder<SoundEvent> entityXenomorphDeath;

    public final Holder<SoundEvent> entityXenomorphEat;

    public final Holder<SoundEvent> entityXenomorphHurt;

    public final Holder<SoundEvent> entityXenomorphHurtScreech;

    public final EntitySoundHolderSet entityYautja;

    public final Holder<SoundEvent> entityYautjaCloak;

    public final Holder<SoundEvent> entityYautjaDecloak;

    public final Holder<SoundEvent> entityYautjaIntimidate;

    public final Holder<SoundEvent> itemWeaponAk47Shoot;

    public final Holder<SoundEvent> itemWeaponCombatPistolReload;

    public final Holder<SoundEvent> itemWeaponCombatPistolShoot;

    public final Holder<SoundEvent> itemWeaponFlamethrowerReloadFinish;

    public final Holder<SoundEvent> itemWeaponFlamethrowerReloadStart;

    public final Holder<SoundEvent> itemWeaponFlamethrowerShoot;

    public final Holder<SoundEvent> itemWeaponFxRicochetDirt;

    public final Holder<SoundEvent> itemWeaponFxRicochetGeneric;

    public final Holder<SoundEvent> itemWeaponFxRicochetGlass;

    public final Holder<SoundEvent> itemWeaponFxRicochetMetal;

    public final Holder<SoundEvent> itemWeaponFxRocketExplosion;

    public final Holder<SoundEvent> itemWeaponGenericReload;

    public final Holder<SoundEvent> itemWeaponGenericShoot;

    public final Holder<SoundEvent> itemWeaponGenericShootFail;

    public final Holder<SoundEvent> itemWeaponOldPainlessShootLoop;

    public final Holder<SoundEvent> itemWeaponOldPainlessShootSpinning;

    public final Holder<SoundEvent> itemWeaponOldPainlessShootStart;

    public final Holder<SoundEvent> itemWeaponOldPainlessShootStop;

    public final Holder<SoundEvent> itemWeaponPulseRifleShoot;

    public final Holder<SoundEvent> itemWeaponRocketLauncherReloadFinish;

    public final Holder<SoundEvent> itemWeaponRocketLauncherReloadStart;

    public final Holder<SoundEvent> itemWeaponRocketLauncherShoot;

    public final Holder<SoundEvent> itemWeaponShotgunShoot;

    public final Holder<SoundEvent> itemWeaponSniperRifleShoot;

    public final Holder<SoundEvent> itemWeaponWristbladeClose;

    public final Holder<SoundEvent> itemWeaponWristbladeOpen;

    @Override
    protected Holder<SoundEvent> createHolder(String registryName, Supplier<SoundEvent> supplier) {
        var holder = Services.SOUND_EVENT_SERVICE.createHolder(registryName, supplier);
        entries.put(registryName, holder);
        return holder;
    }

    private Holder<SoundEvent> createHolder(String registryName) {
        return createHolder(registryName, () -> SoundEvent.createVariableRangeEvent(AVPResources.location(registryName)));
    }

    private EntitySoundHolderSet createEntitySoundHolderSet(String baseRegistryName) {
        return new EntitySoundHolderSet(
            createHolder(baseRegistryName + ".ambient"),
            createHolder(baseRegistryName + ".death"),
            createHolder(baseRegistryName + ".hurt")
        );
    }

    private AVPSoundEvents() {
        blockAcidBurn = createHolder("block.acid.burn");

        entityBoiler = createEntitySoundHolderSet("entity.boiler");

        entityChestbursterDeath = createHolder("entity.chestburster.death");

        entityChestbursterHurt = createHolder("entity.chestburster.hurt");

        entityCrusher = createEntitySoundHolderSet("entity.crusher");

        entityDracomorph = createEntitySoundHolderSet("entity.dracomorph");

        entityDrone = createEntitySoundHolderSet("entity.drone");

        entityNauticomorph = createEntitySoundHolderSet("entity.nauticomorph");

        entityPraetorian = createEntitySoundHolderSet("entity.praetorian");

        entityQueen = createEntitySoundHolderSet("entity.queen");

        entityQueenAmbientLoop = createHolder("entity.queen.ambient.loop");

        entitySpitter = createEntitySoundHolderSet("entity.spitter");

        entityUltramorph = createEntitySoundHolderSet("entity.ultramorph");

        entityWarrior = createEntitySoundHolderSet("entity.warrior");

        entityXenomorphAmbient = createHolder("entity.xenomorph.ambient");

        entityXenomorphAttack = createHolder("entity.xenomorph.attack");

        entityXenomorphDeath = createHolder("entity.xenomorph.death");

        entityXenomorphEat = createHolder("entity.xenomorph.eat");

        entityXenomorphHurt = createHolder("entity.xenomorph.hurt");

        entityXenomorphHurtScreech = createHolder("entity.xenomorph.hurt.screech");

        entityYautja = createEntitySoundHolderSet("entity.yautja");

        entityYautjaCloak = createHolder("entity.yautja.cloak");

        entityYautjaDecloak = createHolder("entity.yautja.decloak");

        entityYautjaIntimidate = createHolder("entity.yautja.intimidate");

        itemWeaponAk47Shoot = createHolder("item.weapon.ak_47.shoot");

        itemWeaponCombatPistolReload = createHolder("item.weapon.combat_pistol.reload");

        itemWeaponCombatPistolShoot = createHolder("item.weapon.combat_pistol.shoot");

        itemWeaponFlamethrowerReloadFinish = createHolder("item.weapon.flamethrower.reload_finish");

        itemWeaponFlamethrowerReloadStart = createHolder("item.weapon.flamethrower.reload_start");

        itemWeaponFlamethrowerShoot = createHolder("item.weapon.flamethrower.shoot");

        itemWeaponFxRicochetDirt = createHolder("item.weapon.fx.ricochet.dirt");

        itemWeaponFxRicochetGeneric = createHolder("item.weapon.fx.ricochet.generic");

        itemWeaponFxRicochetGlass = createHolder("item.weapon.fx.ricochet.glass");

        itemWeaponFxRicochetMetal = createHolder("item.weapon.fx.ricochet.metal");

        itemWeaponFxRocketExplosion = createHolder("item.weapon.fx.rocket_explosion");

        itemWeaponGenericReload = createHolder("item.weapon.generic.reload");

        itemWeaponGenericShoot = createHolder("item.weapon.generic.shoot");

        itemWeaponGenericShootFail = createHolder("item.weapon.generic.shoot_fail");

        itemWeaponOldPainlessShootLoop = createHolder("item.weapon.old_painless.shoot_loop");

        itemWeaponOldPainlessShootSpinning = createHolder("item.weapon.old_painless.shoot_spinning");

        itemWeaponOldPainlessShootStart = createHolder("item.weapon.old_painless.shoot_start");

        itemWeaponOldPainlessShootStop = createHolder("item.weapon.old_painless.shoot_stop");

        itemWeaponPulseRifleShoot = createHolder("item.weapon.pulse_rifle.shoot");

        itemWeaponRocketLauncherReloadFinish = createHolder("item.weapon.rocket_launcher.reload_finish");

        itemWeaponRocketLauncherReloadStart = createHolder("item.weapon.rocket_launcher.reload_start");

        itemWeaponRocketLauncherShoot = createHolder("item.weapon.rocket_launcher.shoot");

        itemWeaponShotgunShoot = createHolder("item.weapon.shotgun.shoot");

        itemWeaponSniperRifleShoot = createHolder("item.weapon.sniper_rifle.shoot");

        itemWeaponWristbladeClose = createHolder("item.weapon.wristblade.close");

        itemWeaponWristbladeOpen = createHolder("item.weapon.wristblade.open");
    }

    @Override
    public void register() {
        getValues().forEach(Services.SOUND_EVENT_SERVICE::register);
    }

    public record EntitySoundHolderSet(
        Holder<SoundEvent> ambient,
        Holder<SoundEvent> death,
        Holder<SoundEvent> hurt
    ) {}
}
