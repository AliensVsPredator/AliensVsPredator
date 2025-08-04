package com.human.common.gameplay.power.grid;

import com.human.common.gameplay.power.PowerNode;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class PowerGrid {

    private final Set<PowerNode.PowerConsumer> consumers;

    private final Set<PowerNode.PowerProducer> producers;

    public PowerGrid() {
        this.consumers = new HashSet<>();
        this.producers = new HashSet<>();
    }

    /**
     * Registers a power node into the grid.
     */
    public void add(PowerNode node) {
        switch (node) {
            case PowerNode.PowerConsumer powerConsumer -> consumers.add(powerConsumer);
            case PowerNode.PowerProducer powerProducer -> producers.add(powerProducer);
        }
    }

    /**
     * Unregisters a power node from the grid.
     */
    public void remove(PowerNode node) {
        switch (node) {
            case PowerNode.PowerConsumer powerConsumer -> consumers.remove(powerConsumer);
            case PowerNode.PowerProducer powerProducer -> producers.remove(powerProducer);
        }
    }

    /**
     * Called once per tick to manage energy flow.
     */
    public void tick() {
        var totalAvailable = producers.stream()
            .mapToLong(PowerNode.PowerProducer::getAvailablePower)
            .sum();

        if (totalAvailable == 0) {
            // No power to provide, nothing to do.
            return;
        }

        var totalRequested = consumers.stream()
            .mapToLong(PowerNode.PowerConsumer::getRequestedPower)
            .sum();

        if (totalRequested == 0) {
            // Nothing wants power, nothing to do.
            return;
        }

        if (totalAvailable < totalRequested) {
            // Grid underpowered — drop power entirely.
            return;
        }

        // Fully powered — satisfy all consumers.
        for (var consumer : consumers) {
            var requested = consumer.getRequestedPower();
            consumer.receivePower(requested);
        }

        for (var producer : producers) {
            var offered = producer.getAvailablePower();
            producer.extractPower(offered);
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
}
