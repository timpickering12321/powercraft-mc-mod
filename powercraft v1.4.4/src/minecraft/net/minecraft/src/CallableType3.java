package net.minecraft.src;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import java.util.concurrent.Callable;

@SideOnly(Side.CLIENT)
class CallableType3 implements Callable
{
    /** Reference to the IntegratedServer object. */
    final IntegratedServer theIntegratedServer;

    CallableType3(IntegratedServer par1IntegratedServer)
    {
        this.theIntegratedServer = par1IntegratedServer;
    }

    public String getType()
    {
        return "Integrated Server (map_client.txt)";
    }

    public Object call()
    {
        return this.getType();
    }
}
