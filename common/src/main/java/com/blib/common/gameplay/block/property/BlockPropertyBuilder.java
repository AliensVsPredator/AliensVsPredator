package com.blib.common.gameplay.block.property;

import com.just.core.functional.option.Option;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.Nullable;

import java.util.function.ToIntFunction;

/**
 * This class exists to allow modifying block properties safely and immutably. Because Mojang made the horrible decision
 * to use mutations for properties, properties that are reused and modified on-the-fly can lead to unintentional,
 * hard-to-debug block property issues of which the source of the bug isn't immediately obvious. This builder enforces
 * that property changes on-the-fly are done so immutably, eliminating mutation bugs altogether.
 */
public class BlockPropertyBuilder {

    public static BlockPropertyBuilder inherit(BlockBehaviour blockBehaviour) {
        return new BlockPropertyBuilder(blockBehaviour);
    }

    public static BlockPropertyBuilder of() {
        return new BlockPropertyBuilder();
    }

    private final Option<BlockBehaviour> blockBehaviourOption;

    private Option<Float> explosionResistanceOption;

    private Option<Boolean> forceSolidOnOption;

    private Option<Boolean> forceSolidOffOption;

    private Option<Float> frictionOption;

    private Option<NoteBlockInstrument> instrumentOption;

    private Option<BlockBehaviour.StatePredicate> isRedstoneConductorOption;

    private Option<BlockBehaviour.StatePredicate> isSuffocatingOption;

    private Option<BlockBehaviour.StateArgumentPredicate<EntityType<?>>> isValidSpawnOption;

    private Option<BlockBehaviour.StatePredicate> isViewBlockingOption;

    private Option<ToIntFunction<BlockState>> lightLevelOption;

    private Option<MapColor> mapColorOption;

    private Option<Boolean> noCollisionOption;

    private Option<Boolean> noOcclusionOption;

    private Option<PushReaction> pushReactionOption;

    private Option<Boolean> randomTicksOption;

    private Option<Boolean> replaceableOption;

    private Option<Boolean> requiresCorrectToolForDropsOption;

    private Option<SoundType> soundTypeOption;

    private Option<Float> strengthOption;

    private BlockPropertyBuilder() {
        this(null);
    }

    private BlockPropertyBuilder(
        @Nullable BlockBehaviour blockBehaviour
    ) {
        this(
            Option.ofNullable(blockBehaviour),
            Option.none(),
            Option.none(),
            Option.none(),
            Option.none(),
            Option.none(),
            Option.none(),
            Option.none(),
            Option.none(),
            Option.none(),
            Option.none(),
            Option.none(),
            Option.none(),
            Option.none(),
            Option.none(),
            Option.none(),
            Option.none(),
            Option.none(),
            Option.none(),
            Option.none()
        );
    }

    private BlockPropertyBuilder(
        Option<BlockBehaviour> blockBehaviourOption,
        Option<Float> explosionResistanceOption,
        Option<Boolean> forceSolidOnOption,
        Option<Boolean> forceSolidOffOption,
        Option<Float> frictionOption,
        Option<NoteBlockInstrument> instrumentOption,
        Option<BlockBehaviour.StatePredicate> isRedstoneConductorOption,
        Option<BlockBehaviour.StatePredicate> isSuffocatingOption,
        Option<BlockBehaviour.StateArgumentPredicate<EntityType<?>>> isValidSpawnOption,
        Option<BlockBehaviour.StatePredicate> isViewBlockingOption,
        Option<ToIntFunction<BlockState>> lightLevelOption,
        Option<MapColor> mapColorOption,
        Option<Boolean> noCollisionOption,
        Option<Boolean> noOcclusionOption,
        Option<PushReaction> pushReactionOption,
        Option<Boolean> randomTicksOption,
        Option<Boolean> replaceableOption,
        Option<Boolean> requiresCorrectToolForDropsOption,
        Option<SoundType> soundTypeOption,
        Option<Float> strengthOption
    ) {
        this.blockBehaviourOption = blockBehaviourOption;
        this.explosionResistanceOption = explosionResistanceOption;
        this.forceSolidOnOption = forceSolidOnOption;
        this.forceSolidOffOption = forceSolidOffOption;
        this.frictionOption = frictionOption;
        this.instrumentOption = instrumentOption;
        this.isRedstoneConductorOption = isRedstoneConductorOption;
        this.isSuffocatingOption = isSuffocatingOption;
        this.isValidSpawnOption = isValidSpawnOption;
        this.isViewBlockingOption = isViewBlockingOption;
        this.lightLevelOption = lightLevelOption;
        this.mapColorOption = mapColorOption;
        this.noCollisionOption = noCollisionOption;
        this.noOcclusionOption = noOcclusionOption;
        this.pushReactionOption = pushReactionOption;
        this.randomTicksOption = randomTicksOption;
        this.replaceableOption = replaceableOption;
        this.requiresCorrectToolForDropsOption = requiresCorrectToolForDropsOption;
        this.soundTypeOption = soundTypeOption;
        this.strengthOption = strengthOption;
    }

    public BlockPropertyBuilder explosionResistance(float explosionResistance) {
        var copy = copy();
        copy.explosionResistanceOption = Option.some(explosionResistance);
        return copy;
    }

    public BlockPropertyBuilder forceSolidOn() {
        var copy = copy();
        copy.forceSolidOnOption = Option.some(true);
        return copy;
    }

    public BlockPropertyBuilder forceSolidOff() {
        var copy = copy();
        copy.forceSolidOffOption = Option.some(true);
        return copy;
    }

    public BlockPropertyBuilder friction(float friction) {
        var copy = copy();
        copy.frictionOption = Option.some(friction);
        return copy;
    }

    public BlockPropertyBuilder instrument(NoteBlockInstrument instrument) {
        var copy = copy();
        copy.instrumentOption = Option.some(instrument);
        return copy;
    }

    public BlockPropertyBuilder isRedstoneConductor(BlockBehaviour.StatePredicate predicate) {
        var copy = copy();
        copy.isRedstoneConductorOption = Option.some(predicate);
        return copy;
    }

    public BlockPropertyBuilder isSuffocating(BlockBehaviour.StatePredicate predicate) {
        var copy = copy();
        copy.isSuffocatingOption = Option.some(predicate);
        return copy;
    }

    public BlockPropertyBuilder isValidSpawn(BlockBehaviour.StateArgumentPredicate<EntityType<?>> predicate) {
        var copy = copy();
        copy.isValidSpawnOption = Option.some(predicate);
        return copy;
    }

    public BlockPropertyBuilder isViewBlocking(BlockBehaviour.StatePredicate predicate) {
        var copy = copy();
        copy.isViewBlockingOption = Option.some(predicate);
        return copy;
    }

    public BlockPropertyBuilder lightLevel(ToIntFunction<BlockState> toIntFunction) {
        var copy = copy();
        copy.lightLevelOption = Option.some(toIntFunction);
        return copy;
    }

    public BlockPropertyBuilder mapColor(DyeColor dyeColor) {
        return mapColor(dyeColor.getMapColor());
    }

    public BlockPropertyBuilder mapColor(MapColor mapColor) {
        var copy = copy();
        copy.mapColorOption = Option.some(mapColor);
        return copy;
    }

    public BlockPropertyBuilder noCollision() {
        var copy = copy();
        copy.noCollisionOption = Option.some(true);
        return copy;
    }

    public BlockPropertyBuilder noOcclusion() {
        var copy = copy();
        copy.noOcclusionOption = Option.some(true);
        return copy;
    }

    public BlockPropertyBuilder pushReaction(PushReaction pushReaction) {
        var copy = copy();
        copy.pushReactionOption = Option.some(pushReaction);
        return copy;
    }

    public BlockPropertyBuilder randomTicks() {
        var copy = copy();
        copy.randomTicksOption = Option.some(true);
        return copy;
    }

    public BlockPropertyBuilder replaceable() {
        var copy = copy();
        copy.replaceableOption = Option.some(true);
        return copy;
    }

    public BlockPropertyBuilder requiresCorrectToolForDrops() {
        var copy = copy();
        copy.requiresCorrectToolForDropsOption = Option.some(true);
        return copy;
    }

    public BlockPropertyBuilder sound(SoundType soundType) {
        var copy = copy();
        copy.soundTypeOption = Option.some(soundType);
        return copy;
    }

    public BlockPropertyBuilder strength(float strength) {
        var copy = copy();
        copy.strengthOption = Option.some(strength);
        return copy;
    }

    public BlockPropertyBuilder strength(float strength, float explosionResistance) {
        return strength(strength).explosionResistance(explosionResistance);
    }

    public BlockPropertyBuilder copy() {
        return new BlockPropertyBuilder(
            blockBehaviourOption,
            explosionResistanceOption,
            forceSolidOnOption,
            forceSolidOffOption,
            frictionOption,
            instrumentOption,
            isRedstoneConductorOption,
            isSuffocatingOption,
            isValidSpawnOption,
            isViewBlockingOption,
            lightLevelOption,
            mapColorOption,
            noCollisionOption,
            noOcclusionOption,
            pushReactionOption,
            randomTicksOption,
            replaceableOption,
            requiresCorrectToolForDropsOption,
            soundTypeOption,
            strengthOption
        );
    }

    public BlockBehaviour.Properties build() {
        var baseProperties = blockBehaviourOption.map(BlockBehaviour.Properties::ofFullCopy)
            .unwrapOr(BlockBehaviour.Properties.of());

        explosionResistanceOption.ifSome(baseProperties::explosionResistance);
        forceSolidOnOption.ifSome($ -> baseProperties.forceSolidOn());
        forceSolidOffOption.ifSome($ -> baseProperties.forceSolidOff());
        frictionOption.ifSome(baseProperties::friction);
        instrumentOption.ifSome(baseProperties::instrument);
        isSuffocatingOption.ifSome(baseProperties::isSuffocating);
        isRedstoneConductorOption.ifSome(baseProperties::isRedstoneConductor);
        isValidSpawnOption.ifSome(baseProperties::isValidSpawn);
        isViewBlockingOption.ifSome(baseProperties::isViewBlocking);
        lightLevelOption.ifSome(baseProperties::lightLevel);
        mapColorOption.ifSome(baseProperties::mapColor);
        noCollisionOption.ifSome($ -> baseProperties.noCollission());
        noOcclusionOption.ifSome($ -> baseProperties.noOcclusion());
        pushReactionOption.ifSome(baseProperties::pushReaction);
        randomTicksOption.ifSome($ -> baseProperties.randomTicks());
        replaceableOption.ifSome($ -> baseProperties.replaceable());
        requiresCorrectToolForDropsOption.ifSome($ -> baseProperties.requiresCorrectToolForDrops());
        soundTypeOption.ifSome(baseProperties::sound);
        strengthOption.ifSome(baseProperties::strength);

        return baseProperties;
    }
}
