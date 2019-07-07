package com.stal111.weapon_craftery.util;

import com.stal111.weapon_craftery.Main;
import net.minecraft.util.ResourceLocation;

public class ModUtils {

    public static ResourceLocation location(String path) {
        return new ResourceLocation(Main.MOD_ID, path);
    }
}
