package powercraft.teleport;

import java.util.List;

import powercraft.launcher.loader.PC_ClientModule;
import powercraft.launcher.loader.PC_ClientModule.PC_InitLanguage;
import powercraft.launcher.loader.PC_Module.PC_RegisterGuis;
import powercraft.management.PC_Struct2;
import powercraft.management.registry.PC_LangRegistry.LangEntry;

@PC_ClientModule
public class PCtp_AppClient extends PCtp_App {

	@PC_InitLanguage
	public List<LangEntry> initLanguage(List<LangEntry> lang) {
		lang.add(new LangEntry("pc.gui.teleporter.name", "Name:"));
		lang.add(new LangEntry("pc.gui.teleporter.target", "Target:"));
		lang.add(new LangEntry("pc.gui.teleporter.nothing", "<Nothing>"));
		lang.add(new LangEntry("pc.gui.teleporter.page1", "Teleporter"));
		lang.add(new LangEntry("pc.gui.teleporter.animals", "animals"));
		lang.add(new LangEntry("pc.gui.teleporter.monsters", "monsters"));
		lang.add(new LangEntry("pc.gui.teleporter.items", "items"));
		lang.add(new LangEntry("pc.gui.teleporter.players", "players"));
		lang.add(new LangEntry("pc.gui.teleporter.lasers", "lasers"));
		lang.add(new LangEntry("pc.gui.teleporter.sneakTrigger", "sneakTrigger"));
		lang.add(new LangEntry("pc.gui.teleporter.playerChoose", "playerChoose"));
		lang.add(new LangEntry("pc.gui.teleporter.soundEnabled", "soundEnabled"));
		lang.add(new LangEntry("pc.gui.teleporter.north", "north"));
		lang.add(new LangEntry("pc.gui.teleporter.east", "east"));
		lang.add(new LangEntry("pc.gui.teleporter.south", "south"));
		lang.add(new LangEntry("pc.gui.teleporter.west", "west"));
		lang.add(new LangEntry("pc.gui.teleporter.page2", "Property"));
		lang.add(new LangEntry("pc.gui.teleportTo.title", "Teleport To"));
		return lang;
	}

	@PC_RegisterGuis
	public List<PC_Struct2<String, Class>> registerGuis(
			List<PC_Struct2<String, Class>> guis) {
		guis.add(new PC_Struct2<String, Class>("Teleporter", PCtp_GuiTeleporter.class));
		guis.add(new PC_Struct2<String, Class>("PlayerTeleport", PCtp_GuiPlayerTeleport.class));
		return guis;
	}
	
}
