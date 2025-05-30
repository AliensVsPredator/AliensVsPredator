package com.predator;

import com.predator.common.registry.init.PredatorEntityTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.avp.AVP;

public class Predator {

    public static final Logger LOGGER = LoggerFactory.getLogger(AVP.MOD_ID);

    public static void initialize() {
        PredatorEntityTypes.initialize();
    }
}
