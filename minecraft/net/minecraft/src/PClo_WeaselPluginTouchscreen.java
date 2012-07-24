package net.minecraft.src;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import weasel.Calc;
import weasel.WeaselEngine;
import weasel.exception.WeaselRuntimeException;
import weasel.obj.WeaselInteger;
import weasel.obj.WeaselObject;

public class PClo_WeaselPluginTouchscreen extends PClo_WeaselPlugin {

	public static final int WIDTH = 14*4, HEIGHT = 12*4;
	
	public int screen[][] = new int[WIDTH][HEIGHT];
	/** Rotation, like sign, 0-15 */
	public int rotation;
	
	public PClo_WeaselPluginTouchscreen(PClo_TileEntityWeasel tew){
		super(tew);
	}
	
	@Override
	public boolean doesProvideFunction(String functionName) {
		return getProvidedFunctionNames().contains(functionName);
	}

	private WeaselObject setgetPixel(String functionName,  WeaselObject[] args){
		if(args.length!=3 && args.length!=2)
			throw new WeaselRuntimeException("Invalid call of function " + functionName);
		int x = Calc.toInteger(args[0]);
		int y = Calc.toInteger(args[1]);
		if(x<0 || x>=WIDTH || y<0 || y>=HEIGHT)
			throw new WeaselRuntimeException("Invalid call of function " + functionName);
		WeaselObject c = new WeaselInteger(screen[x][y]);
		if(args.length==3){
			int color = Calc.toInteger(args[2]);
			screen[x][y] = color;
		}
		return c;
	}
	
	private void setPixel(String functionName,  WeaselObject[] args){
		if(args.length!=3)
			throw new WeaselRuntimeException("Invalid call of function " + functionName);
		int x = Calc.toInteger(args[0]);
		int y = Calc.toInteger(args[1]);
		int color = Calc.toInteger(args[2]);
		if(x<0 || x>=WIDTH || y<0 || y>=HEIGHT)
			throw new WeaselRuntimeException("Invalid call of function " + functionName);
		screen[x][y] = color;
	}
	
	private WeaselObject getPixel(String functionName,  WeaselObject[] args){
		if(args.length!=2)
			throw new WeaselRuntimeException("Invalid call of function " + functionName);
		int x = Calc.toInteger(args[0]);
		int y = Calc.toInteger(args[1]);
		if(x<0 || x>=WIDTH || y<0 || y>=HEIGHT)
			throw new WeaselRuntimeException("Invalid call of function " + functionName);
		return new WeaselInteger(screen[x][y]);
	}
	
	private void drawRect(String functionName,  WeaselObject[] args){
		if(args.length!=5 && args.length!=6)
			throw new WeaselRuntimeException("Invalid call of function " + functionName);
		int x1 = Calc.toInteger(args[0]);
		int y1 = Calc.toInteger(args[1]);
		int x2 = Calc.toInteger(args[2]);
		int y2 = Calc.toInteger(args[3]);
		int color = Calc.toInteger(args[4]);
		int flag = args.length<6?0:Calc.toInteger(args[5]);
		int tmp;
		
		if(x2<x1){
			tmp = x1;
			x1 = x2;
			x2 = tmp;
		}
		if(y2<y1){
			tmp = y1;
			y1 = y2;
			y2 = tmp;
		}
		
		if((flag & 1)!=0){
		
			for(int j=y1; j<=y2; j++){
				for(int i=x1; i<=x2; i++){
					if(i>=0 && i<WIDTH && j>=0 && j<HEIGHT)
						screen[i][j] = color;
				}
			}
		
		}else{
			
			for(int i=x1; i<=x2; i++){
				if(i>=0 && i<WIDTH && y1>=0 && y1<HEIGHT)
					screen[i][y1] = color;
				if(i>=0 && i<WIDTH && y2>=0 && y2<HEIGHT)
					screen[i][y2] = color;
			}
			
			for(int j=y1; j<=y2; j++){
				if(x1>=0 && x1<WIDTH && j>=0 && j<HEIGHT)
					screen[x1][j] = color;
				if(x2>=0 && x2<WIDTH && j>=0 && j<HEIGHT)
					screen[x2][j] = color;
			}
			
		}
		
	}
	
	private void drawLine(String functionName, WeaselObject[] args) {
		if(args.length!=5)
			throw new WeaselRuntimeException("Invalid call of function " + functionName);
		int x1 = Calc.toInteger(args[0]);
		int y1 = Calc.toInteger(args[1]);
		int x2 = Calc.toInteger(args[2]);
		int y2 = Calc.toInteger(args[3]);
		int color = Calc.toInteger(args[4]);
		int tmp;
		
		if(x2<x1){
			tmp = x1;
			x1 = x2;
			x2 = tmp;
		}
		if(y2<y1){
			tmp = y1;
			y1 = y2;
			y2 = tmp;
		}
		
		float f;
		int x, y, xd = x2-x1, yd = y2-y1;
		if(xd>yd){
			f = (float)yd/xd;
			for(int i=0; i<xd; i++){
				x = x1 + i;
				y = (int)(y1 + f * i);
				if(x>=0 && x<WIDTH && y>=0 && y<HEIGHT)
					screen[x][y] = color;
			}
		}else{
			f = (float)xd/yd;
			for(int j=0; j<yd; j++){
				x = (int)(x1 + f * j);
				y = y1 + j;
				if(x>=0 && x<WIDTH && y>=0 && y<HEIGHT)
					screen[x][y] = color;
			}
		}
		
	}
	
	private void drawChar(ByteBuffer image, int imageWidth, int x, int y, char _char, int color){
		int xT = (_char % 16) * 8;
		int yT = (_char / 16) * 8;
		int width = PC_Utils.mc().fontRenderer.getCharWidth(_char);

        int xp, yp;
        
        for(int j=0; j<PC_Utils.mc().fontRenderer.FONT_HEIGHT; j++){
        	for(int i=0; i<width; i++){
        		xp = i+x;
        		yp = j+y;
        		if(xp>=0 && xp<WIDTH && yp>=0 && yp<HEIGHT){
        			if(image.get(((i+xT)+(j+yT)*imageWidth)*4)!=0)
        				screen[xp][yp] = color;
        		}
        	}
        }
	}
	
	private void drawString(String functionName,  WeaselObject[] args){
		if(args.length!=4)
			throw new WeaselRuntimeException("Invalid call of function " + functionName);
		int x = Calc.toInteger(args[0]);
		int y = Calc.toInteger(args[1]);
		String s = Calc.toString(args[2]);
		int color = Calc.toInteger(args[3]);
		
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, PC_Utils.mc().fontRenderer.fontTextureName);
		int width = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
		int height = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);
		ByteBuffer image = BufferUtils.createByteBuffer(width * height * 4);
		GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, image);
		
		for(int i=0; i<s.length(); i++){
			char c = s.charAt(i);
			drawChar(image, width, x, y, c, color);
			x += PC_Utils.mc().fontRenderer.getCharWidth(c);
		}
		
	}
	
	@Override
	public WeaselObject callProvidedFunction(WeaselEngine engine,
			String functionName, WeaselObject[] args) {
		if(functionName.equals(getName())){
			return setgetPixel(functionName, args);
		}else if(functionName.equals(getName()+".set") || functionName.equals(getName()+".draw.pixel")){
			setPixel(functionName, args);
		}else if(functionName.equals(getName()+".get")){
			return getPixel(functionName, args);
		}else if(functionName.equals(getName()+".draw.rect")){
			drawRect(functionName, args);
		}else if(functionName.equals(getName()+".draw.line")){
			drawLine(functionName, args);
		}else if(functionName.equals(getName()+".draw.string")){
			drawString(functionName, args);	
		}else{
			throw new WeaselRuntimeException("Invalid call of function " + functionName);
		}
		return null;
	}

	@Override
	public WeaselObject getVariable(String name) {
		if(name.equals(getName()+".WIDTH")){
			return new WeaselInteger(WIDTH);
		}else if(name.equals(getName()+".HEIGHT")){
			return new WeaselInteger(HEIGHT);
		}else if(name.startsWith(getName())){
			String[] s = name.split("\\.");
			int x = Integer.valueOf(s[1].substring(1));
			int y = Integer.valueOf(s[2].substring(1));
			if(x>=0 && x<WIDTH && y>=0 && y<HEIGHT)
				return new WeaselInteger(screen[x][y]);
		}
		return null;
	}

	@Override
	public void setVariable(String name, Object object) {
		if(name.equals(getName()+".WIDTH")){
			return;
		}else if(name.equals(getName()+".HEIGHT")){
			return;
		}else if(name.startsWith(getName())){
			String[] s = name.split("\\.");
			int x = Integer.valueOf(s[1].substring(1));
			int y = Integer.valueOf(s[2].substring(1));
			if(x>=0 && x<WIDTH && y>=0 && y<HEIGHT)
				screen[x][y] = Calc.toInteger(object);
		}
	}

	@Override
	public List<String> getProvidedFunctionNames() {
		ArrayList<String> list = new ArrayList<String>(1);
		list.add(getName());
		list.add(getName()+".set");
		list.add(getName()+".get");
		list.add(getName()+".draw.pixel");
		list.add(getName()+".draw.rect");
		list.add(getName()+".draw.line");
		list.add(getName()+".draw.string");
		return list;
	}

	@Override
	public List<String> getProvidedVariableNames() {
		List<String> list = new ArrayList<String>(1);
		for(int j=0; j<HEIGHT; j++){
			for(int i=0; i<WIDTH; i++){
				list.add(getName()+".x"+i+".y"+j);
			}
		}
		list.add(getName()+".WIDTH");
		list.add(getName()+".HEIGHT");
		return list;
	}

	@Override
	public int getType() {
		return PClo_WeaselType.TOUCHSCREEN;
	}

	@Override
	protected void updateTick() {}

	@Override
	public void onRedstoneSignalChanged() {}

	@Override
	public void restartDevice() {
		for(int j=0; j<HEIGHT; j++){
			for(int i=0; i<WIDTH; i++){
				screen[i][j] = 0x00000000;
			}
		}
	}

	@Override
	public String getError() {
		return null;
	}

	@Override
	public boolean hasError() {
		return false;
	}

	@Override
	public WeaselEngine getWeaselEngine() {
		return null;
	}

	@Override
	public boolean isMaster() {
		return false;
	}

	@Override
	protected void onNetworkChanged() {}

	@Override
	protected void onDeviceDestroyed() {}

	@Override
	public Object callFunctionExternalDelegated(String function, Object... args) {
		return null;
	}

	@Override
	protected PClo_WeaselPlugin readPluginFromNBT(NBTTagCompound tag) {
		for(int j=0; j<HEIGHT; j++){
			for(int i=0; i<WIDTH; i++){
				screen[i][j] = tag.getInteger("pixel"+i+"-"+j);
			}
		}
		rotation = tag.getInteger("rotation");
		return this;
	}

	@Override
	protected NBTTagCompound writePluginToNBT(NBTTagCompound tag) {
		for(int j=0; j<HEIGHT; j++){
			for(int i=0; i<WIDTH; i++){
				tag.setInteger("pixel"+i+"-"+j, screen[i][j]);
			}
		}
		tag.setInteger("rotation", rotation);
		return tag;
	}

	@Override
	public void onBlockPlaced(EntityLiving entityliving) {
		rotation = MathHelper.floor_double(((entityliving.rotationYaw + 180F) * 16F) / 360F + 0.5D) & 0xf;
		restartDevice();
	}

	@Override
	public void onRandomDisplayTick(Random random) {}

	@Override
	public boolean onClick(EntityPlayer player) {
		PC_Utils.openGres(player, new PClo_GuiWeaselTouchscreen(this));
		return true;
	}

	@Override
	public float[] getBounds() {
		return new float[] {0,0,0,1,1,1};
	}

	
}
