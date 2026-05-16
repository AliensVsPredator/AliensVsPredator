package com.blib.engine.modeler.animation;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.blib.engine.modeler.ModelerBone;
import com.blib.engine.modeler.ModelerCube;
import com.blib.engine.modeler.ModelerTransforms;
import com.blib.engine.modeler.animation.AnimationEditorState.AnimationKey;
import com.blib.engine.modeler.animation.AnimationEditorState.TransformChannel;

/**
 * Optional animation collision analysis for the modeler. It is intentionally inert while disabled because collision
 * checks compare every animated cube pair at sampled timeline positions.
 */
@ApiStatus.Internal
public final class AnimationCollisionState {

    public record CollisionSpan(String boneName, double startSeconds, double endSeconds) {}

    private record CollisionReport(Map<String, List<CollisionSpan>> spansByBone) {

        private static final CollisionReport EMPTY = new CollisionReport(Map.of());
    }

    private record CollisionSnapshot(Set<ModelerCube> cubes, Set<String> boneNames) {

        private static final CollisionSnapshot EMPTY = new CollisionSnapshot(Set.of(), Set.of());
    }

    private record CollisionBox(
        ModelerBone owner,
        ModelerCube cube,
        Vec center,
        Vec axisX,
        Vec axisY,
        Vec axisZ,
        double halfX,
        double halfY,
        double halfZ,
        double minX,
        double minY,
        double minZ,
        double maxX,
        double maxY,
        double maxZ
    ) {

        Vec axis(int index) {
            return switch (index) {
                case 0 -> axisX;
                case 1 -> axisY;
                default -> axisZ;
            };
        }

        double half(int index) {
            return switch (index) {
                case 0 -> halfX;
                case 1 -> halfY;
                default -> halfZ;
            };
        }
    }

    private record CubePair(ModelerCube a, ModelerCube b) {

        static CubePair of(ModelerCube a, ModelerCube b) {
            return new CubePair(a, b);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof CubePair pair)) {
                return false;
            }
            return (a == pair.a && b == pair.b) || (a == pair.b && b == pair.a);
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(a) ^ System.identityHashCode(b);
        }
    }

    private record Vec(double x, double y, double z) {

        Vec subtract(Vec other) {
            return new Vec(x - other.x, y - other.y, z - other.z);
        }

        Vec add(Vec other) {
            return new Vec(x + other.x, y + other.y, z + other.z);
        }

        Vec scale(double value) {
            return new Vec(x * value, y * value, z * value);
        }

        double dot(Vec other) {
            return x * other.x + y * other.y + z * other.z;
        }

        double length() {
            return Math.sqrt(dot(this));
        }

        @Nullable Vec normalized() {
            var length = length();
            return length <= AXIS_EPSILON ? null : scale(1.0 / length);
        }
    }

    private static final AnimationCollisionState INSTANCE = new AnimationCollisionState();

    private static final double SAMPLE_STEP_SECONDS = 0.05;

    private static final int MAX_SAMPLE_COUNT = 1200;

    private static final int BOUNDARY_REFINEMENT_STEPS = 8;

    private static final double AXIS_EPSILON = 1.0e-8;

    private static final double SAT_EPSILON = 1.0e-6;

    private static final double BASELINE_ADJACENCY_TOLERANCE = 0.05;

    private static final double RUNTIME_PENETRATION_THRESHOLD = 0.02;

    private boolean enabled;

    private long reportFingerprint = Long.MIN_VALUE;

    private double currentSnapshotSeconds = Double.NaN;

    private CollisionReport report = CollisionReport.EMPTY;

    private CollisionSnapshot currentSnapshot = CollisionSnapshot.EMPTY;

    private Set<CubePair> baselineIgnoredPairs = Set.of();

    private AnimationCollisionState() {}

    public static AnimationCollisionState get() {
        return INSTANCE;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void toggleEnabled() {
        setEnabled(!enabled);
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled == enabled) {
            return;
        }
        this.enabled = enabled;
        clearCache();
    }

    public void refresh(ModelerBone root, AnimationEditorState state) {
        if (!enabled || root == null || !state.hasPlayableSelection()) {
            clearCache();
            return;
        }

        var fingerprint = fingerprint(root, state);
        if (fingerprint != reportFingerprint) {
            baselineIgnoredPairs = buildBaselineIgnoredPairs(root);
            report = buildReport(root, state, baselineIgnoredPairs);
            reportFingerprint = fingerprint;
            currentSnapshot = CollisionSnapshot.EMPTY;
            currentSnapshotSeconds = Double.NaN;
        }

        var playhead = state.playheadSeconds();
        if (!Double.isFinite(currentSnapshotSeconds) || Math.abs(playhead - currentSnapshotSeconds) > 1.0e-6) {
            currentSnapshot = collisionSnapshot(root, state, playhead);
            currentSnapshotSeconds = playhead;
        }
    }

    public Set<ModelerCube> currentCollisionCubes() {
        return currentSnapshot.cubes();
    }

    public List<CollisionSpan> spansForBone(String boneName) {
        if (!enabled || boneName == null || boneName.isBlank()) {
            return List.of();
        }
        return report.spansByBone().getOrDefault(boneName, List.of());
    }

    private void clearCache() {
        reportFingerprint = Long.MIN_VALUE;
        currentSnapshotSeconds = Double.NaN;
        report = CollisionReport.EMPTY;
        currentSnapshot = CollisionSnapshot.EMPTY;
        baselineIgnoredPairs = Set.of();
    }

    private CollisionReport buildReport(ModelerBone root, AnimationEditorState state, Set<CubePair> ignoredPairs) {
        var duration = Math.max(0.0, state.selectedAnimationLengthSeconds());
        if (duration <= 0.0) {
            return CollisionReport.EMPTY;
        }

        var times = sampleTimes(root, state, duration);
        if (times.isEmpty()) {
            return CollisionReport.EMPTY;
        }

        var spansByBone = new HashMap<String, List<CollisionSpan>>();
        var activeStarts = new HashMap<String, Double>();
        Set<String> previous = Set.of();
        var previousTime = times.getFirst();
        var first = true;

        for (var time : times) {
            var current = collisionSnapshot(root, state, time, ignoredPairs).boneNames();
            for (var boneName : current) {
                if (!activeStarts.containsKey(boneName)) {
                    var start = first || previous.contains(boneName)
                        ? time
                        : refineBoundary(root, state, ignoredPairs, boneName, previousTime, time, true);
                    activeStarts.put(boneName, start);
                }
            }

            var closed = new ArrayList<String>();
            for (var entry : activeStarts.entrySet()) {
                if (!current.contains(entry.getKey())) {
                    var end = first || !previous.contains(entry.getKey())
                        ? time
                        : refineBoundary(root, state, ignoredPairs, entry.getKey(), previousTime, time, false);
                    addSpan(spansByBone, entry.getKey(), entry.getValue(), end);
                    closed.add(entry.getKey());
                }
            }
            for (var boneName : closed) {
                activeStarts.remove(boneName);
            }

            previous = current;
            previousTime = time;
            first = false;
        }

        for (var entry : activeStarts.entrySet()) {
            addSpan(spansByBone, entry.getKey(), entry.getValue(), duration);
        }
        for (var spans : spansByBone.values()) {
            spans.sort(Comparator.comparingDouble(CollisionSpan::startSeconds));
        }
        return new CollisionReport(Collections.unmodifiableMap(spansByBone));
    }

    private static void addSpan(Map<String, List<CollisionSpan>> spansByBone, String boneName, double start, double end) {
        var normalizedStart = Math.max(0.0, Math.min(start, end));
        var normalizedEnd = Math.max(normalizedStart, end);
        spansByBone.computeIfAbsent(boneName, ignored -> new ArrayList<>())
            .add(new CollisionSpan(boneName, normalizedStart, normalizedEnd));
    }

    private double refineBoundary(
        ModelerBone root,
        AnimationEditorState state,
        Set<CubePair> ignoredPairs,
        String boneName,
        double from,
        double to,
        boolean collidingAtUpper
    ) {
        var low = Math.min(from, to);
        var high = Math.max(from, to);
        for (var i = 0; i < BOUNDARY_REFINEMENT_STEPS; i++) {
            var mid = (low + high) * 0.5;
            var colliding = collisionSnapshot(root, state, mid, ignoredPairs).boneNames().contains(boneName);
            if (colliding == collidingAtUpper) {
                high = mid;
            } else {
                low = mid;
            }
        }
        return high;
    }

    private List<Double> sampleTimes(ModelerBone root, AnimationEditorState state, double duration) {
        var times = new TreeSet<Double>();
        times.add(0.0);
        times.add(duration);

        var step = Math.max(SAMPLE_STEP_SECONDS, duration / Math.max(1.0, MAX_SAMPLE_COUNT));
        for (var t = 0.0; t <= duration; t += step) {
            times.add(canonicalTime(Math.min(duration, t)));
        }

        var animations = animationKeys(state);
        collectKeyframeTimes(root, state, animations, duration, times);
        return new ArrayList<>(times);
    }

    private void collectKeyframeTimes(
        ModelerBone bone,
        AnimationEditorState state,
        List<AnimationKey> animations,
        double duration,
        TreeSet<Double> times
    ) {
        for (var animation : animations) {
            for (var channel : TransformChannel.values()) {
                for (var frame : state.keyframes(animation.documentId(), animation.animationName(), bone.name, channel)) {
                    if (frame.timestamp() >= 0.0 && frame.timestamp() <= duration) {
                        times.add(canonicalTime(frame.timestamp()));
                    }
                }
            }
        }
        for (var child : bone.children) {
            collectKeyframeTimes(child, state, animations, duration, times);
        }
    }

    private static double canonicalTime(double seconds) {
        try {
            return Double.parseDouble(AnimationEditorState.formatTimestamp(seconds));
        } catch (NumberFormatException ignored) {
            return Math.max(0.0, seconds);
        }
    }

    private Set<CubePair> buildBaselineIgnoredPairs(ModelerBone root) {
        var boxes = new ArrayList<CollisionBox>();
        collectRestBoxes(root, new Matrix4f(), boxes);
        if (boxes.size() < 2) {
            return Set.of();
        }

        var ignored = new HashSet<CubePair>();
        forEachCandidatePair(boxes, (a, b) -> {
            if (a.owner() != b.owner() && collisionDepth(a, b) > 0.0) {
                ignored.add(CubePair.of(a.cube(), b.cube()));
            }
        });
        return Set.copyOf(ignored);
    }

    private CollisionSnapshot collisionSnapshot(ModelerBone root, AnimationEditorState state, double seconds) {
        return collisionSnapshot(root, state, seconds, baselineIgnoredPairs);
    }

    private CollisionSnapshot collisionSnapshot(ModelerBone root, AnimationEditorState state, double seconds, Set<CubePair> ignoredPairs) {
        var boxes = new ArrayList<CollisionBox>();
        collectBoxes(root, state, seconds, new Matrix4f(), boxes);
        if (boxes.size() < 2) {
            return CollisionSnapshot.EMPTY;
        }

        var cubes = new HashSet<ModelerCube>();
        var boneNames = new HashSet<String>();
        forEachCandidatePair(boxes, (a, b) -> {
            if (a.owner() == b.owner() || ignoredPairs.contains(CubePair.of(a.cube(), b.cube()))) {
                return;
            }
            if (collisionDepth(a, b) > RUNTIME_PENETRATION_THRESHOLD) {
                cubes.add(a.cube());
                cubes.add(b.cube());
                boneNames.add(a.owner().name);
                boneNames.add(b.owner().name);
            }
        });
        if (cubes.isEmpty()) {
            return CollisionSnapshot.EMPTY;
        }
        return new CollisionSnapshot(Set.copyOf(cubes), Set.copyOf(boneNames));
    }

    private static void forEachCandidatePair(List<CollisionBox> boxes, CollisionPairConsumer consumer) {
        boxes.sort(Comparator.comparingDouble(CollisionBox::minX));
        for (var i = 0; i < boxes.size(); i++) {
            var a = boxes.get(i);
            for (var j = i + 1; j < boxes.size(); j++) {
                var b = boxes.get(j);
                if (b.minX() > a.maxX()) {
                    break;
                }
                if (a.maxY() < b.minY() || b.maxY() < a.minY() || a.maxZ() < b.minZ() || b.maxZ() < a.minZ()) {
                    continue;
                }
                consumer.accept(a, b);
            }
        }
    }

    @FunctionalInterface
    private interface CollisionPairConsumer {

        void accept(CollisionBox a, CollisionBox b);
    }

    private void collectBoxes(
        ModelerBone bone,
        AnimationEditorState state,
        double seconds,
        Matrix4f parentMatrix,
        List<CollisionBox> out
    ) {
        var boneMatrix = new Matrix4f(parentMatrix);
        applyBoneTransform(boneMatrix, bone, state, seconds);
        for (var cube : bone.cubes) {
            var cubeMatrix = new Matrix4f(boneMatrix);
            ModelerTransforms.applyCube(cubeMatrix, cube);
            var box = collisionBox(bone, cube, cubeMatrix);
            if (box != null) {
                out.add(box);
            }
        }
        for (var child : bone.children) {
            collectBoxes(child, state, seconds, boneMatrix, out);
        }
    }

    private static void applyBoneTransform(Matrix4f matrix, ModelerBone bone, AnimationEditorState state, double seconds) {
        var preview = state.previewTransformFor(bone, seconds);
        var position = preview == null || preview.position() == null ? bone.position : preview.position();
        var rotation = preview == null || preview.rotationDelta() == null ? bone.rotation : bone.rotation.add(preview.rotationDelta());
        var scale = preview == null || preview.scale() == null ? bone.scale : preview.scale();

        matrix.translate((float) position.x, (float) position.y, (float) position.z);
        matrix.translate((float) bone.pivot.x, (float) bone.pivot.y, (float) bone.pivot.z);
        matrix.rotateZ((float) Math.toRadians(rotation.z));
        matrix.rotateY((float) Math.toRadians(rotation.y));
        matrix.rotateX((float) Math.toRadians(rotation.x));
        matrix.scale((float) scale.x, (float) scale.y, (float) scale.z);
        matrix.translate((float) -bone.pivot.x, (float) -bone.pivot.y, (float) -bone.pivot.z);
    }

    private static @Nullable CollisionBox collisionBox(ModelerBone owner, ModelerCube cube, Matrix4f matrix) {
        return collisionBox(owner, cube, matrix, 0.0);
    }

    private static @Nullable CollisionBox collisionBox(
        ModelerBone owner,
        ModelerCube cube,
        Matrix4f matrix,
        double extraInflate
    ) {
        var inflate = cube.inflate + extraInflate;
        var x0 = Math.min(cube.origin.x, cube.origin.x + cube.size.x) - inflate;
        var y0 = Math.min(cube.origin.y, cube.origin.y + cube.size.y) - inflate;
        var z0 = Math.min(cube.origin.z, cube.origin.z + cube.size.z) - inflate;
        var x1 = Math.max(cube.origin.x, cube.origin.x + cube.size.x) + inflate;
        var y1 = Math.max(cube.origin.y, cube.origin.y + cube.size.y) + inflate;
        var z1 = Math.max(cube.origin.z, cube.origin.z + cube.size.z) + inflate;

        var p000 = transform(matrix, x0, y0, z0);
        var p100 = transform(matrix, x1, y0, z0);
        var p010 = transform(matrix, x0, y1, z0);
        var p001 = transform(matrix, x0, y0, z1);

        var edgeX = p100.subtract(p000);
        var edgeY = p010.subtract(p000);
        var edgeZ = p001.subtract(p000);
        var halfX = edgeX.length() * 0.5;
        var halfY = edgeY.length() * 0.5;
        var halfZ = edgeZ.length() * 0.5;
        if (halfX <= AXIS_EPSILON || halfY <= AXIS_EPSILON || halfZ <= AXIS_EPSILON) {
            return null;
        }

        var axisX = edgeX.normalized();
        var axisY = edgeY.normalized();
        var axisZ = edgeZ.normalized();
        if (axisX == null || axisY == null || axisZ == null) {
            return null;
        }
        var center = p000.add(edgeX.scale(0.5)).add(edgeY.scale(0.5)).add(edgeZ.scale(0.5));
        var aabbHalfX = Math.abs(axisX.x()) * halfX + Math.abs(axisY.x()) * halfY + Math.abs(axisZ.x()) * halfZ;
        var aabbHalfY = Math.abs(axisX.y()) * halfX + Math.abs(axisY.y()) * halfY + Math.abs(axisZ.y()) * halfZ;
        var aabbHalfZ = Math.abs(axisX.z()) * halfX + Math.abs(axisY.z()) * halfY + Math.abs(axisZ.z()) * halfZ;
        return new CollisionBox(
            owner,
            cube,
            center,
            axisX,
            axisY,
            axisZ,
            halfX,
            halfY,
            halfZ,
            center.x() - aabbHalfX,
            center.y() - aabbHalfY,
            center.z() - aabbHalfZ,
            center.x() + aabbHalfX,
            center.y() + aabbHalfY,
            center.z() + aabbHalfZ
        );
    }

    private void collectRestBoxes(ModelerBone bone, Matrix4f parentMatrix, List<CollisionBox> out) {
        var boneMatrix = new Matrix4f(parentMatrix);
        applyRestBoneTransform(boneMatrix, bone);
        for (var cube : bone.cubes) {
            var cubeMatrix = new Matrix4f(boneMatrix);
            ModelerTransforms.applyCube(cubeMatrix, cube);
            var box = collisionBox(bone, cube, cubeMatrix, BASELINE_ADJACENCY_TOLERANCE);
            if (box != null) {
                out.add(box);
            }
        }
        for (var child : bone.children) {
            collectRestBoxes(child, boneMatrix, out);
        }
    }

    private static void applyRestBoneTransform(Matrix4f matrix, ModelerBone bone) {
        matrix.translate((float) bone.position.x, (float) bone.position.y, (float) bone.position.z);
        matrix.translate((float) bone.pivot.x, (float) bone.pivot.y, (float) bone.pivot.z);
        matrix.rotateZ((float) Math.toRadians(bone.rotation.z));
        matrix.rotateY((float) Math.toRadians(bone.rotation.y));
        matrix.rotateX((float) Math.toRadians(bone.rotation.x));
        matrix.scale((float) bone.scale.x, (float) bone.scale.y, (float) bone.scale.z);
        matrix.translate((float) -bone.pivot.x, (float) -bone.pivot.y, (float) -bone.pivot.z);
    }

    private static Vec transform(Matrix4f matrix, double x, double y, double z) {
        var out = matrix.transformPosition((float) x, (float) y, (float) z, new Vector3f());
        return new Vec(out.x, out.y, out.z);
    }

    private static double collisionDepth(CollisionBox a, CollisionBox b) {
        var r = new double[3][3];
        var absR = new double[3][3];
        for (var i = 0; i < 3; i++) {
            for (var j = 0; j < 3; j++) {
                r[i][j] = a.axis(i).dot(b.axis(j));
                absR[i][j] = Math.abs(r[i][j]) + SAT_EPSILON;
            }
        }

        var delta = b.center().subtract(a.center());
        var t = new double[] {
            delta.dot(a.axis(0)),
            delta.dot(a.axis(1)),
            delta.dot(a.axis(2))
        };
        var minOverlap = Double.POSITIVE_INFINITY;

        for (var i = 0; i < 3; i++) {
            var ra = a.half(i);
            var rb = b.half(0) * absR[i][0] + b.half(1) * absR[i][1] + b.half(2) * absR[i][2];
            var overlap = ra + rb - Math.abs(t[i]);
            if (overlap <= 0.0) {
                return -1.0;
            }
            minOverlap = Math.min(minOverlap, overlap);
        }

        for (var j = 0; j < 3; j++) {
            var ra = a.half(0) * absR[0][j] + a.half(1) * absR[1][j] + a.half(2) * absR[2][j];
            var rb = b.half(j);
            var projected = Math.abs(t[0] * r[0][j] + t[1] * r[1][j] + t[2] * r[2][j]);
            var overlap = ra + rb - projected;
            if (overlap <= 0.0) {
                return -1.0;
            }
            minOverlap = Math.min(minOverlap, overlap);
        }

        for (var i = 0; i < 3; i++) {
            var i1 = (i + 1) % 3;
            var i2 = (i + 2) % 3;
            for (var j = 0; j < 3; j++) {
                var axisLength = Math.sqrt(Math.max(0.0, 1.0 - r[i][j] * r[i][j]));
                if (axisLength <= AXIS_EPSILON) {
                    continue;
                }
                var j1 = (j + 1) % 3;
                var j2 = (j + 2) % 3;
                var ra = a.half(i1) * absR[i2][j] + a.half(i2) * absR[i1][j];
                var rb = b.half(j1) * absR[i][j2] + b.half(j2) * absR[i][j1];
                var projected = Math.abs(t[i2] * r[i1][j] - t[i1] * r[i2][j]);
                var overlap = ra + rb - projected;
                if (overlap <= 0.0) {
                    return -1.0;
                }
                minOverlap = Math.min(minOverlap, overlap / axisLength);
            }
        }
        return minOverlap;
    }

    private long fingerprint(ModelerBone root, AnimationEditorState state) {
        var animations = animationKeys(state);
        var hash = 0xcbf29ce484222325L;
        hash = mix(hash, System.identityHashCode(root));
        hash = mix(hash, Double.doubleToLongBits(state.selectedAnimationLengthSeconds()));
        for (var key : animations) {
            hash = mix(hash, key.documentId());
            hash = mix(hash, key.animationName().hashCode());
        }
        return fingerprintBone(hash, root, state, animations);
    }

    private static List<AnimationKey> animationKeys(AnimationEditorState state) {
        var selected = state.selectedAnimationKeys();
        if (!selected.isEmpty()) {
            return selected;
        }
        var documentId = state.selectedDocumentId();
        var animationName = state.selectedAnimationName();
        if (documentId != null && animationName != null && state.animationObject(documentId, animationName) != null) {
            return List.of(new AnimationKey(documentId, animationName));
        }
        return List.of();
    }

    private long fingerprintBone(long hash, ModelerBone bone, AnimationEditorState state, List<AnimationKey> animations) {
        hash = mix(hash, bone.name.hashCode());
        hash = mixVec(hash, bone.position);
        hash = mixVec(hash, bone.rotation);
        hash = mixVec(hash, bone.scale);
        hash = mixVec(hash, bone.pivot);
        for (var cube : bone.cubes) {
            hash = mix(hash, cube.name.hashCode());
            hash = mixVec(hash, cube.origin);
            hash = mixVec(hash, cube.size);
            hash = mixVec(hash, cube.rotation);
            hash = mixVec(hash, cube.pivot);
            hash = mix(hash, Double.doubleToLongBits(cube.inflate));
            hash = mix(hash, cube.blockElementRescale ? 1 : 0);
        }
        for (var animation : animations) {
            hash = mix(hash, animation.documentId());
            hash = mix(hash, animation.animationName().hashCode());
            for (var channel : TransformChannel.values()) {
                for (var frame : state.keyframes(animation.documentId(), animation.animationName(), bone.name, channel)) {
                    hash = mix(hash, channel.ordinal());
                    hash = mix(hash, Double.doubleToLongBits(frame.timestamp()));
                    hash = mix(hash, frame.keyframe().toString().hashCode());
                }
            }
        }
        for (var child : bone.children) {
            hash = fingerprintBone(hash, child, state, animations);
        }
        return hash;
    }

    private static long mixVec(long hash, net.minecraft.world.phys.Vec3 value) {
        hash = mix(hash, Double.doubleToLongBits(value.x));
        hash = mix(hash, Double.doubleToLongBits(value.y));
        return mix(hash, Double.doubleToLongBits(value.z));
    }

    private static long mix(long hash, long value) {
        hash ^= value;
        return hash * 0x100000001b3L;
    }
}
