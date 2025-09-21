package com.avp.common.data.fixer.migration.impl;

import com.lib.common.util.Version;
import net.minecraft.core.registries.BuiltInRegistries;

import com.avp.common.data.fixer.AVPDataFixerEntry;
import com.avp.common.data.fixer.AVPDataFixerRegistry;
import com.avp.common.data.fixer.migration.AVPDataMigration;

public class AVP_Migrate_0_1_9_To_0_2_0 implements AVPDataMigration {

    @Override
    public Version fromVersion() {
        return new Version(0, 1, 9);
    }

    @Override
    public Version toVersion() {
        return new Version(0, 2, 0);
    }

    @Override
    public void apply() {
        register(AVPDataFixerEntry.avpToAvp(BuiltInRegistries.ENTITY_TYPE, "ovamorph", "ovomorph"));
        register(AVPDataFixerEntry.avpToAvp(BuiltInRegistries.ENTITY_TYPE, "royal_ovamorph", "royal_ovomorph"));
        register(AVPDataFixerEntry.avpToAvp(BuiltInRegistries.ENTITY_TYPE, "aberrant_ovamorph", "aberrant_ovomorph"));
        register(AVPDataFixerEntry.avpToAvp(BuiltInRegistries.ENTITY_TYPE, "royal_aberrant_ovamorph", "royal_aberrant_ovomorph"));
        register(AVPDataFixerEntry.avpToAvp(BuiltInRegistries.ENTITY_TYPE, "nether_ovamorph", "nether_ovomorph"));
        register(AVPDataFixerEntry.avpToAvp(BuiltInRegistries.ENTITY_TYPE, "royal_nether_ovamorph", "royal_nether_ovomorph"));

        register(AVPDataFixerEntry.avpToAvp(BuiltInRegistries.ITEM, "ovamorph_spawn_egg", "ovomorph_spawn_egg"));
        register(AVPDataFixerEntry.avpToAvp(BuiltInRegistries.ITEM, "royal_ovamorph_spawn_egg", "royal_ovomorph_spawn_egg"));
        register(AVPDataFixerEntry.avpToAvp(BuiltInRegistries.ITEM, "aberrant_ovamorph_spawn_egg", "aberrant_ovomorph_spawn_egg"));
        register(AVPDataFixerEntry.avpToAvp(BuiltInRegistries.ITEM, "royal_aberrant_ovamorph_spawn_egg", "royal_aberrant_ovomorph_spawn_egg"));
        register(AVPDataFixerEntry.avpToAvp(BuiltInRegistries.ITEM, "nether_ovamorph_spawn_egg", "nether_ovomorph_spawn_egg"));
        register(AVPDataFixerEntry.avpToAvp(BuiltInRegistries.ITEM, "royal_nether_ovamorph_spawn_egg", "royal_nether_ovomorph_spawn_egg"));
    }

    public static void register(AVPDataFixerEntry entry) {
        AVPDataFixerRegistry.register(entry);
    }
}
