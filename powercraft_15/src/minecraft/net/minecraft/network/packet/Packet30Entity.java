package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet30Entity extends Packet
{
    /** The ID of this entity. */
    public int entityId;

    /** The X axis relative movement. */
    public byte xPosition;

    /** The Y axis relative movement. */
    public byte yPosition;

    /** The Z axis relative movement. */
    public byte zPosition;

    /** The X axis rotation. */
    public byte yaw;

    /** The Y axis rotation. */
    public byte pitch;

    /** Boolean set to true if the entity is rotating. */
    public boolean rotating = false;

    public Packet30Entity() {}

    public Packet30Entity(int par1)
    {
        this.entityId = par1;
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInputStream par1DataInputStream) throws IOException
    {
        this.entityId = par1DataInputStream.readInt();
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(DataOutputStream par1DataOutputStream) throws IOException
    {
        par1DataOutputStream.writeInt(this.entityId);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.handleEntity(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 4;
    }

    public String toString()
    {
        return "Entity_" + super.toString();
    }

    /**
     * only false for the abstract Packet class, all real packets return true
     */
    public boolean isRealPacket()
    {
        return true;
    }

    /**
     * eg return packet30entity.entityId == entityId; WARNING : will throw if you compare a packet to a different packet
     * class
     */
    public boolean containsSameEntityIDAs(Packet par1Packet)
    {
        Packet30Entity packet30entity = (Packet30Entity)par1Packet;
        return packet30entity.entityId == this.entityId;
    }
}
