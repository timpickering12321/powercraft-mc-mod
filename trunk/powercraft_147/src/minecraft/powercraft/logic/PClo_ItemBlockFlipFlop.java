package powercraft.logic;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import powercraft.management.PC_ItemBlock;
import powercraft.management.PC_MathHelper;
import powercraft.management.PC_Utils.Lang;
import powercraft.management.registry.PC_LangRegistry.LangEntry;
import powercraft.management.registry.PC_MSGRegistry;

public class PClo_ItemBlockFlipFlop extends PC_ItemBlock
{
    public PClo_ItemBlockFlipFlop(int id)
    {
        super(id);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    @Override
    public List<ItemStack> getItemStacks(List<ItemStack> arrayList)
    {
        for (int i = 0; i < PClo_FlipFlopType.TOTAL_FLIPFLOP_COUNT; i++)
        {
            arrayList.add(new ItemStack(this, 1, i));
        }

        return arrayList;
    }

    @Override
    public int getIconFromDamage(int i)
    {
        return PClo_App.flipFlop.getBlockTextureFromSideAndMetadata(1, 0);
    }

    @Override
    public String getItemNameIS(ItemStack itemstack)
    {
        return getItemName() + ".flipflop" + itemstack.getItemDamage();
    }

    @Override
    public boolean isFull3D()
    {
        return false;
    }

    @Override
    public boolean shouldRotateAroundWhenRendering()
    {
        return false;
    }

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean b)
    {
        list.add(getDescriptionForGate(itemStack.getItemDamage()));
    }

    public static String getDescriptionForGate(int dmg)
    {
        return Lang.tr("pc.flipflop." + PClo_FlipFlopType.names[PC_MathHelper.clamp_int(dmg, 0, PClo_FlipFlopType.TOTAL_FLIPFLOP_COUNT - 1)] + ".desc");
    }

	@Override
	public Object msg(int msg, Object... obj) {
		switch(msg){
		case PC_MSGRegistry.MSG_DEFAULT_NAME:
			List<LangEntry> names = (List<LangEntry>)obj[0];
			for (int i = 0; i < PClo_FlipFlopType.TOTAL_FLIPFLOP_COUNT; i++)
	        {
				names.add(new LangEntry(getItemName() + ".flipflop"+i, PClo_FlipFlopType.names[i] + " flipflop"));
	        };
            return names;
		}
		return null;
	}
}
