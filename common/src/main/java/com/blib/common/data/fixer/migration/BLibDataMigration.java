package com.blib.common.data.fixer.migration;

import com.blib.common.model.Version;

public interface BLibDataMigration {

    // Inclusive
    Version fromVersion();

    // Exclusive
    Version toVersion();

    void apply();
}
