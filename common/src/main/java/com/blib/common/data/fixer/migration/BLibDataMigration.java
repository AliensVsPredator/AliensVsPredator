package com.blib.common.data.fixer.migration;

import com.lib.common.util.Version;

public interface BLibDataMigration {

    // Inclusive
    Version fromVersion();

    // Exclusive
    Version toVersion();

    void apply();
}
