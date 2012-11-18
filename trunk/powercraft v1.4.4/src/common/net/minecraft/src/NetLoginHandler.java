package net.minecraft.src;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import javax.crypto.SecretKey;

import cpw.mods.fml.common.network.FMLNetworkHandler;
import net.minecraft.server.MinecraftServer;

public class NetLoginHandler extends NetHandler
{
    private byte[] field_72536_d;

    public static Logger logger = Logger.getLogger("Minecraft");

    private static Random rand = new Random();
    public TcpConnection myTCPConnection;
    public boolean connectionComplete = false;

    private MinecraftServer mcServer;
    private int connectionTimer = 0;
    public String clientUsername = null;
    private volatile boolean field_72544_i = false;

    private String loginServerId = "";
    private SecretKey field_72542_k = null;

    public NetLoginHandler(MinecraftServer par1MinecraftServer, Socket par2Socket, String par3Str) throws IOException
    {
        this.mcServer = par1MinecraftServer;
        this.myTCPConnection = new TcpConnection(par2Socket, par3Str, this, par1MinecraftServer.getKeyPair().getPrivate());
        this.myTCPConnection.field_74468_e = 0;
    }

    public void tryLogin()
    {
        if (this.field_72544_i)
        {
            this.initializePlayerConnection();
        }

        if (this.connectionTimer++ == 6000)
        {
            this.raiseErrorAndDisconnect("Took too long to log in");
        }
        else
        {
            this.myTCPConnection.processReadPackets();
        }
    }

    public void raiseErrorAndDisconnect(String par1Str)
    {
        try
        {
            logger.info("Disconnecting " + this.getUsernameAndAddress() + ": " + par1Str);
            this.myTCPConnection.addToSendQueue(new Packet255KickDisconnect(par1Str));
            this.myTCPConnection.serverShutdown();
            this.connectionComplete = true;
        }
        catch (Exception var3)
        {
            var3.printStackTrace();
        }
    }

    public void handleClientProtocol(Packet2ClientProtocol par1Packet2ClientProtocol)
    {
        this.clientUsername = par1Packet2ClientProtocol.getUsername();

        if (!this.clientUsername.equals(StringUtils.stripControlCodes(this.clientUsername)))
        {
            this.raiseErrorAndDisconnect("Invalid username!");
        }
        else
        {
            PublicKey var2 = this.mcServer.getKeyPair().getPublic();

            if (par1Packet2ClientProtocol.getProtocolVersion() != 49)
            {
                if (par1Packet2ClientProtocol.getProtocolVersion() > 49)
                {
                    this.raiseErrorAndDisconnect("Outdated server!");
                }
                else
                {
                    this.raiseErrorAndDisconnect("Outdated client!");
                }
            }
            else
            {
                this.loginServerId = this.mcServer.isServerInOnlineMode() ? Long.toString(rand.nextLong(), 16) : "-";
                this.field_72536_d = new byte[4];
                rand.nextBytes(this.field_72536_d);
                this.myTCPConnection.addToSendQueue(new Packet253ServerAuthData(this.loginServerId, var2, this.field_72536_d));
            }
        }
    }

    public void handleSharedKey(Packet252SharedKey par1Packet252SharedKey)
    {
        PrivateKey var2 = this.mcServer.getKeyPair().getPrivate();
        this.field_72542_k = par1Packet252SharedKey.func_73303_a(var2);

        if (!Arrays.equals(this.field_72536_d, par1Packet252SharedKey.func_73302_b(var2)))
        {
            this.raiseErrorAndDisconnect("Invalid client reply");
        }

        this.myTCPConnection.addToSendQueue(new Packet252SharedKey());
    }

    public void handleClientCommand(Packet205ClientCommand par1Packet205ClientCommand)
    {
        if (par1Packet205ClientCommand.forceRespawn == 0)
        {
            if (this.mcServer.isServerInOnlineMode())
            {
                (new ThreadLoginVerifier(this)).start();
            }
            else
            {
                this.field_72544_i = true;
            }
        }
    }

    public void handleLogin(Packet1Login par1Packet1Login)
    {
        FMLNetworkHandler.handleLoginPacketOnServer(this, par1Packet1Login);
    }

    public void initializePlayerConnection()
    {
        FMLNetworkHandler.onConnectionReceivedFromClient(this, this.mcServer, this.myTCPConnection.getSocketAddress(), this.clientUsername);
    }

    public void completeConnection(String var1)
    {
        if (var1 != null)
        {
            this.raiseErrorAndDisconnect(var1);
        }
        else
        {
            EntityPlayerMP var2 = this.mcServer.getConfigurationManager().createPlayerForUser(this.clientUsername);

            if (var2 != null)
            {
                this.mcServer.getConfigurationManager().initializeConnectionToPlayer(this.myTCPConnection, var2);
            }
        }

        this.connectionComplete = true;
    }

    public void handleErrorMessage(String par1Str, Object[] par2ArrayOfObj)
    {
        logger.info(this.getUsernameAndAddress() + " lost connection");
        this.connectionComplete = true;
    }

    public void handleServerPing(Packet254ServerPing par1Packet254ServerPing)
    {
        try
        {
            ServerConfigurationManager var2 = this.mcServer.getConfigurationManager();
            String var3 = null;

            if (par1Packet254ServerPing.field_82559_a == 1)
            {
                List var4 = Arrays.asList(new Serializable[] {Integer.valueOf(1), Integer.valueOf(49), this.mcServer.getMinecraftVersion(), this.mcServer.getMOTD(), Integer.valueOf(var2.getCurrentPlayerCount()), Integer.valueOf(var2.getMaxPlayers())});
                Object var6;

                for (Iterator var5 = var4.iterator(); var5.hasNext(); var3 = var3 + var6.toString().replaceAll("\u0000", ""))
                {
                    var6 = var5.next();

                    if (var3 == null)
                    {
                        var3 = "\u00a7";
                    }
                    else
                    {
                        var3 = var3 + "\u0000";
                    }
                }
            }
            else
            {
                var3 = this.mcServer.getMOTD() + "\u00a7" + var2.getCurrentPlayerCount() + "\u00a7" + var2.getMaxPlayers();
            }

            InetAddress var8 = null;

            if (this.myTCPConnection.getSocket() != null)
            {
                var8 = this.myTCPConnection.getSocket().getInetAddress();
            }

            this.myTCPConnection.addToSendQueue(new Packet255KickDisconnect(var3));
            this.myTCPConnection.serverShutdown();

            if (var8 != null && this.mcServer.getNetworkThread() instanceof DedicatedServerListenThread)
            {
                ((DedicatedServerListenThread)this.mcServer.getNetworkThread()).func_71761_a(var8);
            }

            this.connectionComplete = true;
        }
        catch (Exception var7)
        {
            var7.printStackTrace();
        }
    }

    public void unexpectedPacket(Packet par1Packet)
    {
        this.raiseErrorAndDisconnect("Protocol error");
    }

    public String getUsernameAndAddress()
    {
        return this.clientUsername != null ? this.clientUsername + " [" + this.myTCPConnection.getSocketAddress().toString() + "]" : this.myTCPConnection.getSocketAddress().toString();
    }

    public boolean isServerHandler()
    {
        return true;
    }

    static String getServerId(NetLoginHandler par0NetLoginHandler)
    {
        return par0NetLoginHandler.loginServerId;
    }

    static MinecraftServer getLoginMinecraftServer(NetLoginHandler par0NetLoginHandler)
    {
        return par0NetLoginHandler.mcServer;
    }

    static SecretKey func_72525_c(NetLoginHandler par0NetLoginHandler)
    {
        return par0NetLoginHandler.field_72542_k;
    }

    static String getClientUsername(NetLoginHandler par0NetLoginHandler)
    {
        return par0NetLoginHandler.clientUsername;
    }

    public static boolean func_72531_a(NetLoginHandler par0NetLoginHandler, boolean par1)
    {
        return par0NetLoginHandler.field_72544_i = par1;
    }

    public void handleCustomPayload(Packet250CustomPayload par1Packet250CustomPayload)
    {
        FMLNetworkHandler.handlePacket250Packet(par1Packet250CustomPayload, myTCPConnection, this);
    }

    @Override
    public void handleVanilla250Packet(Packet250CustomPayload payload)
    {
    }

    public EntityPlayer getPlayer()
    {
        return null;
    };
}
