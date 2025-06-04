package com.human;

import com.human.common.registry.init.HumanDataComponents;
import com.human.common.registry.init.HumanMenuTypes;
import com.human.common.registry.init.block.HumanFerroaluminumBlocks;
import com.human.common.registry.init.block.HumanIndustrialConcreteBlocks;
import com.human.common.registry.init.block.HumanIndustrialGlassBlocks;
import com.human.common.registry.init.block.HumanPaddingBlocks;
import com.human.common.registry.init.block.HumanPlasticBlocks;
import com.human.common.registry.init.block.HumanSteelBlocks;
import com.human.common.registry.init.block.HumanTitaniumBlocks;
import com.human.common.registry.init.item.HumanFerroaluminumBlockItems;
import com.human.common.registry.init.item.HumanGunItems;
import com.human.common.registry.init.item.HumanIndustrialConcreteBlockItems;
import com.human.common.registry.init.item.HumanIndustrialGlassBlockItems;
import com.human.common.registry.init.item.HumanPaddingBlockItems;
import com.human.common.registry.init.item.HumanPlasticBlockItems;
import com.human.common.registry.init.item.HumanSpawnEggItems;
import com.human.common.registry.init.item.HumanSteelBlockItems;
import com.human.common.registry.init.item.HumanTitaniumBlockItems;

import com.avp.common.registry.init.entity_type.HumanEntityTypes;

public class Human {

    public static void initialize() {
        // Blocks
        HumanFerroaluminumBlocks.initialize();
        HumanIndustrialConcreteBlocks.initialize();
        HumanIndustrialGlassBlocks.initialize();
        HumanPaddingBlocks.initialize();
        HumanPlasticBlocks.initialize();
        HumanSteelBlocks.initialize();
        HumanTitaniumBlocks.initialize();

        // Items
        HumanFerroaluminumBlockItems.initialize();
        HumanGunItems.initialize();
        HumanIndustrialConcreteBlockItems.initialize();
        HumanIndustrialGlassBlockItems.initialize();
        HumanPaddingBlockItems.initialize();
        HumanPlasticBlockItems.initialize();
        HumanSpawnEggItems.initialize();
        HumanSteelBlockItems.initialize();
        HumanTitaniumBlockItems.initialize();

        HumanEntityTypes.initialize();
        HumanMenuTypes.initialize();
        HumanDataComponents.initialize();
    }
}
