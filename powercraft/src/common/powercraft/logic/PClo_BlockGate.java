package powercraft.logic;

import java.util.Random;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Block;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import powercraft.core.PC_Block;
import powercraft.core.PC_IRotatedBox;
import powercraft.core.PC_ISwapTerrain;
import powercraft.core.PC_MathHelper;
import powercraft.core.PC_Renderer;
import powercraft.core.PC_Utils;

public class PClo_BlockGate extends PC_Block implements PC_IRotatedBox, PC_ISwapTerrain {
	
	public PClo_BlockGate(int id){
		super(id, 6, Material.ground);
		setBlockName("PCloLogicGate");
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
		return new PClo_TileEntityGate();
	}
	
	@Override
	public void updateTick(World world, int x, int y, int z, Random random) {
		boolean on = isActive(world, x, y, z);
		boolean outputActive = isOutputActive(world, x, y, z);
		if (on && !outputActive) {
			// turn off
			changeGateState(false, world, x, y, z);
		} else if (!on && outputActive) {
			// turn on
			changeGateState(true, world, x, y, z);
		}
	}

	@Override
	public int tickRate() {
		return 1;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int side) {
		boolean on = isActive(world, x, y, z);
		boolean outputActive = isOutputActive(world, x, y, z);
		if (on != outputActive) 
			world.scheduleBlockUpdate(x, y, z, blockID, tickRate());
		
	}

	/**
	 * Change the gate block from on (lighting) to off state, and preserve the
	 * tile entity.
	 * 
	 * @param state new state, on or off
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 */
	private void changeGateState(boolean state, World world, int x, int y, int z) {
		
		((PClo_TileEntityGate)PC_Utils.getTE(world, x, y, z)).setState(state);

	}
	
	private boolean isOutputActive(World world, int x, int y, int z) {

		int rot = getRotation_static(PC_Utils.getMD(world, x, y, z));
		
		return PClo_GateType.getGateOutput(getType(world, x, y, z), getInp(world, x, y, z), PC_Utils.poweredFromInput(world, x, y, z, PC_Utils.LEFT, rot), 
				PC_Utils.poweredFromInput(world, x, y, z, PC_Utils.BACK, rot), PC_Utils.poweredFromInput(world, x, y, z, PC_Utils.RIGHT, rot));
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
		
		if((rotation==0 && side==3) || (rotation==1 && side==4) || (rotation==2 && side==2) || (rotation==3 && side==5))
			return true;
		
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
	public String getTextureFile() {
		return getTerrainFile();
	}

	@Override
	public String getTerrainFile() {
		return mod_PowerCraftLogic.getInstance().getTerrainFile();
	}
	
	@Override
	public int getRenderType() {
		return PC_Renderer.getRendererID(true);
	}
	
	public static PClo_TileEntityGate getTE(IBlockAccess world, int x, int y, int z){
		TileEntity te = PC_Utils.getTE(world, x, y, z);;
		if(te instanceof PClo_TileEntityGate)
			return (PClo_TileEntityGate)te;
		return null;
	}
	
	public static int getType(IBlockAccess world, int x, int y, int z){
		PClo_TileEntityGate te = getTE(world, x, y, z);
		if(te!=null)
			return te.getType();
		return 0;
	}
	
	public static int getInp(IBlockAccess world, int x, int y, int z){
		PClo_TileEntityGate te = getTE(world, x, y, z);
		if(te!=null)
			return te.getInp();
		return 0;
	}
	
	public static boolean isActive(IBlockAccess world, int x, int y, int z){
		PClo_TileEntityGate te = getTE(world, x, y, z);
		if(te!=null)
			return te.getState();
		return false;
	}
	
	@Override
	public int getBlockTexture(IBlockAccess iblockaccess, int x, int y, int z, int side) {
		if (side == 1) {
			return getTopFaceFromEnum(getType(iblockaccess, x, y, z), getInp(iblockaccess, x, y, z)) + (isActive(iblockaccess, x, y, z) ? 16 : 0);
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
			return getTopFaceFromEnum(meta, PClo_GateType.ROT_L_D_R) + 16; // top face
		} else {
			return 5; // side
		}
	}
	
	private int getTopFaceFromEnum(int meta, int rotation) {
		if(meta>=0 && meta<PClo_GateType.TOTAL_GATE_COUNT)
			return PClo_GateType.index[meta]+rotation;
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

		if (PC_Utils.isPlacingReversed()) {
			l = PC_Utils.reverseSide(l);
		}
		
		world.setBlockMetadataWithNotify(x, y, z, l);
		
		onNeighborBlockChange(world, x, y, z, 0);
		
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9) {
		ItemStack ihold = player.getCurrentEquippedItem();
		if (ihold != null) {
			if (ihold.getItem().shiftedIndex == Item.stick.shiftedIndex) {
				if(!world.isRemote)
					getTE(world, x, y, z).rotInp();
				return true;
			}
		}

		return false;
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z){
		return isActive(world, x, y, z) ? 15 : 0;
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
			dropBlockAsItem_do(world, x, y, z, new ItemStack(mod_PowerCraftLogic.gate, 1, type));
		}

		return remove;

	}
	
}
