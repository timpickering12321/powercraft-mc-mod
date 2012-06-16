package net.minecraft.src;


/**
 * Item Block replacement for lights (sets tile entity)
 * 
 * @author MightyPork
 * @copy (c) 2012
 * 
 */
public class PClo_ItemBlockLight extends ItemBlock {

	/**
	 * @param i ID
	 */
	public PClo_ItemBlockLight(int i) {
		super(i);
		setMaxDamage(0);
		setHasSubtypes(true);
	}

	@Override
	public int getBlockID() {
		return mod_PClogic.lightOff.blockID;
	}

	@Override
	public String getItemNameIS(ItemStack itemstack) {
		return super.getItemName() + "." + itemstack.getItemDamage();
	}

	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int l) {
		int id = world.getBlockId(i, j, k);

		if (id == Block.snow.blockID) {
			l = 1;
		} else if (id != Block.vine.blockID && id != Block.tallGrass.blockID && id != Block.deadBush.blockID) {
			if (l == 0) {
				j--;
			}

			if (l == 1) {
				j++;
			}

			if (l == 2) {
				k--;
			}

			if (l == 3) {
				k++;
			}

			if (l == 4) {
				i--;
			}

			if (l == 5) {
				i++;
			}
		}

		if (itemstack.stackSize == 0) { return false; }

		if (!entityplayer.canPlayerEdit(i, j, k)) { return false; }

		if (j == 255 && Block.blocksList[getBlockID()].blockMaterial.isSolid()) { return false; }

		if (world.canBlockBePlacedAt(mod_PClogic.lightOff.blockID, i, j, k, false, l)) {
			Block block = mod_PClogic.lightOff;
			if (world.setBlockWithNotify(i, j, k, block.blockID)) {
				block.onBlockPlaced(world, i, j, k, l);
				block.onBlockPlacedBy(world, i, j, k, entityplayer);
				world.playSoundEffect(i + 0.5F, j + 0.5F, k + 0.5F, block.stepSound.getStepSound(),
						(block.stepSound.getVolume() + 1.0F) / 2.0F, block.stepSound.getPitch() * 0.8F);

				// set tile entity
				PClo_TileEntityLight tei = (PClo_TileEntityLight) world.getBlockTileEntity(i, j, k);
				if (tei == null) {
					tei = (PClo_TileEntityLight) ((BlockContainer) block).getBlockEntity();
				}
				tei.setColor(itemstack.getItemDamage());
				world.setBlockTileEntity(i, j, k, tei);
				itemstack.stackSize--;
			}
		}
		return true;
	}

	@Override
	public int getIconFromDamage(int i) {
		return mod_PClogic.lightOn.getBlockTextureFromSideAndMetadata(1, 0);
	}
}
