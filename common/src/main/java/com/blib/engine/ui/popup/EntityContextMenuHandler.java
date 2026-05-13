package com.blib.engine.ui.popup;

import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.ui.panel.viewport.ViewportPanel;

/**
 * Open the engine's entity context menu (View GOAP Details / Delete Entity / etc.) at the supplied screen-logical
 * coordinates for {@code entity}. Implemented by {@code EngineWorkspaceScreen} and forwarded through
 * {@link com.blib.engine.layout.PanelRegistry.Context} so non-viewport panels (currently the outliner) can request the
 * same menu the viewport's right-click produces, without reaching back into the screen instance directly.
 * <p>
 * Same shape as {@link ViewportPanel.RightClickHandler#onRightClick} for the entity case — both call sites converge on
 * the screen's single {@code onViewportRightClick} method, so the menu surface stays in lock-step.
 */
@ApiStatus.Internal
@FunctionalInterface
public interface EntityContextMenuHandler {

    void onEntityRightClick(LivingEntity entity, double mouseX, double mouseY);
}
