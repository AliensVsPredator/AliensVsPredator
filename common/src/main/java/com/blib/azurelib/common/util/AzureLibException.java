package com.blib.azurelib.common.util;

import net.minecraft.resources.ResourceLocation;

public class AzureLibException extends RuntimeException {

    public AzureLibException(ResourceLocation fileLocation, String message) {
        super(fileLocation + ": " + message);
    }

    public AzureLibException(String message, Throwable cause) {
        super(message, cause);
    }

    public AzureLibException(String message) {
        super(message);
    }

    public AzureLibException(Throwable cause) {
        super(cause);
    }
}
