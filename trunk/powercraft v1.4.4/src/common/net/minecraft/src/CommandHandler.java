package net.minecraft.src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CommandEvent;

public class CommandHandler implements ICommandManager
{
    private final Map commandMap = new HashMap();

    private final Set commandSet = new HashSet();

    public void executeCommand(ICommandSender par1ICommandSender, String par2Str)
    {
        if (par2Str.startsWith("/"))
        {
            par2Str = par2Str.substring(1);
        }

        String[] var3 = par2Str.split(" ");
        String var4 = var3[0];
        var3 = dropFirstString(var3);
        ICommand var5 = (ICommand)this.commandMap.get(var4);
        int var6 = this.getUsernameIndex(var5, var3);

        try
        {
            if (var5 == null)
            {
                throw new CommandNotFoundException();
            }

            if (var5.canCommandSenderUseCommand(par1ICommandSender))
            {
                CommandEvent event = new CommandEvent(var5, par1ICommandSender, var3);

                if (MinecraftForge.EVENT_BUS.post(event))
                {
                    if (event.exception != null)
                    {
                        throw event.exception;
                    }

                    return;
                }

                if (var6 > -1)
                {
                    EntityPlayerMP[] var7 = PlayerSelector.func_82380_c(par1ICommandSender, var3[var6]);
                    String var8 = var3[var6];
                    EntityPlayerMP[] var9 = var7;
                    int var10 = var7.length;

                    for (int var11 = 0; var11 < var10; ++var11)
                    {
                        EntityPlayerMP var12 = var9[var11];
                        var3[var6] = var12.getEntityName();

                        try
                        {
                            var5.processCommand(par1ICommandSender, var3);
                        }
                        catch (PlayerNotFoundException var14)
                        {
                            par1ICommandSender.sendChatToPlayer("\u00a7c" + par1ICommandSender.translateString(var14.getMessage(), var14.getErrorOjbects()));
                        }
                    }

                    var3[var6] = var8;
                }
                else
                {
                    var5.processCommand(par1ICommandSender, var3);
                }
            }
            else
            {
                par1ICommandSender.sendChatToPlayer("\u00a7cYou do not have permission to use this command.");
            }
        }
        catch (WrongUsageException var15)
        {
            par1ICommandSender.sendChatToPlayer("\u00a7c" + par1ICommandSender.translateString("commands.generic.usage", new Object[] {par1ICommandSender.translateString(var15.getMessage(), var15.getErrorOjbects())}));
        }
        catch (CommandException var16)
        {
            par1ICommandSender.sendChatToPlayer("\u00a7c" + par1ICommandSender.translateString(var16.getMessage(), var16.getErrorOjbects()));
        }
        catch (Throwable var17)
        {
            par1ICommandSender.sendChatToPlayer("\u00a7c" + par1ICommandSender.translateString("commands.generic.exception", new Object[0]));
            var17.printStackTrace();
        }
    }

    public ICommand registerCommand(ICommand par1ICommand)
    {
        List var2 = par1ICommand.getCommandAliases();
        this.commandMap.put(par1ICommand.getCommandName(), par1ICommand);
        this.commandSet.add(par1ICommand);

        if (var2 != null)
        {
            Iterator var3 = var2.iterator();

            while (var3.hasNext())
            {
                String var4 = (String)var3.next();
                ICommand var5 = (ICommand)this.commandMap.get(var4);

                if (var5 == null || !var5.getCommandName().equals(var4))
                {
                    this.commandMap.put(var4, par1ICommand);
                }
            }
        }

        return par1ICommand;
    }

    private static String[] dropFirstString(String[] par0ArrayOfStr)
    {
        String[] var1 = new String[par0ArrayOfStr.length - 1];

        for (int var2 = 1; var2 < par0ArrayOfStr.length; ++var2)
        {
            var1[var2 - 1] = par0ArrayOfStr[var2];
        }

        return var1;
    }

    public List getPossibleCommands(ICommandSender par1ICommandSender, String par2Str)
    {
        String[] var3 = par2Str.split(" ", -1);
        String var4 = var3[0];

        if (var3.length == 1)
        {
            ArrayList var8 = new ArrayList();
            Iterator var6 = this.commandMap.entrySet().iterator();

            while (var6.hasNext())
            {
                Entry var7 = (Entry)var6.next();

                if (CommandBase.doesStringStartWith(var4, (String)var7.getKey()) && ((ICommand)var7.getValue()).canCommandSenderUseCommand(par1ICommandSender))
                {
                    var8.add(var7.getKey());
                }
            }

            return var8;
        }
        else
        {
            if (var3.length > 1)
            {
                ICommand var5 = (ICommand)this.commandMap.get(var4);

                if (var5 != null)
                {
                    return var5.addTabCompletionOptions(par1ICommandSender, dropFirstString(var3));
                }
            }

            return null;
        }
    }

    public List getPossibleCommands(ICommandSender par1ICommandSender)
    {
        ArrayList var2 = new ArrayList();
        Iterator var3 = this.commandSet.iterator();

        while (var3.hasNext())
        {
            ICommand var4 = (ICommand)var3.next();

            if (var4.canCommandSenderUseCommand(par1ICommandSender))
            {
                var2.add(var4);
            }
        }

        return var2;
    }

    public Map getCommands()
    {
        return this.commandMap;
    }

    private int getUsernameIndex(ICommand par1ICommand, String[] par2ArrayOfStr)
    {
        if (par1ICommand == null)
        {
            return -1;
        }
        else
        {
            for (int var3 = 0; var3 < par2ArrayOfStr.length; ++var3)
            {
                if (par1ICommand.isUsernameIndex(var3) && PlayerSelector.func_82377_a(par2ArrayOfStr[var3]))
                {
                    return var3;
                }
            }

            return -1;
        }
    }
}
