package com.blib.engine.session;

import org.jetbrains.annotations.ApiStatus;

/**
 * Active selection tool in the engine viewport. {@link #INSPECT} treats a viewport LMB-click as "pick a single object
 * to inspect" (entity, jigsaw, generic block) and only promotes a drag past the click-vs-drag threshold (or a
 * Shift-held press) into a block-volume marquee. {@link #MARQUEE} treats every LMB-click as a marquee corner-anchor and
 * never inspects single blocks.
 * <p>
 * Stored in {@link SelectionToolState}. Defaults to {@link #INSPECT} on engine entry and resets on engine exit.
 */
@ApiStatus.Internal
public enum SelectionTool {

    INSPECT,
    MARQUEE
}
