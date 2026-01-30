package com.blib.internal.common.io.util;

import com.google.gson.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.blib.azurelib.common.loading.json.raw.*;
import com.blib.internal.client.animation.parse.AzBakedAnimationsJsonDeserializer;
import com.blib.internal.client.animation.parse.AzKeyframesJsonDeserializer;
import com.blib.internal.client.animation.primitive.AzBakedAnimations;
import com.blib.internal.client.animation.primitive.AzKeyframes;
import com.blib.internal.client.model.Bone;
import com.blib.internal.client.model.Cube;
import com.blib.internal.client.model.FaceUV;
import com.blib.internal.client.model.LocatorClass;
import com.blib.internal.client.model.LocatorValue;
import com.blib.internal.client.model.MinecraftGeometry;
import com.blib.internal.client.model.Model;
import com.blib.internal.client.model.ModelProperties;
import com.blib.internal.client.model.PolyMesh;
import com.blib.internal.client.model.PolysUnion;
import com.blib.internal.client.model.TextureMesh;
import com.blib.internal.client.model.UVFaces;
import com.blib.internal.client.model.UVUnion;

public record JsonUtil() {

    public static final Gson GEO_GSON = new GsonBuilder().setLenient()
        .registerTypeAdapter(
            Bone.class,
            Bone.deserializer()
        )
        .registerTypeAdapter(
            Cube.class,
            Cube.deserializer()
        )
        .registerTypeAdapter(
            FaceUV.class,
            FaceUV.deserializer()
        )
        .registerTypeAdapter(
            LocatorClass.class,
            LocatorClass.deserializer()
        )
        .registerTypeAdapter(
            LocatorValue.class,
            LocatorValue.deserializer()
        )
        .registerTypeAdapter(
            MinecraftGeometry.class,
            MinecraftGeometry.deserializer()
        )
        .registerTypeAdapter(
            Model.class,
            Model.deserializer()
        )
        .registerTypeAdapter(
            ModelProperties.class,
            ModelProperties.deserializer()
        )
        .registerTypeAdapter(
            PolyMesh.class,
            PolyMesh.deserializer()
        )
        .registerTypeAdapter(
            PolysUnion.class,
            PolysUnion.deserializer()
        )
        .registerTypeAdapter(
            TextureMesh.class,
            TextureMesh.deserializer()
        )
        .registerTypeAdapter(
            UVFaces.class,
            UVFaces.deserializer()
        )
        .registerTypeAdapter(UVUnion.class, UVUnion.deserializer())
        .registerTypeAdapter(AzKeyframes.class, new AzKeyframesJsonDeserializer())
        .registerTypeAdapter(AzBakedAnimations.class, new AzBakedAnimationsJsonDeserializer())
        .create();

    public static double[] jsonArrayToDoubleArray(@Nullable JsonArray array) throws JsonParseException {
        if (array == null)
            return new double[3];

        double[] output = new double[array.size()];

        for (int i = 0; i < array.size(); i++) {
            output[i] = array.get(i).getAsDouble();
        }

        return output;
    }

    public static <T> T[] jsonArrayToObjectArray(
        JsonArray array,
        JsonDeserializationContext context,
        Class<T> objectClass
    ) {
        T[] objArray = (T[]) Array.newInstance(objectClass, array.size());

        for (int i = 0; i < array.size(); i++) {
            objArray[i] = context.deserialize(array.get(i), objectClass);
        }

        return objArray;
    }

    public static <T> List<T> jsonArrayToList(@Nullable JsonArray array, Function<JsonElement, T> elementTransformer) {
        if (array == null)
            return new ObjectArrayList<>();

        List<T> list = new ObjectArrayList<>(array.size());

        for (JsonElement element : array) {
            list.add(elementTransformer.apply(element));
        }

        return list;
    }

    public static <T> Map<String, T> jsonObjToMap(
        JsonObject obj,
        JsonDeserializationContext context,
        Class<T> objectType
    ) {
        Map<String, T> map = new Object2ObjectOpenHashMap<>(obj.size());

        for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
            map.put(entry.getKey(), context.deserialize(entry.getValue(), objectType));
        }

        return map;
    }

    @Nullable
    public static Long getOptionalLong(JsonObject obj, String elementName) {
        return obj.has(elementName) ? GsonHelper.getAsLong(obj, elementName) : null;
    }

    @Nullable
    public static Boolean getOptionalBoolean(JsonObject obj, String elementName) {
        return obj.has(elementName) ? GsonHelper.getAsBoolean(obj, elementName) : null;
    }

    @Nullable
    public static Float getOptionalFloat(JsonObject obj, String elementName) {
        return obj.has(elementName) ? GsonHelper.getAsFloat(obj, elementName) : null;
    }

    @Nullable
    public static Double getOptionalDouble(JsonObject obj, String elementName) {
        return obj.has(elementName) ? GsonHelper.getAsDouble(obj, elementName) : null;
    }

    @Nullable
    public static Integer getOptionalInteger(JsonObject obj, String elementName) {
        return obj.has(elementName) ? GsonHelper.getAsInt(obj, elementName) : null;
    }
}
