package com.alien.common.gameplay.level.gameevent.listener;

import com.alien.common.data.AlienVariantTypes;
import com.alien.common.gameplay.block.entity.resin.node.ResinSpreader;
import com.alien.common.model.resin.ResinProducer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class ResinSpreadListener implements GameEventListener {

    final ResinSpreader resinSpreader;

    private final PositionSource positionSource;

    private final SpreaderType spreaderType;

    public ResinSpreadListener(PositionSource positionSource, SpreaderType spreaderType) {
        this.positionSource = positionSource;
        this.spreaderType = spreaderType;
        this.resinSpreader = ResinSpreader.create();
    }

    @Override
    public @NotNull PositionSource getListenerSource() {
        return positionSource;
    }

    @Override
    public @NotNull GameEventListener.DeliveryMode getDeliveryMode() {
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
    public boolean handleGameEvent(
        @NotNull ServerLevel serverLevel,
        @NotNull Holder<GameEvent> holder,
        @NotNull GameEvent.Context context,
        @NotNull Vec3 vec3
    ) {
        var sourceEntity = context.sourceEntity();

        if (sourceEntity == null) {
            // Resin spread events should ideally always come from a source entity (in other words, a xenomorph).
            return false;
        }

        if (!(spreaderType instanceof SpreaderType.Block(BlockPos blockPos))) {
            return false;
        }

        var nodeBlockState = serverLevel.getBlockState(blockPos);
        var alienVariantTypeOption = AlienVariantTypes.getFor(nodeBlockState);

        if (
            // If there is no alien variant type for given source entity.
            alienVariantTypeOption.isNone()
                // OR if there is a resin spread event type mismatch...
                || !holder.is(alienVariantTypeOption.unwrap().resinSpreadEvent().getHolder())
        ) {
            // Then ignore the event.
            return false;
        }

        if (context.sourceEntity() instanceof ResinProducer resinProducer) {
            var resin = resinProducer.getResinManager().resinData().resin();
            this.resinSpreader.addCursors(BlockPos.containing(vec3.relative(Direction.UP, 0.5)), resin);
            resinProducer.getResinManager().resinData().setResin(0);
            return true;
        }

        return false;
    }

    public sealed interface SpreaderType {

        record Entity(net.minecraft.world.entity.Entity entity) implements SpreaderType {}

        record Block(BlockPos blockPos) implements SpreaderType {}
    }
}
