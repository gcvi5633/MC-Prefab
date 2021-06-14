package com.wuest.prefab.structures.config.enums;

import net.minecraft.util.EnumFacing;

public class EnderGatewayOptions extends BaseOption {
    public static EnderGatewayOptions Default = new EnderGatewayOptions(
            "item.prefab.ender_gateway",
            "assets/prefab/structures/ender_gateway.zip",
            "textures/gui/ender_gateway_topdown.png",
            103,
            150,
            EnumFacing.SOUTH,
            26,
            17,
            17,
            1,
            8,
            0);

    protected EnderGatewayOptions(String translationString,
                                  String assetLocation,
                                  String pictureLocation,
                                  int imageWidth,
                                  int imageHeight,
                                  EnumFacing direction,
                                  int height,
                                  int width,
                                  int length,
                                  int offsetParallelToPlayer,
                                  int offsetToLeftOfPlayer,
                                  int heightOffset) {
        super(translationString, assetLocation, pictureLocation, imageWidth, imageHeight, direction, height, width, length, offsetParallelToPlayer, offsetToLeftOfPlayer, heightOffset);
    }
}