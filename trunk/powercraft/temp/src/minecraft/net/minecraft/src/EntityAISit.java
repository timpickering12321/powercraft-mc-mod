package net.minecraft.src;

import net.minecraft.src.EntityAIBase;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityTameable;

public class EntityAISit extends EntityAIBase {

   private EntityTameable field_75272_a;
   private boolean field_75271_b = false;


   public EntityAISit(EntityTameable p_i3492_1_) {
      this.field_75272_a = p_i3492_1_;
      this.func_75248_a(5);
   }

   public boolean func_75250_a() {
      if(!this.field_75272_a.func_70909_n()) {
         return false;
      } else if(this.field_75272_a.func_70090_H()) {
         return false;
      } else if(!this.field_75272_a.field_70122_E) {
         return false;
      } else {
         EntityLiving var1 = this.field_75272_a.func_70902_q();
         return var1 == null?true:(this.field_75272_a.func_70068_e(var1) < 144.0D && var1.func_70643_av() != null?false:this.field_75271_b);
      }
   }

   public void func_75249_e() {
      this.field_75272_a.func_70661_as().func_75499_g();
      this.field_75272_a.func_70904_g(true);
   }

   public void func_75251_c() {
      this.field_75272_a.func_70904_g(false);
   }

   public void func_75270_a(boolean p_75270_1_) {
      this.field_75271_b = p_75270_1_;
   }
}
