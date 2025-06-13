package com.alien;

import com.alien.common.registry.init.AlienArmorMaterials;
import com.alien.common.registry.init.AlienBlocks;
import com.alien.common.registry.init.AlienCompostingChances;
import com.alien.common.registry.init.AlienEntityTypes;
import com.alien.common.registry.init.AlienGameEvents;
import com.alien.common.registry.init.AlienInfections;
import com.alien.common.registry.init.AlienItems;
import com.alien.common.registry.init.block.AlienChitinBlocks;
import com.alien.common.registry.init.block.AlienResinBlocks;
import com.alien.common.registry.init.item.AlienArmorItems;
import com.alien.common.registry.init.item.AlienChitinBlockItems;
import com.alien.common.registry.init.item.AlienResinBlockItems;
import com.alien.common.registry.init.item.AlienSpawnEggItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.avp.AVP;
import com.avp.common.registry.init.AlienParticleTypes;

public class Alien {

    public static final Logger LOGGER = LoggerFactory.getLogger(AVP.MOD_ID);

    public static void initialize() {
        // Blocks
        AlienBlocks.initialize();
        AlienChitinBlocks.initialize();
        AlienResinBlocks.initialize();

        // Items
        AlienItems.initialize();
        AlienArmorMaterials.initialize();
        AlienArmorItems.initialize();
        AlienChitinBlockItems.initialize();
        AlienResinBlockItems.initialize();
        AlienSpawnEggItems.initialize();
        AlienEntityTypes.initialize();
        AlienParticleTypes.initialize();
        AlienGameEvents.initialize();

        // Custom
        AlienInfections.initialize();

        // Functionality
        AlienCompostingChances.initialize();
    }
}
