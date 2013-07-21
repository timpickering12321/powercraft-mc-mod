package powercraft.core.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import powercraft.api.PC_ClientRegistry;
import powercraft.api.PC_Direction;
import powercraft.api.PC_Utils;
import powercraft.api.blocks.PC_Block;
import powercraft.api.blocks.PC_BlockInfo;
import powercraft.api.blocks.PC_TileEntity;

@PC_BlockInfo(name="Generator", blockid="generator", defaultid=3001, tileEntity = PC_TileEntityGenerator.class)
public class PC_BlockGenerator extends PC_Block {

	private Icon frontLevel[] = new Icon[4];
	
	public PC_BlockGenerator(int id) {
		super(id, Material.ground);
		setCreativeTab(CreativeTabs.tabDecorations);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void loadIcons() {
		blockIcon = PC_ClientRegistry.registerIcon("DefaultMaschineTexture");
		frontLevel[0] = PC_ClientRegistry.registerIcon("Front_Lvl0");
		frontLevel[1] = PC_ClientRegistry.registerIcon("Front_Lvl1");
		frontLevel[2] = PC_ClientRegistry.registerIcon("Front_Lvl2");
		frontLevel[3] = PC_ClientRegistry.registerIcon("Front_Lvl3");
	}

	@Override
	public void registerRecipes() {
		
	}
	
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack itemStack) {
		int l = MathHelper.floor_double((double)(entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		PC_Utils.setMD(world, x, y, z, PC_Direction.PLAYER2MD[l]);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Icon getBlockTexture(IBlockAccess world, int x, int y, int z, int side) {
		if(side==PC_Utils.getRotation(world, x, y, z)){
			PC_TileEntityGenerator generator = PC_Utils.getTE(world, x, y, z);
			if(generator!=null && generator.getHeat()>0){
				return frontLevel[(generator.getHeat()-1)*3/PC_TileEntityGenerator.maxHeat+1];
			}else{
				return frontLevel[0];
			}
		}else{
			return blockIcon;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIcon(int side, int metadata) {
		if(side==5)
			return frontLevel[0];
		return blockIcon;
	}
	
}