package org.avp.neoforge.common.data;

import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Set;

import org.avp.common.AVPConstants;
import org.avp.neoforge.common.entity.AVPNeoForgeEntitySpawns;

public class AVPNeoForgeData {

    public static void handleGatherDataEvent(GatherDataEvent event) {
        var generator = event.getGenerator();

        var datapackBuiltinEntriesProvider = new DatapackBuiltinEntriesProvider(
            generator.getPackOutput(),
            event.getLookupProvider(),
            AVPNeoForgeEntitySpawns.REGISTRY_SET_BUILDER,
            Set.of(AVPConstants.MOD_ID)
        );

        generator.addProvider(true, datapackBuiltinEntriesProvider);
    }

    private AVPNeoForgeData() {
        throw new UnsupportedOperationException();
    }
}
