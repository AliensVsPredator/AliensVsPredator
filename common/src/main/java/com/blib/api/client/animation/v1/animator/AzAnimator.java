package com.blib.api.client.animation.v1.animator;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.WeakHashMap;

import com.blib.internal.client.animation.AzAnimationContext;
import com.blib.internal.client.animation.AzAnimationTimer;
import com.blib.internal.client.animation.AzAnimatorConfig;
import com.blib.internal.client.animation.cache.AzBakedAnimationCache;
import com.blib.internal.client.animation.cache.AzBoneCache;
import com.blib.internal.client.animation.controller.AzAnimationControllerContainer;
import com.blib.internal.client.animation.primitive.AzBakedAnimation;
import com.blib.internal.common.molang.MolangParser;
import com.blib.internal.common.molang.MolangQueries;

public abstract class AzAnimator<K, T> {

    private AzAnimationContext<T> currentContext;

    private final WeakHashMap<K, AzAnimationContext<T>> contextCache = new WeakHashMap<>();

    private final AzAnimationControllerContainer<T> animationControllerContainer;

    protected final AzAnimatorConfig config;

    public boolean reloadAnimations;

    protected AzAnimator() {
        this(AzAnimatorConfig.defaultConfig());
    }

    protected AzAnimator(AzAnimatorConfig config) {
        this.animationControllerContainer = new AzAnimationControllerContainer<>();

        this.config = config;
    }

    public AzBoneCache createBoneCache() {
        return new AzBoneCache();
    }

    public AzAnimationTimer createAzAnimationTimer(AzAnimatorConfig config) {
        return new AzAnimationTimer(config);
    }

    public AzAnimationContext<T> getOrCreateContext(K uuid) {
        var ctx = contextCache.computeIfAbsent(
            uuid,
            a -> new AzAnimationContext<>(createBoneCache(), config, createAzAnimationTimer(config))
        );
        this.currentContext = ctx;
        return ctx;
    }

    public abstract void registerControllers(AzAnimationControllerContainer<T> animationControllerContainer);

    public abstract @NotNull ResourceLocation getAnimationLocation(T animatable);

    public void animate(T animatable, float partialTicks, boolean updateTimer) {
        this.currentContext.setAnimatable(animatable);

        var boneCache = this.currentContext.boneCache();
        var timer = this.currentContext.timer();

        if (updateTimer) {
            timer.tick();
        }

        preAnimationSetup(animatable, timer.getAnimTime(), partialTicks);

        if (!boneCache.isEmpty()) {

            for (var controller : animationControllerContainer.getAll()) {
                controller.update();
            }

            this.reloadAnimations = false;

            boneCache.update(this.currentContext);
        }

        setCustomAnimations(animatable, partialTicks);
    }

    public void animate(T animatable, float partialTicks) {
        this.animate(animatable, partialTicks, true);
    }

    protected void preAnimationSetup(T animatable, double animTime, float partialTicks) {
        applyMolangQueries(animatable, animTime, partialTicks);
    }

    protected void applyMolangQueries(T animatable, double animTime, float partialTicks) {
        var level = Minecraft.getInstance().level;
        var parser = MolangParser.INSTANCE;

        if (level == null) {
            return;
        }

        parser.setMemoizedValue(MolangQueries.LIFE_TIME, () -> animTime / 20d);
        parser.setMemoizedValue(MolangQueries.ACTOR_COUNT, level::getEntityCount);
        parser.setMemoizedValue(MolangQueries.TIME_OF_DAY, () -> level.getDayTime() / 24000f);
        parser.setMemoizedValue(MolangQueries.MOON_PHASE, level::getMoonPhase);
    }

    public void setCustomAnimations(T animatable, float partialTicks) {}

    public AzBakedAnimation getAnimation(T animatable, String name) {
        var location = getAnimationLocation(animatable);
        var bakedAnimations = AzBakedAnimationCache.getInstance().getNullable(location);

        return bakedAnimations.getAnimation(name);
    }

    public AzAnimationContext<T> context() {
        return currentContext;
    }

    public AzAnimationControllerContainer<T> getAnimationControllerContainer() {
        return animationControllerContainer;
    }
}
