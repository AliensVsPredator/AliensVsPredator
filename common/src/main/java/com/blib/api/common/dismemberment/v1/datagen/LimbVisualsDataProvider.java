package com.blib.api.common.dismemberment.v1.datagen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import com.blib.api.common.dismemberment.v1.LimbPose;
import com.blib.api.common.dismemberment.v1.LimbVisuals;
import com.blib.api.common.registry.v1.BLibHolder;

public abstract class LimbVisualsDataProvider implements DataProvider {

    public static final String DIRECTORY = "blib_limb_visuals";

    private final PackOutput output;

    private final Map<ResourceLocation, FileBuilder> filesById = new LinkedHashMap<>();

    protected LimbVisualsDataProvider(PackOutput output) {
        this.output = output;
    }

    protected abstract void generate();

    protected final FileBuilder file(ResourceLocation id) {
        return filesById.computeIfAbsent(id, FileBuilder::new);
    }

    protected final FileBuilder entity(EntityType<?> entityType) {
        return file(BuiltInRegistries.ENTITY_TYPE.getKey(entityType));
    }

    protected final FileBuilder entity(BLibHolder<? extends EntityType<?>> entityType) {
        return file(entityType.getResourceLocation());
    }

    protected final FileBuilder template(ResourceLocation id) {
        return file(id);
    }

    @Override
    public final @NotNull CompletableFuture<?> run(@NotNull CachedOutput cached) {
        filesById.clear();
        generate();

        var pathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, DIRECTORY);
        var futures = filesById.entrySet()
            .stream()
            .map(entry -> {
                var json = File.CODEC.encodeStart(JsonOps.INSTANCE, entry.getValue().build()).getOrThrow();
                return DataProvider.saveStable(cached, json, pathProvider.json(entry.getKey()));
            });

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override
    public @NotNull String getName() {
        return "BLib Limb Visuals";
    }

    private record File(Optional<ResourceLocation> parent, Map<ResourceLocation, LimbVisuals> visuals) {

        private static final Codec<File> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                ResourceLocation.CODEC.optionalFieldOf("parent").forGetter(File::parent),
                Codec
                    .unboundedMap(ResourceLocation.CODEC, LimbVisuals.CODEC)
                    .optionalFieldOf("visuals", Map.of())
                    .forGetter(File::visuals)
            ).apply(instance, File::new)
        );

        private File {
            visuals = Collections.unmodifiableMap(new LinkedHashMap<>(visuals));
        }
    }

    public static final class FileBuilder {

        private final ResourceLocation id;

        private ResourceLocation parent;

        private final Map<ResourceLocation, LimbVisuals> visuals = new LinkedHashMap<>();

        private FileBuilder(ResourceLocation id) {
            this.id = id;
        }

        public ResourceLocation id() {
            return id;
        }

        public FileBuilder parent(ResourceLocation parent) {
            this.parent = parent;
            return this;
        }

        public FileBuilder visual(ResourceLocation id, String rootBoneName) {
            return visual(id, rootBoneName, $ -> {});
        }

        public FileBuilder visual(ResourceLocation id, String rootBoneName, Consumer<VisualBuilder> configurer) {
            var builder = new VisualBuilder(rootBoneName);
            configurer.accept(builder);
            visuals.put(id, builder.build());
            return this;
        }

        public FileBuilder add(ResourceLocation id, LimbVisuals visuals) {
            this.visuals.put(id, visuals);
            return this;
        }

        private File build() {
            return new File(Optional.ofNullable(parent), visuals);
        }
    }

    public static final class VisualBuilder {

        private final String rootBoneName;

        private List<String> companionBoneNames = List.of();

        private Vec3 renderOffset = Vec3.ZERO;

        private Vec3 renderRotation = Vec3.ZERO;

        private Vec3 renderScale = LimbVisuals.DEFAULT_SCALE;

        private Vec3 renderPivot = LimbVisuals.DEFAULT_PIVOT;

        private boolean modelerTransform;

        private final List<LimbPose> poses = new ArrayList<>();

        private VisualBuilder(String rootBoneName) {
            this.rootBoneName = rootBoneName;
        }

        public VisualBuilder companions(String... companionBoneNames) {
            this.companionBoneNames = List.of(companionBoneNames);
            return this;
        }

        public VisualBuilder renderOffset(double x, double y, double z) {
            return renderOffset(new Vec3(x, y, z));
        }

        public VisualBuilder renderOffset(Vec3 renderOffset) {
            this.renderOffset = renderOffset;
            return this;
        }

        public VisualBuilder renderRotation(double pitch, double yaw, double roll) {
            return renderRotation(new Vec3(pitch, yaw, roll));
        }

        public VisualBuilder renderRotation(Vec3 renderRotation) {
            this.renderRotation = renderRotation;
            return this;
        }

        public VisualBuilder renderScale(double x, double y, double z) {
            return renderScale(new Vec3(x, y, z));
        }

        public VisualBuilder renderScale(Vec3 renderScale) {
            this.renderScale = renderScale;
            return this;
        }

        public VisualBuilder renderPivot(double x, double y, double z) {
            return renderPivot(new Vec3(x, y, z));
        }

        public VisualBuilder renderPivot(Vec3 renderPivot) {
            this.renderPivot = renderPivot;
            modelerTransform = true;
            return this;
        }

        public VisualBuilder pose(LimbPose pose) {
            poses.add(pose);
            return this;
        }

        public VisualBuilder poses(List<LimbPose> poses) {
            this.poses.clear();
            this.poses.addAll(poses);
            return this;
        }

        private LimbVisuals build() {
            return new LimbVisuals(
                rootBoneName,
                companionBoneNames,
                renderOffset,
                renderRotation,
                renderScale,
                renderPivot,
                modelerTransform,
                Collections.unmodifiableList(new ArrayList<>(poses))
            );
        }
    }
}
