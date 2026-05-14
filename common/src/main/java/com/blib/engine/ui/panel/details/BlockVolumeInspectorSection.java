package com.blib.engine.ui.panel.details;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

import com.blib.engine.domain.selection.picking.BlockVolumeSelectable;
import com.blib.engine.domain.selection.picking.PlacedJigsawPieceSelectable;
import com.blib.engine.domain.selection.picking.SelectionManager;
import com.blib.engine.domain.selection.volume.BlockSelection;
import com.blib.engine.jigsaw.ClientPlacedPieceRegistry;
import com.blib.engine.ui.EngineFont;
import com.blib.engine.ui.panel.base.InspectorSection;
import com.blib.engine.ui.widget.SegmentedControl;
import com.blib.engine.ui.widget.TextInput;
import com.blib.mod.BLib;
import com.blib.mod.common.gameplay.jigsaw.PlacedPiece;
import com.blib.mod.common.network.packet.C2SMovePlacedPiecePayload;

/**
 * Inspector section for {@link BlockVolumeSelectable}: tool toolbar (Translate / Scale / Move-Blocks) + Position
 * (min-corner X/Y/Z) + Size (X/Y/Z) text inputs and a block-count readout. The same widgets are shared with the
 * {@link PlacedJigsawPieceInspectorSection} since piece editing piggybacks on the volume editor's plumbing — the piece
 * section reads the widget references from this section so position changes auto-route to either
 * {@link BlockSelection#setBounds} or {@link C2SMovePlacedPiecePayload} depending on which selection is currently
 * active.
 */
@ApiStatus.Internal
public final class BlockVolumeInspectorSection implements InspectorSection<BlockVolumeSelectable> {

    final SegmentedControl volumeToolControl = new SegmentedControl(
        List.of("Translate", "Scale", "Move"),
        BlockSelection.GizmoMode.SCALE_VOLUME.ordinal()
    );

    final TextInput volumePosX = new TextInput("X", v -> commitVolumePosition(0, v));

    final TextInput volumePosY = new TextInput("Y", v -> commitVolumePosition(1, v));

    final TextInput volumePosZ = new TextInput("Z", v -> commitVolumePosition(2, v));

    final TextInput volumeSizeX = new TextInput("X", v -> commitVolumeSize(0, v));

    final TextInput volumeSizeY = new TextInput("Y", v -> commitVolumeSize(1, v));

    final TextInput volumeSizeZ = new TextInput("Z", v -> commitVolumeSize(2, v));

    @SuppressWarnings("unused")
    public BlockVolumeInspectorSection(DetailsPanel panel) {
        // Panel reference no longer needed — the section is fully self-contained — but the constructor signature
        // matches sibling sections for uniform construction in DetailsPanel's section list.
    }

    @Override
    public String id() {
        return "block_volume";
    }

    @Override
    public Class<BlockVolumeSelectable> selectableType() {
        return BlockVolumeSelectable.class;
    }

    @Override
    public int render(GuiGraphics graphics, int x, int y, int width, BlockVolumeSelectable target, int mouseX, int mouseY) {
        return renderVolumeBody(graphics, EngineFont.get(), x, y, width, mouseX, mouseY);
    }

    /**
     * Body of the volume inspector. Lifted from {@code DetailsPanel.internalRenderBlockVolumeView}.
     * {@link PlacedJigsawPieceInspectorSection} also calls this when rendering its embedded volume widgets.
     */
    int renderVolumeBody(GuiGraphics graphics, Font font, int x, int y, int width, int mouseX, int mouseY) {
        var rowY = y;

        rowY = DetailsPanel.drawSectionHeader(graphics, font, x, rowY, width, "Tool");
        rowY += InspectorStyle.CONTENT_PADDING / 2;
        var toolBarX = x + InspectorStyle.CONTENT_PADDING;
        var toolBarW = Math.max(SegmentedControl.HEIGHT * 3, width - 2 * InspectorStyle.CONTENT_PADDING);
        volumeToolControl.setSelectedIndex(BlockSelection.gizmoMode().ordinal());
        volumeToolControl.render(graphics, toolBarX, rowY, toolBarW, mouseX, mouseY);
        rowY += SegmentedControl.HEIGHT + InspectorStyle.CONTENT_PADDING;

        // Mirror the AABB into the inputs every frame so external changes (gizmo drag, drag-to-pick re-pick) flow
        // through to the displayed values without clobbering whatever the user might be mid-typing.
        syncVolumeInputsFromAabb(false);

        rowY = DetailsPanel.drawSectionHeader(graphics, font, x, rowY, width, "Position");
        rowY += InspectorStyle.CONTENT_PADDING / 2;
        rowY = renderXyzRow(graphics, x, rowY, width, volumePosX, volumePosY, volumePosZ, mouseX, mouseY);

        rowY = DetailsPanel.drawSectionHeader(graphics, font, x, rowY, width, "Size");
        rowY += InspectorStyle.CONTENT_PADDING / 2;
        rowY = renderXyzRow(graphics, x, rowY, width, volumeSizeX, volumeSizeY, volumeSizeZ, mouseX, mouseY);

        // Volume readout — small note under Size so the user can see the block count without having to multiply.
        var aabb = BlockSelection.aabb();
        if (aabb.isPresent()) {
            var box = aabb.get();
            var sx = (long) (box.maxX - box.minX);
            var sy = (long) (box.maxY - box.minY);
            var sz = (long) (box.maxZ - box.minZ);
            rowY = DetailsPanel.drawNote(graphics, font, x, rowY, "= " + (sx * sy * sz) + " blocks");
        }
        return rowY;
    }

    /**
     * Three labeled int inputs (X / Y / Z). Returns the next-row y. Package-default so sibling sections (entity,
     * placed-piece) can reuse the same XYZ layout.
     */
    static int renderXyzRow(
        GuiGraphics graphics,
        int x,
        int y,
        int width,
        TextInput xIn,
        TextInput yIn,
        TextInput zIn,
        int mouseX,
        int mouseY
    ) {
        var inputsStart = x + InspectorStyle.CONTENT_PADDING;
        var available = Math.max(0, width - 2 * InspectorStyle.CONTENT_PADDING - 2 * 3);
        var perInput = Math.max(24, available / 3);
        xIn.render(graphics, inputsStart, y, perInput, mouseX, mouseY);
        yIn.render(graphics, inputsStart + perInput + 3, y, perInput, mouseX, mouseY);
        zIn.render(graphics, inputsStart + 2 * (perInput + 3), y, perInput, mouseX, mouseY);
        return y + TextInput.HEIGHT + InspectorStyle.CONTENT_PADDING;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button, BlockVolumeSelectable target) {
        if (volumeToolControl.mouseClicked(mouseX, mouseY, button)) {
            BlockSelection.setGizmoMode(BlockSelection.GizmoMode.values()[volumeToolControl.selectedIndex()]);
            return true;
        }
        // TextInputs handle their own focus on click; calling them all is fine since each rejects clicks outside its
        // own rect.
        volumePosX.mouseClicked(mouseX, mouseY, button);
        volumePosY.mouseClicked(mouseX, mouseY, button);
        volumePosZ.mouseClicked(mouseX, mouseY, button);
        volumeSizeX.mouseClicked(mouseX, mouseY, button);
        volumeSizeY.mouseClicked(mouseX, mouseY, button);
        volumeSizeZ.mouseClicked(mouseX, mouseY, button);
        return false;
    }

    private void commitVolumePosition(int axis, String text) {
        // Dispatch by selection type: piece commits go through the identity-preserving move handler; volume commits go
        // through the original setBounds path. The widgets are shared between views (the piece section reads our
        // refs), so the commit callback has to resolve which mode is active right now.
        var single = SelectionManager.current().single();
        var parsed = DetailsPanel.parseInt(text);
        if (parsed == null) {
            if (single instanceof PlacedJigsawPieceSelectable pjs) {
                var piece = ClientPlacedPieceRegistry.get(pjs.id());
                if (piece != null) {
                    syncVolumeInputsFromPiece(piece, true);
                }
            } else {
                syncVolumeInputsFromAabb(true);
            }
            return;
        }
        if (single instanceof PlacedJigsawPieceSelectable pjs) {
            var piece = ClientPlacedPieceRegistry.get(pjs.id());
            if (piece == null) {
                return;
            }
            var aabb = piece.aabb();
            var newMinX = axis == 0 ? parsed : aabb.minX();
            var newMinY = axis == 1 ? parsed : aabb.minY();
            var newMinZ = axis == 2 ? parsed : aabb.minZ();
            BLib.MOD.networking().sendToServer(new C2SMovePlacedPiecePayload(pjs.id(), new BlockPos(newMinX, newMinY, newMinZ)));
            return;
        }
        var aabb = BlockSelection.aabb();
        if (aabb.isEmpty()) {
            return;
        }
        var box = aabb.get();
        var minX = (int) Math.floor(box.minX);
        var minY = (int) Math.floor(box.minY);
        var minZ = (int) Math.floor(box.minZ);
        var maxX = (int) Math.floor(box.maxX) - 1;
        var maxY = (int) Math.floor(box.maxY) - 1;
        var maxZ = (int) Math.floor(box.maxZ) - 1;
        var sizeX = maxX - minX + 1;
        var sizeY = maxY - minY + 1;
        var sizeZ = maxZ - minZ + 1;
        switch (axis) {
            case 0 -> {
                minX = parsed;
                maxX = parsed + sizeX - 1;
            }
            case 1 -> {
                minY = parsed;
                maxY = parsed + sizeY - 1;
            }
            case 2 -> {
                minZ = parsed;
                maxZ = parsed + sizeZ - 1;
            }
            default -> {
                return;
            }
        }
        BlockSelection.setBounds(new BlockPos(minX, minY, minZ), new BlockPos(maxX, maxY, maxZ));
    }

    private void commitVolumeSize(int axis, String text) {
        var parsed = DetailsPanel.parseInt(text);
        if (parsed == null || parsed < 1) {
            syncVolumeInputsFromAabb(true);
            return;
        }
        var aabb = BlockSelection.aabb();
        if (aabb.isEmpty()) {
            return;
        }
        var box = aabb.get();
        var minX = (int) Math.floor(box.minX);
        var minY = (int) Math.floor(box.minY);
        var minZ = (int) Math.floor(box.minZ);
        var maxX = (int) Math.floor(box.maxX) - 1;
        var maxY = (int) Math.floor(box.maxY) - 1;
        var maxZ = (int) Math.floor(box.maxZ) - 1;
        switch (axis) {
            case 0 -> maxX = minX + parsed - 1;
            case 1 -> maxY = minY + parsed - 1;
            case 2 -> maxZ = minZ + parsed - 1;
            default -> {
                return;
            }
        }
        BlockSelection.setBounds(new BlockPos(minX, minY, minZ), new BlockPos(maxX, maxY, maxZ));
    }

    void syncVolumeInputsFromAabb(boolean force) {
        var aabb = BlockSelection.aabb();
        if (aabb.isEmpty()) {
            syncInput(volumePosX, "", force);
            syncInput(volumePosY, "", force);
            syncInput(volumePosZ, "", force);
            syncInput(volumeSizeX, "", force);
            syncInput(volumeSizeY, "", force);
            syncInput(volumeSizeZ, "", force);
            return;
        }
        var box = aabb.get();
        var minX = (int) Math.floor(box.minX);
        var minY = (int) Math.floor(box.minY);
        var minZ = (int) Math.floor(box.minZ);
        var sizeX = (int) Math.floor(box.maxX) - minX;
        var sizeY = (int) Math.floor(box.maxY) - minY;
        var sizeZ = (int) Math.floor(box.maxZ) - minZ;
        syncInput(volumePosX, String.valueOf(minX), force);
        syncInput(volumePosY, String.valueOf(minY), force);
        syncInput(volumePosZ, String.valueOf(minZ), force);
        syncInput(volumeSizeX, String.valueOf(sizeX), force);
        syncInput(volumeSizeY, String.valueOf(sizeY), force);
        syncInput(volumeSizeZ, String.valueOf(sizeZ), force);
    }

    /** Used by {@link PlacedJigsawPieceInspectorSection} to seed the inputs from a piece's AABB. */
    void syncVolumeInputsFromPiece(PlacedPiece piece, boolean force) {
        var aabb = piece.aabb();
        var sizeX = aabb.maxX() - aabb.minX();
        var sizeY = aabb.maxY() - aabb.minY();
        var sizeZ = aabb.maxZ() - aabb.minZ();
        syncInput(volumePosX, String.valueOf(aabb.minX()), force);
        syncInput(volumePosY, String.valueOf(aabb.minY()), force);
        syncInput(volumePosZ, String.valueOf(aabb.minZ()), force);
        syncInput(volumeSizeX, String.valueOf(sizeX), force);
        syncInput(volumeSizeY, String.valueOf(sizeY), force);
        syncInput(volumeSizeZ, String.valueOf(sizeZ), force);
    }

    /**
     * Update a text input without clobbering active user typing. Package-default so sibling sections (entity view etc.)
     * can reuse this guarded-write pattern.
     */
    static void syncInput(TextInput input, String value, boolean force) {
        if (force || !input.isFocused()) {
            input.setContent(value);
        }
    }
}
