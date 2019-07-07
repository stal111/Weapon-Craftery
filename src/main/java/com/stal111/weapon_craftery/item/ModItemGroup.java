package com.stal111.weapon_craftery.item;

import com.stal111.weapon_craftery.init.ModItems;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class ModItemGroup extends ItemGroup {

    public ModItemGroup(String label) {
        super(label);
    }

    @Override
    public ItemStack createIcon() {
        ItemStack stack = new ItemStack(ModItems.oak_sword);
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt("level", 3);
        nbt.putInt("xp", 0);
        stack.setTag(nbt);
        return stack;
    }

    @Override
    public boolean hasSearchBar() {
        return true;
    }
}
