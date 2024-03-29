package org.avp.common.item;

import java.util.List;

import org.avp.api.item.weapon.FireMode;
import org.avp.api.item.weapon.WeaponDamageTypes;
import org.avp.api.item.weapon.WeaponItemData;
import org.avp.common.sound.AVPSoundEvents;

public class AVPWeaponItemData {

    public static final WeaponItemData AK_47 = new WeaponItemData(
        1.0F,
        WeaponDamageTypes.MEDIUM,
        List.of(
            new FireMode(
                "Automatic",
                1,
                2,
                AVPSoundEvents.ITEM_WEAPON_AK_47_SHOOT,
                0.0F
            )
        ),
        2F * 2,
        0.13F,
        32,
        20 * 5,
        AVPSoundEvents.ITEM_WEAPON_GENERIC_SHOOT_FAIL,
        AVPSoundEvents.ITEM_WEAPON_GENERIC_RELOAD
    );

    public static final WeaponItemData FLAMETHROWER_SEVASTOPOL = new WeaponItemData(
        1.0F,
        WeaponDamageTypes.MEDIUM,
        List.of(
            new FireMode(
                "Automatic",
                1,
                1,
                AVPSoundEvents.ITEM_WEAPON_FLAMETHROWER_SHOOT,
                0.0F
            )
        ),
        1F,
        0.13F,
        32,
        20 * 5,
        AVPSoundEvents.ITEM_WEAPON_GENERIC_SHOOT_FAIL,
        AVPSoundEvents.ITEM_WEAPON_GENERIC_RELOAD
    );

    public static final WeaponItemData M41A_PULSE_RIFLE = new WeaponItemData(
        1.0F,
        WeaponDamageTypes.MEDIUM,
        List.of(
            new FireMode(
                "Burst",
                4,
                10,
                AVPSoundEvents.ITEM_WEAPON_PULSE_RIFLE_SHOOT,
                1F
            ),
            new FireMode(
                "Automatic",
                1,
                1,
                AVPSoundEvents.ITEM_WEAPON_PULSE_RIFLE_SHOOT, // TODO:
                0.25F
            )
        ),
        4F,
        0.13F,
        99,
        20 * 3,
        AVPSoundEvents.ITEM_WEAPON_GENERIC_SHOOT_FAIL,
        AVPSoundEvents.ITEM_WEAPON_GENERIC_RELOAD
    );

    public static final WeaponItemData SHOTGUN = new WeaponItemData(
        1.0F,
        WeaponDamageTypes.MEDIUM,
        List.of(
            new FireMode(
                "SemiAutomatic",
                1,
                20,
                AVPSoundEvents.ITEM_WEAPON_SHOTGUN_SHOOT,
                1.5F
            )
        ),
        8F * 2,
        0.5F,
        6,
        20 * 4,
        AVPSoundEvents.ITEM_WEAPON_GENERIC_SHOOT_FAIL,
        AVPSoundEvents.ITEM_WEAPON_GENERIC_RELOAD
    );

    public static final WeaponItemData SNIPER_RIFLE = new WeaponItemData(
        1.0F,
        WeaponDamageTypes.HEAVY,
        List.of(
            new FireMode(
                "SemiAutomatic",
                1,
                30,
                AVPSoundEvents.ITEM_WEAPON_SNIPER_RIFLE_SHOOT,
                1.5F
            )
        ),
        15F * 2,
        0.13F,
        6,
        20 * 7 + 10,
        AVPSoundEvents.ITEM_WEAPON_GENERIC_SHOOT_FAIL,
        AVPSoundEvents.ITEM_WEAPON_GENERIC_RELOAD
    );

    private AVPWeaponItemData() {
        throw new UnsupportedOperationException();
    }
}
