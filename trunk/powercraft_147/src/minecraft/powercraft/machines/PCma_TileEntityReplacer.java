package powercraft.machines;

import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import powercraft.management.PC_Color;
import powercraft.management.PC_InvUtils;
import powercraft.management.PC_TileEntity;
import powercraft.management.PC_Utils.SaveHandler;
import powercraft.management.PC_Utils.ValueWriting;
import powercraft.management.inventory.PC_ISpecialAccessInventory;
import powercraft.management.PC_VecF;
import powercraft.management.PC_VecI;

public class PCma_TileEntityReplacer extends PC_TileEntity implements IInventory, PC_ISpecialAccessInventory
{
	
	public static final String COORDOFFSET = "coordOffset", AIDENABLED = "aidEnabled", AIDCOLOR = "aidcolor", STATE = "state";
	
	private static Random rand = new Random();
	
    public ItemStack buildBlock;

    private static final int MAXSTACK = 1;
    private static final int SIZE = 1;

//    public PC_VecI coordOffset = new PC_VecI(0, 1, 0);
//
//    public boolean aidEnabled = false;
//    private PC_Color aidcolor;
//
//    public boolean state = false;

    public int extraMeta = -1;
    
    public PCma_TileEntityReplacer(){
    	setData(COORDOFFSET, new PC_VecI(0, 1, 0));
    	setData(AIDENABLED, false);
    	float used = 2.0f;
        float r = rand.nextFloat();
        used -= r;
        float g = rand.nextFloat();
        used -= g;
        float b = used;

        if (rand.nextBoolean())
        {
            float f = r;
            r = g;
            g = f;
        }

        if (rand.nextBoolean())
        {
        	float f = g;
            g = b;
            b = f;
        }

        if (rand.nextBoolean())
        {
        	float f = b;
            b = r;
            r = f;
        }

        setData(AIDCOLOR, new PC_Color(r, g, b));
    	setData(STATE, false);
    }

	public PC_VecI getCoordOffset() {
		return (PC_VecI)getData(COORDOFFSET);
	}

	public void setCoordOffset(PC_VecI coordOffset) {
		setData(COORDOFFSET, coordOffset);
	}

	public boolean isAidEnabled() {
		return (Boolean)getData(AIDENABLED);
	}

	public void setAidEnabled(boolean aidEnabled) {
		setData(AIDENABLED, aidEnabled);
	}

	public PC_Color getAidcolor() {
		return (PC_Color)getData(AIDCOLOR);
	}

	public void setAidcolor(PC_Color aidcolor) {
		setData(AIDCOLOR, aidcolor);
	}

	public boolean isState() {
		return (Boolean)getData(STATE);
	}

	public void setState(boolean state) {
		setData(STATE, state);
	}

	@Override
    public void updateEntity()
    {
        if (isAidEnabled() && worldObj.isRemote)
        {
            double d = xCoord + rand.nextFloat();
            double d1 = yCoord + 1.1f;
            double d2 = zCoord + rand.nextFloat();
            int a = rand.nextInt(3);
            int b = rand.nextInt(3);
            ValueWriting.spawnParticle("PC_EntityLaserParticleFX", worldObj, new PC_VecF((float)d, (float)d1, (float)d2), getAidcolor(), new PC_VecF(), 0);

            for (int q = 0; q < 8; q++)
            {
            	PC_VecI coordOffset = getCoordOffset();
                d = xCoord + coordOffset.x + rand.nextFloat();
                d1 = yCoord + coordOffset.y + rand.nextFloat();
                d2 = zCoord + coordOffset.z + rand.nextFloat();
                a = rand.nextInt(3);
                b = rand.nextInt(3);

                while (a == b)
                {
                    b = rand.nextInt(3);
                }

                boolean aa = rand.nextBoolean();
                boolean bb = rand.nextBoolean();

                switch (a)
                {
                    case 0:
                        d = aa ? Math.floor(d) : Math.ceil(d);
                        break;

                    case 1:
                        d1 = aa ? Math.floor(d1) : Math.ceil(d1);
                        break;

                    case 2:
                        d2 = aa ? Math.floor(d2) : Math.ceil(d2);
                        break;
                }

                switch (b)
                {
                    case 0:
                        d = bb ? Math.floor(d) : Math.ceil(d);
                        break;

                    case 1:
                        d1 = bb ? Math.floor(d1) : Math.ceil(d1);
                        break;

                    case 2:
                        d2 = bb ? Math.floor(d2) : Math.ceil(d2);
                        break;
                }

                ValueWriting.spawnParticle("PC_EntityLaserParticleFX", worldObj, new PC_VecF((float)d, (float)d1, (float)d2), getAidcolor(), new PC_VecF(), 0);
            }
        }

        super.updateEntity();
    }

    @Override
    public boolean canPlayerInsertStackTo(int slot, ItemStack stack)
    {
        if (stack.getItem() instanceof ItemBlock)
        {
            return true;
        }

        return false;
    }

    @Override
    public int getSizeInventory()
    {
        return SIZE;
    }

    @Override
    public ItemStack getStackInSlot(int i)
    {
        return buildBlock;
    }

    @Override
    public ItemStack decrStackSize(int i, int j)
    {
        if (j > 0)
        {
            ItemStack itemStack = buildBlock;
            buildBlock = null;
            return itemStack;
        }

        return null;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int i)
    {
        if (buildBlock != null)
        {
            ItemStack itemstack = buildBlock;
            buildBlock = null;
            return itemstack;
        }
        else
        {
            return null;
        }
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack itemstack)
    {
    	extraMeta = -1;
        buildBlock = itemstack;

        if (itemstack != null && itemstack.stackSize > getInventoryStackLimit())
        {
            itemstack.stackSize = getInventoryStackLimit();
        }

        onInventoryChanged();
    }

    @Override
    public String getInvName()
    {
        return "Block Replacer";
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound)
    {
        super.readFromNBT(nbttagcompound);
        NBTTagList nbttaglist = nbttagcompound.getTagList("Items");

        if (nbttaglist.tagCount() > 0)
        {
            NBTTagCompound nbttagcompound1 = (NBTTagCompound) nbttaglist.tagAt(0);
            buildBlock = ItemStack.loadItemStackFromNBT(nbttagcompound1);
        }
        
        extraMeta = nbttagcompound.getInteger("extraMeta");

    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound)
    {
        super.writeToNBT(nbttagcompound);
        NBTTagList nbttaglist = new NBTTagList();

        if (buildBlock != null)
        {
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            buildBlock.writeToNBT(nbttagcompound1);
            nbttaglist.appendTag(nbttagcompound1);
        }

        nbttagcompound.setTag("Items", nbttaglist);
        nbttagcompound.setInteger("extraMeta", extraMeta);
    }

    @Override
    public int getInventoryStackLimit()
    {
        return MAXSTACK;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer entityplayer)
    {
        return false;
    }

    @Override
    public void openChest()
    {
    }

    @Override
    public void closeChest()
    {
    }

    @Override
    public boolean insertStackIntoInventory(ItemStack stack)
    {
        return PC_InvUtils.addWholeItemStackToInventory(this, stack);
    }

    @Override
    public boolean canDispenseStackFrom(int slot)
    {
        return true;
    }

    @Override
    public boolean needsSpecialInserter()
    {
        return false;
    }

    @Override
    public boolean canUpdate()
    {
        return true;
    }

    @Override
    public boolean canMachineInsertStackTo(int slot, ItemStack stack)
    {
        return canPlayerInsertStackTo(slot, stack);
    }
    
    @Override
	public boolean canDropStackFrom(int slot) {
		return true;
	}

	@Override
	public int getSlotStackLimit(int slotIndex) {
		return getInventoryStackLimit();
	}

	@Override
	public boolean canPlayerTakeStack(int slotIndex, EntityPlayer entityPlayer) {
		return true;
	}
    
}