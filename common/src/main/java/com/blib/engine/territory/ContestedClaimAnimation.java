package com.blib.engine.territory;

import org.jetbrains.annotations.ApiStatus;

/**
 * Single source of truth for the contested-chunk color animation. Both surfaces that show contested claims — the 3D
 * world overlay ({@code ChunkClaimOverlayRenderer}) and the 2D territory map ({@code TerritoryMapPanel}) — call
 * {@link #sampleAt} so they tick in lockstep. Tuning the cycle (hold time, lerp time) happens here and applies
 * everywhere.
 * <p>
 * Animation shape: hold each claimant's pure base color for {@link #HOLD_MS}, then lerp linearly in RGB to the next
 * claimant's color over {@link #LERP_MS}, repeat through every claimant and wrap back to the first. Every segment is
 * the same length — the inspected-faction emphasis comes from the per-segment alpha array each caller passes, not from
 * stretched timing.
 */
@ApiStatus.Internal
public final class ContestedClaimAnimation {

    /** Milliseconds the cell sits at a claimant's pure base color before the lerp to the next begins. */
    public static final double HOLD_MS = 400.0;

    /** Milliseconds spent lerping from one claimant's color to the next. */
    public static final double LERP_MS = 250.0;

    /** Combined segment length per claimant; total cycle = {@code SEGMENT_MS × claimant count}. */
    public static final double SEGMENT_MS = HOLD_MS + LERP_MS;

    private ContestedClaimAnimation() {}

    /**
     * Animated sample at the given timestamp: an RGB packed int (lower 24 bits, alpha cleared) and an alpha value
     * lerped through the per-segment alphas the caller provided. Each renderer assigns per-segment alphas based on "is
     * this segment's claimant the inspected faction?" so the inspected faction's color reads at the "own-claim"
     * brightness while non-inspected factions read at the "other-claim" brightness, just like fully- owned chunks.
     * Alpha lerps smoothly across the transition between segments.
     */
    public record Sample(
        int rgb,
        double alpha
    ) {}

    /**
     * Resolve the animated RGB + alpha at the given timestamp. {@code rgbs[i]} is the pure base color of claimant
     * {@code i} (lower 24 bits used; alpha bits ignored). {@code alphas[i]} is the alpha to show while claimant
     * {@code i} is the current segment — callers fill this with each claimant's appropriate "own" or "other" alpha
     * depending on whether they're the inspected faction. {@code rgbs} and {@code alphas} must be the same length with
     * at least one entry.
     */
    public static Sample sampleAt(int[] rgbs, double[] alphas, long nowMs) {
        if (rgbs.length == 1) {
            return new Sample(rgbs[0] & 0xFFFFFF, alphas[0]);
        }
        var n = rgbs.length;
        var totalCycleMs = (long) (SEGMENT_MS * n);
        var t = nowMs % totalCycleMs;
        var segmentIdx = (int) (t / SEGMENT_MS);
        var segmentT = t - segmentIdx * SEGMENT_MS;
        var current = rgbs[segmentIdx] & 0xFFFFFF;
        var currentAlpha = alphas[segmentIdx];
        if (segmentT < HOLD_MS) {
            return new Sample(current, currentAlpha);
        }
        var nextIdx = (segmentIdx + 1) % n;
        var next = rgbs[nextIdx] & 0xFFFFFF;
        var nextAlpha = alphas[nextIdx];
        var frac = (float) ((segmentT - HOLD_MS) / LERP_MS);
        var rgb = lerpRgb(current, next, frac);
        var alpha = currentAlpha + (nextAlpha - currentAlpha) * frac;
        return new Sample(rgb, alpha);
    }

    private static int lerpRgb(int aRgb, int bRgb, float frac) {
        var a = aRgb & 0xFFFFFF;
        var b = bRgb & 0xFFFFFF;
        var rA = (a >> 16) & 0xFF;
        var gA = (a >> 8) & 0xFF;
        var bA = a & 0xFF;
        var rB = (b >> 16) & 0xFF;
        var gB = (b >> 8) & 0xFF;
        var bB = b & 0xFF;
        var r = (int) (rA + (rB - rA) * frac);
        var g = (int) (gA + (gB - gA) * frac);
        var bb = (int) (bA + (bB - bA) * frac);
        return (r << 16) | (g << 8) | bb;
    }
}
