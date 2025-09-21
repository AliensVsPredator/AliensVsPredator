package com.avp.common.data.fixer.migration.impl;

import com.lib.common.util.Version;
import net.minecraft.core.registries.BuiltInRegistries;

import com.avp.common.data.fixer.AVPDataFixerEntry;
import com.avp.common.data.fixer.AVPDataFixerRegistry;
import com.avp.common.data.fixer.migration.AVPDataMigration;

public class AVP_Migrate_0_2_2_To_0_2_3 implements AVPDataMigration {

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
        register(AVPDataFixerEntry.avpToAvp(BuiltInRegistries.ITEM, "bullet_tip", "lead_nugget"));
        register(AVPDataFixerEntry.avpToMc(BuiltInRegistries.ITEM, "small_casing", "gunpowder"));
        register(AVPDataFixerEntry.avpToMc(BuiltInRegistries.ITEM, "medium_casing", "gunpowder"));
        register(AVPDataFixerEntry.avpToMc(BuiltInRegistries.ITEM, "heavy_casing", "gunpowder"));
        register(AVPDataFixerEntry.avpToMc(BuiltInRegistries.ITEM, "shotgun_casing", "gunpowder"));
        register(AVPDataFixerEntry.avpToMc(BuiltInRegistries.ITEM, "caseless_cartridge", "gunpowder"));
    }

    public static void register(AVPDataFixerEntry entry) {
        AVPDataFixerRegistry.register(entry);
    }
}
