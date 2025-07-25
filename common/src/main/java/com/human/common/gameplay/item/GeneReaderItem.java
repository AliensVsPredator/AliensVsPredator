package com.human.common.gameplay.item;

import com.human.common.gameplay.component.GeneReaderContents;
import com.human.common.gameplay.component.GeneReaderMode;
import com.human.common.registry.init.HumanDataComponents;
import com.lib.common.gameplay.gene.GeneBonusDataEntry;
import com.lib.common.gameplay.gene.GeneOperationType;
import com.lib.common.gameplay.gene.GeneRegistry;
import com.lib.common.model.GeneCarrier;
import com.lib.common.registry.GeneBonusDataRegistry;
import com.lib.common.util.GeneDataUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.List;

public class GeneReaderItem extends Item {

    private static final GeneReaderMode DEFAULT_MODE = GeneReaderMode.BONUS_GENES;

    public GeneReaderItem() {
        super(
            new Properties().stacksTo(1)
                .component(HumanDataComponents.GENE_READER_MODE.get(), DEFAULT_MODE)
                .durability(1024)
        );
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(
        @NotNull Level level,
        @NotNull Player player,
        @NotNull InteractionHand hand
    ) {
        var stack = player.getItemInHand(hand);

        if (player.isShiftKeyDown()) {
            if (!level.isClientSide) {
                cycleGeneReaderMode(stack, player);
            }

            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        } else {
            if (!level.isClientSide) {
                var mode = stack.getOrDefault(HumanDataComponents.GENE_READER_MODE.get(), DEFAULT_MODE);

                if (mode == GeneReaderMode.CLEAR) {
                    stack.set(HumanDataComponents.GENE_READER_CONTENTS.get(), GeneReaderContents.EMPTY);
                    // TODO: Use proper translatable here.
                    player.displayClientMessage(Component.literal("Gene data has been cleared."), true);
                }
            }

            return super.use(level, player, hand);
        }
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        var player = context.getPlayer();

        if (player == null) {
            return super.useOn(context);
        }

        if (player.isShiftKeyDown()) {
            if (!context.getLevel().isClientSide()) {
                cycleGeneReaderMode(context.getItemInHand(), context.getPlayer());
            }

            return InteractionResult.SUCCESS;
        } else {
            if (!context.getLevel().isClientSide()) {
                var stack = context.getItemInHand();
                var mode = stack.getOrDefault(HumanDataComponents.GENE_READER_MODE.get(), DEFAULT_MODE);

                if (mode == GeneReaderMode.CLEAR) {
                    stack.set(HumanDataComponents.GENE_READER_CONTENTS.get(), GeneReaderContents.EMPTY);
                    // TODO: Use proper translatable here.
                    player.displayClientMessage(Component.literal("Gene data has been cleared."), true);
                }
            }

            return super.useOn(context);
        }
    }

    private void cycleGeneReaderMode(ItemStack stack, Player player) {
        var current = stack.getOrDefault(HumanDataComponents.GENE_READER_MODE.get(), DEFAULT_MODE);
        var next = nextMode(current);
        stack.set(HumanDataComponents.GENE_READER_MODE.get(), next);

        player.displayClientMessage(
            // TODO: Use proper translatable here.
            Component.literal("Switched mode to ")
                .append(
                    Component.literal(next.toString())
                        .withStyle(ChatFormatting.YELLOW)
                ),
            true
        );
    }

    private GeneReaderMode nextMode(GeneReaderMode current) {
        var values = GeneReaderMode.values();
        var nextOrdinal = (current.ordinal() + 1) % values.length;
        return values[nextOrdinal];
    }

    public static void interact(
        @NotNull ItemStack stack,
        @NotNull Player player,
        @NotNull LivingEntity interactionTarget,
        @NotNull InteractionHand usedHand
    ) {
        var geneReaderMode = stack.getOrDefault(HumanDataComponents.GENE_READER_MODE.get(), DEFAULT_MODE);

        if (geneReaderMode == GeneReaderMode.CLEAR) {
            return;
        }

        var geneBonusMapData = GeneBonusDataRegistry.getOrDefault(interactionTarget.getType());

        if (geneBonusMapData.isEmpty()) {
            return;
        }

        var geneContainer = ((GeneCarrier) interactionTarget).getOrCreateGeneManager().getGeneContainer();
        var geneReaderContents = new EnumMap<GeneReaderMode, List<GeneBonusDataEntry>>(GeneReaderMode.class);

        geneReaderContents.put(GeneReaderMode.ACTIVE_GENES, GeneDataUtil.toList(geneContainer.getActiveGeneMap().getBackingMap()));
        geneReaderContents.put(GeneReaderMode.DORMANT_GENES, GeneDataUtil.toList(geneContainer.getDormantGeneMap().getBackingMap()));
        geneReaderContents.put(GeneReaderMode.BONUS_GENES, GeneDataUtil.toList(geneBonusMapData));

        // Assign gene reader contents back to gene reader item stack.
        stack.set(HumanDataComponents.GENE_READER_CONTENTS.get(), new GeneReaderContents(geneReaderContents));
        stack.hurtAndBreak(1, player, player.getEquipmentSlotForItem(stack));
        // Assign stack back to player hand to update client-side.
        player.setItemInHand(usedHand, stack);
        // Apply "fake" damage to the entity.
        // TODO: Add a proper damage source here.
        interactionTarget.hurt(interactionTarget.damageSources().generic(), 0.01F);
        // TODO: Use proper translatable here.
        player.displayClientMessage(Component.literal("Processed gene data from target."), true);
    }

    @Override
    public void appendHoverText(
        @NotNull ItemStack itemStack,
        @NotNull TooltipContext tooltipContext,
        @NotNull List<Component> list,
        @NotNull TooltipFlag tooltipFlag
    ) {
        super.appendHoverText(itemStack, tooltipContext, list, tooltipFlag);
        // TODO: Use translatable here.
        list.add(Component.literal("Shift + Right Click to change mode.").withStyle(ChatFormatting.GRAY));
        list.add(CommonComponents.EMPTY);

        var geneReaderMode = itemStack.getOrDefault(HumanDataComponents.GENE_READER_MODE.get(), DEFAULT_MODE);

        list.add(
            Component.literal("Current Mode: ")
                .append(
                    Component.literal(geneReaderMode.toString())
                        .withStyle(ChatFormatting.YELLOW)
                )
        );

        var geneReaderContents = itemStack.getOrDefault(HumanDataComponents.GENE_READER_CONTENTS.get(), GeneReaderContents.EMPTY);

        if (geneReaderContents.equals(GeneReaderContents.EMPTY) || geneReaderMode == GeneReaderMode.CLEAR) {
            return;
        }

        var geneEntries = geneReaderContents.geneBonusDataEntriesByMode().getOrDefault(geneReaderMode, List.of());

        list.add(CommonComponents.EMPTY);
        // TODO: Make this translatable.
        list.add(
            Component.literal("Genes:")
                .withStyle(ChatFormatting.YELLOW)
        );

        if (geneEntries.isEmpty()) {
            list.add(
                Component.literal("No gene data found.")
                    .withStyle(ChatFormatting.GRAY)
            );
        } else {
            // TODO: Pretty this up later.
            geneEntries.stream()
                .filter(entry -> GeneRegistry.getValue(entry.id()).isSome())
                .forEach(
                    entry -> list.add(
                        Component.translatable(GeneRegistry.getValue(entry.id()).unwrap().getTranslationKey())
                            .append(Component.literal(": "))
                            .append(
                                Component.literal(format(entry.operation(), entry.value()))
                                    .withStyle(getColorForValue(entry.value()))
                            )
                    )
                );
        }
    }

    private String format(GeneOperationType operation, double value) {
        var suffix = operation == GeneOperationType.MULTIPLICATIVE ? "%" : "";
        var sign = value > 0 ? "+" : "";
        var formattedValue = String.format("%.2f", operation == GeneOperationType.MULTIPLICATIVE ? value * 100 : value);

        return sign + formattedValue + suffix;
    }

    private ChatFormatting getColorForValue(double value) {
        if (value > 0) {
            return ChatFormatting.GREEN;
        } else if (value < 0) {
            return ChatFormatting.RED;
        }

        return ChatFormatting.GRAY;
    }
}
