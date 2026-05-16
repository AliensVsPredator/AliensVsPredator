package com.blib.engine.ui.panel.animation;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.blib.engine.modeler.ModelerBone;
import com.blib.engine.modeler.ModelerScene;
import com.blib.engine.modeler.Selection;
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

    private static final int BUTTON_BORDER_COLOR = 0xFF3A3A40;

    private static final int ICON_COLOR = 0xFFE0E0E0;

    private static final int ICON_DISABLED_COLOR = 0xFF606068;

    private static final int DIRTY_COLOR = 0xFFE6C26B;

    private static final int PADDING = 6;

    private static final int BUTTON_SIZE = 16;

    private static final int TOOL_ROW_HEIGHT = BUTTON_SIZE;

    private static final int RULER_HEIGHT = 17;

    private static final int TRACK_HEIGHT = 23;

    private static final int TRACK_LABEL_WIDTH = 156;

    private static final int INDENT_PX = 10;

    private static final int CARET_WIDTH = 10;

    private static final int KEYFRAME_RADIUS = 4;

    private static final int KEYFRAME_HIT_PX = 6;

    private final ScrollViewport scroll = new ScrollViewport();

    private final @Nullable PanelMenuOpener menuOpener;

    private final List<TimelineRow> rows = new ArrayList<>();

    private final Set<ModelerBone> collapsedBones = new HashSet<>();

    private @Nullable ModelerBone lastSceneRoot;

    private @Nullable ModelerBone timelineRootBone;

    private @Nullable Selection lastSceneSelection;

    private boolean suppressNextSceneSelectionScope;

    private int panelX, panelY, panelWidth, panelHeight;

    private int playX, playY, playW, playH;

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
        syncTimelineScopeFromScene(state);
        state.updatePlaybackClock();
        timelineDuration = state.selectedAnimationLengthSeconds();

        var font = EngineFont.get();
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
        if (buttonHit(mouseX, mouseY, playX, playY, playW, playH)) {
            if (canPlay(state)) {
                state.togglePlayback();
            }
            return true;
        }

        var keyframe = keyframeAt(mouseX, mouseY);
        if (keyframe != null) {
            var alreadySelected = isSelectedKeyframe(state, keyframe);
            var row = channelRowAt(mouseY);
            if (row != null) {
                selectBoneFromTimeline(row.bone());
            }
            state.selectKeyframe(keyframe.documentId(), keyframe.animationName(), keyframe.boneName(), keyframe.channel(), keyframe.timestamp());
            if (alreadySelected) {
                state.setPlayheadSeconds(keyframe.timestamp());
            }
            return true;
        }
        var row = rowAt(mouseY);
        if (row != null && mouseX >= timelineViewportX && mouseX < timelineGraphX) {
            if (row.isBone() && caretHit(row, mouseX)) {
                if (!collapsedBones.remove(row.bone())) {
                    collapsedBones.add(row.bone());
                }
                return true;
            }
            selectBoneFromTimeline(row.bone());
            state.selectBone(row.bone().name);
            if (!row.isBone()) {
                state.selectChannel(row.channel());
            }
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
        var toolY = y + PADDING;
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
    }

    private void rebuildTracks(AnimationEditorState state) {
        rows.clear();
        var animation = state.selectedAnimationName();
        var root = selectedTimelineRoot(state);
        if (!state.hasDraft() || animation == null || root == null) {
            return;
        }
        collectRows(state, animation, root, rows);
    }

    private void collectRows(AnimationEditorState state, String animation, ModelerBone bone, List<TimelineRow> out) {
        var position = state.keyframes(animation, bone.name, TransformChannel.POSITION);
        var rotation = state.keyframes(animation, bone.name, TransformChannel.ROTATION);
        var scale = state.keyframes(animation, bone.name, TransformChannel.SCALE);
        var hasOwnKeyframes = !position.isEmpty() || !rotation.isEmpty() || !scale.isEmpty();
        if (hasOwnKeyframes) {
            out.add(TimelineRow.bone(bone, 0));
            if (!collapsedBones.contains(bone)) {
                out.add(TimelineRow.channel(bone, 1, TransformChannel.ROTATION, rotation));
                out.add(TimelineRow.channel(bone, 1, TransformChannel.POSITION, position));
                out.add(TimelineRow.channel(bone, 1, TransformChannel.SCALE, scale));
            }
        }
        for (var child : bone.children) {
            collectRows(state, animation, child, out);
        }
    }

    private void syncTimelineScopeFromScene(AnimationEditorState state) {
        var scene = ModelerScene.get();
        if (scene.root != lastSceneRoot) {
            collapsedBones.clear();
            timelineRootBone = null;
            lastSceneRoot = scene.root;
            lastSceneSelection = null;
            suppressNextSceneSelectionScope = false;
        }
        if (scene.selection != lastSceneSelection) {
            lastSceneSelection = scene.selection;
            if (suppressNextSceneSelectionScope) {
                suppressNextSceneSelectionScope = false;
            } else if (scene.selection instanceof Selection.BoneSelection bs) {
                timelineRootBone = bs.bone();
                state.selectBone(bs.bone().name);
            }
        }
        if (timelineRootBone == null) {
            var selectedName = state.selectedBoneName();
            timelineRootBone = selectedName == null ? null : findBone(scene.root, selectedName);
        }
    }

    private @Nullable ModelerBone selectedTimelineRoot(AnimationEditorState state) {
        if (timelineRootBone != null) {
            return timelineRootBone;
        }
        var scene = ModelerScene.get();
        var selectedName = state.selectedBoneName();
        return selectedName == null ? null : findBone(scene.root, selectedName);
    }

    private void selectBoneFromTimeline(ModelerBone bone) {
        suppressNextSceneSelectionScope = true;
        ModelerScene.get().selection = new Selection.BoneSelection(bone);
    }

    private static @Nullable ModelerBone findBone(ModelerBone bone, String name) {
        if (bone.name.equals(name)) {
            return bone;
        }
        for (var child : bone.children) {
            var found = findBone(child, name);
            if (found != null) {
                return found;
            }
        }
        return null;
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
        timelineViewportY = y + PADDING + TOOL_ROW_HEIGHT + 5;
        timelineViewportW = Math.max(0, width - 2 * PADDING);
        timelineViewportH = Math.max(0, y + height - timelineViewportY - PADDING);
        timelineContentHeight = RULER_HEIGHT + Math.max(1, rows.size()) * TRACK_HEIGHT;

        var frame = scroll.begin(graphics, UiRect.of(timelineViewportX, timelineViewportY, timelineViewportW, timelineViewportH), timelineContentHeight);
        var contentX = frame.contentX();
        timelineContentY = frame.contentY();
        var contentWidth = frame.contentWidth();
        var labelWidth = Math.min(contentWidth, Math.min(TRACK_LABEL_WIDTH, Math.max(42, contentWidth / 3)));
        timelineGraphX = contentX + labelWidth;
        timelineGraphWidth = Math.max(1, contentWidth - labelWidth - 2);
        try {
            renderRuler(graphics, font, contentX, timelineContentY, labelWidth, timelineGraphX, timelineGraphWidth, timelineDuration);
            if (rows.isEmpty()) {
                renderEmptyTimeline(graphics, font, state, contentX, timelineContentY + RULER_HEIGHT, contentWidth);
            } else {
                for (var i = 0; i < rows.size(); i++) {
                    renderRow(graphics, font, rows.get(i), i, contentX, labelWidth, mouseX, mouseY, state);
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
        UiText.drawClipped(graphics, font, "track", contentX + 4, rulerY + 4, Math.max(0, labelWidth - 8), META_TEXT_COLOR);
        graphics.fill(graphX, rulerY, graphX + 1, rulerY + RULER_HEIGHT, TRACK_BORDER_COLOR);

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

    private void renderRow(
        GuiGraphics graphics,
        net.minecraft.client.gui.Font font,
        TimelineRow row,
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
        graphics.fill(timelineGraphX, trackY, timelineGraphX + 1, trackBottom, TRACK_BORDER_COLOR);
        graphics.fill(contentX, trackBottom - 1, timelineGraphX + timelineGraphWidth, trackBottom, TRACK_BORDER_COLOR);

        if (row.isBone()) {
            renderBoneLabel(graphics, font, row, contentX, trackY, labelWidth, state);
            return;
        }

        renderChannelLabel(graphics, font, row, contentX, trackY, labelWidth, state);
        var laneY = trackY + TRACK_HEIGHT / 2;
        graphics.fill(timelineGraphX, laneY, timelineGraphX + timelineGraphWidth, laneY + 1, LANE_LINE_COLOR);
        for (var frame : row.frames()) {
            var keyX = timeToX(frame.timestamp(), timelineGraphX, timelineGraphWidth, timelineDuration);
            var selected = state.selectedTimestamp() != null
                && state.selectedDocumentId() != null
                && frame.documentId() == state.selectedDocumentId()
                && frame.channel() == state.selectedChannel()
                && frame.boneName().equals(state.selectedBoneName())
                && Math.abs(frame.timestamp() - state.selectedTimestamp()) < 1.0e-6;
            drawKeyframe(graphics, keyX, laneY, selected ? KEY_SELECTED_COLOR : keyColor(row.channel()), selected);
        }
    }

    private void renderBoneLabel(
        GuiGraphics graphics,
        net.minecraft.client.gui.Font font,
        TimelineRow row,
        int contentX,
        int trackY,
        int labelWidth,
        AnimationEditorState state
    ) {
        var labelY = trackY + (TRACK_HEIGHT - font.lineHeight + 2) / 2;
        var caretX = contentX + 4 + row.depth() * INDENT_PX;
        drawCaret(graphics, caretX + 2, trackY + (TRACK_HEIGHT - 7) / 2, collapsedBones.contains(row.bone()), META_TEXT_COLOR);
        var textX = caretX + CARET_WIDTH;
        UiText.drawClipped(
            graphics,
            font,
            row.bone().name,
            textX,
            labelY,
            Math.max(0, contentX + labelWidth - textX - 4),
            row.bone().name.equals(state.selectedBoneName()) ? TEXT_COLOR : META_TEXT_COLOR
        );
    }

    private void renderChannelLabel(
        GuiGraphics graphics,
        net.minecraft.client.gui.Font font,
        TimelineRow row,
        int contentX,
        int trackY,
        int labelWidth,
        AnimationEditorState state
    ) {
        var textX = contentX + 4 + row.depth() * INDENT_PX + CARET_WIDTH;
        UiText.drawClipped(
            graphics,
            font,
            row.channel().jsonName(),
            textX,
            trackY + (TRACK_HEIGHT - font.lineHeight + 2) / 2,
            Math.max(0, contentX + labelWidth - textX - 4),
            row.bone().name.equals(state.selectedBoneName()) && row.channel() == state.selectedChannel() ? TEXT_COLOR : META_TEXT_COLOR
        );
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
            var row = channelRowAt(mouseY);
            if (row != null) {
                selectBoneFromTimeline(row.bone());
            }
            AnimationEditorState.get().selectKeyframe(keyframe.documentId(), keyframe.animationName(), keyframe.boneName(), keyframe.channel(), keyframe.timestamp());
        }
        var state = AnimationEditorState.get();
        menuOpener.open(
            new DropdownMenu(
                (int) mouseX,
                (int) mouseY,
                List.of(
                    new DropdownMenu.Item(state.isPlaying() ? "Pause" : "Play", state::togglePlayback, canPlay(state))
                )
            )
        );
        return true;
    }

    private static boolean isSelectedKeyframe(AnimationEditorState state, KeyframeRef keyframe) {
        return state.selectedTimestamp() != null
            && state.selectedDocumentId() != null
            && keyframe.documentId() == state.selectedDocumentId()
            && keyframe.animationName().equals(state.selectedAnimationName())
            && keyframe.boneName().equals(state.selectedBoneName())
            && keyframe.channel() == state.selectedChannel()
            && Math.abs(keyframe.timestamp() - state.selectedTimestamp()) < 1.0e-6;
    }

    private @Nullable KeyframeRef keyframeAt(double mouseX, double mouseY) {
        var index = rowIndexAt(mouseY);
        if (index < 0 || index >= rows.size()) {
            return null;
        }
        var track = rows.get(index);
        if (track.isBone()) {
            return null;
        }
        if (mouseX < timelineGraphX - KEYFRAME_HIT_PX || mouseX >= timelineGraphX + timelineGraphWidth + KEYFRAME_HIT_PX) {
            return null;
        }
        var laneY = timelineContentY + RULER_HEIGHT + index * TRACK_HEIGHT + TRACK_HEIGHT / 2;
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

    private @Nullable TimelineRow rowAt(double mouseY) {
        var index = rowIndexAt(mouseY);
        return index >= 0 && index < rows.size() ? rows.get(index) : null;
    }

    private @Nullable TimelineRow channelRowAt(double mouseY) {
        var row = rowAt(mouseY);
        return row == null || row.isBone() ? null : row;
    }

    private int rowIndexAt(double mouseY) {
        var localY = mouseY - timelineContentY - RULER_HEIGHT;
        if (localY < 0.0) {
            return -1;
        }
        return (int) (localY / TRACK_HEIGHT);
    }

    private boolean caretHit(TimelineRow row, double mouseX) {
        var caretX = timelineViewportX + 4 + row.depth() * INDENT_PX;
        return mouseX >= caretX && mouseX < caretX + CARET_WIDTH;
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
        return state.hasDraft() && state.hasPlayableSelection();
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
        return "(no keyframes for this bone/group subtree)";
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

    private static void drawCaret(GuiGraphics graphics, int x, int y, boolean collapsed, int color) {
        if (collapsed) {
            for (var row = 0; row < 7; row++) {
                var width = row <= 3 ? row + 1 : 7 - row;
                graphics.fill(x, y + row, x + width, y + row + 1, color);
            }
            return;
        }
        for (var row = 0; row < 4; row++) {
            graphics.fill(x + row, y + row + 1, x + 7 - row, y + row + 2, color);
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

    private static void drawButtonBorder(GuiGraphics graphics, int x, int y, int width, int height) {
        graphics.fill(x, y, x + width, y + 1, BUTTON_BORDER_COLOR);
        graphics.fill(x, y + height - 1, x + width, y + height, BUTTON_BORDER_COLOR);
        graphics.fill(x, y, x + 1, y + height, BUTTON_BORDER_COLOR);
        graphics.fill(x + width - 1, y, x + width, y + height, BUTTON_BORDER_COLOR);
    }

    private static boolean buttonHit(double mouseX, double mouseY, int x, int y, int width, int height) {
        return width > 0 && height > 0 && mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    private record TimelineRow(
        ModelerBone bone,
        int depth,
        @Nullable TransformChannel channel,
        List<KeyframeRef> frames
    ) {

        static TimelineRow bone(ModelerBone bone, int depth) {
            return new TimelineRow(bone, depth, null, List.of());
        }

        static TimelineRow channel(ModelerBone bone, int depth, TransformChannel channel, List<KeyframeRef> frames) {
            return new TimelineRow(bone, depth, channel, frames);
        }

        boolean isBone() {
            return channel == null;
        }
    }
}
