package com.stal111.weapon_craftery.init;

import com.stal111.weapon_craftery.Main;
import com.stal111.weapon_craftery.item.ModSwordItem;
import com.stal111.weapon_craftery.util.ModUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemTier;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(Main.MOD_ID)
public class ModItems {

    public static final Item
            oak_stick = null,
            oak_sword = null,
            spruce_stick = null,
            spruce_sword = null,
            birch_sword = null,
            birch_stick = null,
            jungle_stick = null,
            jungle_sword = null,
            acacia_stick = null,
            acacia_sword = null,
            dark_oak_stick = null,
            dark_oak_sword = null,
            stone_sword = null,
            iron_sword = null,
            diamond_sword = null;

    @SubscribeEvent
    public static void register(RegistryEvent.Register<Item> registry) {
        registerAll(registry,
                register("oak_stick", new Item(properties())),
                register("oak_sword", new ModSwordItem(ItemTier.WOOD, 3, -2.4F, properties())),
                register("spruce_stick", new Item(properties())),
                register("spruce_sword", new ModSwordItem(ItemTier.WOOD, 3, -2.4F, properties())),
                register("birch_stick", new Item(properties())),
                register("birch_sword", new ModSwordItem(ItemTier.WOOD, 3, -2.4F, properties())),
                register("jungle_stick", new Item(properties())),
                register("jungle_sword", new ModSwordItem(ItemTier.WOOD, 3, -2.4F, properties())),
                register("acacia_stick", new Item(properties())),
                register("acacia_sword", new ModSwordItem(ItemTier.WOOD, 3, -2.4F, properties())),
                register("dark_oak_stick", new Item(properties())),
                register("dark_oak_sword", new ModSwordItem(ItemTier.WOOD, 3, -2.4F, properties())),
                register("stone_sword", new ModSwordItem(ItemTier.STONE, 3, -2.4F, properties())),
                register("iron_sword", new ModSwordItem(ItemTier.IRON, 3, -2.4F, properties())),
                register("diamond_sword", new ModSwordItem(ItemTier.DIAMOND, 3, -2.4F, properties())),
                register("golden_sword", new ModSwordItem(ItemTier.GOLD, 3, -2.4F, properties()))
        );

        ModBlocks.registerItemBlocks(registry);
    }

    public static Item register(String name, Item item) {
        return item.setRegistryName(ModUtils.location(name));
    }

    public static void registerAll(RegistryEvent.Register<Item> registry, Item... items) {
        for (Item item : items) {
            registry.getRegistry().register(item);
        }
    }

    public static Item.Properties properties() {
        return new Item.Properties().group(Main.WEAPON_CRAFTERY);
    }
}
