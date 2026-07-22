package com.example.pvpbotmod.combat;

import java.util.EnumSet;
import java.util.concurrent.ThreadLocalRandom;

import com.example.pvpbotmod.entity.PvpBotEntity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

/**
 * 근접 사거리 안에서 실제로 싸울 때만 좌우 스트레이프 무빙을 하는 Goal.
 * (플러그인 MovementController의 무빙 로직 이식)
 *
 * - 쫓아갈 때(사거리 밖)는 무빙 안 함 — 그건 MeleeAttackGoal의 추격이 담당
 * - 임파서블은 크리스탈 자리잡기가 흐트러지면 안 되므로 무빙 안 함
 * - 랜덤한 간격(0.4~1초)으로 좌우 방향을 바꿔서 예측 불가능하게 움직임
 */
public class PvpStrafeGoal extends Goal {

    private static final float STRAFE_SPEED = 0.6f; // MoveControl.strafe 기준 상대 속도

    private final PvpBotEntity mob;

    private int strafeDirection = 1;
    private int ticksUntilSwitch = 0;

    public PvpStrafeGoal(PvpBotEntity mob) {
        this.mob = mob;
        // MOVE 플래그를 점유하지 않는다 — MeleeAttackGoal(추격)과 동시에 돌아가야 하므로
        // 스트레이프 입력만 얹는 방식. LOOK도 점유 안 함(타겟 바라보기는 공격 Goal이 담당).
        this.setFlags(EnumSet.noneOf(Goal.Flag.class));
    }

    @Override
    public boolean canUse() {
        BotDifficulty difficulty = mob.getDifficulty();
        if (difficulty == BotDifficulty.IMPOSSIBLE) return false;

        LivingEntity target = mob.getTarget();
        if (target == null || !target.isAlive()) return false;
        double distSqr = mob.distanceToSqr(target);
        return distSqr <= difficulty.meleeRange * difficulty.meleeRange;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true; // 매 틱 tick()이 호출되어야 무빙이 자연스러움
    }

    @Override
    public void tick() {
        ticksUntilSwitch--;
        if (ticksUntilSwitch <= 0) {
            strafeDirection = ThreadLocalRandom.current().nextBoolean() ? 1 : -1;
            ticksUntilSwitch = 8 + ThreadLocalRandom.current().nextInt(12); // 0.4~1초마다 전환
        }
        // forward 0, 좌우로만 이동 입력
        mob.getMoveControl().strafe(0.0f, strafeDirection * STRAFE_SPEED);
    }
}
