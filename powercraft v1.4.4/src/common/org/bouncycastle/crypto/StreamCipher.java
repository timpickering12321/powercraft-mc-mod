package org.bouncycastle.crypto;

public interface StreamCipher
{
    byte returnByte(byte var1);

    void func_74850_a(byte[] var1, int var2, int var3, byte[] var4, int var5) throws DataLengthException;
}
