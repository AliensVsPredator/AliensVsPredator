package com.avp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.avp.common.item.TempAVPItems;
import com.avp.service.Services;

public class AVP {

    public static final String MOD_ID = "avp";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void initialize() {
        LOGGER.info("Initializing AVP for platform '{}'", Services.PLATFORM.getPlatformName());

        TempAVPItems.initialize();
    }
}
