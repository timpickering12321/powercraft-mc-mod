package powercraft.checkpoints;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import powercraft.management.PC_TileEntity;
import powercraft.management.PC_Utils.GameInfo;
import powercraft.management.gres.PC_GresBaseWithInventory;
import powercraft.management.inventory.PC_Slot;

public class PCcp_ContainerCheckpoint extends PC_GresBaseWithInventory<PCcp_TileEntityCheckpoint> {
	
	public PCcp_ContainerCheckpoint(EntityPlayer player, PC_TileEntity te, Object[] o) {
		super(player, GameInfo.<PCcp_TileEntityCheckpoint>getTE(player.worldObj, (Integer)o[0], (Integer)o[1], (Integer)o[2]), o);
	}

}