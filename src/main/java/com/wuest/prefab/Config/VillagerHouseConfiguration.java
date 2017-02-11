package com.wuest.prefab.Config;

import com.wuest.prefab.Config.ModConfiguration.CeilingFloorBlockType;
import com.wuest.prefab.Config.ModConfiguration.WallBlockType;
import com.wuest.prefab.Gui.GuiLangKeys;

import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

/**
 * This is the villager house configuration.
 * @author WuestMan
 *
 */
public class VillagerHouseConfiguration extends StructureConfiguration
{
	public static String tagKey = "villagerHouseConfig";
	
	private static String houseStyleTag = "houseStyle";

	public HouseStyle houseStyle;
	
	public VillagerHouseConfiguration()
	{
		super();
	}
	
	@Override
	public void Initialize()
	{
		super.Initialize();
		this.houseStyle = HouseStyle.FLAT_ROOF;
	}

	@Override
	protected NBTTagCompound CustomWriteToNBTTagCompound(NBTTagCompound tag)
	{
		tag.setInteger(VillagerHouseConfiguration.houseStyleTag, this.houseStyle.value);
		return tag;
	}

	@Override
	protected void CustomReadFromNBTTag(NBTTagCompound messageTag, StructureConfiguration config)
	{
		if (messageTag.hasKey(VillagerHouseConfiguration.houseStyleTag))
		{
			((VillagerHouseConfiguration)config).houseStyle = HouseStyle.ValueOf(messageTag.getInteger(VillagerHouseConfiguration.houseStyleTag));
		}
	}
	
	public VillagerHouseConfiguration ReadFromNBTTagCompound(NBTTagCompound messageTag) 
	{
		VillagerHouseConfiguration config = new VillagerHouseConfiguration();
		
		return (VillagerHouseConfiguration)super.ReadFromNBTTagCompound(messageTag, config);
	}

	public enum HouseStyle
	{
		FLAT_ROOF(0, GuiLangKeys.VILLAGER_HOUSE_FLAT_ROOF, new ResourceLocation("prefab", "textures/gui/village_house_flat.png"), 165, 128, "assets/prefab/structures/villager_house_flat.zip", 5, 6, 5, 2),
		ANGLED_ROOF(1, GuiLangKeys.VILLAGER_HOUSE_ANGLED_ROOF, new ResourceLocation("prefab", "textures/gui/village_house_angled.png"), 161, 155, "assets/prefab/structures/villager_house_angled.zip", 5, 6, 6, 2),
		FENCED_ROOF(2, GuiLangKeys.VILLAGER_HOUSE_FENCED_ROOF, new ResourceLocation("prefab", "textures/gui/village_house_fenced.png"), 159, 156, "assets/prefab/structures/villager_house_fenced.zip", 5, 6, 6, 2),
		BLACKSMITH(3, GuiLangKeys.VILLAGER_HOUSE_BLACKSMITH, new ResourceLocation("prefab", "textures/gui/village_house_blacksmith.png"), 157, 73, "assets/prefab/structures/villager_house_blacksmith.zip", 8, 6, 6, 4),
		LONG_HOUSE(4, GuiLangKeys.VILLAGER_HOUSE_LONGHOUSE, new ResourceLocation("prefab", "textures/gui/village_house_long.png"), 157, 114, "assets/prefab/structures/villager_house_long.zip", 9, 7, 7, 4);

		private final int value;
		private final String displayName;
		private final ResourceLocation housePicture;
		private final int imageWidth;
		private final int imageHeight;
		private final String structureLocation;  
		private final int width;
		private final int length;
		private final int height;
		private final int eastOffSet;
		
		HouseStyle(int newValue, String displayName, ResourceLocation housePicture, int imageWidth, int imageHeight, String structureLocation, int width, int length, int height, int eastOffSet) 
		{
			this.value = newValue;
			this.displayName = displayName;
			this.housePicture = housePicture;
			this.imageWidth = imageWidth;
			this.imageHeight = imageHeight;
			this.structureLocation = structureLocation;
			this.width = width;
			this.length = length;
			this.height = height;
			this.eastOffSet = eastOffSet;
		}

		public int getValue() 
		{
			return value; 
		}
		
		public String getDisplayName() 
		{
			return GuiLangKeys.translateString(this.displayName);
		}
		
		public ResourceLocation getHousePicture()
		{
			return this.housePicture;
		}
		
		public int getImageWidth()
		{
			return this.imageWidth;
		}
		
		public int getImageHeight()
		{
			return this.imageHeight;
		}
		
		public int getWidth()
		{
			return this.width;
		}
		
		public int getLength()
		{
			return this.length;
		}
		
		public int getHeight()
		{
			return this.height;
		}
		
		public int getEastOffSet()
		{
			return this.eastOffSet;
		}
		
		public String getStructureLocation()
		{
			return this.structureLocation;
		}

		public static HouseStyle ValueOf(int value)
		{
			switch (value)
			{
				case 0:
				{
					return HouseStyle.FLAT_ROOF;
				}
	
				case 1:
				{
					return HouseStyle.ANGLED_ROOF;
				}
				
				case 2:
				{
					return HouseStyle.FENCED_ROOF;
				}
				
				case 3:
				{
					return HouseStyle.BLACKSMITH;
				}
				
				case 4:
				{
					return HouseStyle.LONG_HOUSE;
				}
	
				default:
				{
					return HouseStyle.FLAT_ROOF;
				}
			}
		}
	}
}