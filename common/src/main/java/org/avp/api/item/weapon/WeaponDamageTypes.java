package org.avp.api.item.weapon;

public class WeaponDamageTypes {

    public static final WeaponDamageType LIGHT = new WeaponDamageType();

    public static final WeaponDamageType MEDIUM = new WeaponDamageType();

    public static final WeaponDamageType HEAVY = new WeaponDamageType();

    private WeaponDamageTypes() {
        throw new UnsupportedOperationException();
    }
}
