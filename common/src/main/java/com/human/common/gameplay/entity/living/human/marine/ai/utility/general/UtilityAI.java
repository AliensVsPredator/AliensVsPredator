package com.human.common.gameplay.entity.living.human.marine.ai.utility.general;

import com.just.core.functional.option.Option;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class UtilityAI<M, C, R, S extends Strategy<M, C, R>> {

    private final List<? extends S> strategies;

    public UtilityAI(List<? extends S> strategies) {
        this.strategies = strategies;
    }

    public @Nullable Pick<M, S> getBestStrategyAndMatchableOrNull(List<M> matchables, C context) {
        return getBestStrategyAndMatchableOrNull(matchables, Function.identity(), context);
    }

    public <T> @Nullable Pick<M, S> getBestStrategyAndMatchableOrNull(
        List<T> list,
        Function<T, M> matchableExtractor,
        C context
    ) {
        // Track the best score and its associated matchable/strategy.
        var bestScore = Double.NEGATIVE_INFINITY;
        S bestStrategy = null;
        M bestMatchable = null;

        // Iterate each entry in the provided list.
        for (var entry : list) {
            // Extract the matchable from the entry.
            var matchable = matchableExtractor.apply(entry);

            // Iterate strategies that match this matchable.
            for (var strategy : getStrategiesFor(matchable)) {
                // Compute score for this strategy.
                var score = strategy.score(matchable, context);

                // Only consider finite scores.
                if (Double.isFinite(score)) {
                    // Update the best if this score is higher.
                    if (score > bestScore) {
                        bestScore = score;
                        bestStrategy = strategy;
                        bestMatchable = matchable;
                    }
                }
            }
        }

        // Return the best pair found or null if none.
        return bestStrategy != null
            ? new Pick<>(bestMatchable, bestStrategy, bestScore)
            : null;
    }

    public @Nullable S getBestStrategyOrNull(M matchable, C context) {
        // Track the best score and best strategy.
        var bestScore = Double.NEGATIVE_INFINITY;
        S bestStrategy = null;

        // Iterate strategies that match this matchable.
        for (var strategy : getStrategiesFor(matchable)) {
            // Compute score for this strategy.
            var score = strategy.score(matchable, context);

            // Only consider finite scores.
            if (Double.isFinite(score)) {
                // Update the best if this score is higher.
                if (score > bestScore) {
                    bestScore = score;
                    bestStrategy = strategy;
                }
            }
        }

        // Return the best strategy or null if none.
        return bestStrategy;
    }

    public Option<S> getBestStrategy(M matchable, C context) {
        return Option.ofNullable(getBestStrategyOrNull(matchable, context));
    }

    public List<S> getStrategiesFor(M matchable) {
        var list = new ArrayList<S>();

        for (var strategy : strategies) {
            if (strategy.matches(matchable)) {
                list.add(strategy);
            }
        }

        return list;
    }

    public @Nullable S getFirstStrategyOrNull(M matchable) {
        for (var strategy : strategies) {
            if (strategy.matches(matchable)) {
                return strategy;
            }
        }

        return null;
    }

    public Option<S> getFirstStrategy(M matchable) {
        return Option.ofNullable(getFirstStrategyOrNull(matchable));
    }

    public record Pick<M, S>(
        M matchable,
        S strategy,
        double score
    ) {}
}
