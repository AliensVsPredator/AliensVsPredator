package com.blib.internal.mixin;

import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.server.packs.repository.ServerPacksSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.blib.internal.common.storage.BLibProjectPackSource;
import com.blib.internal.common.storage.EngineProjectIO;

/**
 * Inject {@link BLibProjectPackSource} into the world's {@link net.minecraft.server.packs.repository.PackRepository} at
 * the moment vanilla constructs it for an integrated server. Without this hook, projects stored under
 * {@code <gameDir>/blib/projects/} would have no path into the world's pack-loading flow — they'd only become visible
 * if copied or symlinked into {@code <world>/datapacks/}.
 * <p>
 * Vanilla's {@code ServerPacksSource.createPackRepository(Path, DirectoryValidator)} ends with
 * {@code return new PackRepository(new ServerPacksSource(validator), new FolderRepositorySource(...));}. The
 * {@code PackRepository} constructor takes a {@code RepositorySource[]} (varargs), which the JVM passes as a single
 * array argument — we modify that array to append our source.
 * <p>
 * This applies to both the integrated server (single-player) and dedicated servers, since both go through the same
 * {@code createPackRepository} entry point. The engine itself is single-player only (gated by other code), but the
 * mixin doing nothing on a dedicated server is harmless: {@code BLibProjectPackSource.loadPacks} no-ops cleanly when
 * {@code <gameDir>/blib/projects/} doesn't exist.
 */
@Mixin(ServerPacksSource.class)
public abstract class MixinServerPacksSource {

    @ModifyArg(
        method = "createPackRepository(Ljava/nio/file/Path;Lnet/minecraft/world/level/validation/DirectoryValidator;)Lnet/minecraft/server/packs/repository/PackRepository;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/packs/repository/PackRepository;<init>([Lnet/minecraft/server/packs/repository/RepositorySource;)V"
        ),
        index = 0
    )
    private static RepositorySource[] blib$appendProjectPackSource(RepositorySource[] vanillaSources) {
        var combined = new RepositorySource[vanillaSources.length + 1];
        System.arraycopy(vanillaSources, 0, combined, 0, vanillaSources.length);
        combined[vanillaSources.length] = new BLibProjectPackSource(EngineProjectIO.projectsRoot());
        return combined;
    }
}
