package powercraft.light;

import java.util.List;

import net.minecraft.src.Block;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.InventoryCrafting;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import powercraft.management.PC_BeamTracer;
import powercraft.management.PC_Color;
import powercraft.management.PC_Item;
import powercraft.management.PC_Struct3;
import powercraft.management.PC_Utils;
import powercraft.management.PC_VecI;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

public class PCli_ItemLaserComposition extends PC_Item
{
    public PCli_ItemLaserComposition()
    {
        setMaxStackSize(1);
        setHasSubtypes(true);
        setCreativeTab(CreativeTabs.tabDecorations);
    }

    @Override
    public int getMetadata(int i)
    {
        return i;
    }
    
    @Override
	@SideOnly(Side.CLIENT)
	public boolean requiresMultipleRenderPasses() {
		return true;
    }

    /**
	 * Get texture from damage and pass
	 * 
	 * @param dmg damage
	 * @param pass pass 0-1
	 */
	@Override
	public int getIconFromDamageForRenderPass(int dmg, int pass) {
		return pass == 0 ? 0 : 1;
	}
    
	@Override
    public int getColorFromItemStack(ItemStack itemStack, int pass)
    {
		if(pass==0)
			return 0xFFFFFF;
        return getColorForItemStack(itemStack).getHex();
    }

	
	
    @Override
    public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int l, float par8, float par9, float par10)
    {
    	
    	TileEntity te = PC_Utils.getTE(world, i, j, k);
    	
    	if(te instanceof PCli_TileEntityLaser){
    		
    		((PCli_TileEntityLaser)te).setItemStack(itemstack.copy());
    		
    		itemstack.stackSize = 0;
    		
    		return true;
    		
    	}

        return false;
    }
    
	@Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean b)
    {
		NBTTagCompound nbtTagCompound = itemStack.getTagCompound();
        if(nbtTagCompound==null){
        	nbtTagCompound = new NBTTagCompound();
        	itemStack.setTagCompound(nbtTagCompound);
        }
        int levelKill = nbtTagCompound.getInteger("level.kill");
        int levelDistance = nbtTagCompound.getInteger("level.distance");
        int levelSensor = nbtTagCompound.getInteger("level.sensor");
        
        if (levelKill > 0)
        {
        	list.add(PC_Utils.tr(getItemName() + ".kill.name", ("" + levelKill)));
        }

        if (levelDistance > 0)
        {
        	list.add(PC_Utils.tr(getItemName() + ".distance.name", ("" + levelDistance)));
        }
        
        if (levelSensor > 0)
        {
        	list.add(PC_Utils.tr(getItemName() + ".sensor.name", ("" + levelSensor)));
        }
    }
	
    @Override
	public void doCrafting(ItemStack itemStack, InventoryCrafting inventoryCrafting) {
    	ItemStack is = inventoryCrafting.getStackInRowAndColumn(1, 1);
    	int levelKill = 0;
        int levelDistance = 0;
    	int levelSensor = 0;
        
    	if(is != null && is.itemID == PC_Utils.getPCObjectIDByName("PCco_BlockPowerCrystal")){
    		switch(is.getItemDamage()){
    		case 0:
    			levelDistance = 1;
    			break;
    		case 1:
    			levelKill = 1;
    			break;
    		case 2:
    			levelSensor = 1;
    			break;
    		}
    	}else{
    		int size = inventoryCrafting.getSizeInventory();
    		for(int i=0; i<size; i++){
    			is = inventoryCrafting.getStackInSlot(i);
    			if(is!=null){
    				if(is.itemID == shiftedIndex){
    					if(is.stackTagCompound!=null){
    						levelDistance += is.stackTagCompound.getInteger("level.distance");
    						levelKill += is.stackTagCompound.getInteger("level.kill");
    						levelSensor += is.stackTagCompound.getInteger("level.sensor");
    					}
    				}
    			}
    		}
    	}
    	
    	itemStack.stackTagCompound = new NBTTagCompound();
		itemStack.stackTagCompound.setInteger("level.distance", levelDistance);
		itemStack.stackTagCompound.setInteger("level.kill", levelKill);
		itemStack.stackTagCompound.setInteger("level.sensor", levelSensor);
	}
    
	@Override
	public List<ItemStack> getItemStacks(List<ItemStack> arrayList) {
		ItemStack itemStack = new ItemStack(this);
		itemStack.stackTagCompound = new NBTTagCompound();
		itemStack.stackTagCompound.setInteger("level.distance", 1);
		arrayList.add(itemStack);
		itemStack = new ItemStack(this);
		itemStack.stackTagCompound = new NBTTagCompound();
		itemStack.stackTagCompound.setInteger("level.kill", 1);
		arrayList.add(itemStack);
		itemStack = new ItemStack(this);
		itemStack.stackTagCompound = new NBTTagCompound();
		itemStack.stackTagCompound.setInteger("level.sensor", 1);
		arrayList.add(itemStack);
		return arrayList;
	}

	public static PC_Color getColorForItemStack(ItemStack itemstack)
    {
    	if(itemstack==null){
    		return new PC_Color(1.0f, 1.0f, 1.0f);
    	}

        NBTTagCompound nbtTagCompound = itemstack.getTagCompound();
        if(nbtTagCompound==null){
        	nbtTagCompound = new NBTTagCompound();
        	itemstack.setTagCompound(nbtTagCompound);
        }
        int levelKill = nbtTagCompound.getInteger("level.kill");
        int maxLevel = levelKill;
        if(maxLevel==0){
        	return new PC_Color(1.0f, 1.0f, 1.0f);
        }
        PC_Color c = new PC_Color();
        c.x = levelKill / (float)maxLevel;
        c.y = 0.0f;
        c.z = 0.0f;
        return c;
    }
    
	public static int getLengthLimit(ItemStack itemstack, boolean b) {
		if(itemstack==null)
			return b?40:20;
		NBTTagCompound nbtTagCompound = itemstack.getTagCompound();
		if(nbtTagCompound==null){
			nbtTagCompound = new NBTTagCompound();
	        itemstack.setTagCompound(nbtTagCompound);
	    }
		int levelDistance = nbtTagCompound.getInteger("level.distance");
		return (20+levelDistance*10)*(b?2:1);
	}

	public static boolean onBlockHit(PC_BeamTracer beamTracer, Block block, PC_VecI coord, ItemStack itemstack, boolean isBurning) {
		if(block.isOpaqueCube() && !PC_Utils.hasFlag(beamTracer.getWorld(), coord, PC_Utils.BEAMTRACER_STOP)){
			return true;
		}
		return false;
	}

	public static boolean onEntityHit(PC_BeamTracer beamTracer, Entity entity, PC_VecI coord, ItemStack itemstack, boolean isBurning) {
		if(itemstack==null)
			return true;
		NBTTagCompound nbtTagCompound = itemstack.getTagCompound();
        if(nbtTagCompound==null){
        	nbtTagCompound = new NBTTagCompound();
        	itemstack.setTagCompound(nbtTagCompound);
        }
        int levelKill = nbtTagCompound.getInteger("level.kill");
        if(isBurning)
        	levelKill *= 2;
        if(levelKill>0)
        	if(entity instanceof EntityItem && levelKill>2){
        		entity.setDead();
        	}else if(!(entity instanceof EntityItem)){
        		entity.attackEntityFrom(PCli_DamageSourceLaser.getDamageSource(), levelKill);
        	}
		return levelKill<=4;
	}

	public static boolean isSensor(ItemStack itemstack) {
		if(itemstack==null)
			return false;
		NBTTagCompound nbtTagCompound = itemstack.getTagCompound();
        if(nbtTagCompound==null){
        	nbtTagCompound = new NBTTagCompound();
        	itemstack.setTagCompound(nbtTagCompound);
        }
        int levelSensor = nbtTagCompound.getInteger("level.sensor");
		return levelSensor>0;
	}

	@Override
	public Object msg(int msg, Object... obj) {
		switch(msg){
		case PC_Utils.MSG_DEFAULT_NAME:
			List<PC_Struct3<String, String, String[]>> names = (List<PC_Struct3<String, String, String[]>>)obj[0];
			names.add(new PC_Struct3<String, String, String[]>(getItemName() + ".kill", "Kill Level %s", null));
			names.add(new PC_Struct3<String, String, String[]>( getItemName() + ".distance", "Distance Level %s", null));
			names.add(new PC_Struct3<String, String, String[]>(getItemName() + ".sensor", "Sensor Level %s", null));;
            return names;
		}
		return null;
	}
	
}
