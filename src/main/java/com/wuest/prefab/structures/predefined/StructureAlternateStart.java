package com.wuest.prefab.structures.predefined;

import com.wuest.prefab.Prefab;
import com.wuest.prefab.Tuple;
import com.wuest.prefab.Utils;
import com.wuest.prefab.config.EntityPlayerConfiguration;
import com.wuest.prefab.proxy.messages.PlayerEntityTagMessage;
import com.wuest.prefab.structures.base.*;
import com.wuest.prefab.structures.config.HouseConfiguration;
import com.wuest.prefab.structures.config.StructureConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fmllegacy.network.NetworkDirection;

import java.util.ArrayList;

/**
 * @author WuestMan
 */
@SuppressWarnings({"unused", "ConstantConditions", "UnusedAssignment"})
public class StructureAlternateStart extends Structure {
    private BlockPos chestPosition = null;
    private BlockPos furnacePosition = null;
    private BlockPos trapDoorPosition = null;
    private BlockPos signPosition = null;
    private ArrayList<Tuple<BlockPos, BlockPos>> bedPositions = new ArrayList<>();

    public static void ScanBasicHouseStructure(Level world, BlockPos originalPos, Direction playerFacing) {
        BuildClear clearedSpace = new BuildClear();
        clearedSpace.getShape().setDirection(Direction.SOUTH);
        clearedSpace.getShape().setHeight(10);
        clearedSpace.getShape().setLength(12);
        clearedSpace.getShape().setWidth(13);
        clearedSpace.getStartingPosition().setSouthOffset(1);
        clearedSpace.getStartingPosition().setEastOffset(5);
        clearedSpace.getStartingPosition().setHeightOffset(-1);

        BlockPos corner = originalPos.east(5).south().below();
        BlockPos corner2 = originalPos.west(8).south(13).above(10);

        Structure.ScanStructure(world, originalPos, corner, corner2,
                "..\\src\\main\\resources\\assets\\prefab\\structures\\basic_house.zip", clearedSpace,
                playerFacing, false, false);
    }

    public static void ScanRanchStructure(Level world, BlockPos originalPos, Direction playerFacing) {
        BuildClear clearedSpace = new BuildClear();
        clearedSpace.getShape().setDirection(Direction.SOUTH);
        clearedSpace.getShape().setHeight(7);
        clearedSpace.getShape().setLength(21);
        clearedSpace.getShape().setWidth(11);
        clearedSpace.getStartingPosition().setSouthOffset(1);
        clearedSpace.getStartingPosition().setEastOffset(8);
        clearedSpace.getStartingPosition().setHeightOffset(-1);

        Structure.ScanStructure(world, originalPos, originalPos.east(8).south().below(), originalPos.south(22).west(3).above(8),
                "..\\src\\main\\resources\\assets\\prefab\\structures\\ranch_house.zip", clearedSpace,
                playerFacing, false, false);
    }

    public static void ScanLoftStructure(Level world, BlockPos originalPos, Direction playerFacing) {
        BuildClear clearedSpace = new BuildClear();
        clearedSpace.getShape().setDirection(Direction.SOUTH);
        clearedSpace.getShape().setHeight(9);
        clearedSpace.getShape().setLength(13);
        clearedSpace.getShape().setWidth(16);
        clearedSpace.getStartingPosition().setSouthOffset(1);
        clearedSpace.getStartingPosition().setEastOffset(7);

        Structure.ScanStructure(world, originalPos, originalPos.east(7).south(), originalPos.south(14).west(8).above(9),
                "..\\src\\main\\resources\\assets\\prefab\\structures\\loft_house.zip", clearedSpace, playerFacing, false, false);
    }

    public static void ScanHobbitStructure(Level world, BlockPos originalPos, Direction playerFacing) {
        BuildClear clearedSpace = new BuildClear();
        clearedSpace.getShape().setDirection(Direction.SOUTH);
        clearedSpace.getShape().setHeight(12);
        clearedSpace.getShape().setLength(16);
        clearedSpace.getShape().setWidth(16);
        clearedSpace.getStartingPosition().setSouthOffset(1);
        clearedSpace.getStartingPosition().setEastOffset(8);
        clearedSpace.getStartingPosition().setHeightOffset(-3);

        Structure.ScanStructure(world, originalPos, originalPos.east(8).south().below(3), originalPos.south(16).west(8).above(12),
                "..\\src\\main\\resources\\assets\\prefab\\structures\\hobbit_house.zip", clearedSpace,
                playerFacing, false, false);
    }

    public static void ScanDesert2Structure(Level world, BlockPos originalPos, Direction playerFacing) {
        BuildClear clearedSpace = new BuildClear();
        clearedSpace.getShape().setDirection(Direction.SOUTH);
        clearedSpace.getShape().setHeight(10);
        clearedSpace.getShape().setLength(14);
        clearedSpace.getShape().setWidth(11);
        clearedSpace.getStartingPosition().setSouthOffset(1);
        clearedSpace.getStartingPosition().setEastOffset(8);
        clearedSpace.getStartingPosition().setHeightOffset(-1);

        BlockPos corner = originalPos.east(8).south().below();
        BlockPos corner2 = originalPos.west(6).south(16).above(10);

        Structure.ScanStructure(world, originalPos, corner, corner2,
                "..\\src\\main\\resources\\assets\\prefab\\structures\\desert_house2.zip", clearedSpace,
                playerFacing, false, false);
    }

    public static void ScanSubAquaticStructure(Level world, BlockPos originalPos, Direction playerFacing) {
        BuildClear clearedSpace = new BuildClear();
        clearedSpace.getShape().setDirection(Direction.SOUTH);
        clearedSpace.getShape().setHeight(13);
        clearedSpace.getShape().setLength(9);
        clearedSpace.getShape().setWidth(12);
        clearedSpace.getStartingPosition().setSouthOffset(1);
        clearedSpace.getStartingPosition().setEastOffset(8);
        clearedSpace.getStartingPosition().setHeightOffset(-1);

        BlockPos corner = originalPos.east(8).south().below();
        BlockPos corner2 = originalPos.west(4).south(10).above(12);

        Structure.ScanStructure(world, originalPos, corner, corner2,
                "..\\src\\main\\resources\\assets\\prefab\\structures\\subaquatic_house.zip", clearedSpace,
                playerFacing, true, true);
    }

    public static void ScanStructure(Level world, BlockPos originalPos, Direction playerFacing, String structureFileName, boolean includeAir, boolean excludeWater) {
        BuildClear clearedSpace = new BuildClear();
        clearedSpace.getShape().setDirection(Direction.SOUTH);
        clearedSpace.getShape().setHeight(8);
        clearedSpace.getShape().setLength(16);
        clearedSpace.getShape().setWidth(16);
        clearedSpace.getStartingPosition().setSouthOffset(1);
        clearedSpace.getStartingPosition().setEastOffset(8);
        clearedSpace.getStartingPosition().setHeightOffset(-1);

        BuildShape buildShape = clearedSpace.getShape();
        PositionOffset offset = clearedSpace.getStartingPosition();

        int downOffset = offset.getHeightOffset() < 0 ? Math.abs(offset.getHeightOffset()) : 0;
        BlockPos cornerPos = originalPos.east(offset.getEastOffset()).south(offset.getSouthOffset()).below(downOffset);

        Structure.ScanStructure(
                world,
                originalPos,
                cornerPos,
                cornerPos.south(buildShape.getLength()).west(buildShape.getWidth()).above(buildShape.getHeight()),
                "..\\src\\main\\resources\\assets\\prefab\\structures\\" + structureFileName + ".zip",
                clearedSpace,
                playerFacing,
                includeAir,
                excludeWater);
    }

    @Override
    protected Boolean CustomBlockProcessingHandled(StructureConfiguration configuration, BuildBlock block, Level world, BlockPos originalPos,
                                                   Direction assumedNorth, Block foundBlock, BlockState blockState, Player player) {
        HouseConfiguration houseConfig = (HouseConfiguration) configuration;

        if ((!houseConfig.addBed && foundBlock instanceof BedBlock) || (!houseConfig.addChest && foundBlock instanceof ChestBlock)
                || (!houseConfig.addTorches && foundBlock instanceof TorchBlock)
                || (!houseConfig.addCraftingTable && foundBlock instanceof CraftingTableBlock)
                || (!houseConfig.addFurnace && foundBlock instanceof FurnaceBlock)
                || (!houseConfig.addChest && foundBlock instanceof BarrelBlock)
                || (foundBlock instanceof SeagrassBlock)
                || (foundBlock instanceof TallSeagrassBlock)) {
            // Don't place the block, returning true means that this has been
            // "handled"
            return true;
        }

        if (foundBlock instanceof FurnaceBlock) {
            this.furnacePosition = block.getStartingPosition().getRelativePosition(
                    originalPos,
                    this.getClearSpace().getShape().getDirection(),
                    configuration.houseFacing);
        } else if (foundBlock instanceof TrapDoorBlock && houseConfig.addMineShaft && this.trapDoorPosition == null) {
            // The trap door will still be added, but the mine shaft may not be
            // built.
            this.trapDoorPosition = block.getStartingPosition().getRelativePosition(
                    originalPos,
                    this.getClearSpace().getShape().getDirection(),
                    configuration.houseFacing);
        } else if ((foundBlock instanceof ChestBlock && this.chestPosition == null)
                || (foundBlock instanceof BarrelBlock && this.chestPosition == null)) {
            this.chestPosition = block.getStartingPosition().getRelativePosition(
                    originalPos,
                    this.getClearSpace().getShape().getDirection(),
                    configuration.houseFacing);
        } else if (foundBlock instanceof StandingSignBlock) {
            this.signPosition = block.getStartingPosition().getRelativePosition(
                    originalPos,
                    this.getClearSpace().getShape().getDirection(),
                    configuration.houseFacing);
        } else if (foundBlock == Blocks.SPONGE && houseConfig.addMineShaft) {
            // Sponges are sometimes used in-place of trapdoors when trapdoors are used for decoration.
            this.trapDoorPosition = block.getStartingPosition().getRelativePosition(
                    originalPos,
                    this.getClearSpace().getShape().getDirection(),
                    configuration.houseFacing).above();
        } else if (foundBlock instanceof BedBlock && houseConfig.addBed) {
            BlockPos bedHeadPosition = block.getStartingPosition().getRelativePosition(originalPos, this.getClearSpace().getShape().getDirection(), configuration.houseFacing);
            BlockPos bedFootPosition = block.getSubBlock().getStartingPosition().getRelativePosition(
                    originalPos,
                    this.getClearSpace().getShape().getDirection(),
                    configuration.houseFacing);

            this.bedPositions.add(new Tuple<>(bedHeadPosition, bedFootPosition));

            // Return true so the bed is not placed.
            return true;
        }

        if (foundBlock.getRegistryName().getNamespace().equals(Blocks.WHITE_STAINED_GLASS.getRegistryName().getNamespace())
                && foundBlock.getRegistryName().getPath().endsWith("stained_glass")) {
            blockState = this.getStainedGlassBlock(houseConfig.glassColor);

            block.setBlockState(blockState);
            this.priorityOneBlocks.add(block);

            return true;
        } else if (foundBlock.getRegistryName().getNamespace().equals(Blocks.WHITE_STAINED_GLASS_PANE.getRegistryName().getNamespace())
                && foundBlock.getRegistryName().getPath().endsWith("stained_glass_pane")) {
            blockState = this.getStainedGlassPaneBlock(houseConfig.glassColor);

            BuildBlock.SetBlockState(
                    configuration,
                    world,
                    originalPos,
                    assumedNorth,
                    block,
                    foundBlock,
                    blockState,
                    this);

            this.priorityOneBlocks.add(block);
            return true;
        }

        return false;
    }

    /**
     * This method is used after the main building is build for any additional structures or modifications.
     *
     * @param configuration The structure configuration.
     * @param world         The current world.
     * @param originalPos   The original position clicked on.
     * @param assumedNorth  The assumed northern direction.
     * @param player        The player which initiated the construction.
     */
    @Override
    public void AfterBuilding(StructureConfiguration configuration, ServerLevel world, BlockPos originalPos, Direction assumedNorth, Player player) {
        HouseConfiguration houseConfig = (HouseConfiguration) configuration;
        EntityPlayerConfiguration playerConfig = EntityPlayerConfiguration.loadFromEntityData(player);

        ArrayList<BlockPos> furnacePositions = new ArrayList<>();

        if (this.furnacePosition != null) {
            furnacePositions.add(this.furnacePosition);
        }

        BuildingMethods.FillFurnaces(world, furnacePositions);

        if (this.chestPosition != null && houseConfig.addChestContents) {
            // Fill the chest.
            BuildingMethods.FillChest(world, this.chestPosition);
        }

        if (this.trapDoorPosition != null && this.trapDoorPosition.getY() > 15 && houseConfig.addMineShaft) {
            // Build the mineshaft.
            BuildingMethods.PlaceMineShaft(world, this.trapDoorPosition.below(), houseConfig.houseFacing, false);
        }

        if (this.signPosition != null) {
            BlockEntity tileEntity = world.getBlockEntity(this.signPosition);

            if (tileEntity instanceof SignBlockEntity) {
                SignBlockEntity signTile = (SignBlockEntity) tileEntity;
                signTile.setMessage(0, Utils.createTextComponent("This is"));

                if (player.getDisplayName().getString().length() >= 15) {
                    signTile.setMessage(1, Utils.createTextComponent(player.getDisplayName().getString()));
                } else {
                    signTile.setMessage(1, Utils.createTextComponent(player.getDisplayName().getString() + "'s"));
                }

                signTile.setMessage(2, Utils.createTextComponent("house!"));
            }
        }

        if (this.bedPositions.size() > 0 && houseConfig.addBed) {
            for (Tuple<BlockPos, BlockPos> bedPosition : this.bedPositions) {
                BuildingMethods.PlaceColoredBed(world, bedPosition.getFirst(), bedPosition.getSecond(), houseConfig.bedColor);
            }
        }

        // Make sure to set this value so the player cannot fill the chest a second time.
        playerConfig.builtStarterHouse = true;
        playerConfig.saveToPlayer(player);

        // Make sure to send a message to the client to sync up the server player information and the client player
        // information.
        Prefab.network.sendTo(new PlayerEntityTagMessage(playerConfig.getModIsPlayerNewTag(player)), ((ServerPlayer) player).connection.connection,
                NetworkDirection.PLAY_TO_CLIENT);
    }
}
