package powercraft.api.multiblocks.cable.redstone;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import powercraft.api.PC_ClientRegistry;
import powercraft.api.items.PC_ItemInfo;
import powercraft.api.multiblocks.PC_MultiblockTileEntity;
import powercraft.api.multiblocks.cable.PC_CableItem;

@PC_ItemInfo(name="Isolated Redstone", itemid="isolatedRedstone", defaultid=17002)
public class PC_RedstoneIsolatedItem extends PC_CableItem {

	private static Icon redstoneIcon;
	private static Icon isolationIcon;
	public static PC_RedstoneIsolatedItem item;
	
	public PC_RedstoneIsolatedItem(int id) {
		super(id);
		item = this;
		setCreativeTab(CreativeTabs.tabRedstone);
		setHasSubtypes(true);
	}

	@Override
	public Class<? extends PC_MultiblockTileEntity> getTileEntityClass() {
		return PC_RedstoneIsolatedTileEntity.class;
	}
	
	@Override
	public PC_MultiblockTileEntity getTileEntity(ItemStack itemStack) {
		return new PC_RedstoneIsolatedTileEntity(itemStack.getItemDamage());
	}

	@Override
	public void loadMultiblockItem() {
		isolationIcon = PC_ClientRegistry.registerIcon("isolation", itemInfo.itemid());
	}

	@Override
	public void registerRecipes() {
		
	}

	@Override
	public void loadIcons() {
		itemIcon = PC_ClientRegistry.registerIcon("isolation");
		redstoneIcon = PC_ClientRegistry.registerIcon("redstone");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIconFromDamageForRenderPass(int damage, int pass) {
		if(pass==0){
			return itemIcon;
		}
		return redstoneIcon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean requiresMultipleRenderPasses(){
		return true;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack itemStack, int pass){
		if(pass==0){
			return 0xFFFFFFFF;
		}
		return ItemDye.dyeColors[itemStack.getItemDamage()];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(int itemID, CreativeTabs creativeTabs, List list) {
		for(int i=0; i<16; i++){
			list.add(new ItemStack(this, 1, i));
		}
	}

	public static Icon getCableIcon() {
		return isolationIcon;
	}
	
}