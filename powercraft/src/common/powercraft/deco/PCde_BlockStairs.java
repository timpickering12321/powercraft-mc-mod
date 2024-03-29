package powercraft.deco;

import java.util.List;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Block;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import powercraft.core.PC_Block;
import powercraft.core.PC_CoordI;
import powercraft.core.PC_IBlockRenderer;
import powercraft.core.PC_MathHelper;
import powercraft.core.PC_Renderer;
import powercraft.core.PC_Utils;

public class PCde_BlockStairs extends PC_Block implements PC_IBlockRenderer {

	public PCde_BlockStairs(int id) {
		super(id, Material.rock);
		setHardness(1.5F);
		setResistance(30.0F);
		setStepSound(Block.soundMetalFootstep);
		setCreativeTab(CreativeTabs.tabDecorations);
	}

	@Override
	public String getDefaultName() {
		return null;
	}
	
	@Override
	public TileEntity createNewTileEntity(World world) {
		return new PCde_TileEntityStairs();
	}

	/**
	 * Get fences that are shown for stairs.
	 * 
	 * @param world world
	 * @param pos block pos
	 * @return bool{X+, X-, Z+, Z-}
	 */
	public static boolean[] getFencesShownStairsAbsolute(World world, PC_CoordI pos) {
		boolean fences[] = { false, false, false, false };

		int j = pos.getMeta(world);

		if (j == 0) {
			fences[0] = fences[1] = true;
		} else if (j == 1) {
			fences[2] = fences[3] = true;
		} else if (j == 2) {
			fences[0] = fences[1] = true;
		} else if (j == 3) {
			fences[2] = fences[3] = true;
		}

		fences[0] &= isFallBlock(world, pos.offset(1, 0, 0)) && isFallBlock(world, pos.offset(1, -1, 0));
		fences[1] &= isFallBlock(world, pos.offset(-1, 0, 0)) && isFallBlock(world, pos.offset(-1, -1, 0));
		fences[2] &= isFallBlock(world, pos.offset(0, 0, 1)) && isFallBlock(world, pos.offset(0, -1, 1));
		fences[3] &= isFallBlock(world, pos.offset(0, 0, -1)) && isFallBlock(world, pos.offset(0, -1, -1));
		return fences;
	}

	/**
	 * Get which stair sides should be shown (left,right)
	 * 
	 * @param world
	 * @param pos
	 * @return left, right
	 */
	public static boolean[] getFencesShownStairsRelative(World world, PC_CoordI pos) {
		boolean fences[] = getFencesShownStairsAbsolute(world, pos);
		boolean rel[] = { false, false };

		int j = pos.getMeta(world);

		if (j == 0) {
			rel[0] = fences[0];
			rel[1] = fences[1];
		} else if (j == 1) {
			rel[0] = fences[2];
			rel[1] = fences[3];
		} else if (j == 2) {
			rel[0] = fences[1];
			rel[1] = fences[0];
		} else if (j == 3) {
			rel[0] = fences[3];
			rel[1] = fences[2];
		}

		return rel;
	}

	private static boolean isFallBlock(World world, PC_CoordI pos) {
		int id = pos.getId(world);
		if (id == 0 || Block.blocksList[id] == null) {
			return true;
		}

		if (id == Block.ladder.blockID || id == Block.vine.blockID) {
			return false;
		}

		if (Block.blocksList[id].getCollisionBoundingBoxFromPool(world, pos.x, pos.y, pos.z) == null) {
			return true;
		}
		if (Block.blocksList[id].blockMaterial.isLiquid() || !Block.blocksList[id].blockMaterial.isSolid()) {
			return true;
		}
		if (PC_Utils.hasFlag(world, pos, "BELT")) {
			return true;
		}
		return false;
	}

	private static boolean isClimbBlock(World world, PC_CoordI pos) {
		int id = pos.getId(world);
		if (id == 0 || Block.blocksList[id] == null) {
			return false;
		}

		if (id == Block.ladder.blockID || id == Block.vine.blockID) {
			return true;
		}
		return false;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess iblockaccess, int i, int j, int k) {
		setBlockBounds(0, 0, 0, 1, 1, 1);
	}

	@Override
	public void addCollidingBlockToList(World world, int x, int y, int z, AxisAlignedBB axisalignedbb, List arraylist, Entity entity) {

		int j = world.getBlockMetadata(x, y, z);

		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
		super.addCollidingBlockToList(world, x, y, z, axisalignedbb, arraylist, entity);

		if (j == 0) {
			setBlockBounds(0.0F, 0.5F, 0.0F, 1.0F, 1.0F, 0.5F);
			super.addCollidingBlockToList(world, x, y, z, axisalignedbb, arraylist, entity);

		} else if (j == 1) {
			setBlockBounds(0.5F, 0.5F, 0.0F, 1.0F, 1.0F, 1.0F);
			super.addCollidingBlockToList(world, x, y, z, axisalignedbb, arraylist, entity);

		} else if (j == 2) {
			setBlockBounds(0.0F, 0.5F, 0.5F, 1.0F, 1.0F, 1.0F);
			super.addCollidingBlockToList(world, x, y, z, axisalignedbb, arraylist, entity);

		} else if (j == 3) {
			setBlockBounds(0.0F, 0.5F, 0.0F, 0.5F, 1.0F, 1.0F);
			super.addCollidingBlockToList(world, x, y, z, axisalignedbb, arraylist, entity);
		}

		// X+, X-, Z+, Z-
		boolean[] fences = getFencesShownStairsAbsolute(world, new PC_CoordI(x, y, z));

		if (fences[0]) {
			setBlockBounds(1 - 0.0625F, 0, 0, 1, 1.8F, 1);
			super.addCollidingBlockToList(world, x, y, z, axisalignedbb, arraylist, entity);
		}
		if (fences[1]) {
			setBlockBounds(0, 0, 0, 0.0625F, 1.8F, 1);
			super.addCollidingBlockToList(world, x, y, z, axisalignedbb, arraylist, entity);
		}
		if (fences[2]) {
			setBlockBounds(0, 0, 1 - 0.0625F, 1, 1.8F, 1);
			super.addCollidingBlockToList(world, x, y, z, axisalignedbb, arraylist, entity);
		}
		if (fences[3]) {
			setBlockBounds(0, 0, 0, 1, 1.8F, 0.0625F);
			super.addCollidingBlockToList(world, x, y, z, axisalignedbb, arraylist, entity);
		}

		setBlockBounds(0, 0, 0, 1, 1, 1);
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving entityliving) {

		int dir = ((PC_MathHelper.floor_double(((entityliving.rotationYaw * 4F) / 360F) + 0.5D) & 3) + 2) % 4;
		if (entityliving instanceof EntityPlayer && PC_Utils.isPlacingReversed((EntityPlayer)entityliving)) {
			dir = PC_Utils.reverseSide(dir);
		}
		world.setBlockMetadataWithNotify(x, y, z, dir);

	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}
	
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, Object renderer) {
		float p = 0.0625F;
		boolean swapped = PC_Renderer.swapTerrain(block);

		block.setBlockBounds(0, 0F, 0.5F, 1, p, 1);
		PC_Renderer.renderInvBoxWithTexture(renderer, block, 22);

		block.setBlockBounds(0, 0.5F - p, 0, 1, 0.5F, 0.5F);
		PC_Renderer.renderInvBoxWithTexture(renderer, block, 22);

		block.setBlockBounds(0, 0, 0.5F, p, 0.5F, 1);
		PC_Renderer.renderInvBoxWithTexture(renderer, block, 20);
		block.setBlockBounds(0, 0.5F, 0, p, 0.5F + 0.5F, 0.5F);
		PC_Renderer.renderInvBoxWithTexture(renderer, block, 20);

		PC_Renderer.resetTerrain(swapped);
	}

	@Override
	public void renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, Object renderer) {
		
	}

	@Override
	public List<String> getBlockFlags(World world, PC_CoordI pos, List<String> list) {

		list.add(PC_Utils.NO_HARVEST);
		list.add(PC_Utils.NO_PICKUP);
		list.add(PC_Utils.PASSIVE);
		
		return list;
	}

	@Override
	public List<String> getItemFlags(ItemStack stack, List<String> list) {
		list.add(PC_Utils.NO_BUILD);
		return list;
	}
	
}
