package com.example.pvpbotmod.item;

import com.example.pvpbotmod.PvpBotMod;
import com.example.pvpbotmod.entity.ModEntityTypes;
import net.fabricmc.fabric.api.itemgroup.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;

import java.util.function.Function;

/**
 * 26.1에서 Fabric API 이름이 공식 매핑에 맞춰 바뀜: ItemGroupEvents -> CreativeModeTabEvents
 * (공식 26.1 릴리즈 노트에 명시된 변경사항)
 */
public class ModItems {

    public static final Item PVP_BOT_SPAWN_EGG_EASY = register(
            "pvp_bot_spawn_egg_easy", SpawnEggItem::new,
            new Item.Properties().spawnEgg(ModEntityTypes.PVP_BOT));

    public static final Item PVP_BOT_SPAWN_EGG_NORMAL = register(
            "pvp_bot_spawn_egg_normal", SpawnEggItem::new,
            new Item.Properties().spawnEgg(ModEntityTypes.PVP_BOT));

    public static final Item PVP_BOT_SPAWN_EGG_HARD = register(
            "pvp_bot_spawn_egg_hard", SpawnEggItem::new,
            new Item.Properties().spawnEgg(ModEntityTypes.PVP_BOT));

    public static final Item PVP_BOT_SPAWN_EGG_IMPOSSIBLE = register(
            "pvp_bot_spawn_egg_impossible", SpawnEggItem::new,
            new Item.Properties().spawnEgg(ModEntityTypes.PVP_BOT));

    private static <T extends Item> T register(String name, Function<Item.Properties, T> factory, Item.Properties props) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM,
                Identifier.fromNamespaceAndPath(PvpBotMod.MOD_ID, name));
        T item = factory.apply(props.setId(key));
        Registry.register(BuiltInRegistries.ITEM, key, item);
        return item;
    }

    public static void register() {
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.SPAWN_EGGS).register(creativeTab -> {
            creativeTab.accept(PVP_BOT_SPAWN_EGG_EASY);
            creativeTab.accept(PVP_BOT_SPAWN_EGG_NORMAL);
            creativeTab.accept(PVP_BOT_SPAWN_EGG_HARD);
            creativeTab.accept(PVP_BOT_SPAWN_EGG_IMPOSSIBLE);
        });
        PvpBotMod.LOGGER.info("PvpBot 아이템 등록 완료");
    }
}
