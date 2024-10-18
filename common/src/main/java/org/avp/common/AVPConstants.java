package org.avp.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AVPConstants {

    public static final String MOD_ID = "avp";

    public static final String MOD_NAME = "AliensVsPredator";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    private AVPConstants() {
        throw new UnsupportedOperationException();
    }
}
