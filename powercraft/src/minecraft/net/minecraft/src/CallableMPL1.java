package net.minecraft.src;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import java.util.concurrent.Callable;

@SideOnly(Side.CLIENT)
class CallableMPL1 implements Callable
{
    /** Reference to the WorldClient object. */
    final WorldClient theWorldClient;

    CallableMPL1(WorldClient par1WorldClient)
    {
        this.theWorldClient = par1WorldClient;
    }

    /**
     * Returns the size and contents of the entity list.
     */
    public String getEntityCountAndList()
    {
        return WorldClient.getEntityList(this.theWorldClient).size() + " total; " + WorldClient.getEntityList(this.theWorldClient).toString();
    }

    public Object call()
    {
        return this.getEntityCountAndList();
    }
}
