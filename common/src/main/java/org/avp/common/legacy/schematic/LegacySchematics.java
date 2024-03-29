package org.avp.common.legacy.schematic;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.avp.common.legacy.schematic.resolver.DerelictSchematicResolver;
import org.avp.common.legacy.schematic.resolver.TestSchematicResolver;
import org.avp.common.service.Services;
import org.avp.api.GameObject;

/**
 * @author Boston Vanseghi
 */
public final class LegacySchematics {

    private static final String SCHEMATICS_DIRECTORY = "schematics";

    private static final Path DERELICT_OLD_PATH;

    private static final Path DERELICT_PATH;

    private static final Path TEST_PATH;

    static {
        var gameDirPath = Services.PLATFORM.getGameDirectory();
        var gameDirString = gameDirPath.toString();
        DERELICT_OLD_PATH = Paths.get(gameDirString, SCHEMATICS_DIRECTORY, "derelictold.schematic");
        DERELICT_PATH = Paths.get(gameDirString, SCHEMATICS_DIRECTORY, "derelict.schematic");
        TEST_PATH = Paths.get(gameDirString, SCHEMATICS_DIRECTORY, "test.schematic");
    }

    public static final GameObject<LegacySchematic> DERELICT_OLD_SCHEMATIC = new GameObject<>(
        "derelict_old",
        () -> LegacySchematicLoader.loadFromFile(DERELICT_OLD_PATH.toFile(), Map.of()).orElse(null)
    );

    public static final GameObject<LegacySchematic> DERELICT_SCHEMATIC = new GameObject<>(
        "derelict",
        () -> LegacySchematicLoader.loadFromFile(DERELICT_PATH.toFile(), DerelictSchematicResolver.RESOLVER_MAP)
            .orElse(null)
    );

    public static final GameObject<LegacySchematic> TEST_SCHEMATIC = new GameObject<>(
        "test",
        () -> LegacySchematicLoader.loadFromFile(TEST_PATH.toFile(), TestSchematicResolver.RESOLVER_MAP).orElse(null)
    );

    private LegacySchematics() {
        throw new UnsupportedOperationException();
    }

}
