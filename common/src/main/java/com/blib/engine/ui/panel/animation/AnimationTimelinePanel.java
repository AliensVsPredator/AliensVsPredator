package com.blib.engine.ui.panel.animation;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import com.blib.engine.modeler.animation.AnimationEditorState;
import com.blib.engine.modeler.animation.AnimationEditorState.KeyframeRef;
import com.blib.engine.modeler.animation.AnimationEditorState.TransformChannel;
import com.blib.engine.ui.EngineFont;
import com.blib.engine.ui.dock.Panel;
import com.blib.engine.ui.layout.ScrollViewport;
import com.blib.engine.ui.layout.UiRect;
import com.blib.engine.ui.layout.UiText;
import com.blib.engine.ui.popup.PanelMenuOpener;
import com.blib.engine.ui.widget.DropdownMenu;
import com.blib.engine.ui.widget.SearchableSelect;
import com.blib.engine.ui.widget.TextInput;

@ApiStatus.Internal
public final class AnimationTimelinePanel implements Panel {

    private static final int BG_COLOR = 0xFF18181C;

    private static final int ROW_HOVER_COLOR = 0xFF24242A;

    private static final int ROW_SELECTED_COLOR = 0xFF3A3A48;

    private static final int TEXT_COLOR = 0xFFD0D0D0;

    private static final int META_TEXT_COLOR = 0xFF808088;

    private static final int CHIP_BG_COLOR = 0xFF2C2C32;

    private static final int CHIP_HOVER_BG_COLOR = 0xFF3C3C46;

    private static final int CHIP_DISABLED_TEXT_COLOR = 0xFF777780;

    private static final int DIRTY_COLOR = 0xFFE6C26B;

    private static final int PADDING = 6;

    private static final int ROW_HEIGHT = TextInput.HEIGHT + 3;

    private static final int TOOL_ROW_HEIGHT = 15;

    private final ScrollViewport scroll = new ScrollViewport();

    private final @Nullable PanelMenuOpener menuOpener;

    private final SearchableSelect<TransformChannel> channelSelect = new SearchableSelect<>(
        AnimationTimelinePanel::channelItems,
        TransformChannel::jsonName,
        TransformChannel.ROTATION,
        channel -> AnimationEditorState.get().selectChannel(channel)
    );

    private final List<KeyframeRef> rows = new ArrayList<>();

    private int panelX, panelY, panelWidth, panelHeight;

    private int addX, addY, addW, addH;

    private int deleteX, deleteY, deleteW, deleteH;

    private int rowsTopY, rowsLeftX, rowsViewportHeight, rowsContentWidth;

    public AnimationTimelinePanel(@Nullable PanelMenuOpener menuOpener) {
        this.menuOpener = menuOpener;
    }

    @Override
    public String title() {
        return "Timeline";
    }

    @Override
    public @Nullable Panel.TabIndicator tabIndicator() {
        return AnimationEditorState.get().isDirty()
            ? new Panel.TabIndicator(DIRTY_COLOR, Component.literal("Unsaved animation changes"))
            : null;
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
        panelX = x;
        panelY = y;
        panelWidth = width;
        panelHeight = height;
        graphics.fill(x, y, x + width, y + height, BG_COLOR);

        var state = AnimationEditorState.get();
        state.syncSelectedBoneFromScene();
        channelSelect.setCurrentValue(state.selectedChannel());

        var font = EngineFont.get();
        var header = state.selectedAnimationName() == null
            ? "No animation selected"
            : state.selectedAnimationName() + " / " + (state.selectedBoneName() == null ? "(no bone)" : state.selectedBoneName());
        UiText.drawClipped(graphics, font, header, x + PADDING, y + PADDING, Math.max(0, width - 2 * PADDING), TEXT_COLOR);

        var toolY = y + PADDING + font.lineHeight + 5;
        channelSelect.render(graphics, x + PADDING, toolY, Math.min(110, Math.max(0, width / 3)), mouseX, mouseY);
        var canAdd = state.hasDraft();
        renderButton(graphics, font, "Add", x + PADDING + Math.min(110, Math.max(0, width / 3)) + 5, toolY, 36, canAdd, mouseX, mouseY);
        addX = x + PADDING + Math.min(110, Math.max(0, width / 3)) + 5;
        addY = toolY;
        addW = 36;
        addH = SearchableSelect.HEIGHT;
        var canDelete = state.selectedKeyframe() != null;
        deleteX = addX + addW + 4;
        deleteY = toolY;
        deleteW = 48;
        deleteH = SearchableSelect.HEIGHT;
        renderButton(graphics, font, "Delete", deleteX, deleteY, deleteW, canDelete, mouseX, mouseY);

        rows.clear();
        if (state.hasDraft() && state.selectedAnimationName() != null && state.selectedBoneName() != null) {
            rows.addAll(state.selectedKeyframes());
        }

        rowsTopY = toolY + TOOL_ROW_HEIGHT + 4;
        rowsLeftX = x + PADDING;
        rowsViewportHeight = Math.max(0, y + height - rowsTopY - PADDING);
        var innerWidth = Math.max(0, width - 2 * PADDING);
        var frame = scroll.begin(graphics, UiRect.of(rowsLeftX, rowsTopY, innerWidth, rowsViewportHeight), rows.size() * ROW_HEIGHT);
        rowsContentWidth = frame.contentWidth();
        try {
            var contentY = frame.contentY();
            for (var i = 0; i < rows.size(); i++) {
                var row = rows.get(i);
                var rowTop = contentY + i * ROW_HEIGHT;
                var rowBottom = rowTop + ROW_HEIGHT;
                if (rowBottom <= rowsTopY || rowTop >= rowsTopY + rowsViewportHeight) {
                    continue;
                }
                var selected = state.selectedTimestamp() != null && Math.abs(state.selectedTimestamp() - row.timestamp()) < 1.0e-6;
                var hovered = mouseX >= rowsLeftX && mouseX < rowsLeftX + rowsContentWidth && mouseY >= rowTop && mouseY < rowBottom;
                if (selected) {
                    graphics.fill(rowsLeftX, rowTop, rowsLeftX + rowsContentWidth, rowBottom, ROW_SELECTED_COLOR);
                } else if (hovered) {
                    graphics.fill(rowsLeftX, rowTop, rowsLeftX + rowsContentWidth, rowBottom, ROW_HOVER_COLOR);
                }
                UiText.drawClipped(
                    graphics,
                    font,
                    rowLabel(row),
                    rowsLeftX + 4,
                    rowTop + (ROW_HEIGHT - font.lineHeight + 2) / 2,
                    Math.max(0, rowsContentWidth - 8),
                    TEXT_COLOR
                );
            }
        } finally {
            scroll.end(graphics, mouseX, mouseY);
        }

        if (rows.isEmpty()) {
            var note = emptyNote(state);
            UiText.drawClipped(graphics, font, note, rowsLeftX + 4, rowsTopY + 4, Math.max(0, rowsContentWidth - 8), META_TEXT_COLOR);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (mouseX < panelX || mouseX >= panelX + panelWidth || mouseY < panelY || mouseY >= panelY + panelHeight) {
            return false;
        }
        if (button == 1) {
            return openContextMenu(mouseX, mouseY);
        }
        if (button != 0) {
            return false;
        }
        if (channelSelect.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (buttonHit(mouseX, mouseY, addX, addY, addW, addH)) {
            if (AnimationEditorState.get().hasDraft()) {
                addKeyframe();
            }
            return true;
        }
        if (buttonHit(mouseX, mouseY, deleteX, deleteY, deleteW, deleteH)) {
            AnimationEditorState.get().deleteSelectedKeyframe();
            return true;
        }
        if (scroll.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        var row = rowAt(mouseX, mouseY);
        if (row == null) {
            return false;
        }
        AnimationEditorState.get().selectKeyframe(row.animationName(), row.boneName(), row.channel(), row.timestamp());
        return true;
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
        if (mouseX < panelX || mouseX >= panelX + panelWidth || mouseY < panelY || mouseY >= panelY + panelHeight) {
            return false;
        }
        return scroll.mouseScrolled(mouseX, mouseY, scrollY);
    }

    private boolean openContextMenu(double mouseX, double mouseY) {
        if (menuOpener == null) {
            return true;
        }
        var row = rowAt(mouseX, mouseY);
        if (row != null) {
            AnimationEditorState.get().selectKeyframe(row.animationName(), row.boneName(), row.channel(), row.timestamp());
        }
        var state = AnimationEditorState.get();
        menuOpener.open(
            new DropdownMenu(
                (int) mouseX,
                (int) mouseY,
                List.of(
                    new DropdownMenu.Item("Add Keyframe", this::addKeyframe, state.hasDraft()),
                    new DropdownMenu.Item("Delete Keyframe", state::deleteSelectedKeyframe, state.selectedKeyframe() != null)
                )
            )
        );
        return true;
    }

    private void addKeyframe() {
        var state = AnimationEditorState.get();
        var animation = state.selectedAnimationName();
        if (animation == null) {
            animation = state.createAnimation(null);
        }
        var bone = state.selectedBoneName();
        if (bone == null || bone.isBlank()) {
            bone = "bone";
        }
        var timestamp = nextTimestamp(state.selectedKeyframes());
        state.selectKeyframe(animation, bone, state.selectedChannel(), timestamp);
        state.createOrUpdateSelectedKeyframe();
    }

    private static double nextTimestamp(List<KeyframeRef> frames) {
        if (frames.isEmpty()) {
            return 0.0;
        }
        return frames.get(frames.size() - 1).timestamp() + 0.25;
    }

    private @Nullable KeyframeRef rowAt(double mouseX, double mouseY) {
        if (mouseY < rowsTopY || mouseY >= rowsTopY + rowsViewportHeight) {
            return null;
        }
        if (mouseX < rowsLeftX || mouseX >= rowsLeftX + rowsContentWidth) {
            return null;
        }
        var contentY = (int) (mouseY - rowsTopY) + scroll.scrollY();
        if (contentY < 0) {
            return null;
        }
        var idx = contentY / ROW_HEIGHT;
        return idx >= 0 && idx < rows.size() ? rows.get(idx) : null;
    }

    private static String rowLabel(KeyframeRef row) {
        var vector = row.keyframe().has("vector") && row.keyframe().get("vector").isJsonArray()
            ? row.keyframe().getAsJsonArray("vector")
            : null;
        var x = vector != null && vector.size() > 0 ? AnimationEditorState.elementToText(vector.get(0)) : "0";
        var y = vector != null && vector.size() > 1 ? AnimationEditorState.elementToText(vector.get(1)) : "0";
        var z = vector != null && vector.size() > 2 ? AnimationEditorState.elementToText(vector.get(2)) : "0";
        var easing = row.keyframe().has("easing") ? "  " + AnimationEditorState.elementToText(row.keyframe().get("easing")) : "";
        return AnimationEditorState.formatTimestamp(row.timestamp()) + "s   [" + x + ", " + y + ", " + z + "]" + easing;
    }

    private static String emptyNote(AnimationEditorState state) {
        if (!state.hasDraft()) {
            return "(no animation file open)";
        }
        if (state.selectedAnimationName() == null) {
            return "(no animation selected)";
        }
        if (state.selectedBoneName() == null) {
            return "(select a bone/group)";
        }
        return "(no " + state.selectedChannel().jsonName() + " keyframes)";
    }

    private static void renderButton(
        GuiGraphics graphics,
        net.minecraft.client.gui.Font font,
        String label,
        int x,
        int y,
        int width,
        boolean enabled,
        int mouseX,
        int mouseY
    ) {
        var hovered = enabled && buttonHit(mouseX, mouseY, x, y, width, SearchableSelect.HEIGHT);
        graphics.fill(x, y, x + width, y + SearchableSelect.HEIGHT, hovered ? CHIP_HOVER_BG_COLOR : CHIP_BG_COLOR);
        UiText.drawClipped(
            graphics,
            font,
            label,
            x + 4,
            y + (SearchableSelect.HEIGHT - font.lineHeight + 2) / 2,
            Math.max(0, width - 8),
            enabled ? TEXT_COLOR : CHIP_DISABLED_TEXT_COLOR
        );
    }

    private static boolean buttonHit(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    private static List<SearchableSelect.Item<TransformChannel>> channelItems() {
        return List.of(
            new SearchableSelect.Item<>(TransformChannel.POSITION, "position"),
            new SearchableSelect.Item<>(TransformChannel.ROTATION, "rotation"),
            new SearchableSelect.Item<>(TransformChannel.SCALE, "scale")
        );
    }
}
