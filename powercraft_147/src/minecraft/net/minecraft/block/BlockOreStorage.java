package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class BlockOreStorage extends Block
{
    public BlockOreStorage(int par1, int par2)
    {
        super(par1, Material.iron);
        this.blockIndexInTexture = par2;
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    /**
     * Returns the block texture based on the side being looked at.  Args: side
     */
    public int getBlockTextureFromSide(int par1)
    {
        return this.blockIndexInTexture;
    }
}
