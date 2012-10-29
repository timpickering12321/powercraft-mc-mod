package powercraft.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.src.CraftingManager;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.EnumGameType;
import net.minecraft.src.FurnaceRecipes;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.IRecipe;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.StringTranslate;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class PC_Utils {
	
	private static PC_Utils instance;
	
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
		
		for(int i=classes.length-1; i>=0; i--){
			if(PC_Block.class.isAssignableFrom(classes[i])){
				blockClass = classes[i];
			}else if(PC_ItemBlock.class.isAssignableFrom(classes[i])){
				itemBlockClass = classes[i];
			}else if(PC_TileEntity.class.isAssignableFrom(classes[i])){
				tileEntityClass = classes[i];
			}else if(PC_Item.class.isAssignableFrom(classes[i])){
				itemClass = classes[i];
			}
		}
		
		if(blockClass == null && itemClass == null){
			throw new IllegalArgumentException("Need n Block or an Item");
		}
		if(blockClass != null && itemClass != null){
			throw new IllegalArgumentException("Need n Block or an Item but not both");
		}
		if(blockClass != null){
			if(classes.length>3)
				throw new IllegalArgumentException("Expackt only three Arguments");
			try {
				int blockID;
				PC_Block block;
				if(blockClass.isAnnotationPresent(PC_Shining.class)){
					blockID = getConfigInt(config, Configuration.CATEGORY_BLOCK, blockClass.getName()+".on", defaultID);
					block = (PC_Block) createClass(blockClass, new Class[]{int.class, boolean.class}, new Object[]{blockID, true});
				}else{
					blockID = getConfigInt(config, Configuration.CATEGORY_BLOCK, blockClass.getName(), defaultID);
					block = (PC_Block) createClass(blockClass, new Class[]{int.class}, new Object[]{blockID});
				}
				if(block instanceof PC_IConfigLoader)
					((PC_IConfigLoader) block).loadFromConfig(config);
				if(itemBlockClass==null){
					GameRegistry.registerBlock(block);
					registerLanguage(module, block.getBlockName(), block.getDefaultName());
				}else{
					GameRegistry.registerBlock(block, itemBlockClass);
					PC_ItemBlock itemBlock = (PC_ItemBlock)Item.itemsList[blockID];
					registerLanguage(module, itemBlock.getDefaultNames());
				}
				if(blockClass.isAnnotationPresent(PC_Shining.class)){
					blockID = getConfigInt(config, Configuration.CATEGORY_BLOCK, blockClass.getName()+".off", defaultID+1);
					PC_Block blockOff = (PC_Block) createClass(blockClass, new Class[]{int.class, boolean.class}, new Object[]{blockID, false});
					if(blockOff instanceof PC_IConfigLoader)
						((PC_IConfigLoader) blockOff).loadFromConfig(config);
					GameRegistry.registerBlock(blockOff);
					Field[] fa = blockClass.getDeclaredFields();
					for(Field f:fa){
						PC_Shining.ON on = f.getAnnotation(PC_Shining.ON.class);
						PC_Shining.OFF off = f.getAnnotation(PC_Shining.OFF.class);
						if(on!=null){
							block.setLightValue(on.lightValue()/15.0f);
							f.setAccessible(true);
							f.set(blockClass, block);
						}else if(off!=null){
							block.setLightValue(off.lightValue()/15.0f);
							f.setAccessible(true);
							f.set(blockClass, blockOff);
						}
					}
				}
				if(tileEntityClass!=null)
					GameRegistry.registerTileEntity(tileEntityClass, tileEntityClass.getName());
				return block;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			if(classes.length>1)
				throw new IllegalArgumentException("Expackt only one Item");
			try {
				int itemID = getConfigInt(config, Configuration.CATEGORY_ITEM, itemClass.getName(), defaultID);
				PC_Item item = (PC_Item) createClass(itemClass, new Class[]{int.class}, new Object[]{itemID});
				if(item instanceof PC_IConfigLoader)
					((PC_IConfigLoader) item).loadFromConfig(config);
				registerLanguage(module, item.getDefaultNames());
				return item;
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
            f.set(o, v);
        }
        catch (IllegalAccessException e)
        {
            
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
	
}
