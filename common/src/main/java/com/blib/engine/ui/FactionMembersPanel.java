package com.blib.engine.ui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.blib.engine.selection.FactionSelectable;
import com.blib.engine.selection.SelectionManager;
import com.blib.internal.client.faction.ClientFactionDirectoryCache;
import com.blib.internal.client.faction.ClientFactionMembersCache;
import com.blib.mod.BLib;
import com.blib.mod.common.network.packet.C2SAddFactionMemberPayload;
import com.blib.mod.common.network.packet.C2SRemoveFactionMemberPayload;
import com.blib.mod.common.network.packet.C2SRequestFactionMembersPayload;
import com.blib.mod.common.network.packet.S2CFactionMembersPayload;

/**
 * Faction Members — roster of the currently-selected faction. Reads {@link ClientFactionMembersCache}, which is
 * populated by {@link S2CFactionMembersPayload} on selection change and on server-side member adds/removes.
 * <p>
 * If no faction is selected, the panel renders a hint and the inputs are inert. With a faction selected, an inline UUID
 * text input + Add button at the top let the user add a member by UUID; per-row Remove buttons fire a confirm dialog
 * before removing. The active-faction id is tracked so a swap dispatches a fresh request packet.
 */
@ApiStatus.Internal
public final class FactionMembersPanel implements Panel {

    private static final int BACKGROUND_COLOR = 0xFF14141A;

    private static final int ROW_BG_HOVER_COLOR = 0xFF1F1F26;

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

    private static final int ROW_HEIGHT = 13;

    private static final int BUTTON_HEIGHT = 10;

    private static final int BUTTON_WIDTH = 42;

    private static final int HEADER_BUTTON_HEIGHT = TextInput.HEIGHT;

    private static final int HEADER_BUTTON_WIDTH = 42;

    private final TextInput memberInput = new TextInput("Entity UUID");

    private final ScrollContainer scroll = new ScrollContainer();

    private final @Nullable ProjectContentActionHandler actionHandler;

    private final List<RowHit> rowHits = new ArrayList<>();

    private @Nullable Rect addRect;

    private @Nullable Rect refreshRect;

    private int rectX;

    private int rectY;

    private int rectWidth;

    private int rectHeight;

    /** Last faction we requested members for — used to detect selection swaps and re-fetch. */
    private @Nullable ResourceLocation lastRequestedFaction;

    public FactionMembersPanel() {
        this(null);
    }

    public FactionMembersPanel(@Nullable ProjectContentActionHandler actionHandler) {
        this.actionHandler = actionHandler;
    }

    @Override
    public String title() {
        return "Faction Members";
    }

    @Override
    public void onShown() {
        scroll.reset();
        var active = activeFactionId();
        if (active != null) {
            requestMembers(active);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
        this.rectX = x;
        this.rectY = y;
        this.rectWidth = width;
        this.rectHeight = height;
        rowHits.clear();
        addRect = null;
        refreshRect = null;

        graphics.fill(x, y, x + width, y + height, BACKGROUND_COLOR);

        var font = Minecraft.getInstance().font;
        var active = activeFactionId();

        // No faction selected → render a clear hint and bail. Don't disable the input visually since drawing it
        // disabled would mean swap-and-mouse-fall-through edge cases; just show the hint message instead.
        if (active == null) {
            graphics.drawString(
                font,
                Component.literal("(select a faction in the Browser)"),
                x + CONTENT_PADDING,
                y + CONTENT_PADDING,
                EMPTY_TEXT_COLOR,
                false
            );
            return;
        }

        // Detect selection swap and re-request the roster.
        if (!active.equals(lastRequestedFaction)) {
            requestMembers(active);
            lastRequestedFaction = active;
        }

        var topRowY = y + CONTENT_PADDING;
        var refreshX = x + width - CONTENT_PADDING - HEADER_BUTTON_WIDTH;
        var addX = refreshX - 4 - HEADER_BUTTON_WIDTH;
        addRect = new Rect(addX, topRowY, HEADER_BUTTON_WIDTH, HEADER_BUTTON_HEIGHT);
        refreshRect = new Rect(refreshX, topRowY, HEADER_BUTTON_WIDTH, HEADER_BUTTON_HEIGHT);
        var inputW = Math.max(0, addX - (x + CONTENT_PADDING) - 4);
        memberInput.render(graphics, x + CONTENT_PADDING, topRowY, inputW, mouseX, mouseY);
        renderButton(graphics, addRect, "Add", mouseX, mouseY, BUTTON_TEXT);
        renderButton(graphics, refreshRect, "Refresh", mouseX, mouseY, BUTTON_TEXT);

        var listX = x + CONTENT_PADDING;
        var listY = topRowY + HEADER_BUTTON_HEIGHT + SEARCH_GAP_BELOW;
        var listW = width - 2 * CONTENT_PADDING;
        var listH = Math.max(0, height - (listY - y) - CONTENT_PADDING);
        if (listH <= 0) {
            return;
        }

        // Sub-panel header showing which faction we're inspecting (for clarity when this tab is brought to front).
        var directoryEntry = ClientFactionDirectoryCache.get(active);
        var headerLabel = "Members of " + (directoryEntry == null ? active.toString() : directoryEntry.name());
        graphics.drawString(font, Component.literal(headerLabel), listX, listY, META_COLOR, false);
        listY += font.lineHeight + 2;
        listH = Math.max(0, listH - font.lineHeight - 2);

        // Members may not have arrived yet for this faction (we just requested above). Show "(loading…)".
        var cachedFaction = ClientFactionMembersCache.factionId();
        var members = ClientFactionMembersCache.members();
        if (cachedFaction == null || !cachedFaction.equals(active)) {
            graphics.drawString(font, Component.literal("(loading…)"), listX, listY, EMPTY_TEXT_COLOR, false);
            return;
        }
        if (members.isEmpty()) {
            graphics.drawString(font, Component.literal("(no members)"), listX, listY, EMPTY_TEXT_COLOR, false);
            return;
        }

        var contentHeight = members.size() * ROW_HEIGHT;
        scroll.layout(listH, contentHeight);

        applyRawScissor(graphics, listX, listY, listW, listH);
        try {
            var scrollY = (int) scroll.scrollY();
            var firstVisibleRow = Math.max(0, scrollY / ROW_HEIGHT);
            var lastVisibleRow = Math.min(members.size() - 1, (scrollY + listH) / ROW_HEIGHT);
            for (var i = firstVisibleRow; i <= lastVisibleRow; i++) {
                var entry = members.get(i);
                var rowY = listY + i * ROW_HEIGHT - scrollY;
                renderRow(graphics, listX, rowY, listW, entry, active, mouseX, mouseY);
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
        S2CFactionMembersPayload.MemberEntry entry,
        ResourceLocation factionId,
        int mouseX,
        int mouseY
    ) {
        var hovered = mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + ROW_HEIGHT;
        if (hovered) {
            graphics.fill(x, y, x + width, y + ROW_HEIGHT, ROW_BG_HOVER_COLOR);
        }

        var font = Minecraft.getInstance().font;
        var textY = y + (ROW_HEIGHT - font.lineHeight + 2) / 2;
        var buttonY = y + (ROW_HEIGHT - BUTTON_HEIGHT) / 2;

        var removeX = x + width - 4 - BUTTON_WIDTH;
        var removeRect = new Rect(removeX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT);
        renderButton(graphics, removeRect, "Remove", mouseX, mouseY, BUTTON_DESTRUCTIVE_TEXT);

        var label = entry.displayName().isEmpty()
            ? entry.uuid().toString().substring(0, 8) + "…  (offline / unloaded)"
            : entry.displayName() + "  " + entry.uuid().toString().substring(0, 8) + "…";
        var truncated = font.plainSubstrByWidth(label, Math.max(0, removeX - x - 8));
        graphics.drawString(font, Component.literal(truncated), x + 4, textY, hovered ? ROW_TEXT_HOVER_COLOR : ROW_TEXT_COLOR, false);

        rowHits.add(new RowHit(removeRect, factionId, entry.uuid(), entry.displayName()));
    }

    private static void renderButton(GuiGraphics graphics, Rect rect, String label, int mouseX, int mouseY, int textColor) {
        var hovered = rect.contains(mouseX, mouseY);
        graphics.fill(rect.x, rect.y, rect.x + rect.w, rect.y + rect.h, hovered ? BUTTON_BG_HOVER : BUTTON_BG);
        graphics.fill(rect.x, rect.y, rect.x + rect.w, rect.y + 1, BUTTON_BORDER);
        graphics.fill(rect.x, rect.y + rect.h - 1, rect.x + rect.w, rect.y + rect.h, BUTTON_BORDER);
        graphics.fill(rect.x, rect.y, rect.x + 1, rect.y + rect.h, BUTTON_BORDER);
        graphics.fill(rect.x + rect.w - 1, rect.y, rect.x + rect.w, rect.y + rect.h, BUTTON_BORDER);

        var font = Minecraft.getInstance().font;
        var textX = rect.x + (rect.w - font.width(label)) / 2;
        var textY = rect.y + (rect.h - font.lineHeight + 2) / 2;
        graphics.drawString(font, Component.literal(label), textX, textY, textColor, false);
    }

    private static @Nullable ResourceLocation activeFactionId() {
        var sel = SelectionManager.current().single();
        return sel instanceof FactionSelectable fs ? fs.factionId() : null;
    }

    private void requestMembers(ResourceLocation factionId) {
        BLib.MOD.networking().sendToServer(new C2SRequestFactionMembersPayload(factionId));
    }

    private void tryAddMember(ResourceLocation factionId) {
        var raw = memberInput.content().trim();
        if (raw.isEmpty()) {
            return;
        }
        UUID uuid;
        try {
            uuid = UUID.fromString(raw);
        } catch (IllegalArgumentException e) {
            // Bad UUID — clear the input as feedback. A future iteration could surface an error tooltip.
            memberInput.setContent("");
            return;
        }
        BLib.MOD.networking().sendToServer(new C2SAddFactionMemberPayload(factionId, uuid));
        memberInput.setContent("");
    }

    private void requestRemove(ResourceLocation factionId, UUID memberUuid, String displayName) {
        if (actionHandler == null) {
            return;
        }
        var label = displayName.isEmpty() ? memberUuid.toString().substring(0, 8) + "…" : displayName;
        actionHandler.confirmDelete(
            "Remove member?",
            "Remove '" + label + "' from this faction? They can be re-added afterwards.",
            () -> BLib.MOD.networking().sendToServer(new C2SRemoveFactionMemberPayload(factionId, memberUuid))
        );
    }

    @Override
    public boolean mouseClickedCapture(double mouseX, double mouseY, int button) {
        return scroll.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (memberInput.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (button != 0) {
            return false;
        }
        var active = activeFactionId();
        if (active == null) {
            return false;
        }
        if (addRect != null && addRect.contains(mouseX, mouseY)) {
            tryAddMember(active);
            return true;
        }
        if (refreshRect != null && refreshRect.contains(mouseX, mouseY)) {
            requestMembers(active);
            return true;
        }
        for (var hit : rowHits) {
            if (hit.removeButton.contains(mouseX, mouseY)) {
                requestRemove(hit.factionId, hit.uuid, hit.displayName);
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
        Rect removeButton,
        ResourceLocation factionId,
        UUID uuid,
        String displayName
    ) {}
}
