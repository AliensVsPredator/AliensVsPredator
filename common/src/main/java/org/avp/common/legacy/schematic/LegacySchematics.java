package org.avp.common.legacy.schematic;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.avp.api.Holder;
import org.avp.common.legacy.schematic.resolver.DerelictSchematicResolver;
import org.avp.common.legacy.schematic.resolver.LVRockSchematicResolver;
import org.avp.common.legacy.schematic.resolver.TestSchematicResolver;
import org.avp.common.service.Services;

public final class LegacySchematics {

    private static final String SCHEMATICS_DIRECTORY = "schematics";

    private static final Path DERELICT_OLD_PATH;

    private static final Path DERELICT_PATH;

    private static final Path TEST_PATH;

    private static final Path LV_426_SPIKE_01_PATH;

    private static final Path LV_426_SPIKE_02_PATH;

    private static final Path LV_426_SPIKE_03_PATH;

    private static final Path LV_426_SPIKE_04_PATH;

    private static final Path LV_426_SPIKE_05_PATH;

    private static final Path LV_426_SPIKE_06_PATH;

    static {
        var gameDirPath = Services.PLATFORM.getGameDirectory();
        var gameDirString = gameDirPath.toString();
        DERELICT_OLD_PATH = Paths.get(gameDirString, SCHEMATICS_DIRECTORY, "derelictold.schematic");
        DERELICT_PATH = Paths.get(gameDirString, SCHEMATICS_DIRECTORY, "derelict.schematic");
        LV_426_SPIKE_01_PATH = Paths.get(gameDirString, SCHEMATICS_DIRECTORY, "lv426spike01.schematic");
        LV_426_SPIKE_02_PATH = Paths.get(gameDirString, SCHEMATICS_DIRECTORY, "lv426spike02.schematic");
        LV_426_SPIKE_03_PATH = Paths.get(gameDirString, SCHEMATICS_DIRECTORY, "lv426spike03.schematic");
        LV_426_SPIKE_04_PATH = Paths.get(gameDirString, SCHEMATICS_DIRECTORY, "lv426spike04.schematic");
        LV_426_SPIKE_05_PATH = Paths.get(gameDirString, SCHEMATICS_DIRECTORY, "lv426spike05.schematic");
        LV_426_SPIKE_06_PATH = Paths.get(gameDirString, SCHEMATICS_DIRECTORY, "lv426spike06.schematic");
        TEST_PATH = Paths.get(gameDirString, SCHEMATICS_DIRECTORY, "test.schematic");
    }

    public static final Holder<LegacySchematic> DERELICT_OLD_SCHEMATIC = new Holder<>(
        "derelict_old",
        () -> LegacySchematicLoader.loadFromFile(DERELICT_OLD_PATH.toFile(), Map.of()).orElse(null)
    );

    public static final Holder<LegacySchematic> DERELICT_SCHEMATIC = new Holder<>(
        "derelict",
        () -> LegacySchematicLoader.loadFromFile(DERELICT_PATH.toFile(), DerelictSchematicResolver.RESOLVER_MAP)
            .orElse(null)
    );

    public static final Holder<LegacySchematic> LV_426_SPIKE_01_SCHEMATIC = loadLVSpike("lv_426_spike_01", LV_426_SPIKE_01_PATH);

    public static final Holder<LegacySchematic> LV_426_SPIKE_02_SCHEMATIC = loadLVSpike("lv_426_spike_02", LV_426_SPIKE_02_PATH);

    public static final Holder<LegacySchematic> LV_426_SPIKE_03_SCHEMATIC = loadLVSpike("lv_426_spike_03", LV_426_SPIKE_03_PATH);

    public static final Holder<LegacySchematic> LV_426_SPIKE_04_SCHEMATIC = loadLVSpike("lv_426_spike_04", LV_426_SPIKE_04_PATH);

    public static final Holder<LegacySchematic> LV_426_SPIKE_05_SCHEMATIC = loadLVSpike("lv_426_spike_05", LV_426_SPIKE_05_PATH);

    public static final Holder<LegacySchematic> LV_426_SPIKE_06_SCHEMATIC = loadLVSpike("lv_426_spike_06", LV_426_SPIKE_06_PATH);

    public static final Holder<LegacySchematic> TEST_SCHEMATIC = new Holder<>(
        "test",
        () -> LegacySchematicLoader.loadFromFile(TEST_PATH.toFile(), TestSchematicResolver.RESOLVER_MAP).orElse(null)
    );

    private static Holder<LegacySchematic> loadLVSpike(String name, Path path) {
        return new Holder<>(
            name,
            () -> LegacySchematicLoader.loadFromFile(path.toFile(), LVRockSchematicResolver.RESOLVER_MAP).orElse(null)
        );
    }

    private LegacySchematics() {
        throw new UnsupportedOperationException();
    }

}
