package com.wuest.prefab.structures.base;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.wuest.prefab.Prefab;
import com.wuest.prefab.Triple;
import com.wuest.prefab.ZipUtil;
import com.wuest.prefab.blocks.FullDyeColor;
import com.wuest.prefab.gui.GuiLangKeys;
import com.wuest.prefab.structures.config.StructureConfiguration;
import com.wuest.prefab.structures.events.StructureEventHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Material;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Each structure represents a building which is pre-defined in a JSON file.
 *
 * @author WuestMan
 */
@SuppressWarnings({"unchecked", "WeakerAccess", "ConstantConditions"})
public class Structure {
    public ArrayList<BlockPos> allBlockPositions = new ArrayList<>();
    public ArrayList<BlockPos> clearedBlockPos = new ArrayList<BlockPos>();
    public ArrayList<BuildBlock> priorityOneBlocks = new ArrayList<BuildBlock>();
    public ArrayList<BuildBlock> priorityTwoBlocks = new ArrayList<>();
    public ArrayList<BuildBlock> priorityThreeBlocks = new ArrayList<>();
    public ArrayList<BuildBlock> priorityFourBlocks = new ArrayList<>();
    public ArrayList<BuildBlock> priorityFiveBlocks = new ArrayList<>();
    public ArrayList<BuildBlock> airBlocks = new ArrayList<>();
    public StructureConfiguration configuration;
    public ServerLevel world;
    public BlockPos originalPos;
    public Direction assumedNorth;
    public boolean hasAirBlocks = false;
    public boolean entitiesRemoved = false;

    @Expose
    public ArrayList<BuildTileEntity> tileEntities = new ArrayList<BuildTileEntity>();
    @Expose
    public ArrayList<BuildEntity> entities = new ArrayList<BuildEntity>();
    @Expose
    private String name;
    @Expose
    private BuildClear clearSpace;
    @Expose
    private ArrayList<BuildBlock> blocks;

    public Structure() {
        this.Initialize();
    }

    /**
     * Creates an instance of the structure after reading from a resource location and converting it from JSON.
     *
     * @param <T>              The type which extends Structure.
     * @param resourceLocation The location of the JSON file to load. Example: "assets/prefab/structures/warehouse.json"
     * @param child            The child class which extends Structure.
     * @return Null if the resource wasn't found or the JSON could not be parsed, otherwise the de-serialized object.
     */
    public static <T extends Structure> T CreateInstance(String resourceLocation, Class<? extends Structure> child) {
        T structure = null;

        Gson file = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        structure = (T) file.fromJson(ZipUtil.decompressResource(resourceLocation), child);

        return structure;
    }

    public static void CreateStructureFile(Structure structure, String fileLocation) {
        try {
            Gson converter = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            StringWriter stringWriter = new StringWriter();
            converter.toJson(structure, stringWriter);

            ZipUtil.zipStringToFile(stringWriter.toString(), fileLocation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void ScanStructure(Level world, BlockPos originalPos, BlockPos cornerPos1, BlockPos cornerPos2, String fileLocation, BuildClear clearedSpace,
                                     Direction playerFacing, boolean includeAir, boolean excludeWater) {
        Structure scannedStructure = new Structure();
        scannedStructure.setClearSpace(clearedSpace);

        for (BlockPos currentPos : BlockPos.betweenClosed(cornerPos1, cornerPos2)) {
            if (world.isEmptyBlock(currentPos) && !includeAir) {
                continue;
            }

            BlockState currentState = world.getBlockState(currentPos);
            Block currentBlock = currentState.getBlock();

            if (currentState.getMaterial() == Material.WATER && excludeWater) {
                continue;
            }

            BuildBlock buildBlock = Structure.createBuildBlockFromBlockState(currentState, currentBlock, currentPos, originalPos);

            if (currentBlock instanceof DoorBlock) {
                DoubleBlockHalf blockHalf = currentState.getValue(DoorBlock.HALF);

                if (blockHalf == DoubleBlockHalf.LOWER) {
                    BlockState upperHalfState = world.getBlockState(currentPos.above());

                    if (upperHalfState.getBlock() instanceof DoorBlock) {
                        Block upperBlock = upperHalfState.getBlock();
                        BuildBlock upperHalf = Structure.createBuildBlockFromBlockState(upperHalfState, upperBlock, currentPos.above(), originalPos);

                        buildBlock.setSubBlock(upperHalf);
                    }
                } else {
                    // Don't process upper door halves. These were already done.
                    continue;
                }
            } else if (currentBlock instanceof BedBlock) {
                BedPart bedPart = currentState.getValue(BedBlock.PART);

                if (bedPart == BedPart.HEAD) {
                    BlockState bedFoot = null;
                    boolean foundFoot = false;
                    Direction facing = Direction.NORTH;

                    while (!foundFoot) {
                        bedFoot = world.getBlockState(currentPos.relative(facing));

                        if (bedFoot.getBlock() instanceof BedBlock && bedFoot.getValue(BedBlock.PART) == BedPart.FOOT) {
                            foundFoot = true;
                            break;
                        }

                        facing = facing.getClockWise();

                        if (facing == Direction.NORTH) {
                            // Got back to north, break out to avoid infinite loop.
                            break;
                        }
                    }

                    if (foundFoot) {
                        Block footBedBlock = bedFoot.getBlock();
                        BuildBlock bed = Structure.createBuildBlockFromBlockState(bedFoot, footBedBlock, currentPos.relative(facing), originalPos);
                        buildBlock.setSubBlock(bed);
                    }
                } else {
                    // Don't process foot of bed, it was already done.
                    continue;
                }
            }

            scannedStructure.getBlocks().add(buildBlock);

            BlockEntity tileEntity = world.getBlockEntity(currentPos);

            if (tileEntity != null) {
                // Don't write data for empty tile entities.
                if ((tileEntity instanceof ChestBlockEntity && ((ChestBlockEntity) tileEntity).isEmpty())
                        || (tileEntity instanceof FurnaceBlockEntity && ((FurnaceBlockEntity) tileEntity).isEmpty())) {
                    continue;
                }

                ResourceLocation resourceLocation = ForgeRegistries.BLOCK_ENTITIES.getKey(tileEntity.getType());
                CompoundTag tagCompound = new CompoundTag();
                tileEntity.save(tagCompound);

                BuildTileEntity buildTileEntity = new BuildTileEntity();
                assert resourceLocation != null;
                buildTileEntity.setEntityDomain(resourceLocation.getNamespace());
                buildTileEntity.setEntityName(resourceLocation.getPath());
                buildTileEntity.setStartingPosition(Structure.getStartingPositionFromOriginalAndCurrentPosition(currentPos, originalPos));
                buildTileEntity.setEntityNBTData(tagCompound);
                scannedStructure.tileEntities.add(buildTileEntity);
            }
        }

        int x_radiusRangeBegin = Math.min(cornerPos1.getX(), cornerPos2.getX());
        int x_radiusRangeEnd = Math.max(cornerPos1.getX(), cornerPos2.getX());
        int y_radiusRangeBegin = Math.min(cornerPos1.getY(), cornerPos2.getY());
        int y_radiusRangeEnd = Math.max(cornerPos1.getY(), cornerPos2.getY());
        int z_radiusRangeBegin = Math.min(cornerPos1.getZ(), cornerPos2.getZ());
        int z_radiusRangeEnd = Math.max(cornerPos1.getZ(), cornerPos2.getZ());

        AABB axis = new AABB(cornerPos1, cornerPos2);

        for (Entity entity : world.getEntities(null, axis)) {
            // TODO: This was the "getPosition" method.
            BlockPos entityPos = entity.blockPosition();

            if (entity instanceof HangingEntity) {
                // Use the HangingEntity getPos function instead since it is more accurate for itemframes and paintings.
                entityPos = ((HangingEntity)entity).getPos();
            }

            if (entityPos.getX() >= x_radiusRangeBegin && entityPos.getX() <= x_radiusRangeEnd
                    && entityPos.getZ() >= z_radiusRangeBegin && entityPos.getZ() <= z_radiusRangeEnd
                    && entityPos.getY() >= y_radiusRangeBegin && entityPos.getY() <= y_radiusRangeEnd) {
                BuildEntity buildEntity = new BuildEntity();
                buildEntity.setEntityResourceString(ForgeRegistries.ENTITIES.getKey(entity.getType()));
                buildEntity.setStartingPosition(Structure.getStartingPositionFromOriginalAndCurrentPosition(entityPos, originalPos));

                // The function calls below get the following fields from the "entity" class. posX, posY, posZ.
                // This will probably have to change when the mappings get updated.
                buildEntity.entityXAxisOffset = entityPos.getX() - entity.getX();
                buildEntity.entityYAxisOffset = entityPos.getY() - entity.getY();
                buildEntity.entityZAxisOffset = entityPos.getZ() - entity.getZ();

                if (entity instanceof ItemFrame) {
                    buildEntity.entityYAxisOffset = buildEntity.entityYAxisOffset * -1;
                }

                if (entity instanceof HangingEntity) {
                    buildEntity.entityFacing = entity.getDirection();
                }

                CompoundTag entityTagCompound = new CompoundTag();
                entity.saveAsPassenger(entityTagCompound);
                buildEntity.setEntityNBTData(entityTagCompound);
                scannedStructure.entities.add(buildEntity);
            }
        }

        Structure.CreateStructureFile(scannedStructure, fileLocation);
    }

    /**
     * Creates a build block from the current block state.
     *
     * @param currentState The block state.
     * @param currentBlock The current block.
     * @param currentPos   The current position.
     * @return A new Build block object.
     */
    public static BuildBlock createBuildBlockFromBlockState(BlockState currentState, Block currentBlock, BlockPos currentPos, BlockPos originalPos) {
        BuildBlock buildBlock = new BuildBlock();
        buildBlock.setBlockDomain(currentBlock.getRegistryName().getNamespace());
        buildBlock.setBlockName(currentBlock.getRegistryName().getPath());
        buildBlock.setStartingPosition(Structure.getStartingPositionFromOriginalAndCurrentPosition(currentPos, originalPos));
        buildBlock.blockPos = currentPos;

        Collection<Property<?>> properties = currentState.getProperties();

        for (Property<?> entry : properties) {
            BuildProperty property = new BuildProperty();

            property.setName(entry.getName());

            Comparable<?> value = currentState.getValue(entry);

            try {
                if (currentBlock instanceof RotatedPillarBlock && property.getName().equals("axis")) {
                    property.setValue(((Direction.Axis) value).getSerializedName());
                } else if (currentBlock instanceof CarpetBlock && property.getName().equals("color")) {
                    DyeColor dyeColor = (DyeColor) value;
                    property.setValue(dyeColor.getSerializedName());
                } else if (value instanceof StringRepresentable) {
                    StringRepresentable stringSerializable = (StringRepresentable) value;
                    property.setValue(stringSerializable.getSerializedName());
                } else {
                    property.setValue(value.toString());
                }
            } catch (Exception ex) {
                Prefab.LOGGER.error("Unable to set property [" + property.getName() + "] to value [" + value + "] for Block [" + buildBlock.getBlockDomain() + ":" + buildBlock.getBlockName() + "].");
                throw ex;
            }

            buildBlock.getProperties().add(property);
        }

        return buildBlock;
    }

    public static PositionOffset getStartingPositionFromOriginalAndCurrentPosition(BlockPos currentPos, BlockPos originalPos) {
        // if (currentPos.getX() > originalPos.getX()). currentPos is "East"
        // of hitBlock
        // if (currentPos.getZ() > originalPos.getZ()). currentPos is
        // "South" of hitBlock
        PositionOffset positionOffSet = new PositionOffset();

        if (currentPos.getX() > originalPos.getX()) {
            positionOffSet.setEastOffset(currentPos.getX() - originalPos.getX());
        } else {
            positionOffSet.setWestOffset(originalPos.getX() - currentPos.getX());
        }

        if (currentPos.getZ() > originalPos.getZ()) {
            positionOffSet.setSouthOffset(currentPos.getZ() - originalPos.getZ());
        } else {
            positionOffSet.setNorthOffset(originalPos.getZ() - currentPos.getZ());
        }

        positionOffSet.setHeightOffset(currentPos.getY() - originalPos.getY());

        return positionOffSet;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public BuildClear getClearSpace() {
        return this.clearSpace;
    }

    public void setClearSpace(BuildClear value) {
        this.clearSpace = value;
    }

    public ArrayList<BuildBlock> getBlocks() {
        return this.blocks;
    }

    public void setBlocks(ArrayList<BuildBlock> value) {
        this.blocks = value;
    }

    public void Initialize() {
        this.name = "";
        this.clearSpace = new BuildClear();
        this.blocks = new ArrayList<>();
    }

    /**
     * This is the main building method for this structure.
     *
     * @param configuration The configuration the user updated.
     * @param world         The current world.
     * @param originalPos   The block the user clicked on.
     * @param assumedNorth  This should always be "NORTH" when the file is based on a scan.
     * @param player        The player requesting the structure.
     * @return True if the build can occur, otherwise false.
     */
    public boolean BuildStructure(StructureConfiguration configuration, ServerLevel world, BlockPos originalPos, Direction assumedNorth, Player player) {
        BlockPos startBlockPos = this.clearSpace.getStartingPosition().getRelativePosition(originalPos, this.clearSpace.getShape().getDirection(), configuration.houseFacing);
        BlockPos endBlockPos = startBlockPos.relative(configuration.houseFacing.getCounterClockWise(), this.clearSpace.getShape().getWidth() - 1)
                .relative(configuration.houseFacing.getOpposite(), this.clearSpace.getShape().getWidth() - 1)
                .relative(Direction.UP, this.clearSpace.getShape().getHeight());

        // Make sure this structure can be placed here.
        Triple<Boolean, BlockState, BlockPos> checkResult = BuildingMethods.CheckBuildSpaceForAllowedBlockReplacement(world, startBlockPos, endBlockPos, player);

        if (!checkResult.getFirst()) {
            // Send a message to the player saying that the structure could not
            // be built.
            TranslatableComponent message = new TranslatableComponent(
                    GuiLangKeys.GUI_STRUCTURE_NOBUILD,
                    checkResult.getSecond().getBlock().getRegistryName().toString(),
                    checkResult.getThird().getX(),
                    checkResult.getThird().getY(),
                    checkResult.getThird().getZ());

            message.setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN));
            player.sendMessage(message, player.getUUID());
            return false;
        }

        if (!this.BeforeBuilding(configuration, world, originalPos, assumedNorth, player)) {
            // First, clear the area where the structure will be built.
            this.ClearSpace(configuration, world, originalPos, assumedNorth);

            boolean blockPlacedWithCobbleStoneInstead = false;

            // Now place all of the blocks.
            for (BuildBlock block : this.getBlocks()) {
                Block foundBlock = ForgeRegistries.BLOCKS.getValue(block.getResourceLocation());

                if (foundBlock != null) {
                    BlockState blockState = foundBlock.defaultBlockState();
                    BuildBlock subBlock = null;

                    // Check if water should be replaced with cobble.
                    if (!this.WaterReplacedWithCobbleStone(configuration, block, world, originalPos, assumedNorth, foundBlock, blockState, player)
                            && !this.CustomBlockProcessingHandled(configuration, block, world, originalPos, assumedNorth, foundBlock, blockState, player)) {
                        block = BuildBlock.SetBlockState(configuration, world, originalPos, assumedNorth, block, foundBlock, blockState, this);

                        if (block.getSubBlock() != null) {
                            foundBlock = ForgeRegistries.BLOCKS.getValue(block.getSubBlock().getResourceLocation());
                            blockState = foundBlock.defaultBlockState();

                            subBlock = BuildBlock.SetBlockState(configuration, world, originalPos, assumedNorth, block.getSubBlock(), foundBlock, blockState, this);
                        }

                        if (subBlock != null) {
                            block.setSubBlock(subBlock);
                        }

                        boolean priorityTwoBlock = foundBlock instanceof HopperBlock;

                        boolean priorityThreeBlock = foundBlock instanceof TorchBlock
                                || foundBlock instanceof SignBlock
                                || foundBlock instanceof LeverBlock
                                || foundBlock instanceof ButtonBlock
                                || foundBlock instanceof BedBlock
                                || foundBlock instanceof CarpetBlock
                                || foundBlock instanceof FlowerPotBlock
                                || foundBlock instanceof SugarCaneBlock
                                || foundBlock instanceof BasePressurePlateBlock
                                || foundBlock instanceof DoorBlock
                                || foundBlock instanceof LadderBlock
                                || foundBlock instanceof VineBlock
                                || foundBlock instanceof RedStoneWireBlock
                                || foundBlock instanceof DiodeBlock
                                || foundBlock instanceof AbstractBannerBlock
                                || foundBlock instanceof LanternBlock
                                || foundBlock instanceof MushroomBlock
                                || foundBlock instanceof BaseRailBlock;

                        boolean priorityFourBlock = foundBlock instanceof SandBlock;

                        boolean priorityFiveBlock = foundBlock instanceof SugarCaneBlock ||
                                foundBlock instanceof CactusBlock
                                || foundBlock instanceof DeadBushBlock
                                || foundBlock instanceof CoralBlock
                                || foundBlock instanceof RedstoneTorchBlock;

                        if (!block.getHasFacing()) {
                            if (subBlock != null) {
                                block.setSubBlock(subBlock);
                            }

                            if (priorityFiveBlock) {
                                this.priorityFiveBlocks.add(block);
                            } else if (priorityFourBlock) {
                                this.priorityFourBlocks.add(block);
                            } else if (priorityThreeBlock) {
                                this.priorityThreeBlocks.add(block);
                            } else if (foundBlock instanceof AirBlock) {
                                this.airBlocks.add(block);
                            } else if (foundBlock instanceof EntityBlock || priorityTwoBlock) {
                                this.priorityTwoBlocks.add(block);
                            } else {
                                this.priorityOneBlocks.add(block);
                            }
                        } else {
                            // These blocks may be attached to other facing blocks and must be done later.
                            if (priorityThreeBlock) {
                                this.priorityThreeBlocks.add(block);
                            } else {
                                this.priorityTwoBlocks.add(block);
                            }
                        }
                    }
                } else {
                    // Cannot find this block in the registry. This can happen if a structure file has a mod block that
                    // no longer exists.
                    // In this case, print an informational message and replace it with cobblestone.
                    String blockTypeNotFound = block.getResourceLocation().toString();
                    block = BuildBlock.SetBlockState(configuration, world, originalPos, assumedNorth, block, Blocks.COBBLESTONE, Blocks.COBBLESTONE.defaultBlockState(), this);
                    this.priorityOneBlocks.add(block);

                    if (!blockPlacedWithCobbleStoneInstead) {
                        blockPlacedWithCobbleStoneInstead = true;
                        Prefab.LOGGER
                                .warn("A Block was in the structure, but it is not registered. This block was replaced with vanilla cobblestone instead. Block type not found: ["
                                        + blockTypeNotFound + "]");
                    }
                }
            }

            this.configuration = configuration;
            this.world = world;
            this.assumedNorth = assumedNorth;
            this.originalPos = originalPos;

            if (StructureEventHandler.structuresToBuild.containsKey(player)) {
                StructureEventHandler.structuresToBuild.get(player).add(this);
            } else {
                ArrayList<Structure> structures = new ArrayList<Structure>();
                structures.add(this);
                StructureEventHandler.structuresToBuild.put(player, structures);
            }
        }

        return true;
    }

    /**
     * This method is to process before a clear space block is set to air.
     *
     * @param pos The block position being processed.
     */
    public void BeforeClearSpaceBlockReplaced(BlockPos pos) {
    }

    public void BeforeHangingEntityRemoved(HangingEntity hangingEntity) {
    }

    public BlockState getStainedGlassBlock(FullDyeColor color) {
        switch (color) {
            case BLACK: {
                return Blocks.BLACK_STAINED_GLASS.defaultBlockState();
            }
            case BLUE: {
                return Blocks.BLUE_STAINED_GLASS.defaultBlockState();
            }
            case BROWN: {
                return Blocks.BROWN_STAINED_GLASS.defaultBlockState();
            }
            case GRAY: {
                return Blocks.GRAY_STAINED_GLASS.defaultBlockState();
            }
            case GREEN: {
                return Blocks.GREEN_STAINED_GLASS.defaultBlockState();
            }
            case LIGHT_BLUE: {
                return Blocks.LIGHT_BLUE_STAINED_GLASS.defaultBlockState();
            }
            case LIGHT_GRAY: {
                return Blocks.LIGHT_GRAY_STAINED_GLASS.defaultBlockState();
            }
            case LIME: {
                return Blocks.LIME_STAINED_GLASS.defaultBlockState();
            }
            case MAGENTA: {
                return Blocks.MAGENTA_STAINED_GLASS.defaultBlockState();
            }
            case ORANGE: {
                return Blocks.ORANGE_STAINED_GLASS.defaultBlockState();
            }
            case PINK: {
                return Blocks.PINK_STAINED_GLASS.defaultBlockState();
            }
            case PURPLE: {
                return Blocks.PURPLE_STAINED_GLASS.defaultBlockState();
            }
            case RED: {
                return Blocks.RED_STAINED_GLASS.defaultBlockState();
            }
            case WHITE: {
                return Blocks.WHITE_STAINED_GLASS.defaultBlockState();
            }
            case YELLOW: {
                return Blocks.YELLOW_STAINED_GLASS.defaultBlockState();
            }
            case CLEAR: {
                return Blocks.GLASS.defaultBlockState();
            }
            default: {
                return Blocks.CYAN_STAINED_GLASS.defaultBlockState();
            }
        }
    }

    public BlockState getStainedGlassPaneBlock(FullDyeColor color) {
        switch (color) {
            case BLACK: {
                return Blocks.BLACK_STAINED_GLASS_PANE.defaultBlockState();
            }
            case BLUE: {
                return Blocks.BLUE_STAINED_GLASS_PANE.defaultBlockState();
            }
            case BROWN: {
                return Blocks.BROWN_STAINED_GLASS_PANE.defaultBlockState();
            }
            case GRAY: {
                return Blocks.GRAY_STAINED_GLASS_PANE.defaultBlockState();
            }
            case GREEN: {
                return Blocks.GREEN_STAINED_GLASS_PANE.defaultBlockState();
            }
            case LIGHT_BLUE: {
                return Blocks.LIGHT_BLUE_STAINED_GLASS_PANE.defaultBlockState();
            }
            case LIGHT_GRAY: {
                return Blocks.LIGHT_GRAY_STAINED_GLASS_PANE.defaultBlockState();
            }
            case LIME: {
                return Blocks.LIME_STAINED_GLASS_PANE.defaultBlockState();
            }
            case MAGENTA: {
                return Blocks.MAGENTA_STAINED_GLASS_PANE.defaultBlockState();
            }
            case ORANGE: {
                return Blocks.ORANGE_STAINED_GLASS_PANE.defaultBlockState();
            }
            case PINK: {
                return Blocks.PINK_STAINED_GLASS_PANE.defaultBlockState();
            }
            case PURPLE: {
                return Blocks.PURPLE_STAINED_GLASS_PANE.defaultBlockState();
            }
            case RED: {
                return Blocks.RED_STAINED_GLASS_PANE.defaultBlockState();
            }
            case WHITE: {
                return Blocks.WHITE_STAINED_GLASS_PANE.defaultBlockState();
            }
            case YELLOW: {
                return Blocks.YELLOW_STAINED_GLASS_PANE.defaultBlockState();
            }
            case CLEAR: {
                return Blocks.GLASS_PANE.defaultBlockState();
            }
            default: {
                return Blocks.CYAN_STAINED_GLASS_PANE.defaultBlockState();
            }
        }
    }

    /**
     * This method is used before any building occurs to check for things or possibly pre-build locations. Note: This is
     * even done before blocks are cleared.
     *
     * @param configuration The structure configuration.
     * @param world         The current world.
     * @param originalPos   The original position clicked on.
     * @param assumedNorth  The assumed northern direction.
     * @param player        The player which initiated the construction.
     * @return False if processing should continue, otherwise true to cancel processing.
     */
    protected boolean BeforeBuilding(StructureConfiguration configuration, Level world, BlockPos originalPos, Direction assumedNorth, Player player) {
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
    public void AfterBuilding(StructureConfiguration configuration, ServerLevel world, BlockPos originalPos, Direction assumedNorth, Player player) {
    }

    protected void ClearSpace(StructureConfiguration configuration, Level world, BlockPos originalPos, Direction assumedNorth) {
        if (this.clearSpace.getShape().getWidth() > 0
                && this.clearSpace.getShape().getLength() > 0) {
            BlockPos startBlockPos = this.clearSpace.getStartingPosition().getRelativePosition(originalPos, this.clearSpace.getShape().getDirection(), configuration.houseFacing);

            BlockPos endBlockPos = startBlockPos
                    .relative(configuration.houseFacing.getOpposite().getClockWise(), this.clearSpace.getShape().getWidth() - 1)
                    .relative(configuration.houseFacing.getOpposite(), this.clearSpace.getShape().getLength() - 1)
                    .relative(Direction.UP, this.clearSpace.getShape().getHeight());

            this.clearedBlockPos = new ArrayList<>();

            for (BlockPos pos : BlockPos.betweenClosed(startBlockPos, endBlockPos)) {
                if (this.BlockShouldBeClearedDuringConstruction(configuration, world, originalPos, assumedNorth, pos)) {
                    this.clearedBlockPos.add(new BlockPos(pos));
                    this.allBlockPositions.add(new BlockPos(pos));
                }
            }
        } else {
            this.clearedBlockPos = new ArrayList<>();
        }
    }

    protected Boolean CustomBlockProcessingHandled(StructureConfiguration configuration, BuildBlock block, Level world, BlockPos originalPos,
                                                   Direction assumedNorth, Block foundBlock, BlockState blockState, Player player) {
        return false;
    }

    protected Boolean BlockShouldBeClearedDuringConstruction(StructureConfiguration configuration, Level world, BlockPos originalPos, Direction assumedNorth, BlockPos blockPos) {
        return true;
    }

    /**
     * Determines if a water block was replaced with cobblestone because this structure was built in the nether or the
     * end.
     *
     * @param configuration The structure configuration.
     * @param block         The build block object.
     * @param world         The workd object.
     * @param originalPos   The original block position this structure was built on.
     * @param assumedNorth  The assumed north direction (typically north).
     * @param foundBlock    The actual block found at the current location.
     * @param blockState    The block state to set for the current block.
     * @param player        The player requesting this build.
     * @return Returns true if the water block was replaced by cobblestone, otherwise false.
     */
    protected Boolean WaterReplacedWithCobbleStone(StructureConfiguration configuration, BuildBlock block, Level world, BlockPos originalPos,
                                                   Direction assumedNorth, Block foundBlock, BlockState blockState, Player player) {
        // Replace water blocks with cobblestone when this is not the overworld.
        if (foundBlock instanceof LiquidBlock && blockState.getMaterial() == Material.WATER
                && (Level.OVERWORLD.compareTo(world.dimension()) != 0)) {
            block.setBlockDomain(Blocks.COBBLESTONE.getRegistryName().getNamespace());
            block.setBlockName(Blocks.COBBLESTONE.getRegistryName().getPath());
            block.setBlockState(Blocks.COBBLESTONE.defaultBlockState());

            // Add this as a priority 3 block since it should be done at the end.
            this.priorityThreeBlocks.add(block);
            return true;
        }

        return false;
    }
}