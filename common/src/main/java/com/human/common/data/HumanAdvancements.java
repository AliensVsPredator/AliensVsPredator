package com.human.common.data;

import com.avp.AVP;
import com.blib.common.data.BLibAdvancementAccess;

public class HumanAdvancements {

    public static final BLibAdvancementAccess BLAST_STEEL = create("blast_steel");

    public static final BLibAdvancementAccess EQUIP_FULL_ARMOR_SET_WITH_ARMOR_CASE = create("equip_full_armor_set_with_armor_case");

    public static final BLibAdvancementAccess FILL_CANISTER = create("fill_canister");

    public static final BLibAdvancementAccess HAS_GUN = create("has_gun");

    public static final BLibAdvancementAccess ROOT = create("root");

    public static final BLibAdvancementAccess SMELT_BRASS = create("smelt_brass");

    public static final BLibAdvancementAccess SMELT_PLASTIC = create("smelt_plastic");

    public static final BLibAdvancementAccess SMELT_TITANIUM = create("smelt_titanium");

    private static BLibAdvancementAccess create(String path) {
        return new BLibAdvancementAccess(AVP.MOD_ID, "humans", path);
    }
}
