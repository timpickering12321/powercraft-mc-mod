package net.minecraft.src;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import weasel.Calc;
import weasel.WeaselEngine;
import weasel.exception.WeaselRuntimeException;
import weasel.obj.WeaselInteger;
import weasel.obj.WeaselObject;
import weasel.obj.WeaselString;


/**
 * @author MightyPork
 */
public class PClo_WeaselPluginDiskManager extends PClo_WeaselPlugin {

	/**
	 * A digital workbench
	 * 
	 * @param tew
	 */
	public PClo_WeaselPluginDiskManager(PClo_TileEntityWeasel tew) {
		super(tew);
	}
	
	
	@Override
	public boolean onClick(EntityPlayer player) {
		PC_Utils.openGres(player, new PClo_GuiWeaselDiskManager());
		return true;
	}

	@Override
	public boolean doesProvideFunction(String functionName) {
		return getProvidedFunctionNames().contains(functionName);
	}

	@Override
	public WeaselObject callProvidedFunction(WeaselEngine engine, String functionName, WeaselObject[] args) {
		return null;
	}

	@Override
	public WeaselObject getVariable(String name) {
		return null;
	}

	@Override
	public void setVariable(String name, Object object) {
	}

	@Override
	public List<String> getProvidedFunctionNames() {
		List<String> list = new ArrayList<String>(0);
		return list;
	}

	@Override
	public List<String> getProvidedVariableNames() {
		List<String> list = new ArrayList<String>(1);
		return list;
	}

	@Override
	public int getType() {
		return PClo_WeaselType.DISK_MANAGER;
	}

	@Override
	protected boolean updateTick() {return false;}

	@Override
	public void onRedstoneSignalChanged() {}

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
		return this;
	}

	@Override
	protected NBTTagCompound writePluginToNBT(NBTTagCompound tag) {
		return tag;
	}

	@Override
	public void restartDevice() {}
	
	@Override
	public void onBlockPlaced(EntityLiving entityliving) {}


	@Override
	public void onRandomDisplayTick(Random random) {}
	
	
	@Override
	public float[] getBounds() {
		return new float[] {0,0,0,1,1-2*0.0625F,1};
	}
}
