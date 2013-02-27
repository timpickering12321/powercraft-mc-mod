package powercraft.machines;

import net.minecraft.entity.player.EntityPlayer;
import powercraft.management.gres.PC_GresBaseWithInventory;
import powercraft.management.tileentity.PC_TileEntity;

public class PCma_ContainerRoaster extends PC_GresBaseWithInventory<PCma_TileEntityRoaster>
{
    public PCma_ContainerRoaster(EntityPlayer player, PC_TileEntity te, Object[] o)
    {
        super(player, (PCma_TileEntityRoaster)te, o);
    }
    
    @Override
	protected boolean canShiftTransfer() {
		return true;
	}
    
}