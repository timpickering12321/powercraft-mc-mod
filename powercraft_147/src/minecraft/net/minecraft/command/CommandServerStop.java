package net.minecraft.command;

import net.minecraft.server.MinecraftServer;

public class CommandServerStop extends CommandBase
{
    public String getCommandName()
    {
        return "stop";
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel()
    {
        return 4;
    }

    public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        notifyAdmins(par1ICommandSender, "commands.stop.start", new Object[0]);
        MinecraftServer.getServer().initiateShutdown();
    }
}
