package net.minecraft.src;

import java.util.ArrayList;

import net.minecraftforge.common.IShearable;

public class EntityMooshroom extends EntityCow implements IShearable
{
    public EntityMooshroom(World par1World)
    {
        super(par1World);
        this.texture = "/mob/redcow.png";
        this.setSize(0.9F, 1.3F);
    }

    public boolean interact(EntityPlayer par1EntityPlayer)
    {
        ItemStack var2 = par1EntityPlayer.inventory.getCurrentItem();

        if (var2 != null && var2.itemID == Item.bowlEmpty.shiftedIndex && this.getGrowingAge() >= 0)
        {
            if (var2.stackSize == 1)
            {
                par1EntityPlayer.inventory.setInventorySlotContents(par1EntityPlayer.inventory.currentItem, new ItemStack(Item.bowlSoup));
                return true;
            }

            if (par1EntityPlayer.inventory.addItemStackToInventory(new ItemStack(Item.bowlSoup)) && !par1EntityPlayer.capabilities.isCreativeMode)
            {
                par1EntityPlayer.inventory.decrStackSize(par1EntityPlayer.inventory.currentItem, 1);
                return true;
            }
        }

        return super.interact(par1EntityPlayer);
    }

    public EntityMooshroom spawnBabyAnimal(EntityAgeable par1EntityAgeable)
    {
        return new EntityMooshroom(this.worldObj);
    }

    public EntityAgeable func_90011_a(EntityAgeable par1EntityAgeable)
    {
        return this.spawnBabyAnimal(par1EntityAgeable);
    }

    @Override
    public boolean isShearable(ItemStack item, World world, int X, int Y, int Z)
    {
        return getGrowingAge() >= 0;
    }

    @Override
    public ArrayList<ItemStack> onSheared(ItemStack item, World world, int X, int Y, int Z, int fortune)
    {
        setDead();
        EntityCow entitycow = new EntityCow(worldObj);
        entitycow.setLocationAndAngles(posX, posY, posZ, rotationYaw, rotationPitch);
        entitycow.setEntityHealth(getHealth());
        entitycow.renderYawOffset = renderYawOffset;
        worldObj.spawnEntityInWorld(entitycow);
        worldObj.spawnParticle("largeexplode", posX, posY + (double)(height / 2.0F), posZ, 0.0D, 0.0D, 0.0D);
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>();

        for (int x = 0; x < 5; x++)
        {
            ret.add(new ItemStack(Block.mushroomRed));
        }

        return ret;
    }
}
