package com.alien;

import com.alien.common.registry.init.AlienArmorItems;
import com.alien.common.registry.init.AlienArmorMaterials;
import com.alien.common.registry.init.AlienBlockItems;
import com.alien.common.registry.init.AlienBlocks;
import com.alien.common.registry.init.AlienCompostingChances;
import com.alien.common.registry.init.AlienGameEvents;
import com.alien.common.registry.init.AlienInfections;
import com.alien.common.registry.init.AlienItems;
import com.alien.common.registry.init.AlienLifecycles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.avp.AVP;

public class Alien {

    public static final Logger LOGGER = LoggerFactory.getLogger(AVP.MOD_ID);

    public static void initialize() {
        AlienBlocks.initialize();
        AlienItems.initialize();
        AlienArmorMaterials.initialize();
        AlienArmorItems.initialize();
        AlienBlockItems.initialize();
        AlienGameEvents.initialize();

        // Custom
        AlienInfections.initialize();
        AlienLifecycles.initialize();

        // Functionality
        AlienCompostingChances.initialize();
    }
}
