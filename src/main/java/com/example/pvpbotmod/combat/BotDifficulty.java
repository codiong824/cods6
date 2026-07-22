package com.example.pvpbotmod.combat;

/**
 * 난이도별 AI 파라미터 (플러그인의 DifficultyProfile 이식).
 * 순수 자바 enum이라 마인크래프트 API 의존이 없어서 컴파일 리스크가 없다.
 */
public enum BotDifficulty {

    EASY(8, 0.35, 2.5, 12.0, 100, 4.0, false, false),
    NORMAL(4, 0.15, 2.5, 12.0, 80, 6.0, false, true),
    HARD(1, 0.05, 2.8, 14.0, 75, 7.0, false, true),
    IMPOSSIBLE(1, 0.02, 3.0, 16.0, 70, 8.0, true, true);

    public final int reactionDelayTicks; // 반응 지연 (사람처럼 보이게 하는 핵심)
    public final double missChance;      // 공격 실패 확률
    public final double meleeRange;      // 근접 전투 진입 거리
    public final double bowRange;        // 이 거리보다 멀면 활 사용
    public final int bowDrawTicks;       // 활 장전 시간(틱)
    public final double attackDamage;    // 기본 근접 데미지
    public final boolean useCrystalPvP;  // 임파서블 전용
    public final boolean useWaterClutch; // 노멀 이상

    BotDifficulty(int reactionDelayTicks, double missChance, double meleeRange,
                  double bowRange, int bowDrawTicks, double attackDamage,
                  boolean useCrystalPvP, boolean useWaterClutch) {
        this.reactionDelayTicks = reactionDelayTicks;
        this.missChance = missChance;
        this.meleeRange = meleeRange;
        this.bowRange = bowRange;
        this.bowDrawTicks = bowDrawTicks;
        this.attackDamage = attackDamage;
        this.useCrystalPvP = useCrystalPvP;
        this.useWaterClutch = useWaterClutch;
    }
}
