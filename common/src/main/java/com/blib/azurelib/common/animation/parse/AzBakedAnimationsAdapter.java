package com.blib.azurelib.common.animation.parse;

import com.google.gson.*;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.apache.commons.lang3.math.NumberUtils;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.blib.azurelib.AzureLib;
import com.blib.azurelib.common.animation.controller.keyframe.AzBoneAnimation;
import com.blib.azurelib.common.animation.controller.keyframe.AzKeyframe;
import com.blib.azurelib.common.animation.controller.keyframe.AzKeyframeStack;
import com.blib.azurelib.common.animation.easing.AzEasingType;
import com.blib.azurelib.common.animation.easing.AzEasingTypeLoader;
import com.blib.azurelib.common.animation.easing.AzEasingTypes;
import com.blib.azurelib.common.animation.primitive.AzBakedAnimation;
import com.blib.azurelib.common.animation.primitive.AzBakedAnimations;
import com.blib.azurelib.common.animation.primitive.AzKeyframes;
import com.blib.azurelib.common.animation.primitive.AzLoopType;
import com.blib.azurelib.common.util.JsonUtil;
import com.blib.azurelib.core.math.Constant;
import com.blib.azurelib.core.math.IValue;
import com.blib.azurelib.core.molang.MolangException;
import com.blib.azurelib.core.molang.MolangParser;
import com.blib.azurelib.core.molang.expressions.MolangValue;

public class AzBakedAnimationsAdapter implements JsonDeserializer<AzBakedAnimations> {

    private static List<Pair<String, JsonElement>> getKeyframes(JsonElement element) {
        if (element == null)
            return List.of();

        if (element instanceof JsonPrimitive primitive) {
            JsonArray array = new JsonArray(3);

            array.add(primitive);
            array.add(primitive);
            array.add(primitive);

            element = array;
        }

        if (element instanceof JsonArray array)
            return ObjectArrayList.of(Pair.of("0", array));

        if (element instanceof JsonObject obj) {
            if (obj.has("vector"))
                return ObjectArrayList.of(Pair.of("0", obj));

            List<Pair<String, JsonElement>> list = new ObjectArrayList<>();

            for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                double timestamp = readTimestamp(entry.getKey());

                if (timestamp == 0 && !list.isEmpty())
                    throw new JsonParseException(
                        "Invalid keyframe data - multiple starting keyframes?" + entry.getKey()
                    );

                if (entry.getValue() instanceof JsonObject entryObj && !entryObj.has("vector")) {
                    addBedrockKeyframes(timestamp, entryObj, list);

                    continue;
                }

                list.add(Pair.of(String.valueOf(timestamp), entry.getValue()));
            }

            return list;
        }

        throw new JsonParseException("Invalid object type provided to getTripletObj, got: " + element);
    }

    private static void addBedrockKeyframes(
        double timestamp,
        JsonObject keyframe,
        List<Pair<String, JsonElement>> keyframes
    ) {
        boolean addedFrame = false;

        if (keyframe.has("pre")) {
            JsonElement pre = keyframe.get("pre");
            addedFrame = true;

            keyframes.add(
                Pair.of(
                    String.valueOf(timestamp == 0 ? timestamp : timestamp - 0.001d),
                    pre.isJsonArray()
                        ? pre.getAsJsonArray()
                        : GsonHelper.getAsJsonArray(pre.getAsJsonObject(), "vector")
                )
            );
        }

        if (keyframe.has("post")) {
            JsonElement post = keyframe.get("post");
            JsonArray values = post.isJsonArray()
                ? post.getAsJsonArray()
                : GsonHelper.getAsJsonArray(post.getAsJsonObject(), "vector");

            if (keyframe.has("lerp_mode")) {
                var keyframeObj = new JsonObject();

                keyframeObj.add("vector", values);
                keyframeObj.add("easing", keyframe.get("lerp_mode"));

                keyframes.add(Pair.of(String.valueOf(timestamp), keyframeObj));
            } else {
                keyframes.add(Pair.of(String.valueOf(timestamp), values));
            }

            return;
        }

        if (!addedFrame)
            throw new JsonParseException("Invalid keyframe data - expected array, found " + keyframe);
    }

    private static double calculateAnimationLength(AzBoneAnimation[] boneAnimations) {
        double length = 0;

        for (var animation : boneAnimations) {
            length = Math.max(length, animation.rotationKeyframes().getLastKeyframeTime());
            length = Math.max(length, animation.positionKeyframes().getLastKeyframeTime());
            length = Math.max(length, animation.scaleKeyframes().getLastKeyframeTime());
        }

        return length == 0 ? Double.MAX_VALUE : length;
    }

    @Override
    public AzBakedAnimations deserialize(
        JsonElement json,
        Type type,
        JsonDeserializationContext context
    ) throws JsonParseException {
        JsonObject jsonObj = json.getAsJsonObject();

        JsonObject animationJsonList = jsonObj.getAsJsonObject("animations");
        JsonArray includeListJSONObj = jsonObj.getAsJsonArray("includes");
        Map<String, ResourceLocation> includes = null;
        if (includeListJSONObj != null) {
            includes = new Object2ObjectOpenHashMap<>(includeListJSONObj.size());
            for (JsonElement entry : includeListJSONObj.asList()) {
                JsonObject obj = entry.getAsJsonObject();
                ResourceLocation fileId = ResourceLocation.parse(obj.get("file_id").getAsString());
                for (JsonElement animName : obj.getAsJsonArray("animations")) {
                    String ani = animName.getAsString();
                    if (includes.containsKey(ani)) {
                        AzureLib.LOGGER.warn(
                            "Animation {} is already included! File already including: {}  File trying to include from again: {}",
                            ani,
                            includes.get(ani),
                            fileId
                        );
                    } else {
                        includes.put(ani, fileId);
                    }
                }
            }
        }

        Map<String, AzBakedAnimation> animations = new Object2ObjectOpenHashMap<>(animationJsonList.size());

        for (Map.Entry<String, JsonElement> entry : animationJsonList.entrySet()) {
            try {
                animations.put(
                    entry.getKey(),
                    bakeAnimation(entry.getKey(), entry.getValue().getAsJsonObject(), context)
                );
            } catch (MolangException ex) {
                AzureLib.LOGGER.error("Unable to parse animation: {}", entry.getKey());
                ex.printStackTrace();
            }
        }

        return new AzBakedAnimations(animations, includes);
    }

    private AzBakedAnimation bakeAnimation(
        String name,
        JsonObject animationObj,
        JsonDeserializationContext context
    ) throws MolangException {
        double length = animationObj.has("animation_length")
            ? GsonHelper.getAsDouble(animationObj, "animation_length") * 20d
            : -1;
        AzLoopType loopType = AzLoopType.fromJson(animationObj.get("loop"));
        AzBoneAnimation[] boneAnimations = bakeBoneAnimations(
            GsonHelper.getAsJsonObject(animationObj, "bones", new JsonObject())
        );
        AzKeyframes keyframes = context.deserialize(animationObj, AzKeyframes.class);

        if (length == -1)
            length = calculateAnimationLength(boneAnimations);

        return new AzBakedAnimation(name, length, loopType, boneAnimations, keyframes);
    }

    private AzBoneAnimation[] bakeBoneAnimations(JsonObject bonesObj) throws MolangException {
        AzBoneAnimation[] animations = new AzBoneAnimation[bonesObj.size()];
        int index = 0;

        for (Map.Entry<String, JsonElement> entry : bonesObj.entrySet()) {
            JsonObject entryObj = entry.getValue().getAsJsonObject();
            AzKeyframeStack<AzKeyframe<IValue>> scaleFrames = buildKeyframeStack(
                getKeyframes(entryObj.get("scale")),
                false
            );
            AzKeyframeStack<AzKeyframe<IValue>> positionFrames = buildKeyframeStack(
                getKeyframes(entryObj.get("position")),
                false
            );
            AzKeyframeStack<AzKeyframe<IValue>> rotationFrames = buildKeyframeStack(
                getKeyframes(entryObj.get("rotation")),
                true
            );

            animations[index] = new AzBoneAnimation(entry.getKey(), rotationFrames, positionFrames, scaleFrames);
            index++;
        }

        return animations;
    }

    private AzKeyframeStack<AzKeyframe<IValue>> buildKeyframeStack(
        List<Pair<String, JsonElement>> entries,
        boolean isForRotation
    ) {
        if (entries.isEmpty())
            return new AzKeyframeStack<>();

        List<AzKeyframe<IValue>> xFrames = new ObjectArrayList<>();
        List<AzKeyframe<IValue>> yFrames = new ObjectArrayList<>();
        List<AzKeyframe<IValue>> zFrames = new ObjectArrayList<>();

        IValue xPrev = null;
        IValue yPrev = null;
        IValue zPrev = null;
        Pair<String, JsonElement> prevEntry = null;

        for (Pair<String, JsonElement> entry : entries) {
            String key = entry.getFirst();
            JsonElement element = entry.getSecond();

            if (key.equals("easing") || key.equals("easingArgs") || key.equals("lerp_mode"))
                continue;

            double prevTime = prevEntry != null ? Double.parseDouble(prevEntry.getFirst()) : 0;
            double curTime = NumberUtils.isCreatable(key) ? Double.parseDouble(entry.getFirst()) : 0;
            double timeDelta = curTime - prevTime;

            JsonArray keyframeVector = element instanceof JsonArray array
                ? array
                : GsonHelper.getAsJsonArray(element.getAsJsonObject(), "vector");
            MolangValue rawXValue = MolangParser.parseJson(keyframeVector.get(0));
            MolangValue rawYValue = MolangParser.parseJson(keyframeVector.get(1));
            MolangValue rawZValue = MolangParser.parseJson(keyframeVector.get(2));
            IValue xValue = isForRotation && rawXValue.isConstant()
                ? new Constant(Math.toRadians(-rawXValue.get()))
                : rawXValue;
            IValue yValue = isForRotation && rawYValue.isConstant()
                ? new Constant(Math.toRadians(-rawYValue.get()))
                : rawYValue;
            IValue zValue = isForRotation && rawZValue.isConstant()
                ? new Constant(Math.toRadians(rawZValue.get()))
                : rawZValue;

            JsonObject entryObj = element instanceof JsonObject obj ? obj : null;
            AzEasingType easingType = entryObj != null && entryObj.has("easing")
                ? AzEasingTypeLoader.fromJson(entryObj.get("easing"))
                : AzEasingTypes.LINEAR;
            List<IValue> easingArgs = entryObj != null && entryObj.has("easingArgs")
                ? JsonUtil.jsonArrayToList(
                    GsonHelper.getAsJsonArray(entryObj, "easingArgs"),
                    ele -> new Constant(ele.getAsDouble())
                )
                : new ObjectArrayList<>();

            xFrames.add(
                new AzKeyframe<>(timeDelta * 20, prevEntry == null ? xValue : xPrev, xValue, easingType, easingArgs)
            );
            yFrames.add(
                new AzKeyframe<>(timeDelta * 20, prevEntry == null ? yValue : yPrev, yValue, easingType, easingArgs)
            );
            zFrames.add(
                new AzKeyframe<>(timeDelta * 20, prevEntry == null ? zValue : zPrev, zValue, easingType, easingArgs)
            );

            xPrev = xValue;
            yPrev = yValue;
            zPrev = zValue;
            prevEntry = entry;
        }

        return new AzKeyframeStack<>(xFrames, yFrames, zFrames);
    }

    private static double readTimestamp(String timestamp) {
        return NumberUtils.isCreatable(timestamp) ? Double.parseDouble(timestamp) : 0;
    }
}
