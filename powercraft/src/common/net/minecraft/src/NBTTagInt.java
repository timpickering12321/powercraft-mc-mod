package net.minecraft.src;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagInt extends NBTBase
{
    public int data;

    public NBTTagInt(String par1Str)
    {
        super(par1Str);
    }

    public NBTTagInt(String par1Str, int par2)
    {
        super(par1Str);
        this.data = par2;
    }

    void write(DataOutput par1DataOutput) throws IOException
    {
        par1DataOutput.writeInt(this.data);
    }

    void load(DataInput par1DataInput) throws IOException
    {
        this.data = par1DataInput.readInt();
    }

    public byte getId()
    {
        return (byte)3;
    }

    public String toString()
    {
        return "" + this.data;
    }

    public NBTBase copy()
    {
        return new NBTTagInt(this.getName(), this.data);
    }

    public boolean equals(Object par1Obj)
    {
        if (super.equals(par1Obj))
        {
            NBTTagInt var2 = (NBTTagInt)par1Obj;
            return this.data == var2.data;
        }
        else
        {
            return false;
        }
    }

    public int hashCode()
    {
        return super.hashCode() ^ this.data;
    }
}
