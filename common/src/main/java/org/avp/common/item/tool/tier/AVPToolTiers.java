package org.avp.common.item.tool.tier;

import org.avp.common.item.AVPItems;

public class AVPToolTiers {

    public static final AVPToolTier ALUMINUM = new AVPToolTier(200, 5.0F, 1.5F, 1, 10, AVPItems.INSTANCE.ingotAluminum);

    public static final AVPToolTier ORIONITE = new AVPToolTier(1750, 8.5F, 3.5F, 3, 12, AVPItems.INSTANCE.ingotOrionite);

    public static final AVPToolTier TITANIUM = new AVPToolTier(1000, 7.0F, 2.5F, 2, 12, AVPItems.INSTANCE.ingotTitanium);

    public static final AVPToolTier VERITANIUM = new AVPToolTier(3000, 10.0F, 5F, 5, 10, AVPItems.INSTANCE.veritaniumShard);

    private AVPToolTiers() {
        throw new UnsupportedOperationException();
    }
}
