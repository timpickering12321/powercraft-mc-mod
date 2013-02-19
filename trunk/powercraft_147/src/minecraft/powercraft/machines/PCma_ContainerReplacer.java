package powercraft.machines;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import powercraft.management.PC_TileEntity;
import powercraft.management.PC_Utils.GameInfo;
import powercraft.management.gres.PC_GresBaseWithInventory;

public class PCma_ContainerReplacer extends PC_GresBaseWithInventory<PCma_TileEntityReplacer>
{
    public PCma_ContainerReplacer(EntityPlayer player, PC_TileEntity te, Object[] o)
    {
        super(player, (PCma_TileEntityReplacer)te, o);
    }
}