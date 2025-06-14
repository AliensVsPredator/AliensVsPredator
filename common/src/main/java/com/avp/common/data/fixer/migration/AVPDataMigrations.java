package com.avp.common.data.fixer.migration;

import java.util.List;

import com.avp.common.data.fixer.migration.impl.AVP_Migrate_0_1_9_To_0_2_0;
import com.avp.service.Services;

public class AVPDataMigrations {

    private static final List<AVPDataMigration> MIGRATIONS = List.of(
        new AVP_Migrate_0_1_9_To_0_2_0()
    );

    static {
        var version = Services.PLATFORM.getModVersion();

        if (version != null) {
            MIGRATIONS.forEach(AVPDataMigration::apply);
        }
    }

    public static void initialize() {}
}
