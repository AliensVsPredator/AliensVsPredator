package org.avp.client.model.entity;

import org.avp.client.model.entity.living.AVPGeoModel;
import org.avp.client.model.entity.living.GeoModelType;
import org.avp.common.entity.Rocket;

public class RocketModel extends AVPGeoModel<Rocket> {
    public RocketModel() {
        super("rocket", GeoModelType.ENTITY);
    }
}
