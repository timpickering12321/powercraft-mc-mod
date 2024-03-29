package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityFallingSand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDragonEgg extends Block
{
    public BlockDragonEgg(int par1, int par2)
    {
        super(par1, par2, Material.dragonEgg);
        this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 1.0F, 0.9375F);
    }

    /**
     * Called whenever the block is added into the world. Args: world, x, y, z
     */
    public void onBlockAdded(World par1World, int par2, int par3, int par4)
    {
        par1World.scheduleBlockUpdate(par2, par3, par4, this.blockID, this.tickRate());
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5)
    {
        par1World.scheduleBlockUpdate(par2, par3, par4, this.blockID, this.tickRate());
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random)
    {
        this.fallIfPossible(par1World, par2, par3, par4);
    }

    /**
     * Checks if the dragon egg can fall down, and if so, makes it fall.
     */
    private void fallIfPossible(World par1World, int par2, int par3, int par4)
    {
        if (BlockSand.canFallBelow(par1World, par2, par3 - 1, par4) && par3 >= 0)
        {
            byte var5 = 32;

            if (!BlockSand.fallInstantly && par1World.checkChunksExist(par2 - var5, par3 - var5, par4 - var5, par2 + var5, par3 + var5, par4 + var5))
            {
                EntityFallingSand var6 = new EntityFallingSand(par1World, (double)((float)par2 + 0.5F), (double)((float)par3 + 0.5F), (double)((float)par4 + 0.5F), this.blockID);
                par1World.spawnEntityInWorld(var6);
            }
            else
            {
                par1World.setBlockWithNotify(par2, par3, par4, 0);

                while (BlockSand.canFallBelow(par1World, par2, par3 - 1, par4) && par3 > 0)
                {
                    --par3;
                }

                if (par3 > 0)
                {
                    par1World.setBlockWithNotify(par2, par3, par4, this.blockID);
                }
            }
        }
    }

    /**
     * Called upon block activation (right click on the block.)
     */
    public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9)
    {
        this.teleportNearby(par1World, par2, par3, par4);
        return true;
    }

    /**
     * Called when the block is clicked by a player. Args: x, y, z, entityPlayer
     */
    public void onBlockClicked(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer)
    {
        this.teleportNearby(par1World, par2, par3, par4);
    }

    /**
     * Teleports the dragon egg somewhere else in a 31x19x31 area centered on the egg.
     */
    private void teleportNearby(World par1World, int par2, int par3, int par4)
    {
        if (par1World.getBlockId(par2, par3, par4) == this.blockID)
        {
            for (int var5 = 0; var5 < 1000; ++var5)
            {
                int var6 = par2 + par1World.rand.nextInt(16) - par1World.rand.nextInt(16);
                int var7 = par3 + par1World.rand.nextInt(8) - par1World.rand.nextInt(8);
                int var8 = par4 + par1World.rand.nextInt(16) - par1World.rand.nextInt(16);

                if (par1World.getBlockId(var6, var7, var8) == 0)
                {
                    if (!par1World.isRemote)
                    {
                        par1World.setBlockAndMetadataWithNotify(var6, var7, var8, this.blockID, par1World.getBlockMetadata(par2, par3, par4));
                        par1World.setBlockWithNotify(par2, par3, par4, 0);
                    }
                    else
                    {
                        short var9 = 128;

                        for (int var10 = 0; var10 < var9; ++var10)
                        {
                            double var11 = par1World.rand.nextDouble();
                            float var13 = (par1World.rand.nextFloat() - 0.5F) * 0.2F;
                            float var14 = (par1World.rand.nextFloat() - 0.5F) * 0.2F;
                            float var15 = (par1World.rand.nextFloat() - 0.5F) * 0.2F;
                            double var16 = (double)var6 + (double)(par2 - var6) * var11 + (par1World.rand.nextDouble() - 0.5D) * 1.0D + 0.5D;
                            double var18 = (double)var7 + (double)(par3 - var7) * var11 + par1World.rand.nextDouble() * 1.0D - 0.5D;
                            double var20 = (double)var8 + (double)(par4 - var8) * var11 + (par1World.rand.nextDouble() - 0.5D) * 1.0D + 0.5D;
                            par1World.spawnParticle("portal", var16, var18, var20, (double)var13, (double)var14, (double)var15);
                        }
                    }

                    return;
                }
            }
        }
    }

    /**
     * How many world ticks before ticking
     */
    public int tickRate()
    {
        return 5;
    }

    /**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    public boolean isOpaqueCube()
    {
        return false;
    }

    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns true if the given side of this block type should be rendered, if the adjacent block is at the given
     * coordinates.  Args: blockAccess, x, y, z, side
     */
    public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        return true;
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return 27;
    }

    @SideOnly(Side.CLIENT)

    /**
     * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
     */
    public int idPicked(World par1World, int par2, int par3, int par4)
    {
        return 0;
    }
}
