package net.minecraft.src;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import java.util.List;
import java.util.Random;

public class BlockWoodSlab extends BlockHalfSlab
{
    public static final String[] woodType = new String[] {"oak", "spruce", "birch", "jungle"};

    public BlockWoodSlab(int par1, boolean par2)
    {
        super(par1, par2, Material.wood);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    public int getBlockTextureFromSideAndMetadata(int par1, int par2)
    {
        switch (par2 & 7)
        {
            case 1:
                return 198;

            case 2:
                return 214;

            case 3:
                return 199;

            default:
                return 4;
        }
    }

    public int getBlockTextureFromSide(int par1)
    {
        return this.getBlockTextureFromSideAndMetadata(par1, 0);
    }

    public int idDropped(int par1, Random par2Random, int par3)
    {
        return Block.woodSingleSlab.blockID;
    }

    protected ItemStack createStackedBlock(int par1)
    {
        return new ItemStack(Block.woodSingleSlab.blockID, 2, par1 & 7);
    }

    public String getFullSlabName(int par1)
    {
        if (par1 < 0 || par1 >= woodType.length)
        {
            par1 = 0;
        }

        return super.getBlockName() + "." + woodType[par1];
    }

    @SideOnly(Side.CLIENT)

    public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        if (par1 != Block.woodDoubleSlab.blockID)
        {
            for (int var4 = 0; var4 < 4; ++var4)
            {
                par3List.add(new ItemStack(par1, 1, var4));
            }
        }
    }
}
