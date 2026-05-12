package com.blib.engine.modeler.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.GameRenderer;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4f;

/**
 * Draws the grid floor (lines on the Y=0 plane) and the "N" marker showing north (+Z) for the modeler viewport. All
 * geometry is emitted with {@link DefaultVertexFormat#POSITION_COLOR} via the {@code rendertype_lines} or
 * {@code position_color} shader so the renderer doesn't depend on any custom BLib shader.
 */
@ApiStatus.Internal
public final class ModelerGridRenderer {

    private static final int GRID_HALF_EXTENT = 16; // ±16 units → 32×32 cell grid

    private static final int GRID_COLOR_MINOR = 0xFF2A2A30;

    private static final int GRID_COLOR_MAJOR = 0xFF4A4A55;

    private static final int N_COLOR = 0xFFE0E0E0;

    private static final float N_OFFSET = -14.0f; // -Z is north in Minecraft's coordinate system; marker placed there.

    private static final float N_HEIGHT = 4.0f;

    private static final float N_WIDTH = 3.0f;

    private ModelerGridRenderer() {}

    public static void render(Matrix4f pose) {
        RenderSystem.setShader(GameRenderer::getRendertypeLinesShader);
        RenderSystem.lineWidth(1.0f);

        var tesselator = Tesselator.getInstance();
        var buffer = tesselator.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL);

        // Major axes: through origin, in slightly brighter colour so they read as world axes.
        for (var i = -GRID_HALF_EXTENT; i <= GRID_HALF_EXTENT; i++) {
            var color = (i == 0) ? GRID_COLOR_MAJOR : GRID_COLOR_MINOR;
            // Lines parallel to X axis (varying Z)
            addLine(buffer, pose, -GRID_HALF_EXTENT, 0, i, GRID_HALF_EXTENT, 0, i, color);
            // Lines parallel to Z axis (varying X)
            addLine(buffer, pose, i, 0, -GRID_HALF_EXTENT, i, 0, GRID_HALF_EXTENT, color);
        }

        // "N" glyph — three line segments forming the letter — drawn flat on the floor at +Z.
        // Letter occupies (-W/2..+W/2) × (0..H) in local coords; placed at z = N_OFFSET.
        var halfW = N_WIDTH * 0.5f;
        var x0 = -halfW;
        var x1 = halfW;
        var z0 = N_OFFSET - N_HEIGHT * 0.5f;
        var z1 = N_OFFSET + N_HEIGHT * 0.5f;
        // Left vertical leg
        addLine(buffer, pose, x0, 0, z0, x0, 0, z1, N_COLOR);
        // Diagonal
        addLine(buffer, pose, x0, 0, z1, x1, 0, z0, N_COLOR);
        // Right vertical leg
        addLine(buffer, pose, x1, 0, z0, x1, 0, z1, N_COLOR);

        var built = buffer.build();
        if (built != null) {
            BufferUploader.drawWithShader(built);
        }
    }

    private static void addLine(
        com.mojang.blaze3d.vertex.BufferBuilder buffer,
        Matrix4f pose,
        float x0,
        float y0,
        float z0,
        float x1,
        float y1,
        float z1,
        int argb
    ) {
        var a = ((argb >> 24) & 0xFF) / 255f;
        var r = ((argb >> 16) & 0xFF) / 255f;
        var g = ((argb >> 8) & 0xFF) / 255f;
        var b = (argb & 0xFF) / 255f;
        // POSITION_COLOR_NORMAL is what rendertype_lines expects. The normal here is the line direction, normalized.
        var dx = x1 - x0;
        var dy = y1 - y0;
        var dz = z1 - z0;
        var len = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        var nx = dx / len;
        var ny = dy / len;
        var nz = dz / len;
        buffer.addVertex(pose, x0, y0, z0).setColor(r, g, b, a).setNormal(nx, ny, nz);
        buffer.addVertex(pose, x1, y1, z1).setColor(r, g, b, a).setNormal(nx, ny, nz);
    }
}
