package powercraft.transport.blocks;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import powercraft.api.blocks.PC_Block;
import powercraft.api.blocks.PC_BlockInfo;
import powercraft.transport.blocks.itemblocks.PCtr_ItemBlockUpgradeableBelt;
import powercraft.transport.blocks.tileentities.PCtr_TEUpgradeableBelt;
import powercraft.transport.helper.PCtr_BeltHelper;
import powercraft.transport.helper.PCtr_MaterialConveyor;

@PC_BlockInfo(name = "UpgradeableBelt", blockid = "upgradeableBelt", defaultid = 2051, tileEntity=PCtr_TEUpgradeableBelt.class, itemBlock=PCtr_ItemBlockUpgradeableBelt.class)
public class PCtr_BlockUpgradeableBelt extends PC_Block
{
	
	public PCtr_BlockUpgradeableBelt(int id)
	{
		super(id, PCtr_MaterialConveyor.getMaterial());			
	}

	@Override
	public void loadIcons()
	{
//		this.blockIcon = PC_TextureRegistry.registerIcon("upgradeablebelt");

	}

	@Override
	public void registerRecipes()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onEntityCollidedWithBlock(World world, int i, int j, int k, Entity entity)
	{

	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xHit, float yHit, float zHit)
	{
		return false;		
	}
	
	@Override
	public boolean isOpaqueCube()
	{
		return PCtr_BeltHelper.isOpaqueCube();
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return PCtr_BeltHelper.renderAsNormalBlock();
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(World world, int i, int j, int k)
	{
		return PCtr_BeltHelper.getCollisionBoundingBoxFromPool(world, i, j, k);
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBox(World world, int i, int j, int k)
	{
		return PCtr_BeltHelper.getSelectedBoundingBoxFromPool(world, i, j, k);
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess iblockaccess, int i, int j, int k)
	{
		PCtr_BeltHelper.setBlockBoundsBasedOnState(this, iblockaccess, i, j, k);
	}

	@Override
	public void setBlockBoundsForItemRender()
	{
		PCtr_BeltHelper.setBlockBoundsForItemRender(this);
	}

	@Override
	public int tickRate(World world)
	{
		return PCtr_BeltHelper.tickRate(world);
	}
	@Override
	public int getLightOpacity(World world, int x, int y, int z)
	{
		return 0;
	}
}
