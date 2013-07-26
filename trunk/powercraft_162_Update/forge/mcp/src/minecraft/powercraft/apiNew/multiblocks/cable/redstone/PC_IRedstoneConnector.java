package powercraft.api.multiblocks.cable.redstone;


import powercraft.apiOld.PC_Direction;


public interface PC_IRedstoneConnector {

	public int canConnectTo(PC_Direction dir, PC_Direction dir2, int cables);


	public int getRedstoneValue(PC_Direction dir, PC_Direction dir2, int cable);


	public void setRedstoneValue(PC_Direction dir, PC_Direction dir2, int cable, int value);

}
