package com.blib.api.common.codec.v1;

import com.just.core.functional.result.Result;
import com.mojang.serialization.DataResult;

import java.util.function.Supplier;

public class DataResultUtil {

    public static <T> Result<T, DataResult.Error<T>> mojangToJust(DataResult<T> dataResult) {
        return dataResult.isSuccess()
            ? Result.ok(dataResult.result().get())
            : Result.err(dataResult.error().get());
    }

    public static <T> DataResult<T> justToMojang(Result<T, ?> dataResult, Supplier<String> errorSupplier) {
        return dataResult.isOk()
            ? DataResult.success(dataResult.unwrap())
            : DataResult.error(errorSupplier);
    }
}
