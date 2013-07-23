package powercraft.api.registries;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.Configuration;
import powercraft.api.PC_Logger;
import powercraft.api.PC_Module;
import powercraft.api.PC_Security;
import powercraft.api.blocks.PC_BlockInfo;
import powercraft.api.blocks.PC_ITileEntitySpecialRenderer;
import powercraft.api.blocks.PC_TileEntity;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PC_BlockRegistry {

	@SideOnly(Side.CLIENT)
	private static class PC_TileEntitySpecialRenderer extends TileEntitySpecialRenderer{

		@Override
		public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float timeStamp) {
			if(tileEntity instanceof PC_ITileEntitySpecialRenderer){
				((PC_ITileEntitySpecialRenderer)tileEntity).renderTileEntityAt(x, y, z, timeStamp);
			}
		}
		
	}
	
	@SideOnly(Side.CLIENT)
	private static TileEntitySpecialRenderer specialRenderer;
	
	@SideOnly(Side.CLIENT)
	protected static TileEntitySpecialRenderer getSpecialRenderer(){
		if(specialRenderer==null)
			specialRenderer = new PC_TileEntitySpecialRenderer();
		return specialRenderer;
	}
	
	public static Block registerBlock(PC_Module module, Class<? extends Block> clazz) {

		if(!PC_Security.allowedCallerNoException(PC_Module.class)){
			PC_Logger.warning("PC_BlockRegistry.registerBlock shouln't be called. Use instead @PC_FieldGenerator");
		}
		PC_ModuleRegistry.setActiveModule(module);
		Configuration config = module.getConfig();
		PC_BlockInfo blockInfo = clazz.getAnnotation(PC_BlockInfo.class);
		int blockID = config.getBlock(blockInfo.blockid(), blockInfo.defaultid()).getInt();
		if (blockID == -1) {
			PC_Logger.info("Block %s disabled", blockInfo.name());
			return null;
		}
		try {
			Block block = clazz.getConstructor(int.class).newInstance(blockID);
			block.setUnlocalizedName(clazz.getSimpleName());
			PC_LanguageRegistry.registerLanguage(block.getUnlocalizedName()+".name", blockInfo.name());
			GameRegistry.registerBlock(block, blockInfo.itemBlock(), blockInfo.blockid());
			Class<? extends PC_TileEntity> tileEntity = blockInfo.tileEntity();
			if (tileEntity != PC_TileEntity.class) {
				PC_Registry.sidedRegistry.registerTileEntity(tileEntity);
			}
			PC_ModuleRegistry.releaseActiveModule();
			return block;
		} catch (Exception e) {
			e.printStackTrace();
			PC_Logger.severe("Failed to generate block %s", blockInfo.name());
		}
		PC_ModuleRegistry.releaseActiveModule();
		return null;
	}
	
}
