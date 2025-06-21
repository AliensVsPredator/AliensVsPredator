package com.lib.common.gameplay.entity.manager;

import com.lib.common.gameplay.NBTSerializable;
import com.lib.common.gameplay.gene.Gene;
import com.lib.common.gameplay.gene.GeneRegistry;
import com.lib.common.util.GeneDataUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import com.avp.common.network.packet.S2CSyncGenesPayload;
import com.avp.service.Services;

public class GeneManager implements NBTSerializable {

    private final LivingEntity entity;

    private final GeneContainer geneContainer;

    public GeneManager(LivingEntity entity) {
        this.entity = entity;
        this.geneContainer = new GeneContainer();
    }

    public void tick() {
        if (entity.level().isClientSide) {
            return;
        }

        if (geneContainer.isDirty()) {
            var oldMaxHealth = entity.getMaxHealth();
            var wasFullHealth = entity.getHealth() == oldMaxHealth;

            // TODO: This doesn't handle genes that go missing from the map. Remove all modifiers then re-apply them.
            getGeneContainer().getActiveGenes()
                .forEach(((geneModifierKey, bonusValue) -> {
                    var id = geneModifierKey.resourceLocation();
                    var gene = GeneRegistry.getValueOrNull(id);

                    if (gene != null) {
                        switch (gene) {
                            case Gene.Attribute attribute -> {
                                var attributeInstance = entity.getAttribute(attribute.attributeHolder());

                                if (attributeInstance != null) {
                                    var finalBonusValue = switch (geneModifierKey.operation()) {
                                        case ADDITIVE -> bonusValue;
                                        case MULTIPLICATIVE -> attributeInstance.getBaseValue() * bonusValue;
                                    };

                                    var modifier = new AttributeModifier(id, finalBonusValue, AttributeModifier.Operation.ADD_VALUE);

                                    attributeInstance.addOrReplacePermanentModifier(modifier);
                                }
                            }
                            case Gene.Simple ignored -> { /* NO-OP */ }
                        }
                    }
                }));

            var maxHealthAttributeInstance = entity.getAttribute(Attributes.MAX_HEALTH);

            if (maxHealthAttributeInstance != null) {
                var newMax = maxHealthAttributeInstance.getValue();
                var currentHealth = entity.getHealth();

                if (wasFullHealth || currentHealth > newMax) {
                    entity.setHealth((float) newMax);
                }
            }

            syncToClient();

            getGeneContainer().setDirty(false);
        }
    }

    public void syncToClient() {
        var server = ((ServerLevel) entity.level()).getServer();
        var payload = new S2CSyncGenesPayload(entity.getId(), GeneDataUtil.toList(geneContainer.getActiveGenes()));
        Services.SERVER_NETWORKING.sendToAllClients(server, payload);
    }

    public GeneContainer getGeneContainer() {
        return geneContainer;
    }

    @Override
    public void load(CompoundTag compoundTag) {
        geneContainer.load(compoundTag);
        geneContainer.setDirty(true);
    }

    @Override
    public void save(CompoundTag compoundTag) {
        geneContainer.save(compoundTag);
    }
}
