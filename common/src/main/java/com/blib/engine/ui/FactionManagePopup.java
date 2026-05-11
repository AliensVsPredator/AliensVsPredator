package com.blib.engine.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.blib.internal.client.faction.ClientEntityFactionsCache;
import com.blib.internal.client.faction.ClientFactionDirectoryCache;
import com.blib.mod.BLib;
import com.blib.mod.common.network.packet.C2SAddFactionMemberPayload;
import com.blib.mod.common.network.packet.C2SRemoveFactionMemberPayload;

/**
 * Cursor-anchored popup that lets the user toggle the selected entity's membership in every faction. One row per
 * faction (from {@link ClientFactionDirectoryCache}), with a check mark next to the factions the entity already belongs
 * to ({@link ClientEntityFactionsCache}). Clicking a row sends the appropriate add / remove packet; the server's
 * proactive {@code S2CEntityFactionsPayload} push refreshes the cache, and the popup re-renders with the new state on
 * the next frame.
 * <p>
 * Lifecycle mirrors {@link HslColorPickerPopup}: a singleton {@link #openPopup} field; the workspace routes mouse +
 * scroll + key events through {@link #getOpenPopup} and calls {@link #closeOpenPopup} on Esc / workspace close.
 * <p>
 * Scroll: caps content to {@link #MAX_VISIBLE_ROWS} rows; overflow scrolls vertically. The directory's typical size (a
 * handful of factions per project) fits without scrolling, but the path exists for larger setups.
 */
@ApiStatus.Internal
public final class FactionManagePopup {

    private static final int POPUP_WIDTH = 200;

    private static final int ROW_HEIGHT = 14;

    private static final int HEADER_HEIGHT = 16;

    private static final int MAX_VISIBLE_ROWS = 10;

    private static final int PADDING_X = 8;

    private static final int BORDER_THICKNESS = 1;

    private static final int BG_COLOR = 0xF01A1A1F;

    private static final int BORDER_COLOR = 0xFF353540;

    private static final int HEADER_BG_COLOR = 0xFF252530;

    private static final int ROW_HOVER_BG_COLOR = 0xFF353540;

    private static final int TEXT_COLOR = 0xFFD0D0D0;

    private static final int MUTED_TEXT_COLOR = 0xFF808088;

    private static final int CHECK_COLOR = 0xFFE6C26B;

    private static final int SWATCH_SIZE = 8;

    private static final int SWATCH_GAP = 6;

    private static @Nullable FactionManagePopup openPopup;

    private final int anchorX;

    private final int anchorY;

    private final UUID entityUuid;

    private final String entityDisplayName;

    private boolean positionResolved;

    private int popupX;

    private int popupY;

    private int scrollOffset;

    private FactionManagePopup(int anchorX, int anchorY, UUID entityUuid, String entityDisplayName) {
        this.anchorX = anchorX;
        this.anchorY = anchorY;
        this.entityUuid = entityUuid;
        this.entityDisplayName = entityDisplayName;
    }

    public static void openAt(int anchorX, int anchorY, UUID entityUuid, String entityDisplayName) {
        openPopup = new FactionManagePopup(anchorX, anchorY, entityUuid, entityDisplayName);
        // Prime the reverse-lookup cache so the first render shows existing check marks (otherwise every faction
        // looks unchecked until the first frame's render triggers ensureRequested + the reply lands).
        ClientEntityFactionsCache.ensureRequested(entityUuid);
    }

    public static @Nullable FactionManagePopup getOpenPopup() {
        return openPopup;
    }

    public static void closeOpenPopup() {
        openPopup = null;
    }

    private int contentHeight() {
        var visible = Math.min(MAX_VISIBLE_ROWS, ClientFactionDirectoryCache.entries().size());
        return HEADER_HEIGHT + Math.max(ROW_HEIGHT, visible * ROW_HEIGHT) + 2 * BORDER_THICKNESS;
    }

    public boolean isInside(double mouseX, double mouseY) {
        if (!positionResolved) {
            return false;
        }
        return mouseX >= popupX && mouseX < popupX + POPUP_WIDTH && mouseY >= popupY && mouseY < popupY + contentHeight();
    }

    public void render(GuiGraphics graphics, int mouseX, int mouseY, int logicalWidth, int logicalHeight) {
        if (!positionResolved) {
            popupX = Math.max(4, Math.min(logicalWidth - POPUP_WIDTH - 4, anchorX));
            var height = contentHeight();
            popupY = Math.max(4, Math.min(logicalHeight - height - 4, anchorY));
            positionResolved = true;
        }

        var height = contentHeight();
        graphics.fill(popupX, popupY, popupX + POPUP_WIDTH, popupY + height, BG_COLOR);
        // Border
        graphics.fill(popupX, popupY, popupX + POPUP_WIDTH, popupY + BORDER_THICKNESS, BORDER_COLOR);
        graphics.fill(popupX, popupY + height - BORDER_THICKNESS, popupX + POPUP_WIDTH, popupY + height, BORDER_COLOR);
        graphics.fill(popupX, popupY, popupX + BORDER_THICKNESS, popupY + height, BORDER_COLOR);
        graphics.fill(popupX + POPUP_WIDTH - BORDER_THICKNESS, popupY, popupX + POPUP_WIDTH, popupY + height, BORDER_COLOR);

        var font = Minecraft.getInstance().font;
        var headerY = popupY + BORDER_THICKNESS;
        graphics.fill(
            popupX + BORDER_THICKNESS,
            headerY,
            popupX + POPUP_WIDTH - BORDER_THICKNESS,
            headerY + HEADER_HEIGHT,
            HEADER_BG_COLOR
        );
        var headerText = "Manage factions: " + entityDisplayName;
        var truncated = font.plainSubstrByWidth(headerText, POPUP_WIDTH - 2 * PADDING_X);
        graphics.drawString(
            font,
            Component.literal(truncated),
            popupX + PADDING_X,
            // +2 compensates for MC font's descender padding so labels visually center; see MenuBarPanel.
            headerY + (HEADER_HEIGHT - font.lineHeight + 2) / 2,
            TEXT_COLOR,
            false
        );

        renderRows(graphics, font, mouseX, mouseY);
    }

    private void renderRows(GuiGraphics graphics, net.minecraft.client.gui.Font font, int mouseX, int mouseY) {
        var entries = ClientFactionDirectoryCache.entries();
        var rowsTop = popupY + BORDER_THICKNESS + HEADER_HEIGHT;
        if (entries.isEmpty()) {
            graphics.drawString(
                font,
                Component.literal("(no factions in this project)"),
                popupX + PADDING_X,
                rowsTop + (ROW_HEIGHT - font.lineHeight + 2) / 2,
                MUTED_TEXT_COLOR,
                false
            );
            return;
        }

        Set<net.minecraft.resources.ResourceLocation> currentMemberships = currentMemberships();

        var visible = Math.min(MAX_VISIBLE_ROWS, entries.size());
        for (var i = 0; i < visible; i++) {
            var globalIdx = i + scrollOffset;
            if (globalIdx >= entries.size()) {
                break;
            }
            var entry = entries.get(globalIdx);
            var rowY = rowsTop + i * ROW_HEIGHT;
            var hovered = mouseX >= popupX + BORDER_THICKNESS
                && mouseX < popupX + POPUP_WIDTH - BORDER_THICKNESS
                && mouseY >= rowY
                && mouseY < rowY + ROW_HEIGHT;
            if (hovered) {
                graphics.fill(
                    popupX + BORDER_THICKNESS,
                    rowY,
                    popupX + POPUP_WIDTH - BORDER_THICKNESS,
                    rowY + ROW_HEIGHT,
                    ROW_HOVER_BG_COLOR
                );
            }
            // Color swatch at the left.
            var swatchX = popupX + PADDING_X;
            var swatchY = rowY + (ROW_HEIGHT - SWATCH_SIZE) / 2;
            graphics.fill(swatchX, swatchY, swatchX + SWATCH_SIZE, swatchY + SWATCH_SIZE, entry.color() | 0xFF000000);
            // Faction name, truncated to fit before the check column.
            var labelX = swatchX + SWATCH_SIZE + SWATCH_GAP;
            var labelMax = (popupX + POPUP_WIDTH - PADDING_X - 10) - labelX;
            var truncated = font.plainSubstrByWidth(entry.name(), labelMax);
            graphics.drawString(
                font,
                Component.literal(truncated),
                labelX,
                rowY + (ROW_HEIGHT - font.lineHeight + 2) / 2,
                TEXT_COLOR,
                false
            );
            // Check mark on the right edge if the entity is a member.
            if (currentMemberships.contains(entry.id())) {
                graphics.drawString(
                    font,
                    Component.literal("✓"),
                    popupX + POPUP_WIDTH - PADDING_X - font.width("✓"),
                    rowY + (ROW_HEIGHT - font.lineHeight + 2) / 2,
                    CHECK_COLOR,
                    false
                );
            }
        }
    }

    /** Defensive copy of the reverse-lookup result so per-frame iteration is stable even if the cache is replaced. */
    private Set<net.minecraft.resources.ResourceLocation> currentMemberships() {
        var list = ClientEntityFactionsCache.get(entityUuid);
        return list == null ? Set.of() : new HashSet<>(list);
    }

    /** Map cursor to the faction row under it (or -1). Handles scroll offset. */
    private int hitRowIndexAt(double mouseX, double mouseY) {
        if (!isInside(mouseX, mouseY)) {
            return -1;
        }
        var rowsTop = popupY + BORDER_THICKNESS + HEADER_HEIGHT;
        if (mouseY < rowsTop) {
            return -1;
        }
        var visibleIdx = (int) ((mouseY - rowsTop) / ROW_HEIGHT);
        if (visibleIdx < 0 || visibleIdx >= MAX_VISIBLE_ROWS) {
            return -1;
        }
        var globalIdx = visibleIdx + scrollOffset;
        var entries = ClientFactionDirectoryCache.entries();
        if (globalIdx < 0 || globalIdx >= entries.size()) {
            return -1;
        }
        return globalIdx;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return false;
        }
        var idx = hitRowIndexAt(mouseX, mouseY);
        if (idx < 0) {
            return false;
        }
        var entry = ClientFactionDirectoryCache.entries().get(idx);
        var currentlyMember = currentMemberships().contains(entry.id());
        if (currentlyMember) {
            BLib.MOD.networking().sendToServer(new C2SRemoveFactionMemberPayload(entry.id(), entityUuid));
        } else {
            BLib.MOD.networking().sendToServer(new C2SAddFactionMemberPayload(entry.id(), entityUuid));
        }
        return true;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double scrollY) {
        if (!isInside(mouseX, mouseY)) {
            return false;
        }
        var entries = ClientFactionDirectoryCache.entries();
        if (entries.size() <= MAX_VISIBLE_ROWS) {
            // Not scrollable, but consume so underlying panels don't scroll either.
            return true;
        }
        var max = entries.size() - MAX_VISIBLE_ROWS;
        scrollOffset = Math.max(0, Math.min(max, scrollOffset - (int) Math.signum(scrollY)));
        return true;
    }
}
