package com.example.pvpbotmod.entity;

import com.example.pvpbotmod.combat.BotDifficulty;
import com.example.pvpbotmod.combat.PvpStrafeGoal;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * 사람처럼 PvP를 하는 커스텀 몹.
 * PathfinderMob 상속으로 진짜 바닐라 AI 인프라(goalSelector, navigation, 경로탐색)를 그대로 씀 —
 * 플러그인 시절 10x10 BFS 근사치 경로탐색과 달리 맵 전체를 제대로 우회하는 진짜 A* 탐색.
 *
 * 전투 알고리즘은 combat 패키지의 커스텀 Goal들로 하나씩 이식 중:
 * - PvpStrafeGoal: 근접 전투 중 좌우 무빙 (이식 완료)
 * - 활 예측사격, 크리스탈PvP, 채굴/브릿징 등은 이 뼈대가 빌드 확인된 후 순차 추가 예정
 */
public class PvpBotEntity extends PathfinderMob {

    private BotDifficulty difficulty = BotDifficulty.NORMAL;

    public PvpBotEntity(Level level) {
        this(ModEntityTypes.PVP_BOT, level);
    }

    public PvpBotEntity(EntityType<? extends PvpBotEntity> entityType, Level level) {
        super(entityType, level);
    }

    public BotDifficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(BotDifficulty difficulty) {
        this.difficulty = difficulty;
    }

    /** 엔티티 속성 기본값. FabricDefaultAttributeRegistry에 등록됨. */
    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }

    @Override
    protected void registerGoals() {
        // 숫자가 작을수록 우선순위 높음
        this.goalSelector.addGoal(0, new FloatGoal(this)); // 물에 빠지면 뜨기
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, true)); // 추격 + 근접 공격
        this.goalSelector.addGoal(2, new PvpStrafeGoal(this)); // 근접 전투 중 좌우 무빙 (이지~하드)
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 0.8)); // 타겟 없을 때 배회
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

        // 타겟 선정: 가장 가까운 플레이어
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }
}
