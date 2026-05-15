package com.blib.internal.common.property;

import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.ApiStatus;

import com.blib.api.common.property.v1.BLibProperties;
import com.blib.api.common.registry.v1.BLibBuiltInRegistries;
import com.blib.internal.common.util.BLibSaveTiming;
import com.blib.mod.BLib;

@ApiStatus.Internal
public final class BLibPropertyContainerSaveHandler {

    public static final BLibPropertyContainerSaveHandler INSTANCE = new BLibPropertyContainerSaveHandler();

    private BLibPropertyContainerSaveHandler() {}

    public void save(MinecraftServer server) {
        var totalContainers = 0;
        var dirtyContainers = 0;

        for (var entry : BLibBuiltInRegistries.PROPERTY_CONTAINER_TYPES) {
            totalContainers++;
            var container = entry.container();

            if (!container.isDirty()) {
                continue;
            }

            dirtyContainers++;
            BLibSaveTiming.time(
                BLib.LOGGER,
                "property container " + entry.filePath(),
                () -> {
                    var result = BLibProperties.save(entry.filePath(), container, container.getSchema());

                    if (result.isErr()) {
                        BLib.LOGGER.error("Failed to save property container: {}", result.unwrapErr());
                    }
                }
            );

            container.clearDirty();
        }

        BLib.LOGGER.info(
            "[BLib save timing] property containers scanned={} dirty={}",
            totalContainers,
            dirtyContainers
        );
    }
}
