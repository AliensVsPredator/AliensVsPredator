package com.avp.common.data.fixer.migration.impl;

import com.blib.common.data.fixer.BLibDataFixerRegistry;
import com.blib.common.data.fixer.migration.BLibDataMigration;
import com.lib.common.util.Version;
import net.minecraft.core.registries.BuiltInRegistries;

import com.avp.common.data.fixer.migration.AVPDataFixerEntryUtil;

public class AVP_Migrate_0_2_2_To_0_2_3 implements BLibDataMigration {

    @Override
    public Version fromVersion() {
        return new Version(0, 2, 2);
    }

    @Override
    public Version toVersion() {
        return new Version(0, 2, 3);
    }

    @Override
    public void apply() {
        register(AVPDataFixerEntryUtil.avpToAvp(BuiltInRegistries.ITEM, "bullet_tip", "lead_nugget"));
        register(AVPDataFixerEntryUtil.avpToMc(BuiltInRegistries.ITEM, "small_casing", "gunpowder"));
        register(AVPDataFixerEntryUtil.avpToMc(BuiltInRegistries.ITEM, "medium_casing", "gunpowder"));
        register(AVPDataFixerEntryUtil.avpToMc(BuiltInRegistries.ITEM, "heavy_casing", "gunpowder"));
        register(AVPDataFixerEntryUtil.avpToMc(BuiltInRegistries.ITEM, "shotgun_casing", "gunpowder"));
        register(AVPDataFixerEntryUtil.avpToMc(BuiltInRegistries.ITEM, "caseless_cartridge", "gunpowder"));
    }

    public static void register(BLibDataFixerRegistry.Entry entry) {
        BLibDataFixerRegistry.register(entry);
    }
}
