package com.alien.common.data;

import com.lib.common.data.AdvancementAccess;

public class AlienAdvancements {

    public static final AdvancementAccess KILL_A_HIVE = create("kill_a_hive");

    public static final AdvancementAccess KILL_A_ROYAL_ALIEN = create("kill_a_royal_alien");

    public static final AdvancementAccess KILL_ALL_ALIENS = create("kill_all_aliens");

    public static final AdvancementAccess KILL_AN_ALIEN = create("kill_an_alien");

    public static final AdvancementAccess REMOVE_EMBRYO_WITH_CHORUS_FRUIT = create("remove_embryo_with_chorus_fruit");

    public static final AdvancementAccess ROOT = create("root");

    public static final AdvancementAccess SHEAR_AN_OVOMORPH = create("shear_an_ovomorph");

    public static final AdvancementAccess WEAR_CHITIN_ARMOR = create("chitin_armor");

    public static final AdvancementAccess WEAR_PLATED_CHITIN_ARMOR = create("plated_chitin_armor");

    private static AdvancementAccess create(String path) {
        return new AdvancementAccess("aliens", path);
    }
}
