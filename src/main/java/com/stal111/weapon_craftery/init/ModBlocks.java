package com.stal111.weapon_craftery.init;

import com.stal111.weapon_craftery.Main;
import com.stal111.weapon_craftery.block.BasicBlock;
import com.stal111.weapon_craftery.util.ModUtils;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(Main.MOD_ID)
public class ModBlocks {

    public static List<Block> blockList = new ArrayList<>();

    public static final Block
            weapon_upgrade_table = null;

    @SubscribeEvent
    public static void register(RegistryEvent.Register<Block> registry) {
        registerAll(registry,
                register("weapon_upgrade_table", new BasicBlock(Block.Properties.from(Blocks.CRAFTING_TABLE))));
    }

    public static Block register(String name, Block block) {
        return block.setRegistryName(ModUtils.location(name));
    }

    public static void registerAll(RegistryEvent.Register<Block> registry, Block... blocks) {
        for (Block block : blocks) {
            blockList.add(block);
            registry.getRegistry().register(block);
        }
    }

    public static void registerItemBlocks(RegistryEvent.Register<Item> registry) {
        Item.Properties properties = new Item.Properties().group(Main.WEAPON_CRAFTERY);
        for (Block block : blockList) {
            Item item = new BlockItem(block, properties);
            item.setRegistryName(block.getRegistryName());
            registry.getRegistry().register(item);
        }
    }
}
