package net.minecraft.client.renderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.concurrent.Callable;
import net.minecraft.crash.CrashReportCategory;

@SideOnly(Side.CLIENT)
class CallableParticlePositionInfo implements Callable
{
    final double field_85101_a;

    final double field_85099_b;

    final double field_85100_c;

    final RenderGlobal field_85098_d;

    CallableParticlePositionInfo(RenderGlobal par1RenderGlobal, double par2, double par4, double par6)
    {
        this.field_85098_d = par1RenderGlobal;
        this.field_85101_a = par2;
        this.field_85099_b = par4;
        this.field_85100_c = par6;
    }

    public String func_85097_a()
    {
        return CrashReportCategory.func_85074_a(this.field_85101_a, this.field_85099_b, this.field_85100_c);
    }

    public Object call()
    {
        return this.func_85097_a();
    }
}
