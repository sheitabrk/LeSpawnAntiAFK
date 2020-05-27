package com.noxiii.lespawnafk;


import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

public class CommonPlugin {
    public static String prefix = "&7[&aLeSpawnAFK&7] ";

    public static Verbosity getConfigVerbosity(YamlConfiguration config) {
        String verb_string = config.getString("verbosité", "normal");
        if (verb_string.equalsIgnoreCase("doucement"))
            return Verbosity.LOW;
        if (verb_string.equalsIgnoreCase("fort"))
            return Verbosity.HIGH;
        if (verb_string.equalsIgnoreCase("très fort"))
            return Verbosity.HIGHEST;
        if (verb_string.equalsIgnoreCase("extreme"))
            return Verbosity.EXTREME;
        return Verbosity.NORMAL;
    }

    public static void sendMessage(boolean usePrefix, CommandSender s, String msg) {
        if (usePrefix) {
            s.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(prefix) + msg));
        } else {
            s.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
        }
    }

    public static <E extends Enum<E>> E enumValue(Class<E> clazz, String name) {
        try {
            return Enum.valueOf(clazz, name);
        } catch (IllegalArgumentException illegalArgumentException) {
            return null;
        }
    }
}
