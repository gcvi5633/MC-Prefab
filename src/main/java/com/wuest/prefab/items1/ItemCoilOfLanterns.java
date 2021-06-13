package com.wuest.prefab.items;

import com.wuest.prefab.ModRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

/**
 * @author WuestMan
 */
public class ItemCoilOfLanterns extends Item {
    public ItemCoilOfLanterns(String name) {
        super();

        this.setCreativeTab(CreativeTabs.MATERIALS);
        ModRegistry.setItemName(this, name);
    }
}
