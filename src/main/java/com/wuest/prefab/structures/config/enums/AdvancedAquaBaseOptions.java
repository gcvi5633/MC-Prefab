package com.wuest.prefab.structures.config.enums;

import net.minecraft.core.Direction;

public class AdvancedAquaBaseOptions extends BaseOption {
    public static AdvancedAquaBaseOptions Default = new AdvancedAquaBaseOptions(
            "item.prefab.advanced_aqua_base",
            "assets/prefab/structures/advanced_aqua_base.zip",
            "textures/gui/advanced_aqua_base_topdown.png",
            Direction.SOUTH,
            27,
            25,
            38,
            1,
            12,
            0,
            false,
            false);

    protected AdvancedAquaBaseOptions(String translationString,
                                      String assetLocation,
                                      String pictureLocation,
                                      Direction direction,
                                      int height,
                                      int width,
                                      int length,
                                      int offsetParallelToPlayer,
                                      int offsetToLeftOfPlayer,
                                      int heightOffset,
                                      boolean hasBedColor,
                                      boolean hasGlassColor) {
        super(
                translationString,
                assetLocation,
                pictureLocation,
                direction,
                height,
                width,
                length,
                offsetParallelToPlayer,
                offsetToLeftOfPlayer,
                heightOffset,
                hasBedColor,
                hasGlassColor);
    }
}
