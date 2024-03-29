package net.minecraft.command;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.MinecraftServer;

public class CommandServerOp extends CommandBase
{
    public String getCommandName()
    {
        return "op";
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel()
    {
        return 3;
    }

    public String getCommandUsage(ICommandSender par1ICommandSender)
    {
        return par1ICommandSender.translateString("commands.op.usage", new Object[0]);
    }

    public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        if (par2ArrayOfStr.length == 1 && par2ArrayOfStr[0].length() > 0)
        {
            MinecraftServer.getServer().getConfigurationManager().addOp(par2ArrayOfStr[0]);
            notifyAdmins(par1ICommandSender, "commands.op.success", new Object[] {par2ArrayOfStr[0]});
        }
        else
        {
            throw new WrongUsageException("commands.op.usage", new Object[0]);
        }
    }

    /**
     * Adds the strings available in this command to the given list of tab completion options.
     */
    public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        if (par2ArrayOfStr.length == 1)
        {
            String s = par2ArrayOfStr[par2ArrayOfStr.length - 1];
            ArrayList arraylist = new ArrayList();
            String[] astring1 = MinecraftServer.getServer().getAllUsernames();
            int i = astring1.length;

            for (int j = 0; j < i; ++j)
            {
                String s1 = astring1[j];

                if (!MinecraftServer.getServer().getConfigurationManager().areCommandsAllowed(s1) && doesStringStartWith(s, s1))
                {
                    arraylist.add(s1);
                }
            }

            return arraylist;
        }
        else
        {
            return null;
        }
    }
}
