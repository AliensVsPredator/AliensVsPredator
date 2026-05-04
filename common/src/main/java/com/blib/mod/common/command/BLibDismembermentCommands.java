package com.blib.mod.common.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import org.jetbrains.annotations.ApiStatus;

import com.blib.api.common.dismemberment.v1.Dismemberable;
import com.blib.api.common.dismemberment.v1.LimbCategories;
import com.blib.api.common.dismemberment.v1.LimbCategory;
import com.blib.api.common.dismemberment.v1.LimbDefinitionRegistry;
import com.blib.api.common.dismemberment.v1.LimbDismemberer;

/**
 * Debug-only command for triggering dismemberment on the entity in the player's crosshair.
 * <p>
 * Subcommands:
 * <ul>
 * <li>{@code /blib dismember} — detach one random remaining limb from the targeted entity.</li>
 * <li>{@code /blib dismember all} — detach every registered limb on the targeted entity.</li>
 * <li>{@code /blib dismember head|arm|leg|tail} — detach the first remaining limb in that category.</li>
 * </ul>
 * Gated by op-level-2 (cheats) <em>and</em> only registered when running in a development environment, so it never ends
 * up in shipped builds.
 */
@ApiStatus.Internal
public final class BLibDismembermentCommands {

    private static final double RAYCAST_DISTANCE = 64.0;

    private static final SimpleCommandExceptionType NO_TARGET = new SimpleCommandExceptionType(
        Component.literal("No living entity in crosshair within " + (int) RAYCAST_DISTANCE + " blocks.")
    );

    private static final SimpleCommandExceptionType NOT_DISMEMBERABLE = new SimpleCommandExceptionType(
        Component.literal("Target has no registered limb definitions.")
    );

    private BLibDismembermentCommands() {
        throw new UnsupportedOperationException();
    }

    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        var node = Commands.literal("dismember")
            .executes(BLibDismembermentCommands::executeRandom)
            .then(Commands.literal("all").executes(BLibDismembermentCommands::executeAll));

        for (
            var entry : new CategoryEntry[] {
                new CategoryEntry("head", LimbCategories.HEAD),
                new CategoryEntry("arm", LimbCategories.ARM),
                new CategoryEntry("leg", LimbCategories.LEG),
                new CategoryEntry("tail", LimbCategories.TAIL)
            }
        ) {
            node = node.then(
                Commands.literal(entry.literal)
                    .executes(ctx -> executeCategory(ctx, entry.category, entry.literal))
            );
        }

        return node;
    }

    private static int executeRandom(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var target = pickTarget(context);
        var defs = LimbDefinitionRegistry.getDefinitions(target.getType());

        if (defs.isEmpty()) {
            throw NOT_DISMEMBERABLE.create();
        }

        var manager = ((Dismemberable) target).getDismembermentManager();

        for (var def : defs) {
            if (manager.isDetached(def)) {
                continue;
            }

            LimbDismemberer.detach(target, def.id(), null);
            final var name = def.id();
            context.getSource()
                .sendSuccess(
                    () -> Component.literal("Detached " + name + " from " + target.getName().getString() + "."),
                    false
                );
            return Command.SINGLE_SUCCESS;
        }

        context.getSource().sendFailure(Component.literal("Target has no remaining limbs to detach."));
        return 0;
    }

    private static int executeAll(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var target = pickTarget(context);
        var defs = LimbDefinitionRegistry.getDefinitions(target.getType());

        if (defs.isEmpty()) {
            throw NOT_DISMEMBERABLE.create();
        }

        var dismemberable = (Dismemberable) target;
        var manager = dismemberable.getDismembermentManager();
        var detached = 0;

        for (var def : defs) {
            if (manager.isDetached(def)) {
                continue;
            }

            if (LimbDismemberer.detach(target, def.id(), null).isPresent()) {
                detached++;
            }
        }

        final var count = detached;
        context.getSource()
            .sendSuccess(
                () -> Component.literal("Detached " + count + " limb(s) from " + target.getName().getString() + "."),
                false
            );
        return Command.SINGLE_SUCCESS;
    }

    private static int executeCategory(
        CommandContext<CommandSourceStack> context,
        LimbCategory category,
        String literal
    ) throws CommandSyntaxException {
        var target = pickTarget(context);

        if (LimbDefinitionRegistry.getDefinitions(target.getType()).isEmpty()) {
            throw NOT_DISMEMBERABLE.create();
        }

        var spawned = LimbDismemberer.detachFirstOfCategory(target, category, null);

        if (spawned.isEmpty()) {
            context.getSource()
                .sendFailure(
                    Component.literal("Target has no remaining " + literal + " limbs to detach.")
                );
            return 0;
        }

        context.getSource()
            .sendSuccess(
                () -> Component.literal("Detached " + literal + " limb from " + target.getName().getString() + "."),
                false
            );
        return Command.SINGLE_SUCCESS;
    }

    private static LivingEntity pickTarget(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var source = context.getSource();
        var player = source.getPlayerOrException();

        var eye = player.getEyePosition(1.0F);
        var view = player.getViewVector(1.0F);
        var endpoint = eye.add(view.x * RAYCAST_DISTANCE, view.y * RAYCAST_DISTANCE, view.z * RAYCAST_DISTANCE);
        var aabb = player.getBoundingBox()
            .expandTowards(view.x * RAYCAST_DISTANCE, view.y * RAYCAST_DISTANCE, view.z * RAYCAST_DISTANCE)
            .inflate(1.0);

        var hit = ProjectileUtil.getEntityHitResult(
            player,
            eye,
            endpoint,
            aabb,
            entity -> !entity.isSpectator() && entity != player && entity instanceof LivingEntity,
            RAYCAST_DISTANCE * RAYCAST_DISTANCE
        );

        if (hit == null || !(hit.getEntity() instanceof LivingEntity living)) {
            throw NO_TARGET.create();
        }

        if (!(living instanceof Dismemberable)) {
            throw NOT_DISMEMBERABLE.create();
        }

        return living;
    }

    /** Category literal → registry constant. */
    private record CategoryEntry(
        String literal,
        LimbCategory category
    ) {}
}
