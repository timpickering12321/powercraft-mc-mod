package mods.betterworld.CB.item;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.betterworld.CB.block.BWCB_BlockWoodR;
import mods.betterworld.CB.core.BWCB_BlockList;
import mods.betterworld.CB.tileEntity.BWCB_TileEntityBlockWoodR;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager.ForceChunkEvent;

public class BWCB_ItemBlockWoodR extends ItemBlock {

	public BWCB_ItemBlockWoodR(int id) {
		super(id);
		this.setMaxDamage(0);
		setHasSubtypes(true);
		setUnlocalizedName("BlockPlanksResistent");

	}

	@Override
	public int getMetadata(int damageValue) {
		return damageValue;
	}

	public int getItemDamageVal(ItemStack itemstack) {
		return itemstack.getItemDamage();
	}

	String[] a = new String[BWCB_BlockList.blockPlanksResistentName.size()];

	@Override
	public String getUnlocalizedName(ItemStack itemStack) {
		return "BlockPlanksResistent."
				+ BWCB_BlockList.blockPlanksResistentName.get(itemStack
						.getItemDamage());

	}

	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIconFromDamage(int par1) {
		return super.getIconFromDamage(par1);
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player,
			World world, int x, int y, int z, int side, float hitX, float hitY,
			float hitZ, int metadata) {

		if (super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY,
				hitZ, metadata)) {
			((BWCB_TileEntityBlockWoodR) world.getBlockTileEntity(x, y, z))
					.setBlockDamageID(metadata);
			((BWCB_TileEntityBlockWoodR) world.getBlockTileEntity(x, y, z))
					.setUserName(player.username);
			((BWCB_TileEntityBlockWoodR) world.getBlockTileEntity(x, y, z))
					.setLightValue(BWCB_BlockList.blockPlanksResistentLight
							.get(metadata));
			((BWCB_TileEntityBlockWoodR) world.getBlockTileEntity(x, y, z))
					.setBlockLocked(false);
			return true;
		}
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(int var1, CreativeTabs var2, List var3) {
		BWCB_BlockWoodR block = (BWCB_BlockWoodR) Block.blocksList[getBlockID()];
		for (int i = 0; i < block.getSubBlockCount(); i++) {
			var3.add(new ItemStack(var1, 1, i));

		}
	}
}