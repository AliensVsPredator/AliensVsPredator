package com.blib.engine.ui.modal;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Z-ordered registry of currently-open {@link ModalDialog}s. Push/pop is explicit (driven by
 * {@link ModalDialog#open}/{@link ModalDialog#close}) so there is no per-frame supplier-polling reconciliation to keep
 * in sync. The workspace screen consults {@link #top} and {@link #order} when dispatching input + render.
 * <p>
 * This is a global singleton rather than a workspace-screen field because dialogs are constructed in many places
 * (menus, panels, dialogs that spawn child dialogs) and threading a manager reference through each construction site
 * would be busier than the modest cost of a static.
 */
@ApiStatus.Internal
public final class ModalManager {

    private static final List<ModalDialog> STACK = new ArrayList<>();

    private ModalManager() {}

    static synchronized void push(ModalDialog dialog) {
        STACK.remove(dialog);
        STACK.add(dialog);
    }

    static synchronized void pop(ModalDialog dialog) {
        STACK.remove(dialog);
    }

    public static synchronized boolean isAnyOpen() {
        return !STACK.isEmpty();
    }

    public static synchronized @Nullable ModalDialog top() {
        return STACK.isEmpty() ? null : STACK.get(STACK.size() - 1);
    }

    public static synchronized List<ModalDialog> order() {
        return Collections.unmodifiableList(new ArrayList<>(STACK));
    }
}
