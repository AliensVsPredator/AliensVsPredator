package com.blib.engine.ui.panel.details;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

import com.blib.api.common.faction.v1.ClaimVisibility;
import com.blib.api.common.faction.v1.ProtectionMode;
import com.blib.engine.domain.selection.picking.FactionSelectable;
import com.blib.engine.domain.selection.picking.SelectionManager;
import com.blib.engine.territory.ClaimPaintTool;
import com.blib.engine.ui.EngineFont;
import com.blib.engine.ui.panel.base.InspectorSection;
import com.blib.engine.ui.popup.HslColorPickerPopup;
import com.blib.engine.ui.widget.SegmentedControl;
import com.blib.engine.ui.widget.TextInput;
import com.blib.internal.client.faction.ClientFactionInspectionCache;
import com.blib.internal.client.territory.ClientTerritoryCache;
import com.blib.mod.BLib;
import com.blib.mod.common.network.packet.C2SRequestFactionInspectionPayload;
import com.blib.mod.common.network.packet.C2SUpdateFactionFieldPayload;

/**
 * Editable inspector for {@link FactionSelectable}. Identity (name + color swatch + ID + type), Territory (chunk count
 * + Paint Claims toggle), Protection (5 segmented controls), Flags (PvP / Explosions / Mob Griefing). Reads from
 * {@link ClientFactionInspectionCache} (populated by the server's inspection push); each input commits via
 * {@link C2SUpdateFactionFieldPayload}.
 */
@ApiStatus.Internal
public final class FactionInspectorSection implements InspectorSection<FactionSelectable> {

    private static final Component HELP_FACTION_VISIBILITY = Component.literal(
        "Who can see this faction's claims on the map. Public: anyone. Allied: members and allied factions. Private: members only."
    );

    private static final Component HELP_FACTION_BLOCK_BREAK = Component.literal(
        "Who can break blocks inside this faction's claims. Public: anyone. Allied: members and allied factions. Private: members only."
    );

    private static final Component HELP_FACTION_BLOCK_INTERACT = Component.literal(
        "Who can right-click blocks (doors, chests, buttons, etc.) inside this faction's claims. Public: anyone. Allied: members and allied factions. Private: members only."
    );

    private static final Component HELP_FACTION_ENTITY_INTERACT = Component.literal(
        "Who can interact with this faction's entities — trading with villagers, mounting horses, etc. Public: anyone. Allied: members and allied factions. Private: members only."
    );

    private static final Component HELP_FACTION_NON_LIVING_ATTACK = Component.literal(
        "Who can attack non-living entities owned by this faction — item frames, armor stands, paintings, minecarts, and boats. Public: anyone. Allied: members and allied factions. Private: members only."
    );

    private static final Component HELP_FACTION_ALLOW_PVP = Component.literal(
        "When On, faction members can damage each other. When Off, friendly-fire is blocked between members."
    );

    private static final Component HELP_FACTION_ALLOW_EXPLOSIONS = Component.literal(
        "When On, explosions (TNT, creepers, etc.) can break blocks inside this faction's claims. When Off, claims are protected from blast damage."
    );

    private static final Component HELP_FACTION_ALLOW_MOB_GRIEFING = Component.literal(
        "When On, mobs (creepers, endermen, zombies breaking doors, etc.) can modify blocks inside this faction's claims. When Off, claims are protected from mob block changes."
    );

    private static final Component HELP_FACTION_TERRITORY = Component.literal(
        "Chunks this faction has claimed. Paint Claims enters a viewport mode where LMB-drag claims and RMB-drag unclaims chunks under the cursor. The Territory Map panel offers the same edits on a 2D grid."
    );

    private final DetailsPanel panel;

    private final TextInput factionName = new TextInput("Name", v -> commitFactionField(C2SUpdateFactionFieldPayload.Field.NAME, v));

    private final TextInput factionColor = new TextInput("#RRGGBB", v -> commitFactionField(C2SUpdateFactionFieldPayload.Field.COLOR, v));

    private final SegmentedControl factionVisibility = new SegmentedControl(List.of("Public", "Allied", "Private"), 0);

    private final SegmentedControl factionBlockBreak = new SegmentedControl(List.of("Public", "Allied", "Private"), 0);

    private final SegmentedControl factionBlockInteract = new SegmentedControl(List.of("Public", "Allied", "Private"), 0);

    private final SegmentedControl factionEntityInteract = new SegmentedControl(List.of("Public", "Allied", "Private"), 0);

    private final SegmentedControl factionNonLivingAttack = new SegmentedControl(List.of("Public", "Allied", "Private"), 0);

    private final SegmentedControl factionAllowPvp = new SegmentedControl(List.of("Off", "On"), 0);

    private final SegmentedControl factionAllowExplosions = new SegmentedControl(List.of("Off", "On"), 0);

    private final SegmentedControl factionAllowMobGriefing = new SegmentedControl(List.of("Off", "On"), 0);

    private @Nullable ResourceLocation lastInspectedFactionId;

    private int factionSwatchX;

    private int factionSwatchY;

    private int factionSwatchSize;

    private int factionSwatchArgb;

    private int factionPaintToggleX;

    private int factionPaintToggleY;

    private int factionPaintToggleW;

    private int factionPaintToggleH;

    public FactionInspectorSection(DetailsPanel panel) {
        this.panel = panel;
    }

    @Override
    public String id() {
        return "faction";
    }

    @Override
    public Class<FactionSelectable> selectableType() {
        return FactionSelectable.class;
    }

    /** Called each frame from {@link DetailsPanel#render} so stale hit-rects don't fire after the selection swaps. */
    void resetHitRects() {
        factionSwatchSize = 0;
        factionPaintToggleW = 0;
    }

    @Override
    public int render(GuiGraphics graphics, int x, int y, int width, FactionSelectable target, int mouseX, int mouseY) {
        return renderBody(graphics, EngineFont.get(), x, y, width, mouseX, mouseY, target);
    }

    private int renderBody(
        GuiGraphics graphics,
        Font font,
        int x,
        int y,
        int width,
        int mouseX,
        int mouseY,
        FactionSelectable selectable
    ) {
        var rowY = y;
        var factionId = selectable.factionId();

        var force = lastInspectedFactionId == null || !lastInspectedFactionId.equals(factionId);
        if (force) {
            BLib.MOD.networking().sendToServer(new C2SRequestFactionInspectionPayload(factionId));
            lastInspectedFactionId = factionId;
        }

        var inspection = ClientFactionInspectionCache.current();
        if (inspection == null || !inspection.id().equals(factionId)) {
            rowY = DetailsPanel.drawSectionHeader(graphics, font, x, rowY, width, "Faction");
            rowY += InspectorStyle.CONTENT_PADDING / 2;
            return DetailsPanel.drawNote(graphics, font, x, rowY, "(loading…)");
        }

        syncFactionInputsFromInspection(inspection, force);

        rowY = DetailsPanel.drawSectionHeader(graphics, font, x, rowY, width, "Identity");
        rowY += InspectorStyle.CONTENT_PADDING / 2;
        rowY = panel.drawInputRow(graphics, font, x, rowY, width, "Name", null, factionName, mouseX, mouseY);
        rowY = drawColorRow(graphics, font, x, rowY, width, inspection.color() | 0xFF000000, mouseX, mouseY);
        rowY = DetailsPanel.drawClippedRow(graphics, font, x, rowY, width, "ID", factionId.toString());
        rowY = DetailsPanel.drawRow(graphics, font, x, rowY, "Type", inspection.typeId().toString());

        rowY = panel.drawSectionHeaderWithHelp(graphics, font, x, rowY, width, "Territory", HELP_FACTION_TERRITORY, mouseX, mouseY);
        rowY += InspectorStyle.CONTENT_PADDING / 2;
        var level = Minecraft.getInstance().level;
        var chunkCount = level == null ? 0 : ClientTerritoryCache.INSTANCE.chunkCountForFaction(level.dimension().location(), factionId);
        rowY = DetailsPanel.drawRow(graphics, font, x, rowY, "Chunks", Integer.toString(chunkCount));
        rowY = drawPaintToggleRow(graphics, font, x, rowY, width, factionId, mouseX, mouseY);

        rowY = DetailsPanel.drawSectionHeader(graphics, font, x, rowY, width, "Protection");
        rowY += InspectorStyle.CONTENT_PADDING / 2;
        var factionLabelW = 100;
        rowY = panel.drawLabeledSegmentedRow(
            graphics,
            font,
            x,
            rowY,
            width,
            factionLabelW,
            "Visibility",
            HELP_FACTION_VISIBILITY,
            factionVisibility,
            mouseX,
            mouseY
        );
        rowY = panel.drawLabeledSegmentedRow(
            graphics,
            font,
            x,
            rowY,
            width,
            factionLabelW,
            "Block Break",
            HELP_FACTION_BLOCK_BREAK,
            factionBlockBreak,
            mouseX,
            mouseY
        );
        rowY = panel.drawLabeledSegmentedRow(
            graphics,
            font,
            x,
            rowY,
            width,
            factionLabelW,
            "Block Interact",
            HELP_FACTION_BLOCK_INTERACT,
            factionBlockInteract,
            mouseX,
            mouseY
        );
        rowY = panel.drawLabeledSegmentedRow(
            graphics,
            font,
            x,
            rowY,
            width,
            factionLabelW,
            "Entity Interact",
            HELP_FACTION_ENTITY_INTERACT,
            factionEntityInteract,
            mouseX,
            mouseY
        );
        rowY = panel.drawLabeledSegmentedRow(
            graphics,
            font,
            x,
            rowY,
            width,
            factionLabelW,
            "Non-Living Attack",
            HELP_FACTION_NON_LIVING_ATTACK,
            factionNonLivingAttack,
            mouseX,
            mouseY
        );

        rowY = DetailsPanel.drawSectionHeader(graphics, font, x, rowY, width, "Flags");
        rowY += InspectorStyle.CONTENT_PADDING / 2;
        rowY = panel.drawLabeledSegmentedRow(
            graphics,
            font,
            x,
            rowY,
            width,
            factionLabelW,
            "PvP",
            HELP_FACTION_ALLOW_PVP,
            factionAllowPvp,
            mouseX,
            mouseY
        );
        rowY = panel.drawLabeledSegmentedRow(
            graphics,
            font,
            x,
            rowY,
            width,
            factionLabelW,
            "Explosions",
            HELP_FACTION_ALLOW_EXPLOSIONS,
            factionAllowExplosions,
            mouseX,
            mouseY
        );
        rowY = panel.drawLabeledSegmentedRow(
            graphics,
            font,
            x,
            rowY,
            width,
            factionLabelW,
            "Mob Griefing",
            HELP_FACTION_ALLOW_MOB_GRIEFING,
            factionAllowMobGriefing,
            mouseX,
            mouseY
        );
        return rowY;
    }

    private int drawColorRow(GuiGraphics graphics, Font font, int x, int y, int width, int currentArgb, int mouseX, int mouseY) {
        var label = "Color";
        var labelY = y + (TextInput.HEIGHT - font.lineHeight + 2) / 2;
        graphics.drawString(font, Component.literal(label), x + InspectorStyle.CONTENT_PADDING, labelY, InspectorStyle.LABEL_COLOR, false);
        DetailsPanel.trackContentRight(x + InspectorStyle.CONTENT_PADDING + font.width(label) + InspectorStyle.CONTENT_PADDING);

        var swatchSize = TextInput.HEIGHT;
        var swatchGap = 4;
        var swatchX = x + width - InspectorStyle.CONTENT_PADDING - swatchSize;
        var inputX = x + InspectorStyle.CONTENT_PADDING + InspectorStyle.LABEL_COLUMN_WIDTH;
        var inputW = Math.max(0, swatchX - inputX - swatchGap);
        factionColor.render(graphics, inputX, y, inputW, mouseX, mouseY);

        var swatchHovered = mouseX >= swatchX && mouseX < swatchX + swatchSize && mouseY >= y && mouseY < y + swatchSize;
        graphics.fill(swatchX, y, swatchX + swatchSize, y + swatchSize, currentArgb);
        var borderColor = swatchHovered ? InspectorStyle.HELP_ICON_HOVER_COLOR : InspectorStyle.LABEL_COLOR;
        graphics.fill(swatchX, y, swatchX + swatchSize, y + 1, borderColor);
        graphics.fill(swatchX, y + swatchSize - 1, swatchX + swatchSize, y + swatchSize, borderColor);
        graphics.fill(swatchX, y, swatchX + 1, y + swatchSize, borderColor);
        graphics.fill(swatchX + swatchSize - 1, y, swatchX + swatchSize, y + swatchSize, borderColor);

        this.factionSwatchX = swatchX;
        this.factionSwatchY = y;
        this.factionSwatchSize = swatchSize;
        this.factionSwatchArgb = currentArgb;
        DetailsPanel.trackControlContentRight(inputX, swatchGap + swatchSize);
        return y + TextInput.HEIGHT + InspectorStyle.ROW_GAP;
    }

    private int drawPaintToggleRow(
        GuiGraphics graphics,
        Font font,
        int x,
        int y,
        int width,
        ResourceLocation factionId,
        int mouseX,
        int mouseY
    ) {
        var paintingThis = ClaimPaintTool.isActive() && factionId.equals(ClaimPaintTool.paintTarget());
        var label = paintingThis ? "Stop Painting" : "Paint Claims";
        var btnW = font.width(label) + 12;
        var btnH = TextInput.HEIGHT;
        var btnX = x + InspectorStyle.CONTENT_PADDING;
        var btnY = y;

        var hovered = mouseX >= btnX && mouseX < btnX + btnW && mouseY >= btnY && mouseY < btnY + btnH;
        var bg = paintingThis ? 0xFF3C3C46 : (hovered ? 0xFF22222C : 0xFF14141A);
        var borderColor = paintingThis ? 0xFFE6C26B : 0xFF353540;
        graphics.fill(btnX, btnY, btnX + btnW, btnY + btnH, bg);
        graphics.fill(btnX, btnY, btnX + btnW, btnY + 1, borderColor);
        graphics.fill(btnX, btnY + btnH - 1, btnX + btnW, btnY + btnH, borderColor);
        graphics.fill(btnX, btnY, btnX + 1, btnY + btnH, borderColor);
        graphics.fill(btnX + btnW - 1, btnY, btnX + btnW, btnY + btnH, borderColor);

        var textColor = paintingThis ? 0xFFE6C26B : (hovered ? 0xFFFFFFFF : 0xFFD0D0D0);
        var textX = btnX + (btnW - font.width(label)) / 2;
        var textY = btnY + (btnH - font.lineHeight + 2) / 2;
        graphics.drawString(font, Component.literal(label), textX, textY, textColor, false);
        DetailsPanel.trackContentRight(btnX + btnW + InspectorStyle.CONTENT_PADDING);

        factionPaintToggleX = btnX;
        factionPaintToggleY = btnY;
        factionPaintToggleW = btnW;
        factionPaintToggleH = btnH;
        return y + btnH + InspectorStyle.ROW_GAP;
    }

    private void syncFactionInputsFromInspection(ClientFactionInspectionCache.ClientFactionInspection inspection, boolean force) {
        if (force || !factionName.isFocused()) {
            factionName.setContent(inspection.name());
        }
        if (force || !factionColor.isFocused()) {
            factionColor.setContent(String.format(Locale.ROOT, "#%06X", inspection.color() & 0xFFFFFF));
        }
        factionVisibility.setSelectedIndex(inspection.claimVisibility().ordinal());
        factionBlockBreak.setSelectedIndex(inspection.blockBreakProtection().ordinal());
        factionBlockInteract.setSelectedIndex(inspection.blockInteractProtection().ordinal());
        factionEntityInteract.setSelectedIndex(inspection.entityInteractProtection().ordinal());
        factionNonLivingAttack.setSelectedIndex(inspection.nonLivingEntityAttackProtection().ordinal());
        factionAllowPvp.setSelectedIndex(inspection.allowPvp() ? 1 : 0);
        factionAllowExplosions.setSelectedIndex(inspection.allowExplosions() ? 1 : 0);
        factionAllowMobGriefing.setSelectedIndex(inspection.allowMobGriefing() ? 1 : 0);
    }

    private void commitFactionField(C2SUpdateFactionFieldPayload.Field field, String value) {
        var single = SelectionManager.current().single();
        if (!(single instanceof FactionSelectable selectable)) {
            return;
        }
        BLib.MOD.networking().sendToServer(C2SUpdateFactionFieldPayload.of(selectable.factionId(), field, value));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button, FactionSelectable factionSel) {
        factionName.mouseClicked(mouseX, mouseY, button);
        factionColor.mouseClicked(mouseX, mouseY, button);

        if (
            button == 0
                && factionPaintToggleW > 0
                && mouseX >= factionPaintToggleX
                && mouseX < factionPaintToggleX + factionPaintToggleW
                && mouseY >= factionPaintToggleY
                && mouseY < factionPaintToggleY + factionPaintToggleH
        ) {
            if (ClaimPaintTool.isActive() && factionSel.factionId().equals(ClaimPaintTool.paintTarget())) {
                ClaimPaintTool.deactivate();
            } else {
                ClaimPaintTool.setPaintTarget(factionSel.factionId());
                ClaimPaintTool.activate();
            }
            return true;
        }

        if (
            button == 0
                && factionSwatchSize > 0
                && mouseX >= factionSwatchX
                && mouseX < factionSwatchX + factionSwatchSize
                && mouseY >= factionSwatchY
                && mouseY < factionSwatchY + factionSwatchSize
        ) {
            var anchorCenterX = factionSwatchX + factionSwatchSize / 2;
            var anchorBottomY = factionSwatchY + factionSwatchSize;
            HslColorPickerPopup.openAt(
                anchorCenterX,
                anchorBottomY,
                factionSwatchArgb,
                argb -> commitFactionField(C2SUpdateFactionFieldPayload.Field.COLOR, String.format(Locale.ROOT, "#%06X", argb & 0xFFFFFF))
            );
            return true;
        }

        if (factionVisibility.mouseClicked(mouseX, mouseY, button)) {
            commitFactionField(
                C2SUpdateFactionFieldPayload.Field.CLAIM_VISIBILITY,
                ClaimVisibility.values()[factionVisibility.selectedIndex()].name()
            );
            return true;
        }
        if (factionBlockBreak.mouseClicked(mouseX, mouseY, button)) {
            commitFactionField(
                C2SUpdateFactionFieldPayload.Field.BLOCK_BREAK_PROTECTION,
                ProtectionMode.values()[factionBlockBreak.selectedIndex()].name()
            );
            return true;
        }
        if (factionBlockInteract.mouseClicked(mouseX, mouseY, button)) {
            commitFactionField(
                C2SUpdateFactionFieldPayload.Field.BLOCK_INTERACT_PROTECTION,
                ProtectionMode.values()[factionBlockInteract.selectedIndex()].name()
            );
            return true;
        }
        if (factionEntityInteract.mouseClicked(mouseX, mouseY, button)) {
            commitFactionField(
                C2SUpdateFactionFieldPayload.Field.ENTITY_INTERACT_PROTECTION,
                ProtectionMode.values()[factionEntityInteract.selectedIndex()].name()
            );
            return true;
        }
        if (factionNonLivingAttack.mouseClicked(mouseX, mouseY, button)) {
            commitFactionField(
                C2SUpdateFactionFieldPayload.Field.NONLIVING_ENTITY_ATTACK_PROTECTION,
                ProtectionMode.values()[factionNonLivingAttack.selectedIndex()].name()
            );
            return true;
        }
        if (factionAllowPvp.mouseClicked(mouseX, mouseY, button)) {
            commitFactionField(
                C2SUpdateFactionFieldPayload.Field.ALLOW_PVP,
                Boolean.toString(factionAllowPvp.selectedIndex() == 1)
            );
            return true;
        }
        if (factionAllowExplosions.mouseClicked(mouseX, mouseY, button)) {
            commitFactionField(
                C2SUpdateFactionFieldPayload.Field.ALLOW_EXPLOSIONS,
                Boolean.toString(factionAllowExplosions.selectedIndex() == 1)
            );
            return true;
        }
        if (factionAllowMobGriefing.mouseClicked(mouseX, mouseY, button)) {
            commitFactionField(
                C2SUpdateFactionFieldPayload.Field.ALLOW_MOB_GRIEFING,
                Boolean.toString(factionAllowMobGriefing.selectedIndex() == 1)
            );
            return true;
        }
        return false;
    }
}
