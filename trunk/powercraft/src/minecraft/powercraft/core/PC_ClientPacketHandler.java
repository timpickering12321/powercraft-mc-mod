package powercraft.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class PC_ClientPacketHandler extends PC_PacketHandler {

	@Override
	protected void handleIncomingPacketHandlerPacket(ObjectInputStream input, EntityPlayer player) throws IOException, ClassNotFoundException{
		String name = (String)input.readObject();
		Object[] o = (Object[])input.readObject();
		PC_IPacketHandler ph = packetHandler.get(name);
		if(ph!=null){
			ph.handleIncomingPacket(player, o);
		}
	}
	
	@Override
	protected void handleIncomingGuiPacket(ObjectInputStream input, EntityPlayer player) throws ClassNotFoundException, IOException{
		PC_Utils.openGres("", player, new Object[]{input});
    }
	
}