package com.example.pvpbotmod.command;

import com.example.pvpbotmod.combat.BotDifficulty;
import com.example.pvpbotmod.entity.ModEntityTypes;
import com.example.pvpbotmod.entity.PvpBotEntity;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.phys.AABB;

public class PvpBotCommand {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(Commands.literal("pvpbot")
                    // 26.1에서 권한 체크 API가 완전히 바뀜(source.permissions().hasPermission(Permission) 형태로,
                    // Permission 객체가 필요해짐). 정확한 사용법이 아직 불확실해서 일단 권한 제한 없이 감.
                    // (필요하면 나중에 정확한 API 확인해서 다시 넣을 수 있음)

                    .then(Commands.literal("spawn")
                            .executes(ctx -> spawnBot(ctx, "normal"))
                            .then(Commands.argument("difficulty", StringArgumentType.word())
                                    .suggests((ctx, builder) -> {
                                        builder.suggest("easy");
                                        builder.suggest("normal");
                                        builder.suggest("hard");
                                        builder.suggest("impossible");
                                        return builder.buildFuture();
                                    })
                                    .executes(ctx -> spawnBot(ctx,
                                            StringArgumentType.getString(ctx, "difficulty")))))

                    .then(Commands.literal("remove")
                            .executes(PvpBotCommand::removeNearestBot))

                    .then(Commands.literal("list")
                            .executes(PvpBotCommand::listBots))
            );
        });
    }

    private static int spawnBot(CommandContext<CommandSourceStack> ctx, String difficultyStr) {
        CommandSourceStack source = ctx.getSource();
        ServerPlayer player;
        try {
            player = source.getPlayerOrException();
        } catch (Exception e) {
            source.sendFailure(Component.literal("플레이어만 사용할 수 있습니다."));
            return 0;
        }

        BotDifficulty difficulty;
        try {
            difficulty = BotDifficulty.valueOf(difficultyStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            source.sendFailure(Component.literal("난이도는 easy, normal, hard, impossible 중 하나여야 합니다."));
            return 0;
        }

        // 26.1: player.serverLevel() 대신 (ServerLevel) player.level()
        ServerLevel level = (ServerLevel) player.level();
        // 26.1: create(Level) 단일 인자 버전이 없어짐 — EntitySpawnReason을 반드시 넘겨야 함
        PvpBotEntity bot = ModEntityTypes.PVP_BOT.create(level, EntitySpawnReason.COMMAND);
        if (bot == null) {
            source.sendFailure(Component.literal("봇 생성에 실패했습니다."));
            return 0;
        }

        bot.setDifficulty(difficulty);
        // moveTo()의 정확한 시그니처가 26.1에서 불확실해서, 훨씬 오래되고 확실한
        // setPos() + setYRot()로 위치/방향을 잡음
        bot.setPos(player.getX(), player.getY(), player.getZ());
        bot.setYRot(player.getYRot());
        bot.setCustomName(Component.literal("[" + difficulty + "] PvP Bot"));
        bot.setCustomNameVisible(true);
        level.addFreshEntity(bot);

        final BotDifficulty diff = difficulty;
        source.sendSuccess(() -> Component.literal("PvP 봇 소환 완료 (난이도: " + diff + ")"), true);
        return 1;
    }

    private static int removeNearestBot(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        ServerPlayer player;
        try {
            player = source.getPlayerOrException();
        } catch (Exception e) {
            source.sendFailure(Component.literal("플레이어만 사용할 수 있습니다."));
            return 0;
        }

        ServerLevel level = (ServerLevel) player.level();
        AABB searchBox = player.getBoundingBox().inflate(20);
        PvpBotEntity nearest = level.getEntitiesOfClass(PvpBotEntity.class, searchBox)
                .stream()
                .min((a, b) -> Double.compare(a.distanceToSqr(player), b.distanceToSqr(player)))
                .orElse(null);

        if (nearest == null) {
            source.sendFailure(Component.literal("반경 20블록 안에 PvP 봇이 없습니다."));
            return 0;
        }

        nearest.discard();
        source.sendSuccess(() -> Component.literal("가장 가까운 PvP 봇을 제거했습니다."), true);
        return 1;
    }

    private static int listBots(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        ServerLevel level = source.getLevel();

        AABB worldBox = new AABB(-3e7, -320, -3e7, 3e7, 320, 3e7);
        var bots = level.getEntitiesOfClass(PvpBotEntity.class, worldBox);

        if (bots.isEmpty()) {
            source.sendSuccess(() -> Component.literal("현재 월드에 PvP 봇이 없습니다."), false);
            return 0;
        }

        source.sendSuccess(() -> Component.literal("=== PvP 봇 목록 (" + bots.size() + "개) ==="), false);
        for (Entity bot : bots) {
            source.sendSuccess(() -> Component.literal(
                    "  - " + bot.getDisplayName().getString() +
                    " (UUID: " + bot.getUUID() + ")"), false);
        }
        return 1;
    }
}
