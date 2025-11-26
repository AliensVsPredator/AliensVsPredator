package com.avp.common.data.fixer.migration;

import com.blib.common.data.fixer.migration.BLibDataMigration;
import com.blib.service.BLibServices;

import java.util.List;

import com.avp.AVP;
import com.avp.common.data.fixer.migration.impl.AVP_Migrate_0_1_9_To_0_2_0;
import com.avp.common.data.fixer.migration.impl.AVP_Migrate_0_2_2_To_0_2_3;

public class AVPDataMigrations {

    private static final List<BLibDataMigration> MIGRATIONS = List.of(
        new AVP_Migrate_0_1_9_To_0_2_0(),
        new AVP_Migrate_0_2_2_To_0_2_3()
    );

    public static void initialize() {
        var version = BLibServices.MOD_LOADER.getModVersion(AVP.MOD_ID);

        if (version != null) {
            MIGRATIONS.forEach(BLibDataMigration::apply);
        }
    }
}
