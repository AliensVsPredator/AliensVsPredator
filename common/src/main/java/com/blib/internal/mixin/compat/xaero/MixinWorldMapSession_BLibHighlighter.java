package com.blib.internal.mixin.compat.xaero;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xaero.map.WorldMapSession;
import xaero.map.highlight.HighlighterRegistry;

import com.blib.internal.client.territory.compat.xaero.BLibChunkHighlighter;

@Mixin(value = WorldMapSession.class, remap = false)
public class MixinWorldMapSession_BLibHighlighter {

    @Redirect(
        method = "init",
        at = @At(
            value = "INVOKE",
            target = "Lxaero/map/highlight/HighlighterRegistry;end()V"
        ),
        remap = false
    )
    private void blib$registerHighlighter(HighlighterRegistry instance) {
        BLibChunkHighlighter.register(instance);
        instance.end();
    }
}
