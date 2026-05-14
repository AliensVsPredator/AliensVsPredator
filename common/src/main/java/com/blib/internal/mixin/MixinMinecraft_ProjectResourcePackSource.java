package com.blib.internal.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.repository.RepositorySource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.blib.internal.client.storage.BLibProjectResourcePackSource;
import com.blib.internal.common.storage.EngineProjectIO;

/**
 * Client-side counterpart to {@code MixinServerPacksSource}: inject {@link BLibProjectResourcePackSource} into the
 * client-side {@link net.minecraft.server.packs.repository.PackRepository} at the moment {@code Minecraft.<init>}
 * constructs it. Without this hook, the {@code resourcepack/} subfolders the engine writes into projects would never be
 * visible to vanilla's resource-pack loader.
 * <p>
 * The {@code Minecraft} constructor builds the resource-pack repo by calling
 * {@code new PackRepository(new ClientPackSource(...), new FolderRepositorySource(...))}, which compiles to a varargs
 * {@code RepositorySource[]}. Modifying that single array argument with {@link ModifyArg} appends our source.
 */
@Mixin(Minecraft.class)
public abstract class MixinMinecraft_ProjectResourcePackSource {

    @ModifyArg(
        method = "<init>",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/packs/repository/PackRepository;<init>([Lnet/minecraft/server/packs/repository/RepositorySource;)V"
        ),
        index = 0
    )
    private RepositorySource[] blib$appendProjectResourcePackSource(RepositorySource[] vanillaSources) {
        var combined = new RepositorySource[vanillaSources.length + 1];
        System.arraycopy(vanillaSources, 0, combined, 0, vanillaSources.length);
        combined[vanillaSources.length] = new BLibProjectResourcePackSource(EngineProjectIO.projectsRoot());
        return combined;
    }
}
