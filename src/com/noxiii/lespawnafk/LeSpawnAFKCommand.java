package com.noxiii.lespawnafk;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;

public class LeSpawnAFKCommand implements CommandExecutor {
    private final LeSpawnAFK antiafk;
    private LeSpawnAFKConfig config;

    private enum OACommand {
        RELOAD("reload", "r", "antiafk.admin");

        private String perm;

        private String cmdShort;

        private String cmdName;

        OACommand(String name, String abbr, String perm) {
            this.cmdName = name;
            this.cmdShort = abbr;
            this.perm = perm;
        }

        public static OACommand match(String label, String firstArg) {
            boolean arg = false;
            if (label.equalsIgnoreCase("spawnafk"))
                arg = true;
            byte b;
            int i;
            OACommand[] arrayOfOACommand;
            for (i = (arrayOfOACommand = values()).length, b = 0; b < i; ) {
                OACommand cmd = arrayOfOACommand[b];
                if (arg) {
                    byte b1;
                    int j;
                    String[] arrayOfString;
                    for (j = (arrayOfString = cmd.cmdName.split(",")).length, b1 = 0; b1 < j; ) {
                        String item = arrayOfString[b1];
                        if (firstArg.equalsIgnoreCase(item))
                            return cmd;
                        b1++;
                    }
                } else {
                    if (label.equalsIgnoreCase("spawnafk" + cmd.cmdShort))
                        return cmd;
                    byte b1;
                    int j;
                    String[] arrayOfString;
                    for (j = (arrayOfString = cmd.cmdShort.split(",")).length, b1 = 0; b1 < j; ) {
                        String shortcut = arrayOfString[b1];
                        if (label.equalsIgnoreCase("spawnafk" + shortcut))
                            return cmd;
                        b1++;
                    }
                }
                b++;
            }
            return null;
        }

        public String[] trim(String[] args, StringBuffer name) {
            if (args.length == 0)
                return args;
            if (!args[0].equalsIgnoreCase(this.cmdName))
                return args;
            String[] newArgs = new String[args.length - 1];
            System.arraycopy(args, 1, newArgs, 0, newArgs.length);
            if (name != null)
                name.append(" " + args[0]);
            return newArgs;
        }
    }

    public LeSpawnAFKCommand(LeSpawnAFK plugin) {
        this.antiafk = plugin;
    }

    private String getName(CommandSender sender) {
        if (sender instanceof org.bukkit.command.ConsoleCommandSender)
            return "CONSOLE";
        if (sender instanceof Player)
            return ((Player)sender).getName();
        return "UNKNOWN";
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        OACommand cmd = OACommand.match(label, (args.length >= 1) ? args[0] : "");
        if (cmd == null)
            return false;
        StringBuffer cmdName = new StringBuffer(label);
        args = cmd.trim(args, cmdName);
        if (!checkCommandPermissions(sender, args, cmd))
            return true;
        switch (cmd) {
        }
        return true;
    }

    private boolean checkCommandPermissions(CommandSender sender, String[] args, OACommand cmd) {
        boolean pass = false;
        if (cmd.perm.isEmpty()) {
            pass = true;
        } else if (Dependencies.hasPermission((Permissible)sender, cmd.perm)) {
            pass = true;
        }
        if (!pass)
            sender.sendMessage("Tu n'a pas la permission pour cette commande !");
        return pass;
    }

    private void cmdReload(CommandSender sender) {
        this.config.load(sender);
        sender.sendMessage("LeSpawnAFK Config reloaded");
        Log.logInfo("Config reloaded by " + getName(sender) + ".");
    }
}