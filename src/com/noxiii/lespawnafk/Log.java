package com.noxiii.lespawnafk;


import java.util.List;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class Log {
    static ConsoleCommandSender console = null;

    static String pluginName = "";

    static String pluginVersion = "";

    static Logger logger = Logger.getLogger("Minecraft");

    public Log(JavaPlugin plugin) {
        if (plugin != null) {
            pluginName = plugin.getDescription().getName();
            pluginVersion = plugin.getDescription().getVersion();
        }
        if (Bukkit.getServer() == null) {
            console = null;
        } else {
            console = Bukkit.getServer().getConsoleSender();
        }
    }

    public static void logWarning(String msg) {
        logger.warning("[" + pluginName + ":" + pluginVersion + "] " + msg);
    }

    public static void logInfo(List<String> msgs) {
        if (msgs == null || msgs.isEmpty())
            return;
        for (String msg : msgs)
            logInfo(msg);
    }

    public static void logInfo(String msg) {
        if (LeSpawnAFKConfig.verbosity.exceeds(Verbosity.NORMAL))
            logger.info("[" + pluginName + ":" + pluginVersion + "] " + msg);
    }

    public static void logInfoNoVerbosity(String msg) {
        logger.info("[" + pluginName + ":" + pluginVersion + "] " + msg);
    }

    public static void dMsg(String msg) {
        if (LeSpawnAFKConfig.verbosity.exceeds(Verbosity.HIGHEST))
            if (console != null && LeSpawnAFKConfig.gColorLogMessages) {
                console.sendMessage(ChatColor.RED + "[" + pluginName + ":" + pluginVersion + "] " + ChatColor.RESET + msg);
            } else {
                logger.info("[" + pluginName + ":" + pluginVersion + "] " + msg);
            }
    }

    public static void logInfo(String msg, Verbosity level) {
        if (LeSpawnAFKConfig.verbosity.exceeds(level))
            if (console != null && LeSpawnAFKConfig.gColorLogMessages) {
                ChatColor col = ChatColor.GREEN;
                switch (level) {
                    case EXTREME:
                        col = ChatColor.GOLD;
                        break;
                    case HIGHEST:
                        col = ChatColor.YELLOW;
                        break;
                    case HIGH:
                        col = ChatColor.AQUA;
                        break;
                    case NORMAL:
                        col = ChatColor.RESET;
                        break;
                    case LOW:
                        col = ChatColor.RESET;
                        break;
                }
                console.sendMessage(col + "[" + pluginName + ":" + pluginVersion + "] " + ChatColor.RESET + msg);
            } else {
                logger.info("[" + pluginName + ":" + pluginVersion + "] " + msg);
            }
    }

    public static void logWarning(String msg, Verbosity level) {
        if (LeSpawnAFKConfig.verbosity.exceeds(level))
            logWarning(msg);
    }

    public static void stackTrace() {
        if (LeSpawnAFKConfig.verbosity.exceeds(Verbosity.EXTREME))
            Thread.dumpStack();
    }
}

