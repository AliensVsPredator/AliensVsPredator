package com.avp.common.level.gameevent.listener;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import com.avp.common.block.entity.resin_node.ResinSpreader;
import com.avp.common.level.gameevent.AVPGameEvents;
import com.avp.common.util.resin.ResinProducer;

public class ResinSpreadListener implements GameEventListener {

    final ResinSpreader resinSpreader;

    private final PositionSource positionSource;

    private final SpreaderType spreaderType;

    public ResinSpreadListener(PositionSource positionSource, SpreaderType spreaderType) {
        this.positionSource = positionSource;
        this.spreaderType = spreaderType;
        this.resinSpreader = ResinSpreader.createLevelSpreader();
    }

    @Override
    public @NotNull PositionSource getListenerSource() {
        return positionSource;
    }

    @Override
    public GameEventListener.@NotNull DeliveryMode getDeliveryMode() {
        return GameEventListener.DeliveryMode.BY_DISTANCE;
    }

    @Override
    public int getListenerRadius() {
        return 8;
    }

    public ResinSpreader getResinSpreader() {
        return resinSpreader;
    }

    @Override
    public boolean handleGameEvent(ServerLevel serverLevel, Holder<GameEvent> holder, GameEvent.Context context, Vec3 vec3) {
        if (!holder.is(AVPGameEvents.XENOMORPH_RESIN_SPREAD.key())) {
            return false;
        }

        if (spreaderType instanceof SpreaderType.Entity spreaderEntity && spreaderEntity.entity.equals(context.sourceEntity())) {
            // If this type of listener is a spreader type entity, and if the game event was emitted from the
            // same spreader entity, then we want to ignore this event.
            return true;
        }

        if (spreaderType instanceof SpreaderType.Block) {
            if (context.sourceEntity() instanceof ResinProducer resinProducer) {
                var i = resinProducer.resinManager().resinData().resin();
                this.resinSpreader.addCursors(BlockPos.containing(vec3.relative(Direction.UP, 0.5)), i);
                resinProducer.resinManager().resinData().setResin(0);
            }
        }

        return true;
    }

    public sealed interface SpreaderType {

        record Entity(net.minecraft.world.entity.Entity entity) implements SpreaderType {}

        record Block(BlockPos blockPos) implements SpreaderType {}
    }
}
