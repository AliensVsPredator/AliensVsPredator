package com.blib.azurelib.common.platform.services;

import java.nio.file.Path;

public interface IPlatformHelper {

    String getPlatformName();

    boolean isDevelopmentEnvironment();

    boolean isModLoaded(String modId);

    Path getGameDir();

    boolean isServerEnvironment();

    boolean isEnvironmentClient();
}
