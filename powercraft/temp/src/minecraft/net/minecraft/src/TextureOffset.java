package net.minecraft.src;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

@SideOnly(Side.CLIENT)
public class TextureOffset {

   public final int field_78783_a;
   public final int field_78782_b;


   public TextureOffset(int p_i3146_1_, int p_i3146_2_) {
      this.field_78783_a = p_i3146_1_;
      this.field_78782_b = p_i3146_2_;
   }
}
