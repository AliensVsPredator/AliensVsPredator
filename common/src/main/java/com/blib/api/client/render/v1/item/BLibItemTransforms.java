package com.blib.api.client.render.v1.item;

import com.blib.api.client.render.v1.BLibTransform;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

/**
 * Collection of {@link BLibTransform}s keyed by {@link ItemDisplayContext}, used by a
 * {@link BLibGeoBoneItemRenderer} to position its rendered bone differently in each render state (GUI,
 * first-person hands, third-person hands, ground entity, head slot, etc.). Lookup falls back to
 * {@link BLibTransform#IDENTITY} when a context isn't configured.
 */
public class BLibItemTransforms {

    private final Map<ItemDisplayContext, BLibTransform> transforms;

    protected BLibItemTransforms(Map<ItemDisplayContext, BLibTransform> transforms) {
        this.transforms = transforms;
    }

    public BLibTransform get(ItemDisplayContext context) {
        return transforms.getOrDefault(context, BLibTransform.IDENTITY);
    }

    /**
     * Like {@link #get} but returns {@code null} when no transform was explicitly set for this context,
     * so callers can distinguish "user explicitly set identity" from "user did not set this context."
     * Used by the cascade in {@link BLibGeoBoneItemRenderer} that falls blocking transforms through to
     * idle for any unset context.
     */
    public @Nullable BLibTransform getOrNull(ItemDisplayContext context) {
        return transforms.get(context);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private final Map<ItemDisplayContext, BLibTransform> transforms = new EnumMap<>(ItemDisplayContext.class);

        private Builder() {}

        public Builder set(ItemDisplayContext context, BLibTransform transform) {
            transforms.put(context, transform);
            return this;
        }

        public Builder gui(BLibTransform transform) {
            return set(ItemDisplayContext.GUI, transform);
        }

        public Builder ground(BLibTransform transform) {
            return set(ItemDisplayContext.GROUND, transform);
        }

        public Builder fixed(BLibTransform transform) {
            return set(ItemDisplayContext.FIXED, transform);
        }

        public Builder head(BLibTransform transform) {
            return set(ItemDisplayContext.HEAD, transform);
        }

        public Builder firstPersonLeftHand(BLibTransform transform) {
            return set(ItemDisplayContext.FIRST_PERSON_LEFT_HAND, transform);
        }

        public Builder firstPersonRightHand(BLibTransform transform) {
            return set(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, transform);
        }

        public Builder thirdPersonLeftHand(BLibTransform transform) {
            return set(ItemDisplayContext.THIRD_PERSON_LEFT_HAND, transform);
        }

        public Builder thirdPersonRightHand(BLibTransform transform) {
            return set(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, transform);
        }

        /** Apply the same transform to both first-person contexts. */
        public Builder firstPerson(BLibTransform transform) {
            return firstPersonLeftHand(transform).firstPersonRightHand(transform);
        }

        /** Apply the same transform to both third-person contexts. */
        public Builder thirdPerson(BLibTransform transform) {
            return thirdPersonLeftHand(transform).thirdPersonRightHand(transform);
        }

        public BLibItemTransforms build() {
            return new BLibItemTransforms(new EnumMap<>(transforms));
        }
    }
}
