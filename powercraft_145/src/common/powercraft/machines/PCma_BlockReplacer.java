package powercraft.machines;

import java.util.List;
import java.util.Random;

import net.minecraft.src.Block;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;
import net.minecraft.src.MathHelper;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import powercraft.management.PC_Block;
import powercraft.management.PC_FakePlayer;
import powercraft.management.PC_IItemInfo;
import powercraft.management.PC_Struct2;
import powercraft.management.PC_Utils;
import powercraft.management.PC_VecI;

public class PCma_BlockReplacer extends PC_Block implements PC_IItemInfo
{
    private static final int TXDOWN = 109, TXTOP = 153, TXSIDE = 137;

    public PCma_BlockReplacer(int id)
    {
        super(id, TXSIDE, Material.ground);
        setHardness(0.7F);
        setResistance(10.0F);
        setStepSound(Block.soundStoneFootstep);
        setCreativeTab(CreativeTabs.tabDecorations);
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return true;
    }

    @Override
    public int getBlockTextureFromSideAndMetadata(int s, int m)
    {
        if (s == 1)
        {
            return TXTOP;
        }

        if (s == 0)
        {
            return TXDOWN;
        }
        else
        {
            return TXSIDE;
        }
    }

    @Override
    public void onBlockClicked(World world, int i, int j, int k, EntityPlayer entityplayer)
    {
        PCma_TileEntityReplacer tileentity = (PCma_TileEntityReplacer) world.getBlockTileEntity(i, j, k);

        if (tileentity != null)
        {
            tileentity.aidEnabled = !tileentity.aidEnabled;
        }
    }

    @Override
    public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int par6, float par7, float par8, float par9)
    {
        ItemStack ihold = entityplayer.getCurrentEquippedItem();

        if (ihold != null)
        {
            if (ihold.getItem() instanceof ItemBlock && ihold.getItem().shiftedIndex != blockID)
            {
                Block bhold = Block.blocksList[ihold.getItem().shiftedIndex];
            }
            else if (ihold.getItem().shiftedIndex == PC_Utils.getPCObjectIDByName("PCco_ItemActivator"))
            {
                int l = MathHelper.floor_double(((entityplayer.rotationYaw * 4F) / 360F) + 0.5D) & 3;

                if (PC_Utils.isPlacingReversed(entityplayer))
                {
                    l = PC_Utils.reverseSide(l);
                }

                if (entityplayer.isSneaking())
                {
                    l = PC_Utils.isPlacingReversed(entityplayer) ? 5 : 4;
                }

                PCma_TileEntityReplacer tileentity = (PCma_TileEntityReplacer) world.getBlockTileEntity(i, j, k);

                if (tileentity != null)
                {
                    switch (l)
                    {
                        case 0:
                            tileentity.coordOffset.z++;
                            break;

                        case 2:
                            tileentity.coordOffset.z--;
                            break;

                        case 3:
                            tileentity.coordOffset.x++;
                            break;

                        case 1:
                            tileentity.coordOffset.x--;
                            break;

                        case 4:
                            tileentity.coordOffset.y++;
                            break;

                        case 5:
                            tileentity.coordOffset.y--;
                            break;
                    }

                    tileentity.coordOffset.x = MathHelper.clamp_int(tileentity.coordOffset.x, -16, 16);
                    tileentity.coordOffset.y = MathHelper.clamp_int(tileentity.coordOffset.y, -16, 16);
                    tileentity.coordOffset.z = MathHelper.clamp_int(tileentity.coordOffset.z, -16, 16);
                }

                return true;
            }
        }

        if (world.isRemote)
        {
            return true;
        }

        PC_Utils.openGres("Replacer", entityplayer, i, j, k);
        return true;
    }

    @Override
    public int tickRate()
    {
        return 1;
    }

    @Override
    public void onNeighborBlockChange(World world, int i, int j, int k, int l)
    {
        world.scheduleBlockUpdate(i, j, k, blockID, tickRate());
    }

    private boolean replacer_canHarvestBlockAt(World world, PC_VecI pos)
    {
        int id = PC_Utils.getMD(world, pos);

        if (id == 0 || Block.blocksList[id] == null)
        {
            return true;
        }

        if (!PC_Utils.hasFlag(world, pos, PC_Utils.NO_HARVEST))
        {
            return false;
        }

        if (id == 7 && pos.y == 0)
        {
            return false;
        }

        return true;
    }

    private boolean replacer_canPlaceBlockAt(World world, ItemStack itemstack, PC_VecI pos)
    {
        if (itemstack == null)
        {
            return true;
        }

        Item item = itemstack.getItem();

        if (item.shiftedIndex == Block.lockedChest.blockID)
        {
            return PC_Utils.getTE(world, pos) == null;
        }

        if (item instanceof ItemBlock)
        {
            Block block = Block.blocksList[item.shiftedIndex];

            if (block == null)
            {
                return false;
            }

            if (PC_Utils.hasFlag(itemstack, PC_Utils.NO_BUILD))
            {
                return false;
            }

            if (block.hasTileEntity())
            {
                return false;
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    private boolean replacer_placeBlockAt(World world, int meta, ItemStack itemstack, PC_VecI pos)
    {
        if (itemstack == null)
        {
        	PC_Utils.setBID(world, pos, 0, 0);
            return true;
        }

        if (itemstack.itemID == Block.lockedChest.blockID)
        {
        	PC_Utils.setBIDNoNotify(world, pos, 0, 0);
            world.removeBlockTileEntity(pos.x, pos.y, pos.z);

            if (!Item.itemsList[Block.lockedChest.blockID].onItemUse(itemstack, new PC_FakePlayer(world), world, pos.x, pos.y + 1, pos.z, 0, 0.0f, 0.0f, 0.0f))
            {
                return false;
            }

            itemstack.stackSize--;

            if (meta != -1)
            {
            	PC_Utils.setMDNoNotify(world, pos, meta);
            }

            return true;
        }

        if (!replacer_canPlaceBlockAt(world, itemstack, pos))
        {
            return false;
        }

        ItemBlock iblock = (ItemBlock) itemstack.getItem();

        if (iblock.shiftedIndex == Block.waterStill.blockID)
        {
            iblock = (ItemBlock) Item.itemsList[Block.waterMoving.blockID];
        }

        if (iblock.shiftedIndex == Block.lavaStill.blockID)
        {
            iblock = (ItemBlock) Item.itemsList[Block.lavaMoving.blockID];
        }
        
        if (PC_Utils.setBIDNoNotify(world, pos, iblock.getBlockID(), iblock.getMetadata(itemstack.getItemDamage())))
        {
            if (PC_Utils.getBID(world, pos) == iblock.getBlockID())
            {
                world.notifyBlockChange(pos.x, pos.y, pos.z, iblock.getBlockID());
            }

            if (meta != -1 && !iblock.getHasSubtypes())
            {
            	PC_Utils.setMDNoNotify(world, pos, meta);
            }

            itemstack.stackSize--;
        }

        return true;
    }

    private PC_Struct2<ItemStack, Integer> replacer_harvestBlockAt(World world, PC_VecI pos)
    {
        ItemStack loot = null;
        int meta = PC_Utils.getMD(world, pos);

        if (!replacer_canHarvestBlockAt(world, pos))
        {
            return null;
        }

        if (PC_Utils.getTE(world, pos) != null)
        {
            return new PC_Struct2<ItemStack, Integer>(PC_Utils.extractAndRemoveChest(world, pos), meta);
        }

        Block block = Block.blocksList[PC_Utils.getBID(world, pos)];

        if (block == null)
        {
            return null;
        }

        if (PC_Block.canSilkHarvest(block))
        {
            loot = PC_Block.createStackedBlock(block, PC_Utils.getMD(world, pos));
        }
        else
        {
            int dropId = block.blockID;
            int dropMeta = block.damageDropped(PC_Utils.getMD(world, pos));
            int dropQuant = block.quantityDropped(world.rand);

            if (dropId <= 0)
            {
                dropId = PC_Utils.getBID(world, pos);
            }

            if (dropQuant <= 0)
            {
                dropQuant = 1;
            }

            loot = new ItemStack(dropId, dropQuant, dropMeta);
        }

        return new PC_Struct2<ItemStack, Integer>(loot, meta);
    }

    private void swapBlocks(PCma_TileEntityReplacer te)
    {
        PC_VecI pos = te.getCoord().offset(te.coordOffset);

        if (pos.equals(te.getCoord()))
        {
            return;
        }

        if (!replacer_canHarvestBlockAt(te.worldObj, pos))
        {
            return;
        }

        if (!replacer_canPlaceBlockAt(te.worldObj, te.buildBlock, pos))
        {
            return;
        }

        PC_Struct2<ItemStack, Integer> harvested = replacer_harvestBlockAt(te.worldObj, pos);

        if (!replacer_placeBlockAt(te.worldObj, te.extraMeta, te.buildBlock, pos))
        {
            if (harvested != null)
            {
                replacer_placeBlockAt(te.worldObj, harvested.b, harvested.a, pos);
            }

            return;
        }

        if (harvested == null)
        {
            te.buildBlock = null;
            te.extraMeta = -1;
        }
        else
        {
            te.buildBlock = harvested.a;
            te.extraMeta = harvested.b;
        }
    }

    @Override
    public void updateTick(World world, int i, int j, int k, Random random)
    {
        PCma_TileEntityReplacer ter = (PCma_TileEntityReplacer) world.getBlockTileEntity(i, j, k);

        if (ter != null && !world.isRemote)
        {
            boolean powered = isIndirectlyPowered(world, i, j, k);

            if (powered != ter.state)
            {
                swapBlocks(ter);
                ter.state = powered;
            }
        }
    }

    private boolean isIndirectlyPowered(World world, int i, int j, int k)
    {
        if (world.isBlockGettingPowered(i, j, k))
        {
            return true;
        }

        if (world.isBlockIndirectlyGettingPowered(i, j, k))
        {
            return true;
        }

        if (world.isBlockGettingPowered(i, j - 1, k))
        {
            return true;
        }

        if (world.isBlockIndirectlyGettingPowered(i, j - 1, k))
        {
            return true;
        }

        return false;
    }

    @Override
    public TileEntity createNewTileEntity(World world)
    {
        return new PCma_TileEntityReplacer();
    }

    @Override
    public int getMobilityFlag()
    {
        return 0;
    }

    @Override
    public void breakBlock(World world, int i, int j, int k, int par5, int par6)
    {
        PCma_TileEntityReplacer tileentity = (PCma_TileEntityReplacer) world.getBlockTileEntity(i, j, k);
        Random random = new Random();

        if (tileentity != null)
        {
            if (tileentity.buildBlock != null)
            {
                float f = random.nextFloat() * 0.8F + 0.1F;
                float f1 = random.nextFloat() * 0.8F + 0.1F;
                float f2 = random.nextFloat() * 0.8F + 0.1F;
                EntityItem entityitem = new EntityItem(world, i + f, j + f1, k + f2, tileentity.buildBlock);
                float f3 = 0.05F;
                entityitem.motionX = (float) random.nextGaussian() * f3;
                entityitem.motionY = (float) random.nextGaussian() * f3 + 0.2F;
                entityitem.motionZ = (float) random.nextGaussian() * f3;
                world.spawnEntityInWorld(entityitem);
            }
        }

        super.breakBlock(world, i, j, k, par5, par6);
    }

    @Override
    public List<ItemStack> getItemStacks(List<ItemStack> arrayList)
    {
        arrayList.add(new ItemStack(this));
        return arrayList;
    }

	@Override
	public Object msg(World world, PC_VecI pos, int msg, Object... obj) {
		switch (msg){
		case PC_Utils.MSG_DEFAULT_NAME:
			return "Replacer";
		case PC_Utils.MSG_ITEM_FLAGS:{
			List<String> list = (List<String>)obj[1];
			list.add(PC_Utils.NO_BUILD);
			return list;
		}case PC_Utils.MSG_BLOCK_FLAGS:{
			List<String> list = (List<String>)obj[1];
	   		list.add(PC_Utils.NO_HARVEST);
	   		list.add(PC_Utils.NO_PICKUP);
	   		list.add(PC_Utils.HARVEST_STOP);
	   		return list;
		}
		}
		return null;
	}
    
}
