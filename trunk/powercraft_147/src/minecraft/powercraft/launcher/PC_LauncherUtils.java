package powercraft.launcher;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraft.crash.CallableMinecraftVersion;

public class PC_LauncherUtils {

	protected static PC_LauncherUtils instance;
	
	public static boolean isClient(){
		return instance.pIsClient();
	}
	
	public static File createFile(File pfile, String name) {
		File file = new File(pfile, name);
		if(!file.exists())
			file.mkdirs();
	    return file;
	}

	public static File getMCDirectory() {
		return Minecraft.getMinecraftDir();
	}
	
	public static File getPowerCraftFile() {
		return createFile(getMCDirectory(), "PowerCraft");
	}
	
	public static File getPowerCraftModuleFile() {
		return createFile(getPowerCraftFile(), "Modules");
	}
	
	public static String getMinecraftVersion(){
		return new CallableMinecraftVersion(null).minecraftVersion();
	}

	public static mod_PowerCraft getMod() {
		return mod_PowerCraft.getInstance();
	}

	public static String getVersion() {
		return mod_PowerCraft.getInstance().getVersion();
	}

	public static boolean isForge() {
		return true;
	}

	public static void create() {
		if(instance==null){
			instance = new PC_LauncherUtils();
		}
	}
	
	protected boolean pIsClient(){
		return false;
	}
	
}