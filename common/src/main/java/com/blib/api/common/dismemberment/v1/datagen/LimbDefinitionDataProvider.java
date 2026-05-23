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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import com.blib.api.common.dismemberment.v1.LimbCategory;
import com.blib.api.common.dismemberment.v1.LimbDefinition;
import com.blib.api.common.dismemberment.v1.LimbPoseOption;
import com.blib.api.common.dismemberment.v1.SpawnFunctionRegistry;
import com.blib.api.common.registry.v1.BLibHolder;

public abstract class LimbDefinitionDataProvider implements DataProvider {

    public static final String DIRECTORY = "blib_limbs";

    private final PackOutput output;

    private final Map<ResourceLocation, FileBuilder> filesById = new LinkedHashMap<>();

    protected LimbDefinitionDataProvider(PackOutput output) {
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

        var pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, DIRECTORY);
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
        return "BLib Limb Definitions";
    }

    private record File(Optional<ResourceLocation> parent, List<LimbDefinition> limbs, boolean replace) {

        private static final Codec<File> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                ResourceLocation.CODEC.optionalFieldOf("parent").forGetter(File::parent),
                LimbDefinition.CODEC.listOf().optionalFieldOf("limbs", List.of()).forGetter(File::limbs),
                Codec.BOOL.optionalFieldOf("replace", false).forGetter(File::replace)
            ).apply(instance, File::new)
        );

        private File {
            limbs = List.copyOf(limbs);
        }
    }

    public static final class FileBuilder {

        private final ResourceLocation id;

        private ResourceLocation parent;

        private boolean replace = false;

        private final List<LimbDefinition> limbs = new ArrayList<>();

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

        public FileBuilder replace(boolean replace) {
            this.replace = replace;
            return this;
        }

        public FileBuilder limb(ResourceLocation id, LimbCategory category) {
            return limb(id, category, $ -> {});
        }

        public FileBuilder limb(ResourceLocation id, LimbCategory category, Consumer<LimbBuilder> configurer) {
            var builder = new LimbBuilder(id, category);
            configurer.accept(builder);
            limbs.add(builder.build());
            return this;
        }

        public FileBuilder fatalLimb(ResourceLocation id, LimbCategory category) {
            return limb(id, category, LimbBuilder::fatal);
        }

        public FileBuilder add(LimbDefinition definition) {
            limbs.add(definition);
            return this;
        }

        private File build() {
            return new File(Optional.ofNullable(parent), limbs, replace);
        }
    }

    public static final class LimbBuilder {

        private final ResourceLocation id;

        private final LimbCategory category;

        private boolean fatal;

        private final List<LimbPoseOption> poses = new ArrayList<>();

        private LimbBuilder(ResourceLocation id, LimbCategory category) {
            this.id = id;
            this.category = category;
        }

        public LimbBuilder fatal() {
            fatal = true;
            return this;
        }

        public LimbBuilder pose(String id) {
            poses.add(new LimbPoseOption(id));
            return this;
        }

        public LimbBuilder pose(String id, int weight) {
            poses.add(new LimbPoseOption(id, weight));
            return this;
        }

        public LimbBuilder poses(List<LimbPoseOption> poses) {
            this.poses.clear();
            this.poses.addAll(poses);
            return this;
        }

        private LimbDefinition build() {
            return new LimbDefinition(
                id,
                category,
                SpawnFunctionRegistry.DEFAULT_PROVIDER,
                fatal,
                Collections.unmodifiableList(new ArrayList<>(poses))
            );
        }
    }
}
