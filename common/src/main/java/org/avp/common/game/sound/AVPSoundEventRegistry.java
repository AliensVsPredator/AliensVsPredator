package org.avp.common.game.sound;

import net.minecraft.sounds.SoundEvent;

import java.util.function.Supplier;

import org.avp.api.registry.holder.BLHolder;
import org.avp.common.AVPResources;
import org.avp.api.registry.AVPDeferredRegistry;
import org.avp.api.service.Services;

public class AVPSoundEventRegistry extends AVPDeferredRegistry<SoundEvent> {

    public static final AVPSoundEventRegistry INSTANCE = new AVPSoundEventRegistry();

    public final BLHolder<SoundEvent> blockAcidBurn;

    public final EntitySoundHolderSet entityBoiler;

    public final ChestbursterSoundHolderSet entityChestburster;

    public final ChestbursterSoundHolderSet entityChestbursterDraco;

    public final ChestbursterSoundHolderSet entityChestbursterQueen;

    public final ChestbursterSoundHolderSet entityChestbursterRunner;

    public final EntitySoundHolderSet entityCrusher;

    public final EntitySoundHolderSet entityDracomorph;

    public final EntitySoundHolderSet entityDrone;

    public final EntitySoundHolderSet entityDroneRunner;

    public final EntitySoundHolderSet entityNauticomorph;

    public final BLHolder<SoundEvent> entityOvamorphOpen;

    public final BLHolder<SoundEvent> entityParasiteImpregnate;

    public final EntitySoundHolderSet entityPraetorian;

    public final EntitySoundHolderSet entityQueen;

    public final BLHolder<SoundEvent> entityQueenAmbientLoop;

    public final EntitySoundHolderSet entitySpitter;

    public final EntitySoundHolderSet entityUltramorph;

    public final EntitySoundHolderSet entityWarrior;

    public final EntitySoundHolderSet entityWarriorRunner;

    public final EntitySoundHolderSet entityXenomorph;

    public final BLHolder<SoundEvent> entityXenomorphAttack;

    public final BLHolder<SoundEvent> entityXenomorphEat;

    public final BLHolder<SoundEvent> entityXenomorphHurtScreech;

    public final EntitySoundHolderSet entityYautja;

    public final BLHolder<SoundEvent> entityYautjaCloak;

    public final BLHolder<SoundEvent> entityYautjaDecloak;

    public final BLHolder<SoundEvent> entityYautjaIntimidate;

    public final BLHolder<SoundEvent> itemArmorEquipAluminum;

    public final BLHolder<SoundEvent> itemArmorEquipMK50;

    public final BLHolder<SoundEvent> itemArmorEquipOrionite;

    public final BLHolder<SoundEvent> itemArmorEquipPressure;

    public final BLHolder<SoundEvent> itemArmorEquipTactical;

    public final BLHolder<SoundEvent> itemArmorEquipTitanium;

    public final BLHolder<SoundEvent> itemArmorEquipVeritanium;

    public final BLHolder<SoundEvent> itemArmorEquipXenomorphChitin;

    public final BLHolder<SoundEvent> itemWeaponAk47Shoot;

    public final BLHolder<SoundEvent> itemWeaponCombatPistolReload;

    public final BLHolder<SoundEvent> itemWeaponCombatPistolShoot;

    public final BLHolder<SoundEvent> itemWeaponFlamethrowerReloadFinish;

    public final BLHolder<SoundEvent> itemWeaponFlamethrowerReloadStart;

    public final BLHolder<SoundEvent> itemWeaponFlamethrowerShoot;

    public final BLHolder<SoundEvent> itemWeaponFxRicochetDirt;

    public final BLHolder<SoundEvent> itemWeaponFxRicochetGeneric;

    public final BLHolder<SoundEvent> itemWeaponFxRicochetGlass;

    public final BLHolder<SoundEvent> itemWeaponFxRicochetMetal;

    public final BLHolder<SoundEvent> itemWeaponFxRocketExplosion;

    public final BLHolder<SoundEvent> itemWeaponGenericReload;

    public final BLHolder<SoundEvent> itemWeaponGenericShoot;

    public final BLHolder<SoundEvent> itemWeaponGenericShootFail;

    public final BLHolder<SoundEvent> itemWeaponOldPainlessShootLoop;

    public final BLHolder<SoundEvent> itemWeaponOldPainlessShootSpinning;

    public final BLHolder<SoundEvent> itemWeaponOldPainlessShootStart;

    public final BLHolder<SoundEvent> itemWeaponOldPainlessShootStop;

    public final BLHolder<SoundEvent> itemWeaponPulseRifleShoot;

    public final BLHolder<SoundEvent> itemWeaponRocketLauncherReloadFinish;

    public final BLHolder<SoundEvent> itemWeaponRocketLauncherReloadStart;

    public final BLHolder<SoundEvent> itemWeaponRocketLauncherShoot;

    public final BLHolder<SoundEvent> itemWeaponShotgunShoot;

    public final BLHolder<SoundEvent> itemWeaponSniperRifleShoot;

    public final BLHolder<SoundEvent> itemWeaponWristbladeClose;

    public final BLHolder<SoundEvent> itemWeaponWristbladeOpen;

    @Override
    protected BLHolder<SoundEvent> createHolder(String registryName, Supplier<SoundEvent> supplier) {
        var holder = Services.SOUND_EVENT_SERVICE.createHolder(registryName, supplier);
        entries.put(registryName, holder);
        return holder;
    }

    private BLHolder<SoundEvent> createHolder(String registryName) {
        return createHolder(registryName, () -> SoundEvent.createVariableRangeEvent(AVPResources.location(registryName)));
    }

    private ChestbursterSoundHolderSet createChestbursterSoundHolderSet(String baseRegistryName) {
        return new ChestbursterSoundHolderSet(
            createHolder(baseRegistryName + ".death"),
            createHolder(baseRegistryName + ".hurt")
        );
    }

    private EntitySoundHolderSet createEntitySoundHolderSet(String baseRegistryName) {
        return new EntitySoundHolderSet(
            createHolder(baseRegistryName + ".ambient"),
            createHolder(baseRegistryName + ".death"),
            createHolder(baseRegistryName + ".hurt")
        );
    }

    private AVPSoundEventRegistry() {
        blockAcidBurn = createHolder("block.acid.burn");

        entityBoiler = createEntitySoundHolderSet("entity.boiler");

        entityChestburster = createChestbursterSoundHolderSet("entity.chestburster");

        entityChestbursterDraco = createChestbursterSoundHolderSet("entity.chestburster_draco");

        entityChestbursterQueen = createChestbursterSoundHolderSet("entity.chestburster_queen");

        entityChestbursterRunner = createChestbursterSoundHolderSet("entity.chestburster_runner");

        entityCrusher = createEntitySoundHolderSet("entity.crusher");

        entityDracomorph = createEntitySoundHolderSet("entity.dracomorph");

        entityDrone = createEntitySoundHolderSet("entity.drone");

        entityDroneRunner = createEntitySoundHolderSet("entity.drone_runner");

        entityNauticomorph = createEntitySoundHolderSet("entity.nauticomorph");

        entityOvamorphOpen = createHolder("entity.ovamorph.open");

        entityParasiteImpregnate = createHolder("entity.parasite.impregnate");

        entityPraetorian = createEntitySoundHolderSet("entity.praetorian");

        entityQueen = createEntitySoundHolderSet("entity.queen");

        entityQueenAmbientLoop = createHolder("entity.queen.ambient.loop");

        entitySpitter = createEntitySoundHolderSet("entity.spitter");

        entityUltramorph = createEntitySoundHolderSet("entity.ultramorph");

        entityWarrior = createEntitySoundHolderSet("entity.warrior");

        entityWarriorRunner = createEntitySoundHolderSet("entity.warrior_runner");

        entityXenomorph = createEntitySoundHolderSet("entity.xenomorph");

        entityXenomorphAttack = createHolder("entity.xenomorph.attack");

        entityXenomorphEat = createHolder("entity.xenomorph.eat");

        entityXenomorphHurtScreech = createHolder("entity.xenomorph.hurt.screech");

        entityYautja = createEntitySoundHolderSet("entity.yautja");

        entityYautjaCloak = createHolder("entity.yautja.cloak");

        entityYautjaDecloak = createHolder("entity.yautja.decloak");

        entityYautjaIntimidate = createHolder("entity.yautja.intimidate");

        itemArmorEquipAluminum = createHolder("item.armor.equip_aluminum");

        itemArmorEquipMK50 = createHolder("item.armor.equip_mk50");

        itemArmorEquipOrionite = createHolder("item.armor.equip_orionite");

        itemArmorEquipPressure = createHolder("item.armor.equip_pressure");

        itemArmorEquipTactical = createHolder("item.armor.equip_tactical");

        itemArmorEquipTitanium = createHolder("item.armor.equip_titanium");

        itemArmorEquipVeritanium = createHolder("item.armor.equip_veritanium");

        itemArmorEquipXenomorphChitin = createHolder("item.armor.equip_xenomorph_chitin");

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

    public record ChestbursterSoundHolderSet(
        BLHolder<SoundEvent> death,
        BLHolder<SoundEvent> hurt
    ) {}

    public record EntitySoundHolderSet(
        BLHolder<SoundEvent> ambient,
        BLHolder<SoundEvent> death,
        BLHolder<SoundEvent> hurt
    ) {}
}
