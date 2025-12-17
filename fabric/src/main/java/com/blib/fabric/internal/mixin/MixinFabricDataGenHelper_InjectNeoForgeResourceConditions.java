package com.blib.fabric.internal.mixin;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.impl.datagen.FabricDataGenHelper;
import net.fabricmc.fabric.impl.resource.conditions.conditions.AllModsLoadedResourceCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FabricDataGenHelper.class)
public abstract class MixinFabricDataGenHelper_InjectNeoForgeResourceConditions {

    @Inject(
        method = "addConditions(Lcom/google/gson/JsonObject;[Lnet/fabricmc/fabric/api/resource/conditions/v1/ResourceCondition;)V",
        at = @At("TAIL"), remap = false
    )
    private static void blib$addConditions(JsonObject baseObject, ResourceCondition[] conditions, CallbackInfo callbackInfo) {
        if (conditions.length == 0) {
            return;
        }

        var jsonArray = new JsonArray();

        for (var condition : conditions) {
            if (condition instanceof AllModsLoadedResourceCondition(var modIds)) {
                var elements = modIds
                    .stream()
                    .map(modId -> {
                        var neoforgeCondition = new JsonObject();

                        neoforgeCondition.addProperty("type", "neoforge:mod_loaded");
                        neoforgeCondition.addProperty("modid", modId);

                        return neoforgeCondition;
                    })
                    .toList();

                elements.forEach(jsonArray::add);
            }
        }

        baseObject.add("neoforge:conditions", jsonArray);
    }
}
