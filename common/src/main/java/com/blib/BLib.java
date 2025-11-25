package com.blib;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BLib {

    public static final Logger LOGGER = LoggerFactory.getLogger(BLib.class);

    public static BLibMod createMod(String modId) {
        return new BLibMod(modId);
    }
}
