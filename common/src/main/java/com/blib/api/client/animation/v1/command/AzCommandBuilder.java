package com.blib.api.client.animation.v1.command;

import java.util.ArrayList;
import java.util.List;

import com.blib.api.client.animation.v1.command.policy.AzDispatchMode;
import com.blib.api.client.animation.v1.command.policy.AzDispatchPolicy;
import com.blib.api.client.animation.v1.command.policy.OnBlockedByEndless;
import com.blib.api.client.animation.v1.command.policy.OnPropertiesChanged;
import com.blib.internal.client.animation.dispatch.command.action.AzAction;

public abstract class AzCommandBuilder {

    protected final List<AzAction> actions;

    protected AzDispatchMode dispatchMode;

    protected OnBlockedByEndless onBlockedByEndless;

    protected OnPropertiesChanged onPropertiesChanged;

    protected AzCommandBuilder() {
        this.actions = new ArrayList<>();
        this.dispatchMode = null;
        this.onBlockedByEndless = OnBlockedByEndless.APPEND_ANYWAY;
        this.onPropertiesChanged = OnPropertiesChanged.RESTART;
    }

    /**
     * Materializes the {@link AzDispatchPolicy} for play actions added at this point in the builder
     * chain. Throws if no dispatch mode has been set — every play action must declare its dispatch
     * intent. Use {@link AzCommand#replay()}, {@link AzCommand#idempotent()},
     * {@link AzCommand#enqueueing()}, or call {@code dispatchMode(...)} on the builder.
     */
    protected AzDispatchPolicy currentPolicy() {
        if (dispatchMode == null) {
            throw new IllegalStateException(
                "No dispatch mode set on this command builder. Call dispatchMode(...) before adding a "
                    + "play action, or start the builder via AzCommand.replay() / .idempotent() / "
                    + ".enqueueing()."
            );
        }

        return new AzDispatchPolicy(dispatchMode, onBlockedByEndless, onPropertiesChanged);
    }

    public AzCommand build() {
        return new AzCommand(actions);
    }
}
