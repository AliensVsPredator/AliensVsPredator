package com.blib.internal.client.render.item.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

import com.blib.api.client.render.v1.item.BLibItemTransforms;

/**
 * Direct JSON shape of a {@code assets/<ns>/blib/item_renderers/<id>.json} file before parent-chain resolution. All
 * fields are nullable because templates legitimately omit any of them — resolution walks the chain in
 * {@link BLibItemRendererConfigLoader} and reports a missing field only after the merge has had a chance to fill it.
 */
public record RawItemRendererConfig(
    @Nullable ResourceLocation parent,
    @Nullable ResourceLocation model,
    @Nullable ResourceLocation texture,
    @Nullable String bone,
    @Nullable BLibItemTransforms idleTransforms,
    @Nullable BLibItemTransforms blockingTransforms
) {

    public static final Codec<RawItemRendererConfig> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            ResourceLocation.CODEC.optionalFieldOf("parent").forGetter(c -> Optional.ofNullable(c.parent)),
            ResourceLocation.CODEC.optionalFieldOf("model").forGetter(c -> Optional.ofNullable(c.model)),
            ResourceLocation.CODEC.optionalFieldOf("texture").forGetter(c -> Optional.ofNullable(c.texture)),
            Codec.STRING.optionalFieldOf("bone").forGetter(c -> Optional.ofNullable(c.bone)),
            TransformsBlock.CODEC.optionalFieldOf("transforms", new TransformsBlock(null, null))
                .forGetter(c -> new TransformsBlock(c.idleTransforms, c.blockingTransforms))
        )
            .apply(
                instance,
                (parent, model, texture, bone, transforms) -> new RawItemRendererConfig(
                    parent.orElse(null),
                    model.orElse(null),
                    texture.orElse(null),
                    bone.orElse(null),
                    transforms.idle,
                    transforms.blocking
                )
            )
    );

    /**
     * Inner object containing the {@code idle} and {@code blocking} transform sets. A nested record (rather than two
     * top-level optional fields) is what lets the JSON shape stay grouped under a {@code "transforms"} key — matching
     * what the inspector serializer emits and what authors will read in version control.
     */
    private record TransformsBlock(
        @Nullable BLibItemTransforms idle,
        @Nullable BLibItemTransforms blocking
    ) {

        static final Codec<TransformsBlock> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                BLibItemTransforms.CODEC.optionalFieldOf("idle").forGetter(b -> Optional.ofNullable(b.idle)),
                BLibItemTransforms.CODEC.optionalFieldOf("blocking").forGetter(b -> Optional.ofNullable(b.blocking))
            ).apply(instance, (idle, blocking) -> new TransformsBlock(idle.orElse(null), blocking.orElse(null)))
        );
    }
}
