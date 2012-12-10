package powercraft.deco;

import java.util.List;

import net.minecraft.src.IRecipe;
import powercraft.management.PC_IClientModule;
import powercraft.management.PC_LangEntry;
import powercraft.management.PC_Struct2;
import powercraft.management.PC_Utils;

public class PCde_AppClient extends PCde_App implements PC_IClientModule {

	@Override
	public List<String> loadTextureFiles(List<String> textures) {
		textures.add(PC_Utils.getTerrainFile(this));
		textures.add(PC_Utils.getTextureDirectory(this)+"block_deco.png");
		return textures;
	}

	@Override
	public List<PC_Struct2<String, Class>> registerGuis(
			List<PC_Struct2<String, Class>> guis) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PC_LangEntry> initLanguage(List<PC_LangEntry> lang) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> addSplashes(List<String> list) {
		// TODO Auto-generated method stub
		return null;
	}

}
