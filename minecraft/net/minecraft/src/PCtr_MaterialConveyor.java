// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode

package net.minecraft.src;

// Referenced classes of package net.minecraft.src:
// Material, MapColor

public class PCtr_MaterialConveyor extends Material {

	public PCtr_MaterialConveyor() {
		super(MapColor.airColor);
	}

	@Override
	public boolean isSolid() {
		return true;
	}

	@Override
	public boolean getCanBlockGrass() {
		return false;
	}

	@Override
	public boolean blocksMovement() {
		return true;
	}
}
