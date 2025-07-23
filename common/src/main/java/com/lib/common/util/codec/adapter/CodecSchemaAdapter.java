package com.lib.common.util.codec.adapter;

import com.bvanseg.just.functional.result.Result;
import com.bvanseg.just.functional.tuple.Tuple2;
import com.bvanseg.just.serialization.codec.schema.CodecSchema;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DynamicOps;

import java.util.stream.Stream;

public class CodecSchemaAdapter<T> implements CodecSchema<T> {

    private final DynamicOps<T> dynamicOps;

    public CodecSchemaAdapter(DynamicOps<T> dynamicOps) {
        this.dynamicOps = dynamicOps;
    }

    @Override
    public T empty() {
        return dynamicOps.empty();
    }

    @Override
    public Result<Boolean, T> getBooleanValue(T input) {
        return dynamicOps.getBooleanValue(input).mapOrElse(Result::ok, e -> Result.err(input));
    }

    @Override
    public Result<Number, T> getNumberValue(T input) {
        return dynamicOps.getNumberValue(input).mapOrElse(Result::ok, e -> Result.err(input));
    }

    @Override
    public Result<String, T> getStringValue(T input) {
        return dynamicOps.getStringValue(input).mapOrElse(Result::ok, e -> Result.err(input));
    }

    @Override
    public Result<Stream<T>, T> getStream(T input) {
        return dynamicOps.getStream(input).mapOrElse(Result::ok, e -> Result.err(input));
    }

    @Override
    public Result<Stream<Tuple2<T, T>>, T> getMap(T input) {
        return dynamicOps.getMap(input)
            .mapOrElse(
                map -> Result.ok(map.entries().map(pair -> new Tuple2<>(pair.getFirst(), pair.getSecond()))),
                e -> Result.err(input)
            );
    }

    @Override
    public Result<T, T> getField(T map, String key) {
        return dynamicOps.get(map, key).mapOrElse(Result::ok, e -> Result.err(map));
    }

    @Override
    public T createBooleanValue(boolean value) {
        return dynamicOps.createBoolean(value);
    }

    @Override
    public T createNumber(Number value) {
        return dynamicOps.createNumeric(value);
    }

    @Override
    public T createStringValue(String value) {
        return dynamicOps.createString(value);
    }

    @Override
    public T createList(Stream<T> elements) {
        return dynamicOps.createList(elements);
    }

    @Override
    public T createMap(Stream<Tuple2<T, T>> entries) {
        return dynamicOps.createMap(entries.map(tuple2 -> new Pair<>(tuple2.v1(), tuple2.v2())));
    }

    @Override
    public T createField(T map, String key, T value) {
        return dynamicOps.set(map, key, value);
    }
}
