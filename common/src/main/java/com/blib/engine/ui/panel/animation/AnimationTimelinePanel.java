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

@ApiStatus.Internal
public final class AnimationTimelinePanel implements Panel {

    private static final int BG_COLOR = 0xFF18181C;

    private static final int RULER_BG_COLOR = 0xFF202024;

    private static final int TRACK_BG_COLOR = 0xFF151519;

    private static final int TRACK_ALT_BG_COLOR = 0xFF1A1A1F;

    private static final int TRACK_HOVER_COLOR = 0xFF24242A;

    private static final int TRACK_LABEL_BG_COLOR = 0xFF202026;

    private static final int TRACK_BORDER_COLOR = 0xFF2A2A30;

    private static final int TICK_MAJOR_COLOR = 0xFF4A4A54;

    private static final int TICK_MINOR_COLOR = 0xFF33333A;

    private static final int LANE_LINE_COLOR = 0xFF33333A;

    private static final int PLAYHEAD_COLOR = 0xFFFFC857;

    private static final int PLAYHEAD_HANDLE_COLOR = 0xFFFFD983;

    private static final int POSITION_KEY_COLOR = 0xFFE06C75;

    private static final int ROTATION_KEY_COLOR = 0xFF61AFEF;

    private static final int SCALE_KEY_COLOR = 0xFF98C379;

    private static final int KEY_SELECTED_COLOR = 0xFFFFF0A8;

    private static final int TEXT_COLOR = 0xFFD0D0D0;

    private static final int META_TEXT_COLOR = 0xFF808088;

    private static final int CHIP_BG_COLOR = 0xFF2C2C32;

    private static final int CHIP_HOVER_BG_COLOR = 0xFF3C3C46;

    private static final int CHIP_DISABLED_TEXT_COLOR = 0xFF777780;

    private static final int BUTTON_BORDER_COLOR = 0xFF3A3A40;

    private static final int ICON_COLOR = 0xFFE0E0E0;

    private static final int ICON_DISABLED_COLOR = 0xFF606068;

    private static final int DIRTY_COLOR = 0xFFE6C26B;

    private static final int PADDING = 6;

    private static final int BUTTON_SIZE = 16;

    private static final int TOOL_ROW_HEIGHT = BUTTON_SIZE;

    private static final int RULER_HEIGHT = 17;

    private static final int TRACK_HEIGHT = 23;

    private static final int TRACK_LABEL_WIDTH = 74;

    private static final int KEYFRAME_RADIUS = 4;

    private static final int KEYFRAME_HIT_PX = 6;

    private final ScrollViewport scroll = new ScrollViewport();

    private final @Nullable PanelMenuOpener menuOpener;

    private final SearchableSelect<TransformChannel> channelSelect = new SearchableSelect<>(
        AnimationTimelinePanel::channelItems,
        TransformChannel::jsonName,
        TransformChannel.ROTATION,
        channel -> AnimationEditorState.get().selectChannel(channel)
    );

    private final List<TrackRow> tracks = new ArrayList<>();

    private int panelX, panelY, panelWidth, panelHeight;

    private int playX, playY, playW, playH;

    private int addX, addY, addW, addH;

    private int deleteX, deleteY, deleteW, deleteH;

    private int timelineViewportX, timelineViewportY, timelineViewportW, timelineViewportH;

    private int timelineContentY, timelineContentHeight;

    private int timelineGraphX, timelineGraphWidth;

    private double timelineDuration = 1.0;

    private boolean draggingPlayhead;

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
        state.updatePlaybackClock();
        channelSelect.setCurrentValue(state.selectedChannel());
        timelineDuration = state.selectedAnimationLengthSeconds();

        var font = EngineFont.get();
        var header = state.selectedAnimationName() == null
            ? "No animation selected"
            : state.selectedAnimationName() + " / " + (state.selectedBoneName() == null ? "(no bone)" : state.selectedBoneName());
        UiText.drawClipped(graphics, font, header, x + PADDING, y + PADDING, Math.max(0, width - 2 * PADDING), TEXT_COLOR);

        renderToolbar(graphics, font, x, y, width, state, mouseX, mouseY);
        rebuildTracks(state);
        renderTimeline(graphics, font, x, y, width, height, state, mouseX, mouseY);
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

        var state = AnimationEditorState.get();
        if (channelSelect.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (buttonHit(mouseX, mouseY, playX, playY, playW, playH)) {
            if (canPlay(state)) {
                state.togglePlayback();
            }
            return true;
        }
        if (buttonHit(mouseX, mouseY, addX, addY, addW, addH)) {
            if (state.hasDraft()) {
                addKeyframeAtPlayhead();
            }
            return true;
        }
        if (buttonHit(mouseX, mouseY, deleteX, deleteY, deleteW, deleteH)) {
            state.deleteSelectedKeyframe();
            return true;
        }

        var keyframe = keyframeAt(mouseX, mouseY);
        if (keyframe != null) {
            state.selectKeyframe(keyframe.animationName(), keyframe.boneName(), keyframe.channel(), keyframe.timestamp());
            state.setPlayheadSeconds(keyframe.timestamp());
            return true;
        }
        if (timelineGraphHit(mouseX, mouseY)) {
            draggingPlayhead = true;
            setPlayheadFromMouse(mouseX);
            return true;
        }
        return scroll.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (draggingPlayhead && button == 0) {
            setPlayheadFromMouse(mouseX);
            return true;
        }
        return scroll.mouseDragged(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (draggingPlayhead && button == 0) {
            draggingPlayhead = false;
            return true;
        }
        return scroll.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (mouseX < panelX || mouseX >= panelX + panelWidth || mouseY < panelY || mouseY >= panelY + panelHeight) {
            return false;
        }
        return scroll.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    private void renderToolbar(
        GuiGraphics graphics,
        net.minecraft.client.gui.Font font,
        int x,
        int y,
        int width,
        AnimationEditorState state,
        int mouseX,
        int mouseY
    ) {
        var toolY = y + PADDING + font.lineHeight + 5;
        var cursorX = x + PADDING;
        playX = cursorX;
        playY = toolY;
        playW = BUTTON_SIZE;
        playH = BUTTON_SIZE;
        renderPlayButton(graphics, playX, playY, state.isPlaying(), canPlay(state), mouseX, mouseY);
        cursorX += BUTTON_SIZE + 5;

        var right = x + width - PADDING;
        var timeWidth = Math.min(92, Math.max(48, right - cursorX));
        var timeText = AnimationEditorState.formatTimestamp(state.playheadSeconds()) + " / "
            + AnimationEditorState.formatTimestamp(timelineDuration) + "s";
        UiText.drawClipped(
            graphics,
            font,
            timeText,
            cursorX,
            toolY + (BUTTON_SIZE - font.lineHeight + 2) / 2,
            timeWidth,
            TEXT_COLOR
        );
        cursorX += timeWidth + 5;

        var remainingForSelect = right - cursorX - 93;
        var channelWidth = Math.min(104, Math.max(0, remainingForSelect));
        channelSelect.render(graphics, cursorX, toolY, channelWidth, mouseX, mouseY);
        cursorX += channelWidth + 5;

        addX = cursorX;
        addY = toolY;
        addW = 36;
        addH = SearchableSelect.HEIGHT;
        renderButton(graphics, font, "Add", addX, addY, addW, state.hasDraft(), mouseX, mouseY);
        cursorX += addW + 4;

        deleteX = cursorX;
        deleteY = toolY;
        deleteW = 48;
        deleteH = SearchableSelect.HEIGHT;
        renderButton(graphics, font, "Delete", deleteX, deleteY, deleteW, state.selectedKeyframe() != null, mouseX, mouseY);
    }

    private void rebuildTracks(AnimationEditorState state) {
        tracks.clear();
        var animation = state.selectedAnimationName();
        var bone = state.selectedBoneName();
        if (!state.hasDraft() || animation == null || bone == null) {
            return;
        }
        for (var channel : TransformChannel.values()) {
            tracks.add(new TrackRow(channel, state.keyframes(animation, bone, channel)));
        }
    }

    private void renderTimeline(
        GuiGraphics graphics,
        net.minecraft.client.gui.Font font,
        int x,
        int y,
        int width,
        int height,
        AnimationEditorState state,
        int mouseX,
        int mouseY
    ) {
        timelineViewportX = x + PADDING;
        timelineViewportY = y + PADDING + font.lineHeight + 5 + TOOL_ROW_HEIGHT + 5;
        timelineViewportW = Math.max(0, width - 2 * PADDING);
        timelineViewportH = Math.max(0, y + height - timelineViewportY - PADDING);
        timelineContentHeight = RULER_HEIGHT + Math.max(1, tracks.size()) * TRACK_HEIGHT;

        var frame = scroll.begin(graphics, UiRect.of(timelineViewportX, timelineViewportY, timelineViewportW, timelineViewportH), timelineContentHeight);
        var contentX = frame.contentX();
        timelineContentY = frame.contentY();
        var contentWidth = frame.contentWidth();
        var labelWidth = Math.min(contentWidth, Math.min(TRACK_LABEL_WIDTH, Math.max(42, contentWidth / 3)));
        timelineGraphX = contentX + labelWidth;
        timelineGraphWidth = Math.max(1, contentWidth - labelWidth - 2);
        try {
            renderRuler(graphics, font, contentX, timelineContentY, labelWidth, timelineGraphX, timelineGraphWidth, timelineDuration);
            if (tracks.isEmpty()) {
                renderEmptyTimeline(graphics, font, state, contentX, timelineContentY + RULER_HEIGHT, contentWidth);
            } else {
                for (var i = 0; i < tracks.size(); i++) {
                    renderTrack(graphics, font, tracks.get(i), i, contentX, labelWidth, mouseX, mouseY, state);
                }
            }
            renderPlayhead(graphics, timelineContentY, timelineContentHeight, timelineGraphX, timelineGraphWidth, state.playheadSeconds(), timelineDuration);
        } finally {
            scroll.end(graphics, mouseX, mouseY);
        }
    }

    private void renderRuler(
        GuiGraphics graphics,
        net.minecraft.client.gui.Font font,
        int contentX,
        int rulerY,
        int labelWidth,
        int graphX,
        int graphWidth,
        double duration
    ) {
        graphics.fill(contentX, rulerY, graphX + graphWidth, rulerY + RULER_HEIGHT, RULER_BG_COLOR);
        graphics.fill(contentX, rulerY + RULER_HEIGHT - 1, graphX + graphWidth, rulerY + RULER_HEIGHT, TRACK_BORDER_COLOR);
        UiText.drawClipped(graphics, font, "time", contentX + 4, rulerY + 4, Math.max(0, labelWidth - 8), META_TEXT_COLOR);

        var step = tickStep(duration, graphWidth);
        for (var t = 0.0; t <= duration + 1.0e-6; t += step) {
            var tickX = timeToX(t, graphX, graphWidth, duration);
            var major = Math.abs(t - Math.rint(t)) < 1.0e-6 || t == 0.0 || Math.abs(t - duration) < 1.0e-6;
            graphics.fill(tickX, rulerY + (major ? 2 : 8), tickX + 1, rulerY + RULER_HEIGHT, major ? TICK_MAJOR_COLOR : TICK_MINOR_COLOR);
            if (major) {
                UiText.drawClipped(
                    graphics,
                    font,
                    AnimationEditorState.formatTimestamp(t),
                    tickX + 3,
                    rulerY + 2,
                    34,
                    META_TEXT_COLOR
                );
            }
        }
    }

    private void renderTrack(
        GuiGraphics graphics,
        net.minecraft.client.gui.Font font,
        TrackRow row,
        int index,
        int contentX,
        int labelWidth,
        int mouseX,
        int mouseY,
        AnimationEditorState state
    ) {
        var trackY = timelineContentY + RULER_HEIGHT + index * TRACK_HEIGHT;
        var trackBottom = trackY + TRACK_HEIGHT;
        if (trackBottom <= timelineViewportY || trackY >= timelineViewportY + timelineViewportH) {
            return;
        }
        var hovered = mouseY >= trackY
            && mouseY < trackBottom
            && mouseX >= timelineGraphX
            && mouseX < timelineGraphX + timelineGraphWidth;
        var bg = hovered ? TRACK_HOVER_COLOR : (index % 2 == 0 ? TRACK_BG_COLOR : TRACK_ALT_BG_COLOR);
        graphics.fill(contentX, trackY, timelineGraphX + timelineGraphWidth, trackBottom, bg);
        graphics.fill(contentX, trackY, contentX + labelWidth, trackBottom, TRACK_LABEL_BG_COLOR);
        graphics.fill(contentX, trackBottom - 1, timelineGraphX + timelineGraphWidth, trackBottom, TRACK_BORDER_COLOR);

        UiText.drawClipped(
            graphics,
            font,
            row.channel().jsonName(),
            contentX + 4,
            trackY + (TRACK_HEIGHT - font.lineHeight + 2) / 2,
            Math.max(0, labelWidth - 8),
            row.channel() == state.selectedChannel() ? TEXT_COLOR : META_TEXT_COLOR
        );

        var laneY = trackY + TRACK_HEIGHT / 2;
        graphics.fill(timelineGraphX, laneY, timelineGraphX + timelineGraphWidth, laneY + 1, LANE_LINE_COLOR);
        for (var frame : row.frames()) {
            var keyX = timeToX(frame.timestamp(), timelineGraphX, timelineGraphWidth, timelineDuration);
            var selected = state.selectedTimestamp() != null
                && frame.channel() == state.selectedChannel()
                && Math.abs(frame.timestamp() - state.selectedTimestamp()) < 1.0e-6;
            drawKeyframe(graphics, keyX, laneY, selected ? KEY_SELECTED_COLOR : keyColor(row.channel()), selected);
        }
    }

    private void renderEmptyTimeline(
        GuiGraphics graphics,
        net.minecraft.client.gui.Font font,
        AnimationEditorState state,
        int contentX,
        int y,
        int contentWidth
    ) {
        graphics.fill(contentX, y, contentX + contentWidth, y + TRACK_HEIGHT, TRACK_BG_COLOR);
        UiText.drawClipped(graphics, font, emptyNote(state), contentX + 4, y + 6, Math.max(0, contentWidth - 8), META_TEXT_COLOR);
    }

    private void renderPlayhead(
        GuiGraphics graphics,
        int contentY,
        int contentHeight,
        int graphX,
        int graphWidth,
        double playhead,
        double duration
    ) {
        var x = timeToX(playhead, graphX, graphWidth, duration);
        graphics.fill(x, contentY, x + 1, contentY + contentHeight, PLAYHEAD_COLOR);
        graphics.fill(x - 3, contentY + 1, x + 4, contentY + 3, PLAYHEAD_HANDLE_COLOR);
        graphics.fill(x - 2, contentY + 3, x + 3, contentY + 5, PLAYHEAD_HANDLE_COLOR);
        graphics.fill(x - 1, contentY + 5, x + 2, contentY + 7, PLAYHEAD_HANDLE_COLOR);
    }

    private boolean openContextMenu(double mouseX, double mouseY) {
        if (menuOpener == null) {
            return true;
        }
        var keyframe = keyframeAt(mouseX, mouseY);
        if (keyframe != null) {
            AnimationEditorState.get().selectKeyframe(keyframe.animationName(), keyframe.boneName(), keyframe.channel(), keyframe.timestamp());
            AnimationEditorState.get().setPlayheadSeconds(keyframe.timestamp());
        }
        var state = AnimationEditorState.get();
        menuOpener.open(
            new DropdownMenu(
                (int) mouseX,
                (int) mouseY,
                List.of(
                    new DropdownMenu.Item(state.isPlaying() ? "Pause" : "Play", state::togglePlayback, canPlay(state)),
                    new DropdownMenu.Item("Add Keyframe", this::addKeyframeAtPlayhead, state.hasDraft()),
                    new DropdownMenu.Item("Delete Keyframe", state::deleteSelectedKeyframe, state.selectedKeyframe() != null)
                )
            )
        );
        return true;
    }

    private void addKeyframeAtPlayhead() {
        var state = AnimationEditorState.get();
        var animation = state.selectedAnimationName();
        if (animation == null) {
            animation = state.createAnimation(null);
        }
        var bone = state.selectedBoneName();
        if (bone == null || bone.isBlank()) {
            bone = "bone";
        }
        var timestamp = state.playheadSeconds();
        state.selectKeyframe(animation, bone, state.selectedChannel(), timestamp);
        state.createOrUpdateSelectedKeyframe();
    }

    private @Nullable KeyframeRef keyframeAt(double mouseX, double mouseY) {
        var track = trackAt(mouseY);
        if (track == null) {
            return null;
        }
        if (mouseX < timelineGraphX - KEYFRAME_HIT_PX || mouseX >= timelineGraphX + timelineGraphWidth + KEYFRAME_HIT_PX) {
            return null;
        }
        var laneY = timelineContentY + RULER_HEIGHT + tracks.indexOf(track) * TRACK_HEIGHT + TRACK_HEIGHT / 2;
        if (Math.abs(mouseY - laneY) > KEYFRAME_HIT_PX + 2) {
            return null;
        }

        KeyframeRef nearest = null;
        var nearestDistance = Double.MAX_VALUE;
        for (var frame : track.frames()) {
            var keyX = timeToX(frame.timestamp(), timelineGraphX, timelineGraphWidth, timelineDuration);
            var distance = Math.abs(mouseX - keyX);
            if (distance <= KEYFRAME_HIT_PX && distance < nearestDistance) {
                nearest = frame;
                nearestDistance = distance;
            }
        }
        return nearest;
    }

    private @Nullable TrackRow trackAt(double mouseY) {
        var localY = mouseY - timelineContentY - RULER_HEIGHT;
        if (localY < 0.0) {
            return null;
        }
        var index = (int) (localY / TRACK_HEIGHT);
        return index >= 0 && index < tracks.size() ? tracks.get(index) : null;
    }

    private boolean timelineGraphHit(double mouseX, double mouseY) {
        return mouseX >= timelineGraphX
            && mouseX < timelineGraphX + timelineGraphWidth
            && mouseY >= timelineViewportY
            && mouseY < timelineViewportY + timelineViewportH;
    }

    private void setPlayheadFromMouse(double mouseX) {
        var state = AnimationEditorState.get();
        var ratio = (mouseX - timelineGraphX) / Math.max(1.0, timelineGraphWidth - 1.0);
        ratio = Math.max(0.0, Math.min(1.0, ratio));
        state.setPlayheadSeconds(ratio * timelineDuration);
    }

    private static boolean canPlay(AnimationEditorState state) {
        return state.hasDraft() && state.selectedAnimationName() != null;
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
        return "(no keyframes for this bone/group)";
    }

    private static int timeToX(double seconds, int graphX, int graphWidth, double duration) {
        var ratio = duration <= 0.0 ? 0.0 : Math.max(0.0, Math.min(1.0, seconds / duration));
        return graphX + (int) Math.round(ratio * Math.max(1, graphWidth - 1));
    }

    private static double tickStep(double duration, int graphWidth) {
        if (duration <= 0.5 || graphWidth <= 0) {
            return 0.1;
        }
        var targetTicks = Math.max(2.0, graphWidth / 58.0);
        var rough = duration / targetTicks;
        var choices = new double[] { 0.1, 0.25, 0.5, 1.0, 2.0, 5.0, 10.0, 30.0 };
        for (var choice : choices) {
            if (choice >= rough) {
                return choice;
            }
        }
        return 60.0;
    }

    private static int keyColor(TransformChannel channel) {
        return switch (channel) {
            case POSITION -> POSITION_KEY_COLOR;
            case ROTATION -> ROTATION_KEY_COLOR;
            case SCALE -> SCALE_KEY_COLOR;
        };
    }

    private static void drawKeyframe(GuiGraphics graphics, int centerX, int centerY, int color, boolean selected) {
        if (selected) {
            drawDiamond(graphics, centerX, centerY, KEYFRAME_RADIUS + 2, 0xFF101014);
            drawDiamond(graphics, centerX, centerY, KEYFRAME_RADIUS + 1, color);
        } else {
            drawDiamond(graphics, centerX, centerY, KEYFRAME_RADIUS, color);
        }
    }

    private static void drawDiamond(GuiGraphics graphics, int centerX, int centerY, int radius, int color) {
        for (var y = -radius; y <= radius; y++) {
            var halfWidth = radius - Math.abs(y);
            graphics.fill(centerX - halfWidth, centerY + y, centerX + halfWidth + 1, centerY + y + 1, color);
        }
    }

    private static void renderPlayButton(GuiGraphics graphics, int x, int y, boolean playing, boolean enabled, int mouseX, int mouseY) {
        var hovered = enabled && buttonHit(mouseX, mouseY, x, y, BUTTON_SIZE, BUTTON_SIZE);
        graphics.fill(x, y, x + BUTTON_SIZE, y + BUTTON_SIZE, hovered ? CHIP_HOVER_BG_COLOR : CHIP_BG_COLOR);
        drawButtonBorder(graphics, x, y, BUTTON_SIZE, BUTTON_SIZE);
        if (playing) {
            drawPauseIcon(graphics, x, y, enabled ? ICON_COLOR : ICON_DISABLED_COLOR);
        } else {
            drawPlayIcon(graphics, x, y, enabled ? ICON_COLOR : ICON_DISABLED_COLOR);
        }
    }

    private static void drawPlayIcon(GuiGraphics graphics, int btnX, int btnY, int color) {
        var rows = 7;
        var halfHeight = (rows - 1) / 2;
        var triangleWidth = halfHeight + 1;
        var startX = btnX + (BUTTON_SIZE - triangleWidth) / 2 + 1;
        var startY = btnY + (BUTTON_SIZE - rows) / 2;
        for (var row = 0; row < rows; row++) {
            var width = halfHeight + 1 - Math.abs(row - halfHeight);
            graphics.fill(startX, startY + row, startX + width, startY + row + 1, color);
        }
    }

    private static void drawPauseIcon(GuiGraphics graphics, int btnX, int btnY, int color) {
        var cx = btnX + BUTTON_SIZE / 2;
        var cy = btnY + BUTTON_SIZE / 2;
        graphics.fill(cx - 4, cy - 4, cx - 2, cy + 4, color);
        graphics.fill(cx + 2, cy - 4, cx + 4, cy + 4, color);
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
        drawButtonBorder(graphics, x, y, width, SearchableSelect.HEIGHT);
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

    private static void drawButtonBorder(GuiGraphics graphics, int x, int y, int width, int height) {
        graphics.fill(x, y, x + width, y + 1, BUTTON_BORDER_COLOR);
        graphics.fill(x, y + height - 1, x + width, y + height, BUTTON_BORDER_COLOR);
        graphics.fill(x, y, x + 1, y + height, BUTTON_BORDER_COLOR);
        graphics.fill(x + width - 1, y, x + width, y + height, BUTTON_BORDER_COLOR);
    }

    private static boolean buttonHit(double mouseX, double mouseY, int x, int y, int width, int height) {
        return width > 0 && height > 0 && mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    private static List<SearchableSelect.Item<TransformChannel>> channelItems() {
        return List.of(
            new SearchableSelect.Item<>(TransformChannel.POSITION, "position"),
            new SearchableSelect.Item<>(TransformChannel.ROTATION, "rotation"),
            new SearchableSelect.Item<>(TransformChannel.SCALE, "scale")
        );
    }

    private record TrackRow(
        TransformChannel channel,
        List<KeyframeRef> frames
    ) {}
}
