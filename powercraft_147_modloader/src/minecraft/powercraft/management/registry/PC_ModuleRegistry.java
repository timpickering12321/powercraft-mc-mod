package powercraft.management.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import powercraft.launcher.PC_Launcher;
import powercraft.launcher.PC_ModuleObject;
import powercraft.management.item.PC_IItemInfo;

public final class PC_ModuleRegistry {

	public static HashMap<String, PC_ModuleObject> getModules() {
		return PC_Launcher.getLauncher().getModules();
	}
	
	public static PC_ModuleObject getModule(Object o) {
		if (o instanceof PC_IItemInfo) {
			return ((PC_IItemInfo) o).getModule();
		}
		return null;
	}
	
	public static PC_ModuleObject getModule(String name) {
		HashMap<String, PC_ModuleObject> modules = getModules();
		if (modules.containsKey(name)) {
			return modules.get(name);
		}
		return null;
	}

	public static List<PC_ModuleObject> getModuleList() {
		return new ArrayList<PC_ModuleObject>(getModules().values());
	}
	
}
