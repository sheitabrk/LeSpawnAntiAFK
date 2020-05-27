package com.noxiii.lespawnafk;

import org.bukkit.permissions.Permissible;

public class Dependencies {
    public static boolean hasPermission(Permissible who, String permission) {
        if (who instanceof org.bukkit.command.ConsoleCommandSender)
            return true;
        boolean perm = who.hasPermission(permission);
        if (!perm) {
            Log.logInfo("LeSpawnPermissions - permission (" + permission + ") refusé pour " + who.toString(), Verbosity.HIGHEST);
        } else {
            Log.logInfo("LeSpawnPermissions - permission (" + permission + ") permise pour " + who.toString(), Verbosity.HIGHEST);
        }
        return perm;
    }
}
