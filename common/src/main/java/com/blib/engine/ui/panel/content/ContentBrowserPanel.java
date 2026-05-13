package com.blib.engine.ui.panel.content;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;

import com.blib.engine.domain.selection.volume.BlockSelection;
import com.blib.engine.jigsaw.JigsawPoolSelection;
import com.blib.engine.projectcontents.ProjectContents;
import com.blib.engine.session.ProjectSession;
import com.blib.engine.ui.EngineFont;
import com.blib.engine.ui.PanelPlaceholder;
import com.blib.engine.ui.ProjectContentActionHandler;
import com.blib.engine.ui.dock.Panel;
import com.blib.engine.ui.widget.ScrollContainer;
import com.blib.engine.ui.widget.TextInput;
import com.blib.mod.BLib;
import com.blib.mod.common.network.packet.C2SDeleteCapturePayload;
import com.blib.mod.common.network.packet.C2SDeletePoolPayload;
import com.blib.mod.common.network.packet.C2SDeleteStructurePayload;
import com.blib.mod.common.network.packet.C2SListCapturesPayload;
import com.blib.mod.common.network.packet.C2SListPoolsPayload;
import com.blib.mod.common.network.packet.C2SListStructuresPayload;

/**
 * Project contents browser. Three collapsible sections (Pools / Structures / Captures) that mirror what's authored
 * under {@code <gameDir>/blib/projects/<active>/}, with per-row Open and Delete buttons. Lists are fetched from the
 * server on first show + on every Refresh button click; deletes proactively trigger a list refresh server-side.
 * <p>
 * "Open" on a pool row sets {@link JigsawPoolSelection}; the existing {@code PoolEditorPanel} consumes the request on
 * its next render and switches its active pool. Open on a structure / capture row is reserved for a future iteration
 * (no-op stub). Delete opens a confirm dialog through the {@link ProjectContentActionHandler} the workspace screen
 * provides.
 */
@ApiStatus.Internal
public final class ContentBrowserPanel implements Panel {

    private static final int BACKGROUND_COLOR = 0xFF14141A;

    private static final int HEADER_BG_COLOR = 0xFF1A1A22;

    private static final int HEADER_BG_HOVER_COLOR = 0xFF22222C;

    private static final int HEADER_TEXT_COLOR = 0xFFB0B0B0;

    private static final int HEADER_COUNT_COLOR = 0xFF707078;

    private static final int ROW_BG_HOVER_COLOR = 0xFF1F1F26;

    private static final int ROW_TEXT_COLOR = 0xFFD0D0D0;

    private static final int ROW_TEXT_HOVER_COLOR = 0xFFFFFFFF;

    private static final int EMPTY_TEXT_COLOR = 0xFF606068;

    private static final int BUTTON_BG = 0xFF14141A;

    private static final int BUTTON_BG_HOVER = 0xFF22222C;

    private static final int BUTTON_BORDER = 0xFF353540;

    private static final int BUTTON_TEXT = 0xFFD0D0D0;

    private static final int BUTTON_DESTRUCTIVE_TEXT = 0xFFE06868;

    private static final int BUTTON_DISABLED_TEXT = 0xFF606068;

    private static final int CONTENT_PADDING = 5;

    private static final int SEARCH_GAP_BELOW = 4;

    private static final int HEADER_HEIGHT = 12;

    private static final int ROW_HEIGHT = 13;

    private static final int CARET_WIDTH = 6;

    private static final int BUTTON_HEIGHT = 10;

    private static final int BUTTON_WIDTH = 38;

    private static final int BUTTON_GAP = 3;

    private static final int REFRESH_BUTTON_WIDTH = 56;

    private static final int REFRESH_BUTTON_HEIGHT = TextInput.HEIGHT;

    private enum Section {

        POOLS("Pools", 0xFFE6C26B),
        STRUCTURES("Structures", 0xFF7CB6E0),
        CAPTURES("Captures", 0xFFB6E2A1);

        final String displayName;

        final int accentColor;

        Section(String displayName, int accentColor) {
            this.displayName = displayName;
            this.accentColor = accentColor;
        }
    }

    private final TextInput searchInput = new TextInput("Filter…");

    private final ScrollContainer scroll = new ScrollContainer();

    private final EnumSet<Section> collapsed = EnumSet.noneOf(Section.class);

    private final @Nullable ProjectContentActionHandler actionHandler;

    /** Per-frame hit tests for the row Open / Delete buttons + section header click toggles. */
    private final List<RowButtonHit> rowButtonHits = new ArrayList<>();

    private final List<HeaderHit> headerHits = new ArrayList<>();

    private @Nullable Rect refreshButtonRect;

    private int rectX;

    private int rectY;

    private int rectWidth;

    private int rectHeight;

    /** Project name from the previous render — used to detect swaps and re-fetch the lists. */
    private @Nullable String lastFetchedProject;

    public ContentBrowserPanel() {
        this(null);
    }

    public ContentBrowserPanel(@Nullable ProjectContentActionHandler actionHandler) {
        this.actionHandler = actionHandler;
    }

    @Override
    public String title() {
        return "Project Contents";
    }

    @Override
    public void onShown() {
        scroll.reset();
        // Fresh tab → re-fetch so the list reflects whatever's on disk right now.
        var projectName = ProjectSession.activeProjectName();
        if (!projectName.isEmpty()) {
            requestAllLists(projectName);
            lastFetchedProject = projectName;
        }
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
        this.rectX = x;
        this.rectY = y;
        this.rectWidth = width;
        this.rectHeight = height;
        rowButtonHits.clear();
        headerHits.clear();
        refreshButtonRect = null;

        graphics.fill(x, y, x + width, y + height, BACKGROUND_COLOR);

        // Content browser is project-scoped — and projects require the integrated server to enumerate. Surface both
        // conditions as placeholders so the user knows what's needed.
        if (Minecraft.getInstance().level == null) {
            PanelPlaceholder.drawCentered(graphics, x, y, width, height, PanelPlaceholder.NEEDS_WORLD);
            return;
        }
        if (ProjectSession.activeProject() == null) {
            PanelPlaceholder.drawCentered(graphics, x, y, width, height, PanelPlaceholder.NEEDS_PROJECT);
            return;
        }

        // Auto-fetch on first render if we missed onShown (e.g. panel was constructed via reopenPanel after a project
        // was already active). Also re-fetch on project swap so a switch from project A → B drops A's content.
        var projectName = ProjectSession.activeProjectName();
        if (!projectName.isEmpty() && !projectName.equals(lastFetchedProject)) {
            requestAllLists(projectName);
            lastFetchedProject = projectName;
        }

        var font = EngineFont.get();

        // Top row: search input on the left, Refresh button on the right.
        var topRowY = y + CONTENT_PADDING;
        var refreshX = x + width - CONTENT_PADDING - REFRESH_BUTTON_WIDTH;
        var refreshRect = new Rect(refreshX, topRowY, REFRESH_BUTTON_WIDTH, REFRESH_BUTTON_HEIGHT);
        this.refreshButtonRect = refreshRect;
        var searchInputW = Math.max(0, refreshX - (x + CONTENT_PADDING) - 4);
        searchInput.render(graphics, x + CONTENT_PADDING, topRowY, searchInputW, mouseX, mouseY);
        renderButton(graphics, refreshRect, "Refresh", mouseX, mouseY, BUTTON_TEXT);

        var listX = x + CONTENT_PADDING;
        var listY = topRowY + REFRESH_BUTTON_HEIGHT + SEARCH_GAP_BELOW;
        var listW = width - 2 * CONTENT_PADDING;
        var listH = Math.max(0, height - (listY - y) - CONTENT_PADDING);
        if (listH <= 0) {
            return;
        }

        if (projectName.isEmpty()) {
            graphics.drawString(font, Component.literal("(no project open)"), listX, listY, EMPTY_TEXT_COLOR, false);
            return;
        }

        var query = searchInput.content().toLowerCase(Locale.ROOT).trim();
        var pools = filterPools(query);
        var structures = filterStructures(query);
        var captures = filterCaptures(query);

        // Compute total content height so the scrollbar tracks correctly. Empty sections are still shown (with
        // "(none)" rather than hidden) so users can see "yes I checked, this project has no pools".
        var contentHeight = 0;
        for (var section : Section.values()) {
            contentHeight += HEADER_HEIGHT;
            if (collapsed.contains(section)) {
                continue;
            }
            var rowCount = sectionSize(section, pools, structures, captures);
            contentHeight += Math.max(1, rowCount) * ROW_HEIGHT;
        }
        scroll.layout(listH, contentHeight);

        applyRawScissor(graphics, listX, listY, listW, listH);
        try {
            var scrollY = (int) scroll.scrollY();
            var cursorY = listY - scrollY;
            cursorY = renderSection(graphics, listX, cursorY, listW, Section.POOLS, pools.size(), mouseX, mouseY);
            if (!collapsed.contains(Section.POOLS)) {
                if (pools.isEmpty()) {
                    cursorY = renderEmptyRow(graphics, listX, cursorY, listW);
                } else {
                    for (var id : pools) {
                        cursorY = renderPoolRow(graphics, listX, cursorY, listW, id, projectName, mouseX, mouseY);
                    }
                }
            }
            cursorY = renderSection(graphics, listX, cursorY, listW, Section.STRUCTURES, structures.size(), mouseX, mouseY);
            if (!collapsed.contains(Section.STRUCTURES)) {
                if (structures.isEmpty()) {
                    cursorY = renderEmptyRow(graphics, listX, cursorY, listW);
                } else {
                    for (var id : structures) {
                        cursorY = renderStructureRow(graphics, listX, cursorY, listW, id, projectName, mouseX, mouseY);
                    }
                }
            }
            cursorY = renderSection(graphics, listX, cursorY, listW, Section.CAPTURES, captures.size(), mouseX, mouseY);
            if (!collapsed.contains(Section.CAPTURES)) {
                if (captures.isEmpty()) {
                    cursorY = renderEmptyRow(graphics, listX, cursorY, listW);
                } else {
                    for (var name : captures) {
                        cursorY = renderCaptureRow(graphics, listX, cursorY, listW, name, projectName, mouseX, mouseY);
                    }
                }
            }
        } finally {
            graphics.flush();
            RenderSystem.disableScissor();
        }

        scroll.renderScrollbar(graphics, listX, listY, listW, listH, mouseX, mouseY);
    }

    private static int sectionSize(
        Section section,
        List<ResourceLocation> pools,
        List<ResourceLocation> structures,
        List<String> captures
    ) {
        return switch (section) {
            case POOLS -> pools.size();
            case STRUCTURES -> structures.size();
            case CAPTURES -> captures.size();
        };
    }

    private int renderSection(GuiGraphics graphics, int x, int y, int width, Section section, int count, int mouseX, int mouseY) {
        // Reserve the scrollbar gutter so the header background and right-aligned count don't slide under the bar.
        var rowRight = x + width - ScrollContainer.SCROLLBAR_GUTTER;
        var hovered = mouseX >= x && mouseX < rowRight && mouseY >= y && mouseY < y + HEADER_HEIGHT;
        var bg = hovered ? HEADER_BG_HOVER_COLOR : HEADER_BG_COLOR;
        graphics.fill(x, y, rowRight, y + HEADER_HEIGHT, bg);
        graphics.fill(x, y, x + 2, y + HEADER_HEIGHT, section.accentColor);

        var font = EngineFont.get();
        var caret = collapsed.contains(section) ? "▸" : "▾";
        var textY = y + (HEADER_HEIGHT - font.lineHeight + 2) / 2;
        graphics.drawString(font, Component.literal(caret), x + 4, textY, HEADER_TEXT_COLOR, false);
        graphics.drawString(font, Component.literal(section.displayName), x + 4 + CARET_WIDTH + 2, textY, HEADER_TEXT_COLOR, false);

        var countLabel = "(" + count + ")";
        var countX = rowRight - 4 - font.width(countLabel);
        graphics.drawString(font, Component.literal(countLabel), countX, textY, HEADER_COUNT_COLOR, false);

        headerHits.add(new HeaderHit(x, y, rowRight - x, HEADER_HEIGHT, section));
        return y + HEADER_HEIGHT;
    }

    private int renderEmptyRow(GuiGraphics graphics, int x, int y, int width) {
        var font = EngineFont.get();
        graphics.drawString(font, Component.literal("(none)"), x + 12, y + (ROW_HEIGHT - font.lineHeight + 2) / 2, EMPTY_TEXT_COLOR, false);
        return y + ROW_HEIGHT;
    }

    private int renderPoolRow(
        GuiGraphics graphics,
        int x,
        int y,
        int width,
        ResourceLocation id,
        String projectName,
        int mouseX,
        int mouseY
    ) {
        return renderRow(
            graphics,
            x,
            y,
            width,
            id.toString(),
            mouseX,
            mouseY,
            /* hasOpen= */ true,
            () -> JigsawPoolSelection.request(id),
            () -> requestDeletePool(projectName, id)
        );
    }

    private int renderStructureRow(
        GuiGraphics graphics,
        int x,
        int y,
        int width,
        ResourceLocation id,
        String projectName,
        int mouseX,
        int mouseY
    ) {
        return renderRow(
            graphics,
            x,
            y,
            width,
            id.toString(),
            mouseX,
            mouseY,
            /* hasOpen= */ false,
            () -> {},
            () -> requestDeleteStructure(projectName, id)
        );
    }

    private int renderCaptureRow(GuiGraphics graphics, int x, int y, int width, String name, String projectName, int mouseX, int mouseY) {
        return renderRow(
            graphics,
            x,
            y,
            width,
            name,
            mouseX,
            mouseY,
            /* hasOpen= */ false,
            () -> {},
            () -> requestDeleteCapture(projectName, name)
        );
    }

    private int renderRow(
        GuiGraphics graphics,
        int x,
        int y,
        int width,
        String label,
        int mouseX,
        int mouseY,
        boolean hasOpen,
        Runnable onOpen,
        Runnable onDelete
    ) {
        // Reserve the scrollbar gutter so the row hover background and right-aligned buttons don't sit under the bar.
        var rowRight = x + width - ScrollContainer.SCROLLBAR_GUTTER;
        var hovered = mouseX >= x && mouseX < rowRight && mouseY >= y && mouseY < y + ROW_HEIGHT;
        if (hovered) {
            graphics.fill(x, y, rowRight, y + ROW_HEIGHT, ROW_BG_HOVER_COLOR);
        }

        var font = EngineFont.get();
        var textY = y + (ROW_HEIGHT - font.lineHeight + 2) / 2;
        var buttonY = y + (ROW_HEIGHT - BUTTON_HEIGHT) / 2;

        // Right-aligned buttons: Delete (always), Open (only when hasOpen).
        var deleteX = rowRight - 4 - BUTTON_WIDTH;
        var openX = deleteX - BUTTON_GAP - BUTTON_WIDTH;
        var deleteRect = new Rect(deleteX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT);
        var openRect = hasOpen ? new Rect(openX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT) : null;

        renderButton(graphics, deleteRect, "Delete", mouseX, mouseY, BUTTON_DESTRUCTIVE_TEXT);
        if (openRect != null) {
            renderButton(graphics, openRect, "Open", mouseX, mouseY, BUTTON_TEXT);
        } else {
            // Render a disabled placeholder so row layout matches across sections.
            var phRect = new Rect(openX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT);
            renderButton(graphics, phRect, "Open", mouseX, mouseY, BUTTON_DISABLED_TEXT);
        }

        // Truncate label to avoid overlap with buttons.
        var labelMaxX = openX - 6;
        var labelMaxWidth = Math.max(0, labelMaxX - (x + 12));
        var truncated = font.plainSubstrByWidth(label, labelMaxWidth);
        graphics.drawString(font, Component.literal(truncated), x + 12, textY, hovered ? ROW_TEXT_HOVER_COLOR : ROW_TEXT_COLOR, false);

        rowButtonHits.add(new RowButtonHit(deleteRect, onDelete));
        if (openRect != null) {
            rowButtonHits.add(new RowButtonHit(openRect, onOpen));
        }

        return y + ROW_HEIGHT;
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

    private List<ResourceLocation> filterPools(String query) {
        return filterIds(ProjectContents.pools(), query);
    }

    private List<ResourceLocation> filterStructures(String query) {
        return filterIds(ProjectContents.structures(), query);
    }

    private List<String> filterCaptures(String query) {
        var all = BlockSelection.captures();
        if (query.isEmpty()) {
            return all;
        }
        var out = new ArrayList<String>();
        for (var name : all) {
            if (name.toLowerCase(Locale.ROOT).contains(query)) {
                out.add(name);
            }
        }
        return out;
    }

    private static List<ResourceLocation> filterIds(List<ResourceLocation> all, String query) {
        if (query.isEmpty()) {
            return all;
        }
        var out = new ArrayList<ResourceLocation>();
        for (var id : all) {
            if (id.toString().toLowerCase(Locale.ROOT).contains(query)) {
                out.add(id);
            }
        }
        return out;
    }

    private static void requestAllLists(String projectName) {
        BLib.MOD.networking().sendToServer(new C2SListPoolsPayload(projectName));
        BLib.MOD.networking().sendToServer(new C2SListStructuresPayload(projectName));
        BLib.MOD.networking().sendToServer(new C2SListCapturesPayload(projectName));
    }

    private void requestDeletePool(String projectName, ResourceLocation poolId) {
        if (actionHandler == null) {
            return;
        }
        actionHandler.confirmDelete(
            "Delete pool?",
            "Delete '" + poolId + "' from this project's datapack? "
                + "The file will be removed, but the live registry will still hold the pool until you Reload Project.",
            () -> BLib.MOD.networking().sendToServer(new C2SDeletePoolPayload(projectName, poolId))
        );
    }

    private void requestDeleteStructure(String projectName, ResourceLocation structureId) {
        if (actionHandler == null) {
            return;
        }
        actionHandler.confirmDelete(
            "Delete structure?",
            "Delete '" + structureId + "' from this project's datapack? "
                + "The .nbt file will be removed, but the live StructureTemplate will stay loaded until you Reload Project.",
            () -> BLib.MOD.networking().sendToServer(new C2SDeleteStructurePayload(projectName, structureId))
        );
    }

    private void requestDeleteCapture(String projectName, String captureName) {
        if (actionHandler == null) {
            return;
        }
        actionHandler.confirmDelete(
            "Delete capture?",
            "Delete '" + captureName + "' from this project's captures folder? This cannot be undone.",
            () -> BLib.MOD.networking().sendToServer(new C2SDeleteCapturePayload(projectName, captureName))
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
        if (refreshButtonRect != null && refreshButtonRect.contains(mouseX, mouseY)) {
            var projectName = ProjectSession.activeProjectName();
            if (!projectName.isEmpty()) {
                requestAllLists(projectName);
            }
            return true;
        }
        for (var hh : headerHits) {
            if (mouseX >= hh.x && mouseX < hh.x + hh.w && mouseY >= hh.y && mouseY < hh.y + hh.h) {
                if (collapsed.contains(hh.section)) {
                    collapsed.remove(hh.section);
                } else {
                    collapsed.add(hh.section);
                }
                return true;
            }
        }
        for (var rh : rowButtonHits) {
            if (rh.rect.contains(mouseX, mouseY)) {
                rh.action.run();
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

    private record HeaderHit(
        int x,
        int y,
        int w,
        int h,
        Section section
    ) {}

    private record RowButtonHit(
        Rect rect,
        Runnable action
    ) {}
}
