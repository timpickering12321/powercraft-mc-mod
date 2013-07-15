package powercraft.api.blocks;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import powercraft.api.PC_ClientRegistry;
import powercraft.api.PC_Module;
import powercraft.api.PC_Registry;
import powercraft.api.PC_Renderer;
import powercraft.api.PC_Utils;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class PC_BlockWithoutTileEntity extends Block implements PC_IBlock {

	public final PC_BlockInfo blockInfo;

	public final PC_Module module;
	
	public PC_BlockWithoutTileEntity(int id, Material material) {
		super(id, material);
		blockInfo = getClass().getAnnotation(PC_BlockInfo.class);
		module = PC_Registry.getActiveModule();
	}
	
	@Override
	public PC_BlockInfo getBlockInfo() {
		return blockInfo;
	}

	@Override
	public PC_Module getModule() {
		return module;
	}

	@Override
	public void onBlockMessage(World world, int x, int y, int z, EntityPlayer player, NBTTagCompound nbtTagCompound) {

	}

	@Override
	public void loadFromNBT(World world, int x, int y, int z, NBTTagCompound nbtTagCompound) {

	}
	
	@Override
	public void saveToNBT(World world, int x, int y, int z, NBTTagCompound nbtTagCompound) {

	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegistry) {
		PC_ClientRegistry.registerIcons(this, iconRegistry);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean renderInventoryBlock(int metadata, RenderBlocks renderer) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, RenderBlocks renderer) {
		return false;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderType() {
		return PC_Renderer.getRenderType();
	}
	
}