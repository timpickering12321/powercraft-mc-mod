package powercraft.api.gres;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import powercraft.api.PC_RectI;
import powercraft.api.PC_Vec2I;

@SideOnly(Side.CLIENT)
public class PC_GresCheckBox extends PC_GresComponent {

	private static final String textureName[] = {"CheckBox", "CheckBoxChecked"};
	
	private boolean state;
	
	public PC_GresCheckBox(String title){
		setText(title);
	}
	
	@Override
	protected PC_Vec2I calculateMinSize() {
		PC_Vec2I tm = getTextureMinSize(textureName[state?1:0]);
		return new PC_Vec2I(tm.x+fontRenderer.getStringWidth(text)+(text!=null&&!text.isEmpty()?1:0), tm.y);
	}

	@Override
	protected PC_Vec2I calculateMaxSize() {
		PC_Vec2I tm = getTextureMinSize(textureName[state?1:0]);
		return new PC_Vec2I(tm.x+fontRenderer.getStringWidth(text)+(text!=null&&!text.isEmpty()?1:0), fontRenderer.FONT_HEIGHT);
	}

	@Override
	protected PC_Vec2I calculatePrefSize() {
		return calculateMaxSize();
	}
	
	@Override
	protected void paint(PC_RectI scissor, double scale, int displayHeight, float timeStamp) {
		PC_Vec2I tm = getTextureMinSize(textureName[state?1:0]);
		drawTexture(textureName[state?1:0], 0, 0, tm.x, tm.y);
		drawString(text, tm.x+1, 0, rect.width - tm.x-1, rect.height, PC_GresAlign.H.CENTER, PC_GresAlign.V.CENTER, false);
	}
	
	@Override
	protected boolean handleMouseButtonDown(PC_Vec2I mouse, int buttons, int eventButton) {
		state=!state;
		super.handleMouseButtonDown(mouse, buttons, eventButton);
		notifyChange();
		return true;
	}
	
}
