package net.minecraft.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import java.util.Random;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MathHelper;
import net.minecraft.util.WeightedRandomChestContent;

public class ItemEnchantedBook extends Item
{
    public ItemEnchantedBook(int par1)
    {
        super(par1);
    }

    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack par1ItemStack)
    {
        return true;
    }

    /**
     * Checks isDamagable and if it cannot be stacked
     */
    public boolean isItemTool(ItemStack par1ItemStack)
    {
        return false;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Return an item rarity from EnumRarity
     */
    public EnumRarity getRarity(ItemStack par1ItemStack)
    {
        return this.func_92110_g(par1ItemStack).tagCount() > 0 ? EnumRarity.uncommon : super.getRarity(par1ItemStack);
    }

    public NBTTagList func_92110_g(ItemStack par1ItemStack)
    {
        return par1ItemStack.stackTagCompound != null && par1ItemStack.stackTagCompound.hasKey("StoredEnchantments") ? (NBTTagList)par1ItemStack.stackTagCompound.getTag("StoredEnchantments") : new NBTTagList();
    }

    @SideOnly(Side.CLIENT)

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
    {
        super.addInformation(par1ItemStack, par2EntityPlayer, par3List, par4);
        NBTTagList nbttaglist = this.func_92110_g(par1ItemStack);

        if (nbttaglist != null)
        {
            for (int i = 0; i < nbttaglist.tagCount(); ++i)
            {
                short short1 = ((NBTTagCompound)nbttaglist.tagAt(i)).getShort("id");
                short short2 = ((NBTTagCompound)nbttaglist.tagAt(i)).getShort("lvl");

                if (Enchantment.enchantmentsList[short1] != null)
                {
                    par3List.add(Enchantment.enchantmentsList[short1].getTranslatedName(short2));
                }
            }
        }
    }

    public void func_92115_a(ItemStack par1ItemStack, EnchantmentData par2EnchantmentData)
    {
        NBTTagList nbttaglist = this.func_92110_g(par1ItemStack);
        boolean flag = true;

        for (int i = 0; i < nbttaglist.tagCount(); ++i)
        {
            NBTTagCompound nbttagcompound = (NBTTagCompound)nbttaglist.tagAt(i);

            if (nbttagcompound.getShort("id") == par2EnchantmentData.enchantmentobj.effectId)
            {
                if (nbttagcompound.getShort("lvl") < par2EnchantmentData.enchantmentLevel)
                {
                    nbttagcompound.setShort("lvl", (short)par2EnchantmentData.enchantmentLevel);
                }

                flag = false;
                break;
            }
        }

        if (flag)
        {
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            nbttagcompound1.setShort("id", (short)par2EnchantmentData.enchantmentobj.effectId);
            nbttagcompound1.setShort("lvl", (short)par2EnchantmentData.enchantmentLevel);
            nbttaglist.appendTag(nbttagcompound1);
        }

        if (!par1ItemStack.hasTagCompound())
        {
            par1ItemStack.setTagCompound(new NBTTagCompound());
        }

        par1ItemStack.getTagCompound().setTag("StoredEnchantments", nbttaglist);
    }

    public ItemStack func_92111_a(EnchantmentData par1EnchantmentData)
    {
        ItemStack itemstack = new ItemStack(this);
        this.func_92115_a(itemstack, par1EnchantmentData);
        return itemstack;
    }

    @SideOnly(Side.CLIENT)
    public void func_92113_a(Enchantment par1Enchantment, List par2List)
    {
        for (int i = par1Enchantment.getMinLevel(); i <= par1Enchantment.getMaxLevel(); ++i)
        {
            par2List.add(this.func_92111_a(new EnchantmentData(par1Enchantment, i)));
        }
    }

    public ItemStack func_92109_a(Random par1Random)
    {
        Enchantment enchantment = Enchantment.field_92090_c[par1Random.nextInt(Enchantment.field_92090_c.length)];
        ItemStack itemstack = new ItemStack(this.itemID, 1, 0);
        int i = MathHelper.getRandomIntegerInRange(par1Random, enchantment.getMinLevel(), enchantment.getMaxLevel());
        this.func_92115_a(itemstack, new EnchantmentData(enchantment, i));
        return itemstack;
    }

    public WeightedRandomChestContent func_92114_b(Random par1Random)
    {
        return this.func_92112_a(par1Random, 1, 1, 1);
    }

    public WeightedRandomChestContent func_92112_a(Random par1Random, int par2, int par3, int par4)
    {
        Enchantment enchantment = Enchantment.field_92090_c[par1Random.nextInt(Enchantment.field_92090_c.length)];
        ItemStack itemstack = new ItemStack(this.itemID, 1, 0);
        int l = MathHelper.getRandomIntegerInRange(par1Random, enchantment.getMinLevel(), enchantment.getMaxLevel());
        this.func_92115_a(itemstack, new EnchantmentData(enchantment, l));
        return new WeightedRandomChestContent(itemstack, par2, par3, par4);
    }
}
