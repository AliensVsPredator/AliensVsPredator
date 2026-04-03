package com.blib.api.common.pathfinding.v1.navigator;

/**
 * Interface for entities that use BLib's custom pathfinding system.
 * Entities implementing this interface provide access to their {@link PathNavigator},
 * enabling GOAP actions and other systems to use BLib pathfinding instead of vanilla navigation.
 */
public interface PathNavigatorUser {

    PathNavigator getPathNavigator();
}
