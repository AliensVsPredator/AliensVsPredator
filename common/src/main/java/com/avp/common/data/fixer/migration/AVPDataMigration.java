package com.avp.common.data.fixer.migration;

import com.lib.common.util.Version;

public interface AVPDataMigration {

    // Inclusive
    Version fromVersion();

    // Exclusive
    Version toVersion();

    void apply();
}
