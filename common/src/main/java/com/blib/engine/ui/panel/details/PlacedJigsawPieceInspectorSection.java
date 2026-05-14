package com.blib.engine.ui.panel.details;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.domain.selection.picking.PlacedJigsawPieceSelectable;
import com.blib.engine.domain.selection.volume.BlockSelection;
import com.blib.engine.jigsaw.ClientPlacedPieceRegistry;
import com.blib.engine.ui.EngineFont;
import com.blib.engine.ui.panel.base.InspectorSection;
import com.blib.engine.ui.widget.SegmentedControl;
import com.blib.engine.ui.widget.TextInput;
import com.blib.mod.BLib;
import com.blib.mod.common.network.packet.C2SDeletePlacedPiecePayload;

/**
 * Inspector section for {@link PlacedJigsawPieceSelectable}. Reuses the volume section's tool toolbar + Position/Size
 * text inputs (since piece editing piggybacks on volume input plumbing — the volume section's commit handlers detect
 * piece selection and route through {@link com.blib.mod.common.network.packet.C2SMovePlacedPiecePayload} for moves).
 * Adds a Piece metadata read-out (template id / rotation / mirror / placed time) and an Actions row with Switch-To-
 * Volume-Edit + Delete buttons. Tool clicks on Translate/Scale auto-promote to a volume selection over the piece's
 * AABB; Move + Position inputs stay in piece mode so the user can identity-preservingly move the piece.
 */
@ApiStatus.Internal
public final class PlacedJigsawPieceInspectorSection implements InspectorSection<PlacedJigsawPieceSelectable> {

    private final DetailsPanel panel;

    private int pieceDeleteBtnX;

    private int pieceDeleteBtnY;

    private int pieceDeleteBtnW;

    private int pieceDeleteBtnH;

    private int pieceSwitchBtnX;

    private int pieceSwitchBtnY;

    private int pieceSwitchBtnW;

    private int pieceSwitchBtnH;

    public PlacedJigsawPieceInspectorSection(DetailsPanel panel) {
        this.panel = panel;
    }

    @Override
    public String id() {
        return "placed_jigsaw_piece";
    }

    @Override
    public Class<PlacedJigsawPieceSelectable> selectableType() {
        return PlacedJigsawPieceSelectable.class;
    }

    /** Called each frame from {@link DetailsPanel#render} so stale hit-rects don't fire after the selection swaps. */
    void resetHitRects() {
        pieceDeleteBtnW = 0;
        pieceSwitchBtnW = 0;
    }

    @Override
    public int render(GuiGraphics graphics, int x, int y, int width, PlacedJigsawPieceSelectable target, int mouseX, int mouseY) {
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
        PlacedJigsawPieceSelectable selectable
    ) {
        var piece = ClientPlacedPieceRegistry.get(selectable.id());
        if (piece == null) {
            return DetailsPanel.drawNote(graphics, font, x, y, "Piece is no longer present.");
        }

        var rowY = y;
        var volume = panel.volumeSection;

        // Tool — same segmented control as the volume view. In piece mode the selected index isn't tied to
        // BlockSelection.gizmoMode (the piece doesn't have a gizmo mode); default the visible state to Move so the user
        // can see at a glance which op is identity-preserving for a piece.
        rowY = DetailsPanel.drawSectionHeader(graphics, font, x, rowY, width, "Tool");
        rowY += InspectorStyle.CONTENT_PADDING / 2;
        var toolBarX = x + InspectorStyle.CONTENT_PADDING;
        var toolBarW = Math.max(SegmentedControl.HEIGHT * 3, width - 2 * InspectorStyle.CONTENT_PADDING);
        volume.volumeToolControl.setSelectedIndex(BlockSelection.GizmoMode.MOVE_BLOCKS.ordinal());
        volume.volumeToolControl.render(graphics, toolBarX, rowY, toolBarW, mouseX, mouseY);
        rowY += SegmentedControl.HEIGHT + InspectorStyle.CONTENT_PADDING;

        // Sync the input fields from the piece's AABB so external changes (move commit, etc.) reflect immediately.
        volume.syncVolumeInputsFromPiece(piece, false);

        rowY = DetailsPanel.drawSectionHeader(graphics, font, x, rowY, width, "Position");
        rowY += InspectorStyle.CONTENT_PADDING / 2;
        rowY = BlockVolumeInspectorSection.renderXyzRow(
            graphics,
            x,
            rowY,
            width,
            volume.volumePosX,
            volume.volumePosY,
            volume.volumePosZ,
            mouseX,
            mouseY
        );

        rowY = DetailsPanel.drawSectionHeader(graphics, font, x, rowY, width, "Size");
        rowY += InspectorStyle.CONTENT_PADDING / 2;
        rowY = BlockVolumeInspectorSection.renderXyzRow(
            graphics,
            x,
            rowY,
            width,
            volume.volumeSizeX,
            volume.volumeSizeY,
            volume.volumeSizeZ,
            mouseX,
            mouseY
        );

        var aabb = piece.aabb();
        var sx = aabb.maxX() - aabb.minX() + 1;
        var sy = aabb.maxY() - aabb.minY() + 1;
        var sz = aabb.maxZ() - aabb.minZ() + 1;
        rowY = DetailsPanel.drawNote(graphics, font, x, rowY, "= " + ((long) sx * sy * sz) + " blocks");
        rowY += InspectorStyle.CONTENT_PADDING / 2;

        rowY = DetailsPanel.drawSectionHeader(graphics, font, x, rowY, width, "Piece");
        rowY += InspectorStyle.CONTENT_PADDING / 2;
        rowY = DetailsPanel.drawRow(graphics, font, x, rowY, "Template", piece.templateId().toString());
        rowY = DetailsPanel.drawRow(graphics, font, x, rowY, "Rotation", piece.rotation().name());
        rowY = DetailsPanel.drawRow(graphics, font, x, rowY, "Mirror", piece.mirror().name());
        var mc = Minecraft.getInstance();
        var nowTick = mc.level == null ? piece.placedAtTick() : mc.level.getGameTime();
        var ticksAgo = Math.max(0, nowTick - piece.placedAtTick());
        rowY = DetailsPanel.drawRow(graphics, font, x, rowY, "Placed", (ticksAgo / 20) + "s ago");

        rowY += InspectorStyle.CONTENT_PADDING / 2;
        rowY = DetailsPanel.drawSectionHeader(graphics, font, x, rowY, width, "Actions");
        rowY += InspectorStyle.CONTENT_PADDING / 2;

        // Two-button row: Switch to Volume Edit + Delete. Switch is the neutral promote (no gizmo seeded); Delete keeps
        // the danger styling. Both rects are stored so mouseClicked can hit-test without recomputing layout.
        var switchLabel = "Switch to Volume Edit";
        var switchW = font.width(switchLabel) + 12;
        var btnH = TextInput.HEIGHT;
        var switchX = x + InspectorStyle.CONTENT_PADDING;
        var switchHovered = mouseX >= switchX && mouseX < switchX + switchW && mouseY >= rowY && mouseY < rowY + btnH;
        var switchBg = switchHovered ? 0xFF22222C : 0xFF14141A;
        graphics.fill(switchX, rowY, switchX + switchW, rowY + btnH, switchBg);
        graphics.fill(switchX, rowY, switchX + switchW, rowY + 1, 0xFF353540);
        graphics.fill(switchX, rowY + btnH - 1, switchX + switchW, rowY + btnH, 0xFF353540);
        graphics.fill(switchX, rowY, switchX + 1, rowY + btnH, 0xFF353540);
        graphics.fill(switchX + switchW - 1, rowY, switchX + switchW, rowY + btnH, 0xFF353540);
        graphics.drawString(
            font,
            Component.literal(switchLabel),
            switchX + (switchW - font.width(switchLabel)) / 2,
            rowY + (btnH - font.lineHeight + 2) / 2,
            switchHovered ? 0xFFFFFFFF : 0xFFD0D0D0,
            false
        );
        pieceSwitchBtnX = switchX;
        pieceSwitchBtnY = rowY;
        pieceSwitchBtnW = switchW;
        pieceSwitchBtnH = btnH;

        var deleteLabel = "Delete";
        var deleteW = font.width(deleteLabel) + 12;
        var deleteX = switchX + switchW + 6;
        var deleteHovered = mouseX >= deleteX && mouseX < deleteX + deleteW && mouseY >= rowY && mouseY < rowY + btnH;
        var deleteBg = deleteHovered ? 0xFF3A1F1F : 0xFF14141A;
        graphics.fill(deleteX, rowY, deleteX + deleteW, rowY + btnH, deleteBg);
        graphics.fill(deleteX, rowY, deleteX + deleteW, rowY + 1, 0xFF6E2A2A);
        graphics.fill(deleteX, rowY + btnH - 1, deleteX + deleteW, rowY + btnH, 0xFF6E2A2A);
        graphics.fill(deleteX, rowY, deleteX + 1, rowY + btnH, 0xFF6E2A2A);
        graphics.fill(deleteX + deleteW - 1, rowY, deleteX + deleteW, rowY + btnH, 0xFF6E2A2A);
        graphics.drawString(
            font,
            Component.literal(deleteLabel),
            deleteX + (deleteW - font.width(deleteLabel)) / 2,
            rowY + (btnH - font.lineHeight + 2) / 2,
            deleteHovered ? 0xFFFFB0B0 : 0xFFE6A0A0,
            false
        );
        pieceDeleteBtnX = deleteX;
        pieceDeleteBtnY = rowY;
        pieceDeleteBtnW = deleteW;
        pieceDeleteBtnH = btnH;
        return rowY + btnH;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button, PlacedJigsawPieceSelectable pjs) {
        var volume = panel.volumeSection;

        if (
            button == 0
                && pieceDeleteBtnW > 0
                && mouseX >= pieceDeleteBtnX
                && mouseX < pieceDeleteBtnX + pieceDeleteBtnW
                && mouseY >= pieceDeleteBtnY
                && mouseY < pieceDeleteBtnY + pieceDeleteBtnH
        ) {
            BLib.MOD.networking().sendToServer(new C2SDeletePlacedPiecePayload(pjs.id()));
            return true;
        }
        if (
            button == 0
                && pieceSwitchBtnW > 0
                && mouseX >= pieceSwitchBtnX
                && mouseX < pieceSwitchBtnX + pieceSwitchBtnW
                && mouseY >= pieceSwitchBtnY
                && mouseY < pieceSwitchBtnY + pieceSwitchBtnH
        ) {
            PlacedJigsawPieceSelectable.promoteToVolume(pjs.id(), null);
            return true;
        }
        if (volume.volumeToolControl.mouseClicked(mouseX, mouseY, button)) {
            var seed = BlockSelection.GizmoMode.values()[volume.volumeToolControl.selectedIndex()];
            PlacedJigsawPieceSelectable.promoteToVolume(pjs.id(), seed);
            return true;
        }
        // Size inputs auto-switch to volume mode. Forward the click after promoting so the input still takes focus
        // (the rect is identical between piece and volume views), letting the user type immediately.
        if (
            volume.volumeSizeX.mouseClicked(mouseX, mouseY, button)
                || volume.volumeSizeY.mouseClicked(mouseX, mouseY, button)
                || volume.volumeSizeZ.mouseClicked(mouseX, mouseY, button)
        ) {
            PlacedJigsawPieceSelectable.promoteToVolume(pjs.id(), BlockSelection.GizmoMode.SCALE_VOLUME);
            return true;
        }
        // Position inputs — stay in piece mode; commits go through the volume section's commitVolumePosition which
        // detects the piece selection and dispatches a C2SMovePlacedPiece instead of the volume's setBounds.
        volume.volumePosX.mouseClicked(mouseX, mouseY, button);
        volume.volumePosY.mouseClicked(mouseX, mouseY, button);
        volume.volumePosZ.mouseClicked(mouseX, mouseY, button);
        return false;
    }
}
