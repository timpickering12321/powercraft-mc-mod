package powercraft.deco;

import powercraft.management.PC_ITileEntityRenderer;
import powercraft.management.PC_Renderer;
import powercraft.management.PC_TileEntity;
import powercraft.management.PC_Utils.ModuleInfo;

public class PCde_TileEntityStairs extends PC_TileEntity implements PC_ITileEntityRenderer {
	
	private PCde_ModelStairs model = new PCde_ModelStairs();

	@Override
	public void renderTileEntityAt(double x, double y, double z, float f0) {


		PC_Renderer.glPushMatrix();
		float f = 1.0F;

		PC_Renderer.glTranslatef((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F);

		PC_Renderer.bindTexture(ModuleInfo.getTextureDirectory(ModuleInfo.getModule("Deco")) + "block_deco.png");

		PC_Renderer.glPushMatrix();
		PC_Renderer.glScalef(f, -f, -f);

		int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
		PC_Renderer.glRotatef(90F + 90F * meta, 0, 1, 0);

		boolean[] fences = PCde_BlockStairs.getFencesShownStairsRelative(worldObj, getCoord());
		model.setStairsFences(fences[0], fences[1]);

		model.render();

		PC_Renderer.glPopMatrix();

		PC_Renderer.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		PC_Renderer.glPopMatrix();
		
	}

}
