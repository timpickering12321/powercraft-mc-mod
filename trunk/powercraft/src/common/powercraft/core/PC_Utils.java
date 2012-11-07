package powercraft.core;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Block;
import net.minecraft.src.CraftingManager;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityList;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.EnumGameType;
import net.minecraft.src.FurnaceRecipes;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.IInventory;
import net.minecraft.src.IRecipe;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.StringTranslate;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntityMobSpawner;
import net.minecraft.src.World;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class PC_Utils {
	
	private static PC_Utils instance;
	public static final int BACK = 0, LEFT = 1, RIGHT = 2, FRONT = 3, BOTTOM = 4, TOP = 5;
	
	public static boolean setUtilsInstance(PC_Utils instance){
		if(PC_Utils.instance==null){
			PC_Utils.instance = instance;
			return true;
		}
		return false;
	}

	protected void iRegisterTextureFiles(String[] textureFiles) {}
	
	public static void registerTextureFiles(String... textureFiles) {
		instance.iRegisterTextureFiles(textureFiles);
	}
	
	protected boolean client(){return false;}
	
	public static boolean isClient(){
		return instance.client();
	}
	
	public static boolean isServer(){
		return !instance.client();
	}
	
	protected void iRegisterLanguage(PC_Module module, String lang, String[] translations) {
		for(int i=0; i<translations.length; i+=2)
			LanguageRegistry.instance().addStringLocalization(translations[i], lang, translations[i+1]);
	}
	
	public static void registerLanguageForLang(PC_Module module, String lang, String... translations){
		instance.iRegisterLanguage(module, lang, translations);
	}
	
	public static void registerLanguage(PC_Module module, String... translations){
		instance.registerLanguageForLang(module, "en_US", translations);
	}
	
	protected void iLoadLanguage(PC_Module module) {}
	
	public static void loadLanguage(PC_Module module){
		instance.iLoadLanguage(module);
	}
	
	protected void iSaveLanguage(PC_Module module) {}
	
	public static void saveLanguage(PC_Module module){
		instance.iSaveLanguage(module);
	}

	public static String tr(String identifier) {
		return StringTranslate.getInstance().translateKey(identifier).trim(); 
	}
	
	public static String tr(String identifier, String... replacements) {
		return StringTranslate.getInstance().translateKeyFormat(identifier, (Object[])replacements);
	}
	
	public static Object register(PC_Module module, int defaultID, Class... classes){
		Configuration config = module.getConfig();
		Class<? extends PC_Block> blockClass = null;
		Class<? extends PC_ItemBlock> itemBlockClass = null;
		Class<? extends PC_TileEntity> tileEntityClass = null;
		Class<? extends PC_Item> itemClass = null;
		Class<? extends PC_ItemArmor> itemArmorClass = null;
		
		for(int i=classes.length-1; i>=0; i--){
			if(PC_Block.class.isAssignableFrom(classes[i])){
				blockClass = classes[i];
			}else if(PC_ItemBlock.class.isAssignableFrom(classes[i])){
				itemBlockClass = classes[i];
			}else if(PC_TileEntity.class.isAssignableFrom(classes[i])){
				tileEntityClass = classes[i];
			}else if(PC_Item.class.isAssignableFrom(classes[i])){
				itemClass = classes[i];
			}else if(PC_ItemArmor.class.isAssignableFrom(classes[i])){
				itemArmorClass = classes[i];
			}
		}
		
		if(blockClass == null && itemClass == null && itemArmorClass == null){
			throw new IllegalArgumentException("Need n Block or an Item");
		}
		if(blockClass != null && itemClass != null && itemArmorClass != null){
			throw new IllegalArgumentException("Need n Block or an Item but not both");
		}
		if(blockClass != null){
			if(classes.length>3)
				throw new IllegalArgumentException("Expackt only three Arguments");
			try {
				int blockID = getConfigInt(config, Configuration.CATEGORY_BLOCK, blockClass.getName(), defaultID);
				PC_Block block = (PC_Block) createClass(blockClass, new Class[]{int.class}, new Object[]{blockID});
				block.setTextureFile(module.getTerrainFile());
				if(block instanceof PC_IConfigLoader)
					((PC_IConfigLoader) block).loadFromConfig(config);
				if(itemBlockClass==null){
					GameRegistry.registerBlock(block);
					registerLanguage(module, block.getBlockName(), block.getDefaultName());
				}else{
					GameRegistry.registerBlock(block, itemBlockClass);
					PC_ItemBlock itemBlock = (PC_ItemBlock)Item.itemsList[blockID];
					itemBlock.setCraftingToolModule(module.getNameWithoutPowerCraft());
					registerLanguage(module, itemBlock.getDefaultNames());
				}
				if(tileEntityClass!=null)
					GameRegistry.registerTileEntity(tileEntityClass, tileEntityClass.getName());
				return block;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if(itemClass!=null){
			if(classes.length>1)
				throw new IllegalArgumentException("Expackt only one Item");
			try {
				int itemID = getConfigInt(config, Configuration.CATEGORY_ITEM, itemClass.getName(), defaultID);
				PC_Item item = (PC_Item) createClass(itemClass, new Class[]{int.class}, new Object[]{itemID});
				item.setCraftingToolModule(module.getNameWithoutPowerCraft());
				item.setTextureFile(module.getTerrainFile());
				if(item instanceof PC_IConfigLoader)
					((PC_IConfigLoader) item).loadFromConfig(config);
				registerLanguage(module, item.getDefaultNames());
				return item;
			}catch (Exception e) {
				e.printStackTrace();
			}
		}else if(itemArmorClass!=null){
			if(classes.length>1)
				throw new IllegalArgumentException("Expackt only one Item");
			try {
				int itemID = getConfigInt(config, Configuration.CATEGORY_ITEM, itemArmorClass.getName(), defaultID);
				PC_ItemArmor itemArmor = (PC_ItemArmor) createClass(itemArmorClass, new Class[]{int.class}, new Object[]{itemID});
				itemArmor.setCraftingToolModule(module.getNameWithoutPowerCraft());
				if(itemArmor instanceof PC_IConfigLoader)
					((PC_IConfigLoader) itemArmor).loadFromConfig(config);
				registerLanguage(module, itemArmor.getItemName(), itemArmor.getDefaultName());
				return itemArmor;
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static Object createClass(Class c, Class[] param, Object[] objects) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
		return c.getConstructor(param).newInstance(objects);
	}
	
	public static boolean getConfigBool(Configuration config, String category, String key, boolean defaultValue){
		return config.get(category, key, defaultValue).getBoolean(false);
	}
	
	public static boolean getConfigBool(Configuration config, String category, String key, boolean defaultValue, String description){
		config.get(category, key, defaultValue).comment = description;
		return config.get(category, key, defaultValue).getBoolean(false);
	}
	
	public static int getConfigInt(Configuration config, String category, String key, int defaultValue){
		return config.get(category, key, defaultValue).getInt(0);
	}
	
	public static int getConfigInt(Configuration config, String category, String key, int defaultValue, String description){
		config.get(category, key, defaultValue).comment = description;
		return config.get(category, key, defaultValue).getInt(0);
	}
	
	public static String getConfigString(Configuration config, String category, String key, String defaultValue){
		return config.get(category, key, defaultValue).value;
	}
	
	public static String getConfigString(Configuration config, String category, String key, String defaultValue, String description){
		config.get(category, key, defaultValue).comment = description;
		return config.get(category, key, defaultValue).value;
	}
	
	protected boolean soundEnabled = true; 
	
	public static boolean isSoundEnabled(){
		if(isServer())
			return false;
		return instance.soundEnabled;
	}
	
	public static void enableSound(boolean enable){
		instance.soundEnabled = enable;
	}
	
	protected void iPlaySound(double x, double y, double z, String sound, float soundVolume, float pitch){}
	
	public static void playSound(double x, double y, double z, String sound, float soundVolume, float pitch){
		if(isSoundEnabled()){
			instance.iPlaySound(x, y, z, sound, soundVolume, pitch);
		}
	}

	public static void addShapelessRecipe(ItemStack itemStack, Object[] objects) {
		GameRegistry.addShapelessRecipe(itemStack, objects);
	}
	
	public static void addRecipe(ItemStack itemStack, Object[] objects) {
		GameRegistry.addRecipe(itemStack, objects);
	}
	
	protected static HashMap<String, Class> guis = new HashMap<String, Class>();
	
	public static void registerGres(String name, Class gui){
		guis.put(name, gui);
	}
	
	public static void registerGresArray(Object[] o){
		if(o==null)
			return;
		for(int i=0; i<o.length; i+=2){
			registerGres((String)o[i], (Class)o[i+1]);
		}
	}
	
	protected void iOpenGres(String name, EntityPlayer player, Object[]o){
		if(!(player instanceof EntityPlayerMP))
			return;
		int guiID = 0;
		try{
			Field var6 = EntityPlayerMP.class.getDeclaredFields()[16];
	        var6.setAccessible(true);
	        guiID = var6.getInt(player);
	        guiID = guiID % 100 + 1;
	        var6.setInt(player, guiID);
		} catch (Exception e){
            e.printStackTrace();
        }
		ByteArrayOutputStream data = new ByteArrayOutputStream();
        ObjectOutputStream sendData;
		try
        {
            sendData = new ObjectOutputStream(data);
            sendData.writeInt(PC_PacketHandler.PACKETGUI);
            sendData.writeObject(name);
            sendData.writeInt(guiID);
            sendData.writeObject(o);
            sendData.writeInt(PC_PacketHandler.PACKETGUI);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
		PC_PacketHandler.sendToPlayer(player, data);
		if(guis.containsKey(name)){
			Class c = guis.get(name);
			if(PC_GresBaseWithInventory.class.isAssignableFrom(c)){
				try {
					PC_GresBaseWithInventory bwi = (PC_GresBaseWithInventory)createClass(c, new Class[]{EntityPlayer.class, Object[].class}, new Object[]{player, o});
					player.craftingInventory = bwi;
					player.craftingInventory.windowId = guiID;
					player.craftingInventory.addCraftingToCrafters((EntityPlayerMP)player);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void openGres(String name, EntityPlayer player, Object...o){
		instance.iOpenGres(name, player, o);
	}

	public static int getWorldDimension(World worldObj) {
		return worldObj.provider.dimensionId;
	}

	public static Object getPrivateValue(Class c, Object o, int i) {
		try
        {
            Field f = c.getDeclaredFields()[i];
            f.setAccessible(true);
            return f.get(o);
        }
        catch (IllegalAccessException e)
        {
            return null;
        }
	}
	
	public static void setPrivateValue(Class c, Object o, int i, Object v) {
        try
        {
        	Field f = c.getDeclaredFields()[i];
            f.setAccessible(true);
            Field field_modifiers = Field.class.getDeclaredField("modifiers");
            field_modifiers.setAccessible(true);
            int modifier = field_modifiers.getInt(f);
            if((modifier & Modifier.FINAL)!=0){
            	field_modifiers.setInt(f, modifier & ~Modifier.FINAL);
            }
            f.set(o, v);
            if((modifier & Modifier.FINAL)!=0){
            	field_modifiers.setInt(f, modifier);
            }
        }
        catch (Exception e) {
            PC_Logger.severe("setPrivateValue failed with "+c+":"+o+":"+i);
        }
    }
	
	public static List<IRecipe> getRecipesForProduct(ItemStack prod) {
		List<IRecipe> recipes = new ArrayList<IRecipe>(CraftingManager.getInstance().getRecipeList());
		List<IRecipe> ret = new ArrayList<IRecipe>();
		
		for (IRecipe recipe:recipes) {
			try {
				if (recipe.getRecipeOutput().isItemEqual(prod) || (recipe.getRecipeOutput().itemID == prod.itemID && prod.getItemDamage() == -1)) {
					ret.add(recipe);
				}
			} catch (NullPointerException npe) {
				continue;
			}
		}

		return ret;
	}
	
	protected EnumGameType iGetGameTypeFor(EntityPlayer player){
		return ((EntityPlayerMP)player).theItemInWorldManager.getGameType();
	}
	
	public static EnumGameType getGameTypeFor(EntityPlayer player){
		return instance.iGetGameTypeFor(player);
	}
	
	public static boolean isCreative(EntityPlayer player){
		return getGameTypeFor(player).isCreative();
	}
	
	/**
	 * Tests if a given stack is a fuel
	 * 
	 * @param itemstack stack with item
	 * @return is fuel
	 */
	public static boolean isFuel(ItemStack itemstack) {
		if (itemstack == null) {
			return false;
		}

		return PC_InvUtils.getFuelValue(itemstack, 0f)>0;
		
	}
	
	/**
	 * Tests if a given stack can be smelted
	 * 
	 * @param itemstack stack with item
	 * @return is smeltable
	 */
	public static boolean isSmeltable(ItemStack itemstack) {
		if (itemstack == null || FurnaceRecipes.smelting().getSmeltingResult(itemstack.getItem().shiftedIndex) == null) {
			return false;
		}
		return true;
	}
	
	protected void iChatMsg(String msg, boolean clear){}
	
	/**
	 * Sends chat message onto the screen.
	 * 
	 * @param msg message
	 * @param clear clear screen before the messsage
	 */
	public static void chatMsg(String msg, boolean clear) {
		instance.iChatMsg(msg, clear);
	}

	/**
	 * Convert ticks to seconds
	 * 
	 * @param ticks ticks count
	 * @return seconds (double)
	 */
	public static double ticksToSecs(int ticks) {
		return ticks * 0.05D;
	}

	/**
	 * Convert ticks to seconds
	 * 
	 * @param ticks ticks count
	 * @return rounded seconds (int)
	 */
	public static int ticksToSecsInt(int ticks) {
		return Math.round(ticks * 0.05F);
	}

	/**
	 * Convert seconds to ticks
	 * 
	 * @param secs seconds
	 * @return ticks
	 */
	public static int secsToTicks(double secs) {
		return (int)(secs * 20);
	}
	
	public static String doubleToString(double d) {
		return ""+d;
	}
	
	public static TileEntity getTE(IBlockAccess world, int x, int y, int z){
		if(world!=null)
			return world.getBlockTileEntity(x, y, z);
		return null;
	}
	
	public static int getMD(IBlockAccess world, int x, int y, int z){
		if(world!=null){
			return world.getBlockMetadata(x, y, z);
		}
		return 0;
	}

	public static boolean isPlacingReversed() {
		return false;
	}

	public static int reverseSide(int l) {
		if (l == 0) {
			l = 2;
		} else if (l == 2) {
			l = 0;
		} else if (l == 1) {
			l = 3;
		} else if (l == 3) {
			l = 1;
		}

		return l;
	}

	/**
	 * Perform hide redstone update around this gate.
	 * 
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param id
	 */
	public static void hugeUpdate(World world, int x, int y, int z, int blockID) {
		world.notifyBlocksOfNeighborChange(x, y, z, blockID);
	}

	public static TileEntity setTE(World world, int x, int y, int z, TileEntity createTileEntity) {
		world.setBlockTileEntity(x, y, z, createTileEntity);
		return createTileEntity;
	}
	
	/**
	 * Is the gate powered from given input? This method takes care of rotation
	 * for you. 0 BACK, 1 LEFT, 2 RIGHT, 3 FRONT, 4 BOTTOM, 5 TOP
	 * 
	 * @param world the World
	 * @param x
	 * @param y
	 * @param z
	 * @param inp the input number
	 * @return is powered
	 */
	
	public static boolean poweredFromInput(World world, int x, int y, int z, int inp) {
		return poweredFromInput(world, x, y, z, inp, 0);
	}
	
	/**
	 * Is the gate powered from given input? This method takes care of rotation
	 * for you. 0 BACK, 1 LEFT, 2 RIGHT, 3 FRONT, 4 BOTTOM, 5 TOP
	 * 
	 * @param world the World
	 * @param x
	 * @param y
	 * @param z
	 * @param inp the input number
	 * @param rotation the block rotation
	 * @return is powered
	 */
	public static boolean poweredFromInput(World world, int x, int y, int z, int inp, int rotation) {

		if(world==null)
			return false;
		
		if (inp == 4) {
			boolean isProviding = (world.isBlockIndirectlyProvidingPowerTo(x, y - 1, z, 0) || (world.getBlockId(x, y - 1, z) == Block.redstoneWire.blockID && world
					.getBlockMetadata(x, y - 1, z) > 0));
			return isProviding;
		}
		if (inp == 5) {
			boolean isProviding = (world.isBlockIndirectlyProvidingPowerTo(x, y + 1, z, 1) || (world.getBlockId(x, y + 1, z) == Block.redstoneWire.blockID && world
					.getBlockMetadata(x, y + 1, z) > 0));
			return isProviding;
		}

		int N0 = 0, N1 = 1, N2 = 2, N3 = 3;
		if (inp == 0) {
			N0 = 0;
			N1 = 1;
			N2 = 2;
			N3 = 3;
		}
		if (inp == 1) {
			N0 = 3;
			N1 = 0;
			N2 = 1;
			N3 = 2;
		} else if (inp == 2) {
			N0 = 1;
			N1 = 2;
			N2 = 3;
			N3 = 0;
		} else if (inp == 3) {
			N0 = 2;
			N1 = 3;
			N2 = 0;
			N3 = 1;
		}

		if (rotation == N0) {
			return (world.isBlockIndirectlyProvidingPowerTo(x, y, z + 1, 3) || world.getBlockId(x, y, z + 1) == Block.redstoneWire.blockID
					&& world.getBlockMetadata(x, y, z + 1) > 0);
		}
		if (rotation == N1) {
			return (world.isBlockIndirectlyProvidingPowerTo(x - 1, y, z, 4) || world.getBlockId(x - 1, y, z) == Block.redstoneWire.blockID
					&& world.getBlockMetadata(x - 1, y, z) > 0);
		}
		if (rotation == N2) {
			return (world.isBlockIndirectlyProvidingPowerTo(x, y, z - 1, 2) || world.getBlockId(x, y, z - 1) == Block.redstoneWire.blockID
					&& world.getBlockMetadata(x, y, z - 1) > 0);
		}
		if (rotation == N3) {
			return (world.isBlockIndirectlyProvidingPowerTo(x + 1, y, z, 5) || world.getBlockId(x + 1, y, z) == Block.redstoneWire.blockID
					&& world.getBlockMetadata(x + 1, y, z) > 0);
		}
		return false;
	}

	public static boolean isChestEmpty(World world, int x, int y, int z) {
		
		IInventory invAt = PC_InvUtils.getCompositeInventoryAt(world, new PC_CoordI(x, y, z));
		if (invAt != null) return PC_InvUtils.isInventoryEmpty(invAt);

		List<IInventory> list = world.getEntitiesWithinAABB(IInventory.class,
				AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1).expand(0.6D, 0.6D, 0.6D));

		if (list.size() >= 1) {
			return PC_InvUtils.isInventoryEmpty(list.get(0));
		}
		
		List<PC_IInventoryWrapper> list2 = world.getEntitiesWithinAABB(PC_IInventoryWrapper.class,
				AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1).expand(0.6D, 0.6D, 0.6D));

		if (list2.size() >= 1) {
			if(list2.get(0).getInventory() != null) {
				return PC_InvUtils.isInventoryEmpty(list2.get(0).getInventory());
			}
		}

		return false;
		
	}

	public static boolean isChestFull(World world, int x, int y, int z, boolean allSlotsFull) {
		
		IInventory invAt = PC_InvUtils.getCompositeInventoryAt(world, new PC_CoordI(x, y, z));
		if (invAt != null) {
			if (allSlotsFull) {
				return PC_InvUtils.isInventoryFull(invAt);
			} else {
				return PC_InvUtils.hasInventoryNoFreeSlots(invAt);
			}
		}

		List<IInventory> list = world.getEntitiesWithinAABB(IInventory.class,
				AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1).expand(0.6D, 0.6D, 0.6D));

		if (list.size() >= 1) {
			if (allSlotsFull) {
				return PC_InvUtils.isInventoryFull(list.get(0));
			} else {
				return PC_InvUtils.hasInventoryNoFreeSlots(list.get(0));
			}
		}

		List<PC_IInventoryWrapper> list2 = world.getEntitiesWithinAABB(PC_IInventoryWrapper.class,
				AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1).expand(0.6D, 0.6D, 0.6D));

		if (list2.size() >= 1) {
			if (allSlotsFull) {
				if(list2.get(0).getInventory() != null) {
					return PC_InvUtils.isInventoryFull(list2.get(0).getInventory());
				}
			} else {
				if(list2.get(0).getInventory() != null) {
					return PC_InvUtils.hasInventoryNoFreeSlots(list2.get(0).getInventory());
				}
			}
		}

		return false;
		
	}

	public static void spawnMobFromSpawner(World world, int x, int y, int z) {
		TileEntity te = PC_Utils.getTE(world, x, y, z);
		if (te != null && te instanceof TileEntityMobSpawner) {
			spawnMobs(world, x, y, z, ((TileEntityMobSpawner) te).getMobID());
		}
	}
	
	/**
	 * Spawn blocks near this Special Controller
	 * 
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param type Mob "name" string
	 */
	public static void spawnMobs(World world, int x, int y, int z, String type) {
		byte count = 5;

		boolean spawnParticles = world.getClosestPlayer(x + 0.5D, y + 0.5D, z + 0.5D, 16D) != null;

		for (int q = 0; q < count; q++) {
			EntityLiving entityliving = (EntityLiving) EntityList.createEntityByName(type, world);
			if (entityliving == null) {
				return;
			}
			int c = world.getEntitiesWithinAABB(entityliving.getClass(),
					AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1).expand(8D, 4D, 8D)).size();
			if (c >= 6) {
				if (spawnParticles) {
					double d = world.rand.nextGaussian() * 0.02D;
					double d1 = world.rand.nextGaussian() * 0.02D;
					double d2 = world.rand.nextGaussian() * 0.02D;
					world.spawnParticle("smoke", x + 0.5D, y + 0.4D, z + 0.5D, d, d1, d2);
				}
				return;
			}

			double d3 = x + (world.rand.nextDouble() - world.rand.nextDouble()) * 3D;
			double d4 = (y + world.rand.nextInt(3)) - 1;
			double d5 = z + (world.rand.nextDouble() - world.rand.nextDouble()) * 3D;
			entityliving.setLocationAndAngles(d3, d4, d5, world.rand.nextFloat() * 360F, 0.0F);
			if (world.checkIfAABBIsClear(entityliving.boundingBox)
					&& world.getCollidingBoundingBoxes(entityliving, entityliving.boundingBox).size() == 0) {
				world.spawnEntityInWorld(entityliving);
				if (spawnParticles) {
					world.playAuxSFX(2004, x, y, z, 0);
					entityliving.spawnExplosionParticle();
				}
				return;
			}
		}
	}

	protected File iGetMCDirectory(){
		return MinecraftServer.getServer().getFile("");
	}
	
	public static File getMCDirectory() {
		return instance.iGetMCDirectory();
	}

	public static void notifyBlockOfNeighborChange(World world, int x, int y, int z, int blockId) {
		 Block block = Block.blocksList[world.getBlockId(x, y, z)];

         if (block != null)
         {
        	 block.onNeighborBlockChange(world, x, y, z, blockId);
         }
		
	}

	protected int iAddArmor(String name) {
		return 0;
	}
	
	public static int addArmor(String name) {
		return instance.iAddArmor(name);
	}

	protected boolean iIsEntityFX(Entity entity) {
		return false;
	}
	
	public static boolean isEntityFX(Entity entity) {
		return instance.iIsEntityFX(entity);
	}
	
}
