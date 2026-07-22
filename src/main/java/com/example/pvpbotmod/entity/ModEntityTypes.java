package com.example.pvpbotmod.entity;

import com.example.pvpbotmod.PvpBotMod;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

/**
 * 커스텀 엔티티 타입 등록.
 * 공식 Fabric 26.1.2 문서(Creating Your First Entity)의 등록 패턴을 그대로 따름:
 * - 26.1 비난독화 코드에서는 ResourceLocation이 아니라 Identifier가 실제 클래스명
 * - ResourceKey.create(Registries.ENTITY_TYPE, ...) + Registry.register(BuiltInRegistries.ENTITY_TYPE, ...)
 * - 크기는 .sized(가로, 세로)
 * - 속성 등록은 FabricDefaultAttributeRegistry.register(타입, 빌더)
 */
public class ModEntityTypes {

    public static final EntityType<PvpBotEntity> PVP_BOT = register(
            "pvp_bot",
            EntityType.Builder.<PvpBotEntity>of(PvpBotEntity::new, MobCategory.MISC)
                    .sized(0.6f, 1.8f) // 플레이어와 같은 히트박스
    );

    private static <T extends Entity> EntityType<T> register(String name, EntityType.Builder<T> builder) {
        ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE,
                Identifier.fromNamespaceAndPath(PvpBotMod.MOD_ID, name));
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, key, builder.build(key));
    }

    /** 엔티티 타입 클래스 로드(정적 초기화) + 속성 등록. 모드 초기화 시점에 호출. */
    public static void register() {
        FabricDefaultAttributeRegistry.register(PVP_BOT, PvpBotEntity.createAttributes());
        PvpBotMod.LOGGER.info("PvpBot 엔티티 타입 등록 완료");
    }
}
