package com.blib.engine.ui.panel.animation;

import com.google.gson.JsonArray;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import com.blib.engine.modeler.animation.AnimationEditorState;
import com.blib.engine.modeler.animation.AnimationEditorState.TransformChannel;
import com.blib.engine.ui.EngineFont;
import com.blib.engine.ui.dock.Panel;
import com.blib.engine.ui.layout.UiText;
import com.blib.engine.ui.widget.SearchableSelect;
import com.blib.engine.ui.widget.TextInput;

@ApiStatus.Internal
public final class AnimationKeyframePanel implements Panel {

    private static final int BG_COLOR = 0xFF18181C;

    private static final int TEXT_COLOR = 0xFFD0D0D0;

    private static final int LABEL_COLOR = 0xFF808088;

    private static final int SECTION_HEADER_BG_COLOR = 0xFF26262C;

    private static final int SECTION_HEADER_TEXT_COLOR = 0xFFB8C0D0;

    private static final int DIRTY_COLOR = 0xFFE6C26B;

    private static final int PADDING = 6;

    private static final int ROW_GAP = 4;

    private static final int INPUT_GAP = 3;

    private static final int SECTION_HEADER_HEIGHT = 11;

    private final TextInput timestampInput = new TextInput("Seconds", this::commitTimestamp);

    private final TextInput boneInput = new TextInput("Bone/group", this::commitBone);

    private final TextInput xInput = new TextInput("X", value -> commitVectorAxis(0, value));

    private final TextInput yInput = new TextInput("Y", value -> commitVectorAxis(1, value));

    private final TextInput zInput = new TextInput("Z", value -> commitVectorAxis(2, value));

    private final TextInput easingArgsInput = new TextInput("Args", this::commitEasingArgs);

    private final SearchableSelect<TransformChannel> channelSelect = new SearchableSelect<>(
        AnimationKeyframePanel::channelItems,
        TransformChannel::jsonName,
        TransformChannel.ROTATION,
        this::commitChannel
    );

    private final SearchableSelect<String> easingSelect = new SearchableSelect<>(
        AnimationKeyframePanel::easingItems,
        AnimationKeyframePanel::easingLabel,
        null,
        AnimationKeyframePanel::parseEasingText,
        "",
        this::commitEasing
    );

    private int panelX, panelY, panelWidth, panelHeight;

    @Override
    public String title() {
        return "Keyframe";
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
        var frame = state.selectedKeyframe();

        var font = EngineFont.get();
        if (frame == null) {
            var message = state.hasDraft() ? "Select a keyframe to edit it." : "Open or create an animation JSON first.";
            UiText.drawClipped(graphics, font, message, x + PADDING, y + PADDING, Math.max(0, width - 2 * PADDING), LABEL_COLOR);
            return;
        }

        syncInputs(state, frame);

        var header = frame.boneName() + " / " + frame.channel().jsonName();
        UiText.drawClipped(graphics, font, header, x + PADDING, y + PADDING, Math.max(0, width - 2 * PADDING), TEXT_COLOR);

        var rowY = y + PADDING + font.lineHeight + 5;
        rowY = renderScalarSection(graphics, font, x, rowY, width, "Timestamp", timestampInput, mouseX, mouseY);
        rowY = renderScalarSection(graphics, font, x, rowY, width, "Bone / Group", boneInput, mouseX, mouseY);
        rowY = renderChannelSection(graphics, font, x, rowY, width, mouseX, mouseY);
        rowY = renderVecSection(graphics, font, x, rowY, width, "Vector", mouseX, mouseY);
        rowY = renderEasingSection(graphics, font, x, rowY, width, mouseX, mouseY);
        renderScalarSection(graphics, font, x, rowY, width, "Easing Args", easingArgsInput, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return false;
        }
        if (mouseX < panelX || mouseX >= panelX + panelWidth || mouseY < panelY || mouseY >= panelY + panelHeight) {
            return false;
        }
        if (AnimationEditorState.get().selectedKeyframe() == null) {
            return true;
        }
        if (timestampInput.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (boneInput.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (channelSelect.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (xInput.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (yInput.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (zInput.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (easingSelect.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        return easingArgsInput.mouseClicked(mouseX, mouseY, button);
    }

    private void syncInputs(AnimationEditorState state, @Nullable AnimationEditorState.KeyframeRef frame) {
        syncInput(timestampInput, state.selectedTimestamp() == null ? "0" : AnimationEditorState.formatTimestamp(state.selectedTimestamp()));
        syncInput(boneInput, state.selectedBoneName() == null ? "" : state.selectedBoneName());
        channelSelect.setCurrentValue(state.selectedChannel());

        JsonArray vector = null;
        if (frame != null && frame.keyframe().has("vector") && frame.keyframe().get("vector").isJsonArray()) {
            vector = frame.keyframe().getAsJsonArray("vector");
        }
        syncInput(xInput, vector != null && vector.size() > 0 ? AnimationEditorState.elementToText(vector.get(0)) : "0");
        syncInput(yInput, vector != null && vector.size() > 1 ? AnimationEditorState.elementToText(vector.get(1)) : "0");
        syncInput(zInput, vector != null && vector.size() > 2 ? AnimationEditorState.elementToText(vector.get(2)) : "0");
        easingSelect.setCurrentValue(frame != null && frame.keyframe().has("easing") ? AnimationEditorState.elementToText(frame.keyframe().get("easing")) : "");
        syncInput(easingArgsInput, frame != null ? AnimationEditorState.easingArgsToText(frame.keyframe().get("easingArgs")) : "");
    }

    private static void syncInput(TextInput input, String value) {
        if (!input.isFocused() && !input.content().equals(value)) {
            input.setContent(value);
        }
    }

    private void commitTimestamp(String text) {
        var seconds = AnimationEditorState.parseSeconds(text);
        if (seconds == null) {
            return;
        }
        var state = AnimationEditorState.get();
        var bone = state.selectedBoneName() == null || state.selectedBoneName().isBlank() ? "bone" : state.selectedBoneName();
        if (state.selectedKeyframe() != null) {
            state.moveSelectedKeyframe(bone, state.selectedChannel(), seconds);
        } else if (state.selectedAnimationName() != null) {
            state.selectKeyframe(state.selectedAnimationName(), bone, state.selectedChannel(), seconds);
        }
    }

    private void commitBone(String text) {
        if (text == null || text.isBlank()) {
            return;
        }
        var state = AnimationEditorState.get();
        var timestamp = state.selectedTimestamp() == null ? 0.0 : state.selectedTimestamp();
        if (state.selectedKeyframe() != null) {
            state.moveSelectedKeyframe(text.trim(), state.selectedChannel(), timestamp);
        } else {
            state.selectBone(text.trim());
        }
    }

    private void commitChannel(TransformChannel channel) {
        var state = AnimationEditorState.get();
        var bone = state.selectedBoneName() == null || state.selectedBoneName().isBlank() ? "bone" : state.selectedBoneName();
        var timestamp = state.selectedTimestamp() == null ? 0.0 : state.selectedTimestamp();
        if (state.selectedKeyframe() != null) {
            state.moveSelectedKeyframe(bone, channel, timestamp);
        } else {
            state.selectChannel(channel);
        }
    }

    private void commitVectorAxis(int axis, String text) {
        AnimationEditorState.get().setSelectedVectorAxis(axis, AnimationEditorState.textToElement(text));
    }

    private void commitEasing(String text) {
        AnimationEditorState.get().setSelectedEasing(text);
    }

    private void commitEasingArgs(String text) {
        try {
            AnimationEditorState.get().setSelectedEasingArgs(AnimationEditorState.parseEasingArgs(text));
        } catch (NumberFormatException ignored) {
            // Invalid easing args are left alone; the next sync restores the last valid value.
        }
    }

    private int renderScalarSection(
        GuiGraphics graphics,
        Font font,
        int x,
        int y,
        int width,
        String label,
        TextInput input,
        int mouseX,
        int mouseY
    ) {
        var rowY = drawSectionHeader(graphics, font, x, y, width, label);
        rowY += PADDING / 2;
        input.render(graphics, x + PADDING, rowY, Math.max(0, width - 2 * PADDING), mouseX, mouseY);
        return rowY + TextInput.HEIGHT + ROW_GAP;
    }

    private int renderChannelSection(GuiGraphics graphics, Font font, int x, int y, int width, int mouseX, int mouseY) {
        var rowY = drawSectionHeader(graphics, font, x, y, width, "Transform Channel");
        rowY += PADDING / 2;
        channelSelect.render(graphics, x + PADDING, rowY, Math.max(0, width - 2 * PADDING), mouseX, mouseY);
        return rowY + SearchableSelect.HEIGHT + ROW_GAP;
    }

    private int renderEasingSection(GuiGraphics graphics, Font font, int x, int y, int width, int mouseX, int mouseY) {
        var rowY = drawSectionHeader(graphics, font, x, y, width, "Easing");
        rowY += PADDING / 2;
        easingSelect.render(graphics, x + PADDING, rowY, Math.max(0, width - 2 * PADDING), mouseX, mouseY);
        return rowY + SearchableSelect.HEIGHT + ROW_GAP;
    }

    private int renderVecSection(GuiGraphics graphics, Font font, int x, int y, int width, String label, int mouseX, int mouseY) {
        var rowY = drawSectionHeader(graphics, font, x, y, width, label);
        rowY += PADDING / 2;
        var innerX = x + PADDING;
        var innerW = Math.max(0, width - 2 * PADDING - 2 * INPUT_GAP);
        var per = innerW / 3;
        xInput.render(graphics, innerX, rowY, per, mouseX, mouseY);
        yInput.render(graphics, innerX + per + INPUT_GAP, rowY, per, mouseX, mouseY);
        zInput.render(graphics, innerX + 2 * (per + INPUT_GAP), rowY, per, mouseX, mouseY);
        return rowY + TextInput.HEIGHT + ROW_GAP;
    }

    private static int drawSectionHeader(GuiGraphics graphics, Font font, int x, int y, int width, String label) {
        graphics.fill(x, y, x + width, y + SECTION_HEADER_HEIGHT, SECTION_HEADER_BG_COLOR);
        UiText.drawClipped(
            graphics,
            font,
            label,
            x + PADDING,
            y + (SECTION_HEADER_HEIGHT - font.lineHeight + 2) / 2,
            Math.max(0, width - 2 * PADDING),
            SECTION_HEADER_TEXT_COLOR
        );
        return y + SECTION_HEADER_HEIGHT;
    }

    private static List<SearchableSelect.Item<TransformChannel>> channelItems() {
        return List.of(
            new SearchableSelect.Item<>(TransformChannel.POSITION, "position"),
            new SearchableSelect.Item<>(TransformChannel.ROTATION, "rotation"),
            new SearchableSelect.Item<>(TransformChannel.SCALE, "scale")
        );
    }

    private static List<SearchableSelect.Item<String>> easingItems() {
        var items = new ArrayList<SearchableSelect.Item<String>>();
        items.add(new SearchableSelect.Item<>("", "(unset)"));
        for (var name : AnimationEditorState.easingNames()) {
            items.add(new SearchableSelect.Item<>(name, name));
        }
        return items;
    }

    private static String easingLabel(@Nullable String easing) {
        return easing == null || easing.isBlank() ? "(unset)" : easing;
    }

    private static String parseEasingText(String text) {
        return AnimationEditorState.normalizeEasingName(text);
    }
}
