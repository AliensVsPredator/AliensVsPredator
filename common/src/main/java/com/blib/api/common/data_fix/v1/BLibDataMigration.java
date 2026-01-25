package com.blib.api.common.data_fix.v1;

import com.blib.api.common.mod.v1.model.Version;

public interface BLibDataMigration {

    // Inclusive
    Version fromVersion();

    // Exclusive
    Version toVersion();

    void apply();
}
