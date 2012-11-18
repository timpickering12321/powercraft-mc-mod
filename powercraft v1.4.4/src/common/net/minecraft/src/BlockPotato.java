package net.minecraft.src;

public class BlockPotato extends BlockCrops
{
    public BlockPotato(int par1)
    {
        super(par1, 200);
    }

    public int getBlockTextureFromSideAndMetadata(int par1, int par2)
    {
        if (par2 < 7)
        {
            if (par2 == 6)
            {
                par2 = 5;
            }

            return this.blockIndexInTexture + (par2 >> 1);
        }
        else
        {
            return this.blockIndexInTexture + 4;
        }
    }

    protected int getSeedItem()
    {
        return Item.potatoe.shiftedIndex;
    }

    protected int getCropItem()
    {
        return Item.potatoe.shiftedIndex;
    }

    public void dropBlockAsItemWithChance(World par1World, int par2, int par3, int par4, int par5, float par6, int par7)
    {
        super.dropBlockAsItemWithChance(par1World, par2, par3, par4, par5, par6, 0);

        if (!par1World.isRemote)
        {
            if (par5 >= 7 && par1World.rand.nextInt(50) == 0)
            {
                this.dropBlockAsItem_do(par1World, par2, par3, par4, new ItemStack(Item.poisonousPotato));
            }
        }
    }
}
