package com.example.pvpbotmod;

import com.example.pvpbotmod.command.PvpBotCommand;
import com.example.pvpbotmod.entity.ModEntityTypes;
import com.example.pvpbotmod.item.ModItems;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PvpBotMod implements ModInitializer {

    public static final String MOD_ID = "pvpbotmod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModEntityTypes.register();  // 엔티티 타입 + 속성 등록
        ModItems.register();         // 스폰에그 아이템 + 크리에이티브 탭 등록
        PvpBotCommand.register();    // /pvpbot 명령어 등록
        LOGGER.info("PvpBotMod 초기화 완료");
    }
}
