package com.human.common.data;

import com.lib.common.data.AdvancementAccess;

public class HumanAdvancements {

    public static final AdvancementAccess BLAST_STEEL = create("blast_steel");

    public static final AdvancementAccess EQUIP_FULL_ARMOR_SET_WITH_ARMOR_CASE = create("equip_full_armor_set_with_armor_case");

    public static final AdvancementAccess FILL_CANISTER = create("fill_canister");

    public static final AdvancementAccess HAS_GUN = create("has_gun");

    public static final AdvancementAccess ROOT = create("root");

    public static final AdvancementAccess SMELT_BRASS = create("smelt_brass");

    public static final AdvancementAccess SMELT_PLASTIC = create("smelt_plastic");

    public static final AdvancementAccess SMELT_TITANIUM = create("smelt_titanium");

    private static AdvancementAccess create(String path) {
        return new AdvancementAccess("humans", path);
    }
}
