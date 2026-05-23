package com.blib.fabric.internal.data.lang.en_us.provider;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;

/**
 * en_us strings for BLib's vanilla {@link net.minecraft.client.KeyMapping}s and their category. These appear in MC's
 * Controls / Keybindings menu — the category label groups all BLib bindings together.
 */
@ApiStatus.Internal
public final class EnUsKeybindingProvider {

    public static final Consumer<FabricLanguageProvider.TranslationBuilder> CONSUMER = builder -> {
        builder.add("key.categories.blib", "BLib");
        builder.add("key.blib.toggle_engine", "Toggle BLib Engine");
    };

    private EnUsKeybindingProvider() {}
}
