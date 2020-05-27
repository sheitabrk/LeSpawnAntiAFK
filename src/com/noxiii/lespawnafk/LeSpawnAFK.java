package com.noxiii.lespawnafk;

import java.util.Collection;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class LeSpawnAFK extends JavaPlugin implements Listener {
    public static LeSpawnAFK plugin;

    public LeSpawnAFKConfig config;

    public Log log = null;

    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public static HashMap<Player, Float> playersLocation = new HashMap<>();

    public static HashMap<Player, Integer> afkPlayersTime = new HashMap<>();

    public static HashMap<Player, String> playerPhrase = new HashMap<>();

    boolean enabled;

    public LeSpawnAFK() {
        plugin = this;
    }

    public void onEnable() {
        plugin = this;
        plugin.enabled = true;
        registerListeners();
        initConfig();
        initLogger();
        registerCommands();
    }

    private void registerCommands() {
        getCommand("spawnafk").setExecutor(new LeSpawnAFKCommand(this));
    }

    private void registerListeners() {
        PluginManager pm = Bukkit.getServer().getPluginManager();
        pm.registerEvents(this, this);
        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
            public void run() {
                LeSpawnAFK.checkAFK(LeSpawnAFK.this.getServer().getOnlinePlayers());
            }
        },  0L, 1200L);
    }

    private void initConfig() {
        getDataFolder().mkdirs();
        this.config = new LeSpawnAFKConfig(this);
        this.config.load(null);
    }

    private void initLogger() {
        this.log = new Log(this);
    }

    public static void checkAFK(Collection<? extends Player> online) {
        if (online.size() > 0)
            for (Player p : online) {
                boolean foundPerm = false;
                if (p.hasPermission("antiafk.unlimited"))
                    return;
                if (afkPlayersTime.containsKey(p)) {
                    Log.logInfo("Vérification du joueur " + p.getName() + ".", Verbosity.HIGHEST);
                    if (LeSpawnAFKConfig.worlds.contains(p.getWorld().getName())) {
                        int timeout = 0;
                        for (String perm : LeSpawnAFKConfig.permissions) {
                            if (p.hasPermission(perm)) {
                                foundPerm = true;
                                timeout = Integer.valueOf(perm.substring(15)).intValue();
                                isAFK(p);
                                if (afkPlayersTime.containsKey(p)) {
                                    int timeAFK = afkPlayersTime.get(p).intValue();
                                    if (timeAFK >= timeout)
                                        runCaptcha(p, timeout);
                                    break;
                                }
                                if (!afkPlayersTime.containsKey(p))
                                    afkPlayersTime.put(p, Integer.valueOf(1));
                                break;
                            }
                        }
                        if (!foundPerm) {
                            timeout = LeSpawnAFKConfig.timeout;
                            isAFK(p);
                            if (afkPlayersTime.containsKey(p)) {
                                int timeAFK = afkPlayersTime.get(p).intValue();
                                if (timeAFK >= timeout)
                                    runCaptcha(p, timeout);
                                continue;
                            }
                            if (!afkPlayersTime.containsKey(p))
                                afkPlayersTime.put(p, Integer.valueOf(1));
                        }
                    }
                    continue;
                }
                if (!afkPlayersTime.containsKey(p)) {
                    Log.logInfo("Ajout du joueur " + p.getName() + "pour la première fois avec 0 minute", Verbosity.HIGHEST);
                    afkPlayersTime.put(p, Integer.valueOf(1));
                    playersLocation.put(p, Float.valueOf(p.getLocation().getYaw()));
                }
            }
    }

    public static void isAFK(Player p) {
        if (playersLocation.containsKey(p)) {
            Float currentYaw = Float.valueOf(p.getLocation().getYaw());
            Float lastYaw = playersLocation.get(p);
            if (currentYaw.equals(lastYaw)) {
                int currentTime = afkPlayersTime.get(p).intValue();
                afkPlayersTime.remove(p, Integer.valueOf(currentTime));
                afkPlayersTime.put(p, Integer.valueOf(currentTime + 1));
                Log.logInfo("Inmplementation du joueur " + p.getName() + " a aucun changement de position (" + afkPlayersTime.get(p) + ").", Verbosity.HIGHEST);
                return;
            }
            Log.logInfo("Le joueur " + p.getName() + " s'est deplacé et n'est plus AFK", Verbosity.HIGHEST);
            playersLocation.remove(p);
            afkPlayersTime.remove(p);
        }
        playersLocation.put(p, Float.valueOf(p.getLocation().getYaw()));
    }

    public static void runCaptcha(final Player p, final int timeout) {
        final BukkitScheduler scheduler = plugin.getServer().getScheduler();
        if (!playerPhrase.containsKey(p)) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < 5; i++) {
                int character = (int)(Math.random() * "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".length());
                builder.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".charAt(character));
            }
            playerPhrase.put(p, builder.toString());
            final int task1 = scheduler.scheduleSyncRepeatingTask((Plugin)plugin, new Runnable() {
                public void run() {
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 1.0F);
                }
            },0L, 2L);
            scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
                public void run() {
                    scheduler.cancelTask(task1);
                }
            },  50L);
            Log.logInfo("Le joueur " + p.getName() + " à reçu le message " + builder.toString(), Verbosity.HIGHEST);
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lLeSpawnAFK avertissement - veuillez sais &a" + builder.toString() + " &c&lpour annuler le délai !"));
        }
        scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
            public void run() {
                if (LeSpawnAFK.playerPhrase.containsKey(p)) {
                    Log.logInfo("Le joueur " + p.getName() + " est AFK", Verbosity.NORMAL);
                    for (String command : LeSpawnAFKConfig.commands) {
                        command = command.replaceAll("%p", p.getName());
                        command = command.replaceAll("%t", String.valueOf(timeout));
                        command = ChatColor.translateAlternateColorCodes('&', command);
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
                    }
                }
            }
        },200L);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event) {
        if (playerPhrase.containsKey(event.getPlayer()) && playerPhrase.get(event.getPlayer()).equalsIgnoreCase(event.getMessage())) {
            event.setCancelled(true);
            Log.logInfo("Le joueur " + event.getPlayer().getName() + " a bien envoyé le méssage", Verbosity.HIGHEST);
            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lVous n'etes plus AFK !"));
            playerPhrase.remove(event.getPlayer());
            playersLocation.remove(event.getPlayer());
            afkPlayersTime.remove(event.getPlayer());
            return;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLeave(PlayerQuitEvent event) {
        Log.logInfo("Supression de joueur de la liste: " + event.getPlayer().getName(), Verbosity.HIGHEST);
        playersLocation.remove(event.getPlayer());
        playerPhrase.remove(event.getPlayer());
        afkPlayersTime.remove(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onKick(PlayerKickEvent event) {
        Log.logInfo("Supression de joueur de la liste: " + event.getPlayer().getName(), Verbosity.HIGHEST);
        playersLocation.remove(event.getPlayer());
        playerPhrase.remove(event.getPlayer());
        afkPlayersTime.remove(event.getPlayer());
    }
}