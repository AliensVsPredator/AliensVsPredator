package com.blib.engine.ui.panel.details;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import com.blib.engine.domain.selection.entity.EntityGizmoMode;
import com.blib.engine.domain.selection.picking.EntitySelectable;
import com.blib.engine.domain.selection.picking.SelectionManager;
import com.blib.engine.ui.EngineFont;
import com.blib.engine.ui.panel.base.InspectorSection;
import com.blib.engine.ui.widget.SegmentedControl;
import com.blib.engine.ui.widget.TextInput;
import com.blib.internal.client.faction.ClientEntityFactionsCache;
import com.blib.internal.client.faction.ClientFactionDirectoryCache;
import com.blib.mod.BLib;
import com.blib.mod.common.network.packet.C2SSetEntityScalePayload;
import com.blib.mod.common.network.packet.C2STranslateEntityPayload;

/**
 * Inspector section for {@link EntitySelectable}. Tool toolbar picks the gizmo mode, position XYZ inputs commit
 * teleport packets on Enter, Scale drives {@code Attributes.SCALE}. A read-only Info section follows with type / UUID /
 * health, then a Factions section listing every faction the entity belongs to (populated from
 * {@link ClientEntityFactionsCache}; mutations happen elsewhere in the right-click "Manage Factions" popup).
 */
@ApiStatus.Internal
public final class EntityInspectorSection implements InspectorSection<EntitySelectable> {

    private final DetailsPanel panel;

    private final SegmentedControl entityToolControl = new SegmentedControl(List.of("Translate", "Scale"), 0);

    private final TextInput entityPosX = new TextInput("X", v -> commitEntityPosition(0, v));

    private final TextInput entityPosY = new TextInput("Y", v -> commitEntityPosition(1, v));

    private final TextInput entityPosZ = new TextInput("Z", v -> commitEntityPosition(2, v));

    private final TextInput entityScale = new TextInput("Scale", v -> commitEntityScale(v));

    /** Entity-id seen last frame; used to force-resync inputs on selection swap. */
    private int lastInspectedEntityId = -1;

    public EntityInspectorSection(DetailsPanel panel) {
        this.panel = panel;
    }

    @Override
    public String id() {
        return "entity";
    }

    @Override
    public Class<EntitySelectable> selectableType() {
        return EntitySelectable.class;
    }

    @Override
    public int render(GuiGraphics graphics, int x, int y, int width, EntitySelectable target, int mouseX, int mouseY) {
        return renderBody(graphics, EngineFont.get(), x, y, width, mouseX, mouseY, target);
    }

    private int renderBody(GuiGraphics graphics, Font font, int x, int y, int width, int mouseX, int mouseY, EntitySelectable selectable) {
        var rowY = y;
        var entity = selectable.entity();
        if (entity == null) {
            rowY = DetailsPanel.drawSectionHeader(graphics, font, x, rowY, width, "Entity");
            rowY += InspectorStyle.CONTENT_PADDING / 2;
            return DetailsPanel.drawNote(graphics, font, x, rowY, "(unloaded)");
        }

        rowY = DetailsPanel.drawSectionHeader(graphics, font, x, rowY, width, "Tool");
        rowY += InspectorStyle.CONTENT_PADDING / 2;
        var toolBarX = x + InspectorStyle.CONTENT_PADDING;
        var toolBarW = Math.max(SegmentedControl.HEIGHT * 2, width - 2 * InspectorStyle.CONTENT_PADDING);
        entityToolControl.setSelectedIndex(EntityGizmoMode.get().ordinal());
        entityToolControl.render(graphics, toolBarX, rowY, toolBarW, mouseX, mouseY);
        rowY += SegmentedControl.HEIGHT + InspectorStyle.CONTENT_PADDING;

        var force = lastInspectedEntityId != entity.getId();
        syncEntityInputsFromEntity(entity, force);
        lastInspectedEntityId = entity.getId();

        rowY = DetailsPanel.drawSectionHeader(graphics, font, x, rowY, width, "Position");
        rowY += InspectorStyle.CONTENT_PADDING / 2;
        rowY = BlockVolumeInspectorSection.renderXyzRow(graphics, x, rowY, width, entityPosX, entityPosY, entityPosZ, mouseX, mouseY);

        rowY = DetailsPanel.drawSectionHeader(graphics, font, x, rowY, width, "Scale");
        rowY += InspectorStyle.CONTENT_PADDING / 2;
        rowY = panel.drawInputRow(graphics, font, x, rowY, width, "Scale", null, entityScale, mouseX, mouseY);

        rowY = DetailsPanel.drawSectionHeader(graphics, font, x, rowY, width, "Info");
        rowY += InspectorStyle.CONTENT_PADDING / 2;
        rowY = DetailsPanel.drawRow(graphics, font, x, rowY, "Type", entity.getType().getDescriptionId());
        rowY = DetailsPanel.drawRow(graphics, font, x, rowY, "UUID", entity.getStringUUID().substring(0, 8));
        rowY = DetailsPanel.drawRow(
            graphics,
            font,
            x,
            rowY,
            "Health",
            String.format("%.1f / %.1f", entity.getHealth(), entity.getMaxHealth())
        );

        return renderFactionsSection(graphics, font, x, rowY, width, entity.getUUID());
    }

    private int renderFactionsSection(GuiGraphics graphics, Font font, int x, int rowY, int width, UUID uuid) {
        ClientEntityFactionsCache.ensureRequested(uuid);
        rowY = DetailsPanel.drawSectionHeader(graphics, font, x, rowY, width, "Factions");
        rowY += InspectorStyle.CONTENT_PADDING / 2;

        var factionIds = ClientEntityFactionsCache.get(uuid);
        if (factionIds == null) {
            return DetailsPanel.drawRow(graphics, font, x, rowY, "", "(loading…)");
        }
        if (factionIds.isEmpty()) {
            return DetailsPanel.drawRow(graphics, font, x, rowY, "", "(no factions)");
        }

        var displayCap = 5;
        var shown = Math.min(displayCap, factionIds.size());
        for (var i = 0; i < shown; i++) {
            var factionId = factionIds.get(i);
            var entry = ClientFactionDirectoryCache.get(factionId);
            var label = entry != null ? entry.name() : factionId.toString();
            rowY = drawFactionRow(graphics, font, x, rowY, label, entry != null ? entry.color() : 0xFF808088);
        }
        if (factionIds.size() > displayCap) {
            rowY = DetailsPanel.drawRow(graphics, font, x, rowY, "", "… and " + (factionIds.size() - displayCap) + " more");
        }
        return rowY;
    }

    private static int drawFactionRow(GuiGraphics graphics, Font font, int x, int rowY, String name, int argb) {
        var swatchX = x + InspectorStyle.CONTENT_PADDING;
        var swatchSize = 6;
        var swatchY = rowY + (font.lineHeight - swatchSize) / 2 + 1;
        graphics.fill(swatchX, swatchY, swatchX + swatchSize, swatchY + swatchSize, argb | 0xFF000000);
        graphics.drawString(font, Component.literal(name), swatchX + swatchSize + 4, rowY + 1, 0xFFD0D0D0, false);
        return rowY + font.lineHeight + 1;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button, EntitySelectable target) {
        if (entityToolControl.mouseClicked(mouseX, mouseY, button)) {
            EntityGizmoMode.set(EntityGizmoMode.values()[entityToolControl.selectedIndex()]);
            return true;
        }
        entityPosX.mouseClicked(mouseX, mouseY, button);
        entityPosY.mouseClicked(mouseX, mouseY, button);
        entityPosZ.mouseClicked(mouseX, mouseY, button);
        entityScale.mouseClicked(mouseX, mouseY, button);
        return false;
    }

    private void syncEntityInputsFromEntity(LivingEntity entity, boolean force) {
        var pos = entity.position();
        BlockVolumeInspectorSection.syncInput(entityPosX, String.format(Locale.ROOT, "%.2f", pos.x), force);
        BlockVolumeInspectorSection.syncInput(entityPosY, String.format(Locale.ROOT, "%.2f", pos.y), force);
        BlockVolumeInspectorSection.syncInput(entityPosZ, String.format(Locale.ROOT, "%.2f", pos.z), force);

        var attr = entity.getAttribute(Attributes.SCALE);
        var scaleValue = attr == null ? 1.0 : attr.getValue();
        BlockVolumeInspectorSection.syncInput(entityScale, String.format(Locale.ROOT, "%.2f", scaleValue), force);
    }

    private void commitEntityPosition(int axis, String text) {
        var single = SelectionManager.current().single();
        if (!(single instanceof EntitySelectable es)) {
            return;
        }
        var entity = es.entity();
        if (entity == null) {
            return;
        }
        var parsed = parseDouble(text);
        if (parsed == null) {
            syncEntityInputsFromEntity(entity, true);
            return;
        }
        var pos = entity.position();
        var newX = pos.x;
        var newY = pos.y;
        var newZ = pos.z;
        switch (axis) {
            case 0 -> newX = parsed;
            case 1 -> newY = parsed;
            case 2 -> newZ = parsed;
            default -> {
                return;
            }
        }
        var mc = Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }
        var dim = mc.player.level().dimension().location();
        BLib.MOD.networking().sendToServer(new C2STranslateEntityPayload(entity.getId(), newX, newY, newZ, dim));
    }

    private void commitEntityScale(String text) {
        var single = SelectionManager.current().single();
        if (!(single instanceof EntitySelectable es)) {
            return;
        }
        var entity = es.entity();
        if (entity == null) {
            return;
        }
        var parsed = parseDouble(text);
        if (parsed == null) {
            syncEntityInputsFromEntity(entity, true);
            return;
        }
        var clamped = Math.max(0.1, Math.min(4.0, parsed));
        var mc = Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }
        var dim = mc.player.level().dimension().location();
        BLib.MOD.networking().sendToServer(new C2SSetEntityScalePayload(entity.getId(), clamped, dim));
    }

    static @Nullable Double parseDouble(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        try {
            return Double.parseDouble(text.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
