package org.avp.client.model.entity;

import org.avp.client.model.AVPGeoModel;
import org.avp.client.model.GeoModelType;
import org.avp.common.game.entity.Rocket;

public class RocketModel extends AVPGeoModel<Rocket> {
    public RocketModel() {
        super("rocket", GeoModelType.ENTITY);
    }
}
