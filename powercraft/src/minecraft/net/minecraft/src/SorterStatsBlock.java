package net.minecraft.src;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import java.util.Comparator;

@SideOnly(Side.CLIENT)
class SorterStatsBlock implements Comparator
{
    final GuiStats statsGUI;

    final GuiSlotStatsBlock slotStatsBlockGUI;

    SorterStatsBlock(GuiSlotStatsBlock par1GuiSlotStatsBlock, GuiStats par2GuiStats)
    {
        this.slotStatsBlockGUI = par1GuiSlotStatsBlock;
        this.statsGUI = par2GuiStats;
    }

    public int func_78334_a(StatCrafting par1StatCrafting, StatCrafting par2StatCrafting)
    {
        int var3 = par1StatCrafting.getItemID();
        int var4 = par2StatCrafting.getItemID();
        StatBase var5 = null;
        StatBase var6 = null;

        if (this.slotStatsBlockGUI.field_77264_j == 2)
        {
            var5 = StatList.mineBlockStatArray[var3];
            var6 = StatList.mineBlockStatArray[var4];
        }
        else if (this.slotStatsBlockGUI.field_77264_j == 0)
        {
            var5 = StatList.objectCraftStats[var3];
            var6 = StatList.objectCraftStats[var4];
        }
        else if (this.slotStatsBlockGUI.field_77264_j == 1)
        {
            var5 = StatList.objectUseStats[var3];
            var6 = StatList.objectUseStats[var4];
        }

        if (var5 != null || var6 != null)
        {
            if (var5 == null)
            {
                return 1;
            }

            if (var6 == null)
            {
                return -1;
            }

            int var7 = GuiStats.getStatsFileWriter(this.slotStatsBlockGUI.theStats).writeStat(var5);
            int var8 = GuiStats.getStatsFileWriter(this.slotStatsBlockGUI.theStats).writeStat(var6);

            if (var7 != var8)
            {
                return (var7 - var8) * this.slotStatsBlockGUI.field_77265_k;
            }
        }

        return var3 - var4;
    }

    public int compare(Object par1Obj, Object par2Obj)
    {
        return this.func_78334_a((StatCrafting)par1Obj, (StatCrafting)par2Obj);
    }
}
