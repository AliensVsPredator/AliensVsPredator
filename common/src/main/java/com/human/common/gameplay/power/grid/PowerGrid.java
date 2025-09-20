package com.human.common.gameplay.power.grid;

import com.human.common.gameplay.power.PowerNode;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class PowerGrid {

    private final Set<PowerNode.PowerConsumer> consumers;

    private final Set<PowerNode.PowerProducer> producers;

    private final Set<PowerNode.PowerStore> stores;

    public PowerGrid() {
        this.consumers = new HashSet<>();
        this.producers = new HashSet<>();
        this.stores = new HashSet<>();
    }

    /**
     * Registers a power node into the grid.
     */
    public void add(PowerNode node) {
        switch (node) {
            case PowerNode.PowerStore powerStore -> stores.add(powerStore);
            case PowerNode.PowerConsumer powerConsumer -> consumers.add(powerConsumer);
            case PowerNode.PowerProducer powerProducer -> producers.add(powerProducer);
        }
    }

    /**
     * Unregisters a power node from the grid.
     */
    public void remove(PowerNode node) {
        switch (node) {
            case PowerNode.PowerStore powerStore -> stores.remove(powerStore);
            case PowerNode.PowerConsumer powerConsumer -> consumers.remove(powerConsumer);
            case PowerNode.PowerProducer powerProducer -> producers.remove(powerProducer);
        }
    }

    /**
     * Called once per tick to manage energy flow.
     */
    public void tick() {
        long availableFromProducers = producers.stream()
            .mapToLong(PowerNode.PowerProducer::getAvailablePower)
            .sum();

        if (availableFromProducers == 0 && stores.stream().mapToLong(PowerNode.PowerStore::getStoredPower).sum() == 0) {
            // Nothing to offer from producers or stores.
            return;
        }

        long requestedByConsumers = consumers.stream()
            .mapToLong(PowerNode.PowerConsumer::getRequestedPower)
            .sum();

        // Handle full or partial consumer powering.
        boolean consumersFullyPowered = false;

        if (requestedByConsumers > 0) {
            if (availableFromProducers >= requestedByConsumers) {
                consumersFullyPowered = true;

                // Fully powered — satisfy all consumers.
                for (var consumer : consumers) {
                    var requested = consumer.getRequestedPower();
                    consumer.receivePower(requested);
                }

                // Extract full supply from producers.
                for (var producer : producers) {
                    var offered = producer.getAvailablePower();
                    producer.extractPower(offered);
                }

            } else {
                // Try supplementing with battery power
                var requiredExtra = requestedByConsumers - availableFromProducers;
                var availableFromStores = stores.stream()
                    .mapToLong(PowerNode.PowerStore::getStoredPower)
                    .sum();

                if (availableFromProducers + availableFromStores >= requestedByConsumers) {
                    consumersFullyPowered = true;

                    // Extract from producers.
                    for (var producer : producers) {
                        var offered = producer.getAvailablePower();
                        producer.extractPower(offered);
                    }

                    // Draw remaining power from batteries.
                    var stillNeeded = requiredExtra;

                    for (var store : stores) {
                        if (stillNeeded <= 0) {
                            break;
                        }

                        var drawn = store.extractPower(stillNeeded);
                        stillNeeded -= drawn;
                    }

                    // Power consumers.
                    for (var consumer : consumers) {
                        var requested = consumer.getRequestedPower();
                        consumer.receivePower(requested);
                    }
                }
            }
        }

        // Handle surplus charging *only if* we powered all consumers or had no consumers.
        if (consumersFullyPowered || requestedByConsumers == 0) {
            var usedByConsumers = requestedByConsumers;
            var usedByProducers = Math.min(availableFromProducers, usedByConsumers);
            var surplus = availableFromProducers - usedByProducers;

            if (surplus > 0) {
                for (var store : stores) {
                    if (surplus <= 0) {
                        break;
                    }

                    var accepted = store.receivePower(surplus);
                    surplus -= accepted;
                }
            }

            // Finally, extract all available power from producers, even if it wasn't necessary yet
            // (ensures proper accounting if needed elsewhere later)
            for (var producer : producers) {
                var offered = producer.getAvailablePower();
                producer.extractPower(offered);
            }
        }
    }

    public void addAll(Collection<? extends PowerNode> nodes) {
        nodes.forEach(this::add);
    }

    public void removeAll(Collection<? extends PowerNode> nodes) {
        nodes.forEach(this::remove);
    }

    public Set<PowerNode.PowerConsumer> getConsumers() {
        return Collections.unmodifiableSet(consumers);
    }

    public Set<PowerNode.PowerProducer> getProducers() {
        return Collections.unmodifiableSet(producers);
    }

    public Set<PowerNode.PowerStore> getStores() {
        return Collections.unmodifiableSet(stores);
    }
}
