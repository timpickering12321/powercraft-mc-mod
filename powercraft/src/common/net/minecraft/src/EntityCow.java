package net.minecraft.src;

public class EntityCow extends EntityAnimal
{
    public EntityCow(World par1World)
    {
        super(par1World);
        this.texture = "/mob/cow.png";
        this.setSize(0.9F, 1.3F);
        this.getNavigator().setAvoidsWater(true);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIPanic(this, 0.38F));
        this.tasks.addTask(2, new EntityAIMate(this, 0.2F));
        this.tasks.addTask(3, new EntityAITempt(this, 0.25F, Item.wheat.shiftedIndex, false));
        this.tasks.addTask(4, new EntityAIFollowParent(this, 0.25F));
        this.tasks.addTask(5, new EntityAIWander(this, 0.2F));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        this.tasks.addTask(7, new EntityAILookIdle(this));
    }

    public boolean isAIEnabled()
    {
        return true;
    }

    public int getMaxHealth()
    {
        return 10;
    }

    protected String getLivingSound()
    {
        return "mob.cow.say";
    }

    protected String getHurtSound()
    {
        return "mob.cow.hurt";
    }

    protected String getDeathSound()
    {
        return "mob.cow.hurt";
    }

    protected void playStepSound(int par1, int par2, int par3, int par4)
    {
        this.func_85030_a("mob.cow.step", 0.15F, 1.0F);
    }

    protected float getSoundVolume()
    {
        return 0.4F;
    }

    protected int getDropItemId()
    {
        return Item.leather.shiftedIndex;
    }

    protected void dropFewItems(boolean par1, int par2)
    {
        int var3 = this.rand.nextInt(3) + this.rand.nextInt(1 + par2);
        int var4;

        for (var4 = 0; var4 < var3; ++var4)
        {
            this.dropItem(Item.leather.shiftedIndex, 1);
        }

        var3 = this.rand.nextInt(3) + 1 + this.rand.nextInt(1 + par2);

        for (var4 = 0; var4 < var3; ++var4)
        {
            if (this.isBurning())
            {
                this.dropItem(Item.beefCooked.shiftedIndex, 1);
            }
            else
            {
                this.dropItem(Item.beefRaw.shiftedIndex, 1);
            }
        }
    }

    public boolean interact(EntityPlayer par1EntityPlayer)
    {
        ItemStack var2 = par1EntityPlayer.inventory.getCurrentItem();

        if (var2 != null && var2.itemID == Item.bucketEmpty.shiftedIndex)
        {
            if (--var2.stackSize <= 0)
            {
                par1EntityPlayer.inventory.setInventorySlotContents(par1EntityPlayer.inventory.currentItem, new ItemStack(Item.bucketMilk));
            }
            else if (!par1EntityPlayer.inventory.addItemStackToInventory(new ItemStack(Item.bucketMilk)))
            {
                par1EntityPlayer.dropPlayerItem(new ItemStack(Item.bucketMilk.shiftedIndex, 1, 0));
            }

            return true;
        }
        else
        {
            return super.interact(par1EntityPlayer);
        }
    }

    public EntityCow spawnBabyAnimal(EntityAgeable par1EntityAgeable)
    {
        return new EntityCow(this.worldObj);
    }

    public EntityAgeable func_90011_a(EntityAgeable par1EntityAgeable)
    {
        return this.spawnBabyAnimal(par1EntityAgeable);
    }
}
