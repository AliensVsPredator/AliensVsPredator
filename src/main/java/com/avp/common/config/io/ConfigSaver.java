package com.avp.common.config.io;

import java.io.IOException;
import java.nio.file.Files;

import com.avp.common.config.Config;
import com.avp.common.config.template.ConfigTemplate;

public class ConfigSaver {

    public static void save(Config config, ConfigTemplate template) {
        try {
            // Write all lines to the file, replacing any existing content
            var path = ConfigIOUtil.resolveConfigPath(config.name());
            var lines = template.generateLinesForConfig(config);

            // Ensure the parent directories exist.
            var parentDir = path.getParent();

            if (parentDir != null) {
                Files.createDirectories(parentDir);
            }

            Files.write(path, lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
