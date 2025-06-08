package com.predator;

import com.predator.common.registry.init.PredatorArmorMaterials;
import com.predator.common.registry.init.PredatorBlockItems;
import com.predator.common.registry.init.PredatorBlocks;
import com.predator.common.registry.init.PredatorEntityTypes;
import com.predator.common.registry.init.item.PredatorArmorItems;
import com.predator.common.registry.init.item.PredatorSpawnEggItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.avp.AVP;

public class Predator {

    public static final Logger LOGGER = LoggerFactory.getLogger(AVP.MOD_ID);

    public static void initialize() {
        PredatorBlocks.initialize();
        PredatorBlockItems.initialize();
        PredatorArmorMaterials.initialize();
        PredatorArmorItems.initialize();
        PredatorSpawnEggItems.initialize();
        PredatorEntityTypes.initialize();
    }
}
