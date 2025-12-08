package com.blib.common.util;

import mod.azure.azurelib.common.animation.dispatch.command.AzCommand;
import mod.azure.azurelib.common.animation.play_behavior.AzPlayBehavior;
import mod.azure.azurelib.common.animation.play_behavior.AzPlayBehaviors;

import java.util.List;

public class AzAnimationUtil {

    public static AzCommand compose(List<String> limbNames, String baseName) {
        return compose(limbNames, baseName, AzPlayBehaviors.LOOP);
    }

    public static AzCommand compose(List<String> limbNames, String baseName, AzPlayBehavior playBehavior) {
        return AzCommand.compose(
            limbNames.stream()
                .map(limbName -> AzCommand.create(limbName, baseName + "." + limbName, playBehavior))
                .toList()
        );
    }
}
