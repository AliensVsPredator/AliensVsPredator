package com.blib.engine.tool.gizmo.target;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.tool.gizmo.GizmoTarget;
import com.blib.mod.BLib;
import com.blib.mod.common.network.packet.C2SSetEntityScalePayload;

/**
 * {@link GizmoTarget} adapting a {@link LivingEntity}'s scale attribute. {@link #read} returns the current attribute
 * value; {@link #preview} is a no-op here because the existing entity scale gizmo holds its ghost value inside its own
 * static field (a generic translate gizmo built atop this target would call {@code preview} to publish a ghost value);
 * {@link #commit} fires the server-bound {@link C2SSetEntityScalePayload}, mirroring the inline send that previously
 * lived in {@code ViewportPanel}.
 * <p>
 * Demonstrates how the existing single-purpose gizmos can be decomposed into "drag math" + "what's being manipulated".
 * The latter is the only piece that's domain-specific; sharing the former is what eliminates the parallel gizmo
 * implementations called out in the architecture review.
 */
@ApiStatus.Internal
public final class EntityScaleTarget implements GizmoTarget<Double> {

    private final LivingEntity entity;

    public EntityScaleTarget(LivingEntity entity) {
        this.entity = entity;
    }

    @Override
    public Double read() {
        var attr = entity.getAttribute(Attributes.SCALE);
        return attr == null ? 1.0 : attr.getValue();
    }

    @Override
    public void preview(Double value) {
        // The existing entity-scale gizmo stages its ghost in its own static field; nothing to mirror here.
    }

    @Override
    public void commit(Double before, Double after) {
        if (Math.abs(after - before) < 1.0e-4) {
            return;
        }
        var player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        var dim = player.level().dimension().location();
        BLib.MOD.networking().sendToServer(new C2SSetEntityScalePayload(entity.getId(), after, dim));
    }
}
