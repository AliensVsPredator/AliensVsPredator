package org.avp.common.game.item.tool;

import org.avp.api.game.item.tool.ToolTier;
import org.avp.common.registry.item.AVPItemRegistry;

public class AVPToolTiers {

    public static final ToolTier ALUMINUM = new ToolTier(200, 5, 1.5F, 1, 10, AVPItemRegistry.INSTANCE.ingotAluminum);

    public static final ToolTier ORIONITE = new ToolTier(1750, 8.5F, 3.5F, 3, 12, AVPItemRegistry.INSTANCE.ingotOrionite);

    public static final ToolTier TITANIUM = new ToolTier(1000, 7, 2.5F, 2, 12, AVPItemRegistry.INSTANCE.ingotTitanium);

    public static final ToolTier VERITANIUM = new ToolTier(3000, 10, 5F, 5, 10, AVPItemRegistry.INSTANCE.veritaniumShard);

    private AVPToolTiers() {
        throw new UnsupportedOperationException();
    }
}
