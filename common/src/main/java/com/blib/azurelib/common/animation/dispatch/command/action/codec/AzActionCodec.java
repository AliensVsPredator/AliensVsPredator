package com.blib.azurelib.common.animation.dispatch.command.action.codec;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import com.blib.azurelib.common.animation.dispatch.command.action.AzAction;
import com.blib.azurelib.common.animation.dispatch.command.action.registry.AzActionRegistry;

public class AzActionCodec implements StreamCodec<FriendlyByteBuf, AzAction> {

    @Override
    public @NotNull AzAction decode(@NotNull FriendlyByteBuf byteBuf) {
        var id = byteBuf.readShort();
        var codec = AzActionRegistry
            .<AzAction, StreamCodec<FriendlyByteBuf, AzAction>>getCodecOrNull(id);

        if (codec == null) {
            throw new NullPointerException(
                "Could not find action codec for a given action id while decoding data. ID: " + id
            );
        }

        return codec.decode(byteBuf);
    }

    @Override
    public void encode(@NotNull FriendlyByteBuf byteBuf, @NotNull AzAction action) {
        var resourceLocation = action.getResourceLocation();
        var id = AzActionRegistry.getIdOrNull(resourceLocation);
        var codec = AzActionRegistry
            .<AzAction, StreamCodec<FriendlyByteBuf, AzAction>>getCodecOrNull(resourceLocation);

        if (id == null) {
            throw new NullPointerException(
                "Could not find action id for a given resource location while encoding data. Resource Location: "
                    + resourceLocation
            );
        }

        byteBuf.writeShort(id);

        if (codec == null) {
            throw new NullPointerException(
                "Could not find action codec for a given resource location while encoding data. Resource Location: "
                    + resourceLocation + ", ID: " + id
            );
        }

        codec.encode(byteBuf, action);
    }
}
