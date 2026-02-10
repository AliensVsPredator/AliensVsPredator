package com.blib.mod.common.registry.init;

import java.util.function.Supplier;

import com.blib.api.common.property.v1.BLibPropertyContainerType;
import com.blib.api.common.registry.v1.BLibBuiltInRegistries;
import com.blib.api.common.registry.v1.BLibHolder;
import com.blib.api.common.registry.v1.BLibRegistry;
import com.blib.mod.BLib;
import com.blib.mod.common.property.BLibModPropertyAccess;

public class BLibPropertyContainerTypes {

    private static final BLibRegistry<BLibPropertyContainerType> REGISTRY = BLib.MOD.registries()
        .create(BLibBuiltInRegistries.PROPERTY_CONTAINER_TYPES);

    public static final BLibHolder<BLibPropertyContainerType> BLIB = register(
        "blib",
        () -> new BLibPropertyContainerType(
            BLibModPropertyAccess.INSTANCE.getContainer(),
            BLibModPropertyAccess.INSTANCE.getPath()
        )
    );

    private static BLibHolder<BLibPropertyContainerType> register(
        String path,
        Supplier<BLibPropertyContainerType> supplier
    ) {
        return REGISTRY.createHolder(path, supplier);
    }

    public static void initialize() {
        REGISTRY.registerAll();
    }
}
