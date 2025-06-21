package com.avp.client.network;

import com.alien.common.model.alien.GeneCarrier;
import com.lib.common.util.GeneDataUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

import com.avp.common.network.packet.S2CBulletHitBlockPayload;
import com.avp.common.network.packet.S2CGunRecoilPayload;
import com.avp.common.network.packet.S2CSyncGenesPayload;

public class AVPClientListener {

    public static void handleBulletHitBlockPayload(S2CBulletHitBlockPayload bulletHitBlockPayload) {
        var blockPos = bulletHitBlockPayload.blockPos();
        var direction = bulletHitBlockPayload.direction();

        for (int i = 0; i < 16; i++) {
            Minecraft.getInstance().particleEngine.crack(blockPos, direction);
        }
    }

    public static void handleGunRecoil(S2CGunRecoilPayload gunRecoilPayload, Player player) {
        var level = player.level();
        var baseRecoilX = level.getRandom().nextBoolean() ? 1f : -1f;

        player.turn(baseRecoilX * 2, -gunRecoilPayload.recoil() * 2);
    }

    public static void handleGeneSync(S2CSyncGenesPayload syncGenesPayload, Player player) {
        var targetEntity = player.level().getEntity(syncGenesPayload.entityId());

        if (targetEntity == null) {
            return;
        }

        var geneContainer = ((GeneCarrier) targetEntity).getOrCreateGeneManager().getGeneContainer();
        var map = GeneDataUtil.toMap(syncGenesPayload.geneBonusDataEntries());

        geneContainer.clearActiveGenes();
        geneContainer.putActiveGenes(map);
    }

    private AVPClientListener() {
        throw new UnsupportedOperationException();
    }
}
