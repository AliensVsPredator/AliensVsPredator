package com.blib.internal.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.Target;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Set;

import com.blib.mod.common.property.BLibModProperties;
import com.blib.mod.common.property.BLibModPropertyAccess;

@Mixin(Path.class)
public abstract class MixinPath_Debug {

    @Shadow
    abstract void setDebug(Node[] openSet, Node[] closedSet, Set<Target> targetNodes);

    @Inject(at = @At("TAIL"), method = "<init>")
    private void onNew(List<Node> nodes, BlockPos dest, boolean reached, CallbackInfo ci) {
        if (BLibModPropertyAccess.INSTANCE.get(BLibModProperties.Debug.Render.ENABLED)) {
            setDebug(new Node[0], new Node[0], Set.of(new Target(dest.getX(), dest.getY(), dest.getZ())));
        }
    }
}
