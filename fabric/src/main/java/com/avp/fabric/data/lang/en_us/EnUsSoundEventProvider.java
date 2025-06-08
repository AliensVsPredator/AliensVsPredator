package com.avp.fabric.data.lang.en_us;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.sounds.SoundEvent;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.avp.common.registry.init.AVPSoundEvents;

public class EnUsSoundEventProvider {

    public static final Consumer<FabricLanguageProvider.TranslationBuilder> CONSUMER = builder -> {
        addSound(builder, AVPSoundEvents.BLOCK_ACID_BURN, "Acid burns");
        addSound(builder, AVPSoundEvents.BLOCK_RESIN_SPREAD, "Xenomorph spreads resin");

        addSound(builder, AVPSoundEvents.ENTITY_OVOMORPH_HATCH, "Ovomorph hatches");
        addSound(builder, AVPSoundEvents.ENTITY_OVOMORPH_LAID, "Queen lays egg");
        addSound(builder, AVPSoundEvents.ENTITY_OVOMORPH_ROOT, "Ovomorph takes root");
        addSound(builder, AVPSoundEvents.ENTITY_OVOMORPH_SHEAR, "Ovomorph de-roots");

        addSound(builder, AVPSoundEvents.ENTITY_QUEEN_ARM_ATTACK, "Queen attacks");
        addSound(builder, AVPSoundEvents.ENTITY_QUEEN_BACK_HAND_ATTACK, "Queen back hand attacks");
        addSound(builder, AVPSoundEvents.ENTITY_QUEEN_RAM_ATTACK, "Queen ram attacks");
        addSound(builder, AVPSoundEvents.ENTITY_QUEEN_SCREAM, "Queen screams");
        addSound(builder, AVPSoundEvents.ENTITY_QUEEN_TAIL_ATTACK, "Queen tail attacks");
        addSound(builder, AVPSoundEvents.ENTITY_QUEEN_DEATH, "Queen dies");
        addSound(builder, AVPSoundEvents.ENTITY_QUEEN_HURT, "Queen hurts");
        addSound(builder, AVPSoundEvents.ENTITY_QUEEN_IDLE, "Queen breathes");
        addSound(builder, AVPSoundEvents.ENTITY_QUEEN_STEP_THUMP, "Queen steps");
        addSound(builder, AVPSoundEvents.ENTITY_QUEEN_STEP_THUMP_ROCK, "Queen steps");

        addSound(builder, AVPSoundEvents.ENTITY_XENOMORPH_ATTACK, "Xenomorph attacks");
        addSound(builder, AVPSoundEvents.ENTITY_XENOMORPH_DEATH, "Xenomorph dies");
        addSound(builder, AVPSoundEvents.ENTITY_XENOMORPH_HISS, "Xenomorph hisses");
        addSound(builder, AVPSoundEvents.ENTITY_XENOMORPH_HURT, "Xenomorph hurts");
        addSound(builder, AVPSoundEvents.ENTITY_XENOMORPH_IDLE, "Xenomorph breathes");
        addSound(builder, AVPSoundEvents.ENTITY_XENOMORPH_LUNGE, "Xenomorph lunges");

        addSound(builder, AVPSoundEvents.ITEM_ARMOR_EQUIP_CHITIN, "Chitin armor squishes");
        addSound(builder, AVPSoundEvents.ITEM_ARMOR_EQUIP_MK50, "MK50 armor rustles");
        addSound(builder, AVPSoundEvents.ITEM_ARMOR_EQUIP_PRESSURE, "Pressure armor rustles");
        addSound(builder, AVPSoundEvents.ITEM_ARMOR_EQUIP_STEEL, "Steel armor clanks");
        addSound(builder, AVPSoundEvents.ITEM_ARMOR_EQUIP_TACTICAL, "Tactical armor rustles");
        addSound(builder, AVPSoundEvents.ITEM_ARMOR_EQUIP_TITANIUM, "Titanium armor clanks");
        addSound(builder, AVPSoundEvents.ITEM_ARMOR_EQUIP_VERITANIUM, "Veritanium armor clanks");

        addSound(builder, AVPSoundEvents.WEAPON_FLAMETHROWER_SEVASTOPOL_RELOAD_FINISH, "Flamethrower finishes reloading");
        addSound(builder, AVPSoundEvents.WEAPON_FLAMETHROWER_SEVASTOPOL_RELOAD_START, "Flamethrower reloads");
        addSound(builder, AVPSoundEvents.WEAPON_FLAMETHROWER_SEVASTOPOL_SHOOT, "Flamethrower shoots");
        addSound(builder, AVPSoundEvents.WEAPON_FX_RICOCHET_DIRT, "Bullet ricochets off of dirt");
        addSound(builder, AVPSoundEvents.WEAPON_FX_RICOCHET_GENERIC, "Bullet ricochets");
        addSound(builder, AVPSoundEvents.WEAPON_FX_RICOCHET_GLASS, "Bullet ricochets off of glass");
        addSound(builder, AVPSoundEvents.WEAPON_FX_RICOCHET_METAL, "Bullet ricochets off of metal");
        addSound(builder, AVPSoundEvents.WEAPON_GENERIC_RELOAD, "Gun reloads");
        addSound(builder, AVPSoundEvents.WEAPON_GENERIC_SHOOT, "Gun shoots");
        addSound(builder, AVPSoundEvents.WEAPON_GENERIC_SHOOT_FAIL, "Gun fails to shoot");
        addSound(builder, AVPSoundEvents.WEAPON_M37_12_SHOTGUN_SHOOT, "M37-12 Shotgun shoots");
        addSound(builder, AVPSoundEvents.WEAPON_M41A_PULSE_RIFLE_SHOOT, "M41A Pulse Rifle shoots");
        addSound(builder, AVPSoundEvents.WEAPON_M42A3_SNIPER_RIFLE_SHOOT, "M42A3 Sniper Rifle shoots");
        addSound(builder, AVPSoundEvents.WEAPON_M4RA_BATTLE_RIFLE_SHOOT, "M4RA Battle Rifle shoots");
        addSound(builder, AVPSoundEvents.WEAPON_M56_SMARTGUN_SHOOT, "M56 Smartgun shoots");
        addSound(builder, AVPSoundEvents.WEAPON_M6B_ROCKET_LAUNCHER_RELOAD_FINISH, "M6B Rocket Launcher finishes reloading");
        addSound(builder, AVPSoundEvents.WEAPON_M6B_ROCKET_LAUNCHER_RELOAD_START, "M6B Rocket Launcher reloads");
        addSound(builder, AVPSoundEvents.WEAPON_M6B_ROCKET_LAUNCHER_SHOOT, "M6B Rocket Launcher shoots");
        addSound(builder, AVPSoundEvents.WEAPON_M88_MOD_4_COMBAT_PISTOL_RELOAD, "M88 Mod 4 Combat Pistol reloads");
        addSound(builder, AVPSoundEvents.WEAPON_M88_MOD_4_COMBAT_PISTOL_SHOOT, "M88 Mod 4 Combat Pistol shoots");
        addSound(builder, AVPSoundEvents.WEAPON_OLD_PAINLESS_SHOOT, "Old painless shoots");
        addSound(builder, AVPSoundEvents.WEAPON_OLD_PAINLESS_SHOOT_FINISH, "Old Painless stops shooting");
        addSound(builder, AVPSoundEvents.WEAPON_OLD_PAINLESS_SHOOT_SPINNING, "Old Painless barrel spins");
        addSound(builder, AVPSoundEvents.WEAPON_OLD_PAINLESS_SHOOT_START, "Old Painless barrel starts spinning");
        addSound(builder, AVPSoundEvents.WEAPON_ZX_76_SHOTGUN_SHOOT, "ZX-76 Shotgun shoots");
        addSound(builder, AVPSoundEvents.JUKEBOX_SOUNDS_ALIEN_MUSIC_1, "Silver Smile plays");
        addSound(builder, AVPSoundEvents.JUKEBOX_SOUNDS_PREDATOR_MUSIC_1, "Hunter plays");
    };

    private static void addSound(
        FabricLanguageProvider.TranslationBuilder translationBuilder,
        Supplier<SoundEvent> soundEventSupplier,
        String value
    ) {
        addSound(translationBuilder, soundEventSupplier.get(), value);
    }

    private static void addSound(FabricLanguageProvider.TranslationBuilder translationBuilder, SoundEvent soundEvent, String value) {
        translationBuilder.add("subtitles." + soundEvent.getLocation().getPath(), value);
    }
}
