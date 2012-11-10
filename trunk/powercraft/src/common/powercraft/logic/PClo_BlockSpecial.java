package powercraft.logic;

import java.util.Random;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Block;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraftforge.common.Configuration;
import powercraft.core.PC_Block;
import powercraft.core.PC_IConfigLoader;
import powercraft.core.PC_IRotatedBox;
import powercraft.core.PC_MathHelper;
import powercraft.core.PC_Renderer;
import powercraft.core.PC_Utils;

public class PClo_BlockSpecial extends PC_Block implements PC_IRotatedBox, PC_IConfigLoader {

	private int lightValueOn=15;
	
	public PClo_BlockSpecial(int id){
		super(id, 6, Material.ground);
		setHardness(0.35F);
		setStepSound(Block.soundWoodFootstep);
		disableStats();
		setRequiresSelfNotify();
		setResistance(30.0F);
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.1875F, 1.0F);
		setCreativeTab(CreativeTabs.tabRedstone);
	}
	
	@Override
	public String getDefaultName() {
		return null;
	}
	
	@Override
	public TileEntity createNewTileEntity(World world){
		return new PClo_TileEntitySpecial();
	}
	
	@Override
	public void updateTick(World world, int x, int y, int z, Random random) {
		
		PClo_TileEntitySpecial te = getTE(world, x, y, z);
		boolean shouldState = false;
		boolean state = te.getState();
		
		int rot = getRotation_static(PC_Utils.getMD(world, x, y, z));
		
		int xAdd=0, zAdd=0;
		
		if (rot == 0) {
			zAdd=1;
		}else if (rot == 1) {
			xAdd = -1;
		}else if (rot == 2) {
			zAdd = -1;
		}else if (rot == 3) {
			xAdd = 1;
		}
		
		switch(te.getType()){
		case PClo_SpecialType.DAY:
			shouldState = world.isDaytime();
			break;
		case PClo_SpecialType.NIGHT:
			shouldState = !world.isDaytime();
			break;
		case PClo_SpecialType.RAIN:
			shouldState = world.isRaining();
			break;
		case PClo_SpecialType.CHEST_EMPTY:
			shouldState = PC_Utils.isChestEmpty(world, x + xAdd, y, z + zAdd);
			break;
		case PClo_SpecialType.CHEST_FULL:
			shouldState = PC_Utils.isChestFull(world, x + xAdd, y, z + zAdd, false);
			break;
		case PClo_SpecialType.SPECIAL:
			shouldState = PC_Utils.poweredFromInput(world, x, y, z, PC_Utils.BACK, rot);
			TileEntity tes = PC_Utils.getTE(world, x - xAdd, y, z - zAdd);
			if(tes instanceof PClo_TileEntityPulsar){
				((PClo_TileEntityPulsar) tes).setPaused(shouldState);
			}
			if(shouldState==true && shouldState != state)
				spawnMobsFromSpawners(world, x, y, z);
			break;
		}
		
		if(state != shouldState)
			te.setState(shouldState);
		
	}

	// mob spawning procedure
	private void spawnMobsFromSpawners(World world, int x, int y, int z) {
		PC_Utils.spawnMobFromSpawner(world, x + 1, y, z);
		PC_Utils.spawnMobFromSpawner(world, x - 1, y, z);
		PC_Utils.spawnMobFromSpawner(world, x, y + 1, z);
		PC_Utils.spawnMobFromSpawner(world, x, y, z + 1);
		PC_Utils.spawnMobFromSpawner(world, x, y, z - 1);
	}
	
	@Override
	public int tickRate() {
		return 1;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int side) {
		
		world.scheduleBlockUpdate(x, y, z, blockID, tickRate());
		
	}
	
	private boolean isOutputActive(World world, int x, int y, int z) {

		int rot = getRotation_static(PC_Utils.getMD(world, x, y, z));
		
		return false;
	}
	
	@Override
	public boolean isIndirectlyPoweringTo(IBlockAccess world, int x, int y, int z, int side) {
		return isPoweringTo(world, x, y, z, side);
	}
	
	@Override
	public boolean isPoweringTo(IBlockAccess world, int x, int y, int z, int side) {
		int meta = PC_Utils.getMD(world, x, y, z);
		int rotation = getRotation(meta);
		
		if (!isActive(world, x, y, z)) return false;
		
		switch(getType(world, x, y, z)){
		case PClo_SpecialType.DAY:
		case PClo_SpecialType.NIGHT:
		case PClo_SpecialType.RAIN:
			return true;
		case PClo_SpecialType.CHEST_EMPTY:
		case PClo_SpecialType.CHEST_FULL:
			if((rotation==0 && side==3) || (rotation==1 && side==4) || (rotation==2 && side==2) || (rotation==3 && side==5))
				return true;
			break;
		}
		
		return false;
	}

	@Override
	public boolean canProvidePower() {
		return true;
	}
	
	@Override
	public boolean renderAsNormalBlock()
    {
        return false;
    }
	
	@Override
	public int getRenderType() {
		return PC_Renderer.getRendererID(true);
	}
	
	public static PClo_TileEntitySpecial getTE(IBlockAccess world, int x, int y, int z){
		TileEntity te = PC_Utils.getTE(world, x, y, z);
		if(te instanceof PClo_TileEntitySpecial)
			return (PClo_TileEntitySpecial)te;
		return null;
	}
	
	public static int getType(IBlockAccess world, int x, int y, int z){
		PClo_TileEntitySpecial te = getTE(world, x, y, z);
		if(te!=null)
			return te.getType();
		return 0;
	}
	
	public static boolean isActive(IBlockAccess world, int x, int y, int z){
		PClo_TileEntitySpecial te = getTE(world, x, y, z);
		if(te!=null)
			return te.getState();
		return false;
	}
	
	@Override
	public int getBlockTexture(IBlockAccess iblockaccess, int x, int y, int z, int side) {
		if (side == 1) {
			return getTopFaceFromEnum(getType(iblockaccess, x, y, z)) + (isActive(iblockaccess, x, y, z) ? 16 : 0);
		}

		if (side == 0) {
			return 6;
		}
		return 5;
	}

	@Override
	public int getBlockTextureFromSideAndMetadata(int side, int meta) {
		if (side == 0) {
			return 6; // stone slab particles
		}
		if (side == 1) {
			return getTopFaceFromEnum(meta) + 16; // top face
		} else {
			return 5; // side
		}
	}
	
	private int getTopFaceFromEnum(int meta) {
		if(meta>=0 && meta<PClo_SpecialType.TOTAL_SPECIAL_COUNT)
			return PClo_SpecialType.index[meta];
		return 6;
	}

	@Override
	public int getRotation(int meta) {
		return getRotation_static(meta);
	}

	public static int getRotation_static(int meta) {
		return meta & 0x3;
	}
	
	@Override
	public boolean renderItemHorizontal() {
		return false;
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess iblockaccess, int x, int y, int z) {
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.1875F, 1.0F);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		setBlockBoundsBasedOnState(world, x, y, z);
		return super.getCollisionBoundingBoxFromPool(world, x, y, z);
	}
	
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving entityliving) {

		int type = getType(world, x, y, z);

		int l = ((PC_MathHelper.floor_double(((entityliving.rotationYaw * 4F) / 360F) + 0.5D) & 3) + 2) % 4;

		if (entityliving instanceof EntityPlayer && PC_Utils.isPlacingReversed(((EntityPlayer)entityliving))) {
			l = PC_Utils.reverseSide(l);
		}
		
		world.setBlockMetadataWithNotify(x, y, z, l);
		
		onNeighborBlockChange(world, x, y, z, 0);
		
	}
	
	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z){
		return isActive(world, x, y, z) ? lightValueOn : 0;
	}
	
	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random random) {
		if (!isActive(world, x, y, z)) {
			return;
		}

		if (random.nextInt(3) != 0) {
			return;
		}

		double d = (x + 0.5F) + (random.nextFloat() - 0.5F) * 0.20000000000000001D;
		double d1 = (y + 0.2F) + (random.nextFloat() - 0.5F) * 0.20000000000000001D;
		double d2 = (z + 0.5F) + (random.nextFloat() - 0.5F) * 0.20000000000000001D;

		world.spawnParticle("reddust", d, d1, d2, 0.0D, 0.0D, 0.0D);
	}	
	
	@Override
	public int idDropped(int i, Random random, int j) {
		return -1;
	}

	@Override
	public int quantityDropped(Random random) {
		return 0;
	}
	
	@Override
	public boolean removeBlockByPlayer(World world, EntityPlayer player, int x, int y, int z) {

		int type = getType(world, x, y, z);
		
		boolean remove = super.removeBlockByPlayer(world, player, x, y, z);
		
		if (remove && !PC_Utils.isCreative(player)) {
			dropBlockAsItem_do(world, x, y, z, new ItemStack(mod_PowerCraftLogic.special, 1, type));
		}

		return remove;

	}

	@Override
	public void loadFromConfig(Configuration config) {
		lightValueOn = PC_Utils.getConfigInt(config, Configuration.CATEGORY_GENERAL, "GatesLightValueOn", 7);
	}
	
}