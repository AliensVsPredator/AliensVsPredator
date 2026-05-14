package com.blib.engine.ui.panel.faction;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import com.blib.engine.domain.selection.picking.FactionSelectable;
import com.blib.engine.domain.selection.picking.SelectionManager;
import com.blib.engine.ui.EngineFont;
import com.blib.engine.ui.PanelPlaceholder;
import com.blib.engine.ui.ProjectContentActionHandler;
import com.blib.engine.ui.dock.Panel;
import com.blib.engine.ui.widget.ScrollContainer;
import com.blib.engine.ui.widget.TextInput;
import com.blib.internal.client.faction.ClientFactionDirectoryCache;
import com.blib.mod.BLib;
import com.blib.mod.common.network.packet.C2SCreateFactionPayload;
import com.blib.mod.common.network.packet.C2SDeleteFactionPayload;
import com.blib.mod.common.network.packet.C2SRequestFactionDirectoryPayload;
import com.blib.mod.common.network.packet.S2CFactionDirectoryPayload;

/**
 * Faction Browser — workspace-side list of every faction currently in {@link ClientFactionDirectoryCache}. Each row
 * shows a color swatch, the faction name, member count, and a per-row Delete button. The header has a search filter +
 * [New Faction] + [Refresh].
 * <p>
 * Click a row to set the active selection to a {@link FactionSelectable} for that faction; the Inspector picks up the
 * change and renders the editable view via its {@code case FACTION} branch. New Faction creates a faction with an
 * auto-generated id (next free {@code blib:new_faction_<n>}); the user renames it via the Inspector after creation.
 * Delete uses the screen's {@link ProjectContentActionHandler} for the confirm dialog.
 */
@ApiStatus.Internal
public final class FactionBrowserPanel implements Panel {

    private static final int BACKGROUND_COLOR = 0xFF14141A;

    private static final int ROW_BG_HOVER_COLOR = 0xFF1F1F26;

    private static final int ROW_BG_SELECTED_COLOR = 0xFF2A2A38;

    private static final int ROW_TEXT_COLOR = 0xFFD0D0D0;

    private static final int ROW_TEXT_HOVER_COLOR = 0xFFFFFFFF;

    private static final int META_COLOR = 0xFF808088;

    private static final int EMPTY_TEXT_COLOR = 0xFF606068;

    private static final int BUTTON_BG = 0xFF14141A;

    private static final int BUTTON_BG_HOVER = 0xFF22222C;

    private static final int BUTTON_BORDER = 0xFF353540;

    private static final int BUTTON_TEXT = 0xFFD0D0D0;

    private static final int BUTTON_DESTRUCTIVE_TEXT = 0xFFE06868;

    private static final int CONTENT_PADDING = 5;

    private static final int SEARCH_GAP_BELOW = 4;

    private static final int ROW_HEIGHT = 16;

    private static final int SWATCH_SIZE = 10;

    private static final int BUTTON_HEIGHT = 10;

    private static final int BUTTON_WIDTH = 42;

    private static final int HEADER_BUTTON_HEIGHT = TextInput.HEIGHT;

    private static final int HEADER_BUTTON_WIDTH = 56;

    private final TextInput searchInput = new TextInput("Filter…");

    private final ScrollContainer scroll = new ScrollContainer();

    private final @Nullable ProjectContentActionHandler actionHandler;

    private final List<RowHit> rowHits = new ArrayList<>();

    private @Nullable Rect newFactionRect;

    private int rectX;

    private int rectY;

    private int rectWidth;

    private int rectHeight;

    /** True until the first directory payload arrives, so the panel can show a loading state. */
    private boolean requestedAtLeastOnce;

    public FactionBrowserPanel() {
        this(null);
    }

    public FactionBrowserPanel(@Nullable ProjectContentActionHandler actionHandler) {
        this.actionHandler = actionHandler;
    }

    @Override
    public String title() {
        return "Faction Browser";
    }

    @Override
    public void onShown() {
        scroll.reset();
        requestDirectory();
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
        this.rectX = x;
        this.rectY = y;
        this.rectWidth = width;
        this.rectHeight = height;
        rowHits.clear();
        newFactionRect = null;

        graphics.fill(x, y, x + width, y + height, BACKGROUND_COLOR);

        // Faction directory is fetched from the server. No world ⇒ no server ⇒ no directory.
        if (Minecraft.getInstance().level == null) {
            PanelPlaceholder.drawCentered(graphics, x, y, width, height, PanelPlaceholder.NEEDS_WORLD);
            return;
        }

        // First render with an empty cache → request the directory once. Subsequent renders rely on server pushes
        // (BLibFactionManager marks dirty on every mutation; postLevelTick broadcasts the fresh snapshot).
        if (!requestedAtLeastOnce && ClientFactionDirectoryCache.entries().isEmpty()) {
            requestDirectory();
        }

        var topRowY = y + CONTENT_PADDING;
        var newFactionX = x + width - CONTENT_PADDING - HEADER_BUTTON_WIDTH;
        newFactionRect = new Rect(newFactionX, topRowY, HEADER_BUTTON_WIDTH, HEADER_BUTTON_HEIGHT);
        var searchInputW = Math.max(0, newFactionX - (x + CONTENT_PADDING) - 4);
        searchInput.render(graphics, x + CONTENT_PADDING, topRowY, searchInputW, mouseX, mouseY);
        renderButton(graphics, newFactionRect, "New", mouseX, mouseY, BUTTON_TEXT);

        var listX = x + CONTENT_PADDING;
        var listY = topRowY + HEADER_BUTTON_HEIGHT + SEARCH_GAP_BELOW;
        var listW = width - 2 * CONTENT_PADDING;
        var listH = Math.max(0, height - (listY - y) - CONTENT_PADDING);
        if (listH <= 0) {
            return;
        }

        var query = searchInput.content().toLowerCase(Locale.ROOT).trim();
        var filtered = filter(query);

        if (filtered.isEmpty()) {
            var font = EngineFont.get();
            var msg = ClientFactionDirectoryCache.entries().isEmpty() ? "(no factions)" : "(no matches)";
            graphics.drawString(font, Component.literal(msg), listX, listY, EMPTY_TEXT_COLOR, false);
            return;
        }

        var contentHeight = filtered.size() * ROW_HEIGHT;
        scroll.layout(listH, contentHeight);

        var selected = currentSelectedFactionId();

        applyRawScissor(graphics, listX, listY, listW, listH);
        try {
            var scrollY = (int) scroll.scrollY();
            var firstVisibleRow = Math.max(0, scrollY / ROW_HEIGHT);
            var lastVisibleRow = Math.min(filtered.size() - 1, (scrollY + listH) / ROW_HEIGHT);
            for (var i = firstVisibleRow; i <= lastVisibleRow; i++) {
                var entry = filtered.get(i);
                var rowY = listY + i * ROW_HEIGHT - scrollY;
                renderRow(graphics, listX, rowY, listW, entry, selected, mouseX, mouseY);
            }
        } finally {
            graphics.flush();
            RenderSystem.disableScissor();
        }

        scroll.renderScrollbar(graphics, listX, listY, listW, listH, mouseX, mouseY);
    }

    private void renderRow(
        GuiGraphics graphics,
        int x,
        int y,
        int width,
        S2CFactionDirectoryPayload.FactionEntry entry,
        @Nullable ResourceLocation selectedId,
        int mouseX,
        int mouseY
    ) {
        // Reserve the scrollbar gutter so the row hover background and right-aligned Delete button stay clear of it.
        var rowRight = x + width - ScrollContainer.SCROLLBAR_GUTTER;
        var hovered = mouseX >= x && mouseX < rowRight && mouseY >= y && mouseY < y + ROW_HEIGHT;
        var isSelected = selectedId != null && selectedId.equals(entry.id());
        if (isSelected) {
            graphics.fill(x, y, rowRight, y + ROW_HEIGHT, ROW_BG_SELECTED_COLOR);
        } else if (hovered) {
            graphics.fill(x, y, rowRight, y + ROW_HEIGHT, ROW_BG_HOVER_COLOR);
        }

        var swatchX = x + 4;
        var swatchY = y + (ROW_HEIGHT - SWATCH_SIZE) / 2;
        // Color is opaque RGB; OR with 0xFF000000 so renderer treats it as solid.
        var swatchColor = (entry.color() & 0xFFFFFF) | 0xFF000000;
        graphics.fill(swatchX, swatchY, swatchX + SWATCH_SIZE, swatchY + SWATCH_SIZE, swatchColor);

        var font = EngineFont.get();
        var textY = y + (ROW_HEIGHT - font.lineHeight + 2) / 2;
        var buttonY = y + (ROW_HEIGHT - BUTTON_HEIGHT) / 2;

        // Delete button at the row's right edge. Inspect used to live to its left, but clicking the row already
        // sets the selection to the row's faction (FactionSelectable) which the inspector picks up — the button
        // was a redundant second affordance for the same action, so it's gone.
        var deleteX = rowRight - 4 - BUTTON_WIDTH;
        var deleteRect = new Rect(deleteX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT);
        renderButton(graphics, deleteRect, "Delete", mouseX, mouseY, BUTTON_DESTRUCTIVE_TEXT);

        var nameX = swatchX + SWATCH_SIZE + 6;
        var memberLabel = entry.memberCount() + " member" + (entry.memberCount() == 1 ? "" : "s");
        var memberWidth = font.width(memberLabel);
        var nameMaxWidth = Math.max(0, deleteX - nameX - memberWidth - 12);
        var nameColor = hovered ? ROW_TEXT_HOVER_COLOR : ROW_TEXT_COLOR;
        var truncatedName = font.plainSubstrByWidth(entry.name(), nameMaxWidth);
        graphics.drawString(font, Component.literal(truncatedName), nameX, textY, nameColor, false);
        graphics.drawString(font, Component.literal(memberLabel), deleteX - memberWidth - 6, textY, META_COLOR, false);

        rowHits.add(new RowHit(x, y, width, ROW_HEIGHT, entry.id(), deleteRect));
    }

    private static void renderButton(GuiGraphics graphics, Rect rect, String label, int mouseX, int mouseY, int textColor) {
        var hovered = rect.contains(mouseX, mouseY);
        graphics.fill(rect.x, rect.y, rect.x + rect.w, rect.y + rect.h, hovered ? BUTTON_BG_HOVER : BUTTON_BG);
        graphics.fill(rect.x, rect.y, rect.x + rect.w, rect.y + 1, BUTTON_BORDER);
        graphics.fill(rect.x, rect.y + rect.h - 1, rect.x + rect.w, rect.y + rect.h, BUTTON_BORDER);
        graphics.fill(rect.x, rect.y, rect.x + 1, rect.y + rect.h, BUTTON_BORDER);
        graphics.fill(rect.x + rect.w - 1, rect.y, rect.x + rect.w, rect.y + rect.h, BUTTON_BORDER);

        var font = EngineFont.get();
        var textX = rect.x + (rect.w - font.width(label)) / 2;
        var textY = rect.y + (rect.h - font.lineHeight + 2) / 2;
        graphics.drawString(font, Component.literal(label), textX, textY, textColor, false);
    }

    private List<S2CFactionDirectoryPayload.FactionEntry> filter(String query) {
        var all = ClientFactionDirectoryCache.entries();
        if (query.isEmpty()) {
            return all;
        }
        var out = new ArrayList<S2CFactionDirectoryPayload.FactionEntry>();
        for (var entry : all) {
            if (
                entry.name().toLowerCase(Locale.ROOT).contains(query)
                    || entry.id().toString().toLowerCase(Locale.ROOT).contains(query)
            ) {
                out.add(entry);
            }
        }
        return out;
    }

    private static @Nullable ResourceLocation currentSelectedFactionId() {
        var sel = SelectionManager.current().single();
        return sel instanceof FactionSelectable fs ? fs.factionId() : null;
    }

    private void requestDirectory() {
        BLib.MOD.networking().sendToServer(C2SRequestFactionDirectoryPayload.INSTANCE);
        requestedAtLeastOnce = true;
    }

    /**
     * Auto-generate a fresh faction id by walking {@code blib:new_faction_<n>} until one isn't taken. Keeps the New
     * Faction action a single click — users rename via the Inspector after the faction appears.
     */
    private void createNewFaction() {
        var existing = ClientFactionDirectoryCache.entries();
        var taken = new HashSet<ResourceLocation>(existing.size());
        for (var e : existing) {
            taken.add(e.id());
        }
        ResourceLocation candidate = null;
        for (var n = 1; n < 1000; n++) {
            var id = ResourceLocation.fromNamespaceAndPath("blib", "new_faction_" + n);
            if (!taken.contains(id)) {
                candidate = id;
                break;
            }
        }
        if (candidate == null) {
            return;
        }
        BLib.MOD.networking().sendToServer(new C2SCreateFactionPayload(candidate, ResourceLocation.fromNamespaceAndPath("blib", "empty")));
    }

    private void requestDelete(ResourceLocation factionId, String name) {
        if (actionHandler == null) {
            return;
        }
        actionHandler.confirmDelete(
            "Delete faction?",
            "Delete faction '" + name + "' (" + factionId + ")? "
                + "This removes the faction's data, member roster, and all relationship entries. Cannot be undone.",
            () -> BLib.MOD.networking().sendToServer(new C2SDeleteFactionPayload(factionId))
        );
    }

    @Override
    public boolean mouseClickedCapture(double mouseX, double mouseY, int button) {
        return scroll.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (searchInput.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (button != 0) {
            return false;
        }
        if (newFactionRect != null && newFactionRect.contains(mouseX, mouseY)) {
            createNewFaction();
            return true;
        }
        for (var hit : rowHits) {
            if (hit.deleteButton.contains(mouseX, mouseY)) {
                var entry = ClientFactionDirectoryCache.get(hit.factionId);
                requestDelete(hit.factionId, entry == null ? hit.factionId.toString() : entry.name());
                return true;
            }
            if (mouseX >= hit.x && mouseX < hit.x + hit.w && mouseY >= hit.y && mouseY < hit.y + hit.h) {
                SelectionManager.selectSingle(new FactionSelectable(hit.factionId));
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return scroll.mouseDragged(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return scroll.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (mouseX < rectX || mouseX >= rectX + rectWidth || mouseY < rectY || mouseY >= rectY + rectHeight) {
            return false;
        }
        return scroll.mouseScrolled(scrollY);
    }

    private static void applyRawScissor(GuiGraphics graphics, int x, int y, int w, int h) {
        if (w <= 0 || h <= 0) {
            RenderSystem.disableScissor();
            return;
        }
        graphics.flush();

        var matrix = graphics.pose().last().pose();
        var topLeft = matrix.transformPosition((float) x, (float) y, 0f, new Vector3f());
        var bottomRight = matrix.transformPosition((float) (x + w), (float) (y + h), 0f, new Vector3f());

        var window = Minecraft.getInstance().getWindow();
        var winHeight = window.getHeight();
        var guiScale = window.getGuiScale();
        var leftRaw = (int) ((double) topLeft.x * guiScale);
        var bottomRaw = (int) ((double) winHeight - (double) bottomRight.y * guiScale);
        var widthRaw = Math.max(0, (int) ((double) (bottomRight.x - topLeft.x) * guiScale));
        var heightRaw = Math.max(0, (int) ((double) (bottomRight.y - topLeft.y) * guiScale));
        RenderSystem.enableScissor(leftRaw, bottomRaw, widthRaw, heightRaw);
    }

    private record Rect(
        int x,
        int y,
        int w,
        int h
    ) {

        boolean contains(double mx, double my) {
            return mx >= x && mx < x + w && my >= y && my < y + h;
        }
    }

    private record RowHit(
        int x,
        int y,
        int w,
        int h,
        ResourceLocation factionId,
        Rect deleteButton
    ) {}
}
