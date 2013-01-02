package powercraft.weasel;

import java.util.List;

import net.minecraft.item.crafting.IRecipe;
import powercraft.management.PC_Block;
import powercraft.management.PC_IModule;
import powercraft.management.PC_IPacketHandler;
import powercraft.management.PC_Item;
import powercraft.management.PC_PacketHandler;
import powercraft.management.PC_Property;
import powercraft.management.PC_Struct2;
import powercraft.management.PC_Utils.ModuleInfo;
import powercraft.management.PC_Utils.ModuleLoader;

public class PCws_App implements PC_IModule {

	public static PC_Block weasel;
	public static PC_Block weaselDiskManager;
	
	public static PC_Item weaselDisk;
	
	@Override
	public String getName() {
		return "Weasel";
	}

	@Override
	public String getVersion() {
		return "1.0.0";
	}

	@Override
	public void preInit() {
		PCws_WeaselManager.registerPluginInfo(new PCws_WeaselPluginInfoCore(), 0);
		PCws_WeaselManager.registerPluginInfo(new PCws_WeaselPluginInfoPort(), 1);
		PCws_WeaselManager.registerPluginInfo(new PCws_WeaselPluginInfoSpeaker(), 2);
		PCws_WeaselManager.registerPluginInfo(new PCws_WeaselPluginInfoDisplay(), 3);
		PCws_WeaselManager.registerPluginInfo(new PCws_WeaselPluginInfoTerminal(), 4);
		PCws_WeaselManager.registerPluginInfo(new PCws_WeaselPluginInfoTouchscreen(), 5);
		PCws_WeaselManager.registerPluginInfo(new PCws_WeaselPluginInfoDiskDrive(), 6);
	}

	@Override
	public void init() {
		PCws_WeaselManager weaselManager = new PCws_WeaselManager();
		ModuleInfo.registerMSGObject(weaselManager);
		ModuleLoader.regsterDataHandler("Weasel", weaselManager);
	}

	@Override
	public void postInit() {}

	@Override
	public void initProperties(PC_Property config) {
		PCws_WeaselHighlightHelper.checkConfigFile(config);
	}

	@Override
	public void initBlocks() {
		weasel = ModuleLoader.register(this, PCws_BlockWeasel.class, PCws_ItemBlockWeasel.class, PCws_TileEntityWeasel.class);
		weaselDiskManager = ModuleLoader.register(this, PCws_BlockWeaselDiskManager.class, PCws_TileEntityWeaselDiskManager.class);
		PC_PacketHandler.registerPackethandler("WeaselDiskDrive", (PC_IPacketHandler)weaselDiskManager);
	}

	@Override
	public void initItems() {
		weaselDisk = ModuleLoader.register(this, PCws_ItemWeaselDisk.class);
	}

	@Override
	public List<IRecipe> initRecipes(List<IRecipe> recipes) {
		return null;
	}

	@Override
	public List<PC_Struct2<String, Class>> registerGuis(List<PC_Struct2<String, Class>> guis) {
		guis.add(new PC_Struct2<String, Class>("WeaselDiskManager", PCws_ContainerWeaselDiskManager.class));
		guis.add(new PC_Struct2<String, Class>("WeaselDiskDrive", PCws_ContainerWeaselDiskDrive.class));
		return guis;
	}

}