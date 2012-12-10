package powercraft.transport;

import java.util.List;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Block;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import powercraft.management.PC_Block;
import powercraft.management.PC_IRotatedBox;
import powercraft.management.PC_Utils;

public abstract class PCtr_BlockBeltBase extends PC_Block implements PC_IRotatedBox
{
    public PCtr_BlockBeltBase(int textureID)
    {
        super(textureID, PCtr_MaterialConveyor.getMaterial());
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, PCtr_BeltHelper.HEIGHT, 1.0F);
        setHardness(0.22F);
        setResistance(8.0F);
        setStepSound(Block.soundMetalFootstep);
        setCreativeTab(CreativeTabs.tabTransport);
    }

    @Override
    public abstract String getDefaultName();
    @Override
    public abstract void onEntityCollidedWithBlock(World world, int i, int j, int k, Entity entity);

    @Override
    public int getRotation(int meta)
    {
        return PCtr_BeltHelper.getRotation(meta);
    }

    @Override
    public boolean renderItemHorizontal()
    {
        return false;
    }

    @Override
    public int getBlockTexture(IBlockAccess iblockaccess, int i, int j, int k, int l)
    {
        return getBlockTextureFromSideAndMetadata(l, iblockaccess.getBlockMetadata(i, j, k));
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i, int j, int k)
    {
        return AxisAlignedBB.getBoundingBox(i, 0.0F + j, k, (i + 1), (j + PCtr_BeltHelper.HEIGHT_COLLISION + 0.0F), (k + 1));
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int i, int j, int k)
    {
        float f = 0;
        f = 0.0F + PCtr_BeltHelper.HEIGHT_SELECTED;
        return AxisAlignedBB.getBoundingBox(i, 0.0F + j, k, (i + 1), j + f, (float) k + 1);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess iblockaccess, int i, int j, int k)
    {
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.0F + PCtr_BeltHelper.HEIGHT, 1.0F);
    }

    @Override
    public void setBlockBoundsForItemRender()
    {
        setBlockBounds(0.0F, 0.5F, 0.0F, 1.0F, 0.6F, 1.0F);
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess iblockaccess, int i, int j, int k, int l)
    {
        return l != 1;
    }

    @Override
    public void onBlockPlacedBy(World world, int i, int j, int k, EntityLiving entityliving)
    {
        int l = PCtr_BeltHelper.getPlacedMeta(entityliving);
        world.setBlockMetadataWithNotify(i, j, k, l);
    }

    @Override
    public int tickRate()
    {
        return 1;
    }

    @Override
    public int getBlockTextureFromSideAndMetadata(int i, int j)
    {
        if (i == 0)
        {
            return 1;
        }

        if (i == 1)
        {
            return blockIndexInTexture;
        }
        else
        {
            return 2;
        }
    }

    @Override
    public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int par6, float par7, float par8, float par9)
    {
        return PCtr_BeltHelper.blockActivated(world, i, j, k, entityplayer);
    }
    
    @Override
   	public List<String> getBlockFlags(World world, PC_VecI pos, List<String> list) {

   		list.add(PC_Utils.NO_HARVEST);
   		list.add(PC_Utils.NO_PICKUP);
   		return list;
   	}

   	@Override
   	public List<String> getItemFlags(ItemStack stack, List<String> list) {
   		list.add(PC_Utils.NO_BUILD);
   		return list;
   	}
    
}
