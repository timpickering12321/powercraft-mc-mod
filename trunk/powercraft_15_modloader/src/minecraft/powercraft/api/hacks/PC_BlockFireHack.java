package powercraft.api.hacks;

import net.minecraft.src.Block;
import net.minecraft.src.BlockFire;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.World;
import powercraft.api.block.PC_Block;
import powercraft.api.utils.PC_Utils;

public class PC_BlockFireHack extends BlockFire {
	
	public PC_BlockFireHack(Block fire) {
		super(fire.blockID);
	}
	
	@Override
	public boolean canBlockCatchFire(IBlockAccess world, int x, int y, int z) {
		if (PC_Utils.getBID(world, x, y, z) < 256) {
			return super.canBlockCatchFire(world, x, y, z);
		} else {
			return PC_Hacks.canBlockCatchFire(world, x, y, z);
		}
	}
	
	@Override
	public int getChanceToEncourageFire(World world, int x, int y, int z, int chance) {
		if (PC_Utils.getBID(world, x, y, z) < 256) {
			return super.getChanceToEncourageFire(world, x, y, z, chance);
		}else{
			return PC_Hacks.getChanceToEncourageFire(world, x, y, z, chance);
		}
	}
	
}
