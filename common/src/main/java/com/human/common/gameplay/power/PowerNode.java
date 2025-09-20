package com.human.common.gameplay.power;

public sealed interface PowerNode {

    non-sealed interface PowerConsumer extends PowerNode {

        /**
         * Returns how much power this consumer wants to receive this tick.
         */
        long getRequestedPower();

        /**
         * Called by the power grid to offer power to this consumer.
         *
         * @param maxAmount the maximum amount offered
         * @return the amount actually accepted
         */
        long receivePower(long maxAmount);
    }

    non-sealed interface PowerProducer extends PowerNode {

        /**
         * Returns how much power this producer is willing to provide this tick.
         */
        long getAvailablePower();

        /**
         * Called by the power grid to extract power from this producer.
         *
         * @param maxAmount the maximum amount to extract
         * @return the actual amount extracted
         */
        long extractPower(long maxAmount);
    }

    non-sealed interface PowerStore extends PowerNode, PowerConsumer, PowerProducer {

        long getStoredPower();
    }
}
