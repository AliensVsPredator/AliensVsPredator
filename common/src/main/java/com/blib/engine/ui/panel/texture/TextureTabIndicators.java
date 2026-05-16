package com.blib.engine.ui.panel.texture;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.blib.engine.modeler.ModelerScene;
import com.blib.engine.modeler.texture.TextureSaveState;
import com.blib.engine.ui.dock.Panel;

@ApiStatus.Internal
public final class TextureTabIndicators {

    private static final int UNSAVED_COLOR = 0xFFE6C26B;

    private TextureTabIndicators() {}

    public static @Nullable Panel.TabIndicator activeTextureDirty() {
        var active = ModelerScene.get().activeTexture;
        if (active == null || !TextureSaveState.isDirty(active)) {
            return null;
        }
        return new Panel.TabIndicator(UNSAVED_COLOR, Component.literal("Active texture has unsaved changes."));
    }

    public static @Nullable Panel.TabIndicator anyTextureDirty() {
        var dirtyCount = 0;
        for (var texture : ModelerScene.get().textures) {
            if (TextureSaveState.isDirty(texture)) {
                dirtyCount++;
            }
        }
        if (dirtyCount <= 0) {
            return null;
        }
        var label = dirtyCount == 1 ? "1 texture has unsaved changes." : dirtyCount + " textures have unsaved changes.";
        return new Panel.TabIndicator(UNSAVED_COLOR, Component.literal(label));
    }
}
