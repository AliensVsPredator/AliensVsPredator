package com.human.common.gameplay.item.gun.attack.hitscan;

import com.human.common.gameplay.item.gun.attack.GunAttackConfig;
import com.human.common.gameplay.item.gun.attack.GunHitResult;
import com.lib.common.gameplay.util.EnchantmentUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

import com.avp.AVP;
import com.avp.common.network.packet.S2CBulletHitBlockPayload;
import com.avp.common.registry.init.AVPSoundEvents;
import com.avp.common.registry.tag.AVPBlockTags;
import com.avp.server.BlockBreakProgressManager;
import com.avp.service.Services;

public class BlockGunHitResultHandler {

    public static void handle(GunAttackConfig gunAttackConfig, GunHitResult.Block gunHitResult) {
        var blockPos = gunHitResult.blockPos();
        var direction = gunHitResult.direction();
        var level = gunAttackConfig.shooter().level();
        var blockState = level.getBlockState(blockPos);
        var soundType = blockState.getSoundType();

        var ricochetSoundEvent = getRicochetSoundForSoundType(soundType);
        level.playSound(null, blockPos, ricochetSoundEvent, SoundSource.BLOCKS);

        damageBlock(gunAttackConfig, level, blockPos, blockState);

        var payload = new S2CBulletHitBlockPayload(blockPos, direction);
        Services.SERVER_NETWORKING.sendToAllClients(level.getServer(), payload);
    }

    private static SoundEvent getRicochetSoundForSoundType(SoundType soundType) {
        SoundEvent ricochetSfx;

        if (soundType == SoundType.GLASS) {
            ricochetSfx = AVPSoundEvents.WEAPON_FX_RICOCHET_GLASS.get();
        } else if (soundType == SoundType.GRAVEL) {
            ricochetSfx = AVPSoundEvents.WEAPON_FX_RICOCHET_DIRT.get();
        } else if (soundType == SoundType.METAL) {
            ricochetSfx = AVPSoundEvents.WEAPON_FX_RICOCHET_METAL.get();
        } else {
            ricochetSfx = AVPSoundEvents.WEAPON_FX_RICOCHET_GENERIC.get();
        }
        return ricochetSfx;
    }

    private static void damageBlock(GunAttackConfig gunAttackConfig, Level level, BlockPos blockPos, BlockState blockState) {
        if (
            !AVP.config.weaponConfigs.BULLETS_DAMAGE_BLOCKS_ENABLED
                || !level.getGameRules().getBoolean(GameRules.RULE_PROJECTILESCANBREAKBLOCKS)
                // Only damage blocks if they should be destroyed.
                || blockState.is(AVPBlockTags.SHOULD_NOT_BE_DESTROYED)
                || (gunAttackConfig.shooter() instanceof Player player
                    && !Services.EVENT.beforeBlockBreak(level, blockPos, blockState, player))
        ) {
            return;
        }

        var powerLevel = EnchantmentUtil.getLevel(level, gunAttackConfig.gunItemStack(), Enchantments.POWER);
        var damage = gunAttackConfig.fireModeConfig().damage() * (1 + (0.25F * powerLevel));
        BlockBreakProgressManager.damage(level, blockPos, damage);
    }
}
