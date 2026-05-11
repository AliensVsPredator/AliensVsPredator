package com.blib.engine.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.blib.mod.BLib;

/**
 * Single source-of-truth for the {@link Font} used by every engine UI panel. Returns a {@link Font} whose internal
 * font-set lookup ALWAYS resolves to {@code blib:engine} — so the String-typed {@code drawString}, {@code width}, and
 * {@code plainSubstrByWidth} methods all render in our custom font without each call site having to wrap its text in a
 * {@link net.minecraft.network.chat.Component} with a custom Style.
 * <p>
 * The constructed {@link Font}'s lookup lambda re-fetches the {@link net.minecraft.client.gui.font.FontSet} on every
 * call (a single {@code HashMap.get}) so a resource-pack reload that replaces the underlying FontSet is reflected
 * without us having to invalidate this cache. The {@code Font} instance itself is fine to cache because it only holds
 * the lookup lambda — not a direct FontSet reference.
 * <p>
 * Reaching the FontSet requires widening {@code Minecraft.fontManager} and {@code FontManager.getFontSetCached}; both
 * are exposed via this project's access widener (Fabric) and access transformer (NeoForge).
 */
@ApiStatus.Internal
public final class EngineFont {

    /** Font id resolved from {@code assets/blib/font/engine.json}. */
    public static final ResourceLocation ID = BLib.MOD.resources().createLocation("engine");

    private static @Nullable Font cached;

    private EngineFont() {}

    public static Font get() {
        var font = cached;
        if (font == null) {
            // Lookup ignores the requested font id and always returns OUR font set — that's what makes the String
            // overloads (drawString(String,...), width(String), plainSubstrByWidth(...)) render in JetBrains Mono
            // even though they internally pass {@code Style.DEFAULT_FONT} to the lookup.
            font = new Font(rl -> Minecraft.getInstance().fontManager.getFontSetCached(ID), false);
            cached = font;
        }
        return font;
    }
}
