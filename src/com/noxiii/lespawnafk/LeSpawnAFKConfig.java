package com.noxiii.lespawnafk;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class LeSpawnAFKConfig {
    private final LeSpawnAFK parent;

    protected static Verbosity verbosity = Verbosity.NORMAL;

    public static boolean gColorLogMessages;

    public static int timeout;

    public static List<String> permissions;

    public static List<String> commands;

    public static List<String> worlds;

    public LeSpawnAFKConfig(LeSpawnAFK instance) {
        this.parent = instance;
    }

    public void load(CommandSender sender) {
        List<String> result = new ArrayList<>();
        try {
            firstRun();
            loadConfig();
        } catch (FileNotFoundException e) {
            if (verbosity.exceeds(Verbosity.HIGH))
                e.printStackTrace();
            result.add("Le fichier de configuration n'a pas étais trouvé");
            result.add("L'erreur est:\n" + e.toString());
            result.add("Vous pouvez régler l'erreur en faisant /spawnafk reload");
            sendMessage(sender, result);
        } catch (IOException e) {
            if (verbosity.exceeds(Verbosity.HIGH))
                e.printStackTrace();
            result.add("Une erreur à forcé SpawnAFK à pas se lancer !");
            result.add("L'erreur est:\n" + e.toString());
            result.add("Vous pouvez régler l'erreur en faisant /spawnafk reload.");
            sendMessage(sender, result);
        } catch (InvalidConfigurationException e) {
            if (verbosity.exceeds(Verbosity.HIGH))
                e.printStackTrace();
            result.add("Le fichier de configuration est invalide");
            result.add("L'erreur est:\n" + e.toString());
            result.add("Vous pouvez régler l'erreur en faisant /spawnafk reload.");
            sendMessage(sender, result);
        } catch (NullPointerException e) {
            result.add("Le fichier de configuration n'a pas réussi à se démmarer");
            result.add("L'erreur est:\n" + e.toString());
            if (verbosity.exceeds(Verbosity.NORMAL))
                e.printStackTrace();
            result.add("Apeller Noxiii le développeur de ce plugin !");
            sendMessage(sender, result);
        } catch (Exception e) {
            if (verbosity.exceeds(Verbosity.HIGH))
                e.printStackTrace();
            result.add("Le fichier de configuration n'a pas réussi à se démmarer");
            result.add("L'erreur est:\n" + e.toString());
            result.add("Vous pouvez régler l'erreur en faisant /spawnafk reload");
            sendMessage(sender, result);
        }
    }

    private void sendMessage(CommandSender sender, List<String> result) {
        if (sender != null)
            sender.sendMessage(result.<String>toArray(new String[0]));
        Log.logInfo(result);
    }

    private void firstRun() throws Exception {
        List<String> files = new ArrayList<>();
        files.add("config.yml");
        for (String filename : files) {
            File file = new File(this.parent.getDataFolder(), filename);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                copy(this.parent.getResource(filename), file);
            }
        }
    }

    public void loadConfig() throws FileNotFoundException, IOException, InvalidConfigurationException {
        String filename = "config.yml";
        File global = new File(this.parent.getDataFolder(), filename);
        YamlConfiguration globalConfig = YamlConfiguration.loadConfiguration(global);
        if (!global.exists())
            try {
                global.createNewFile();
                Log.logInfo("Un fichier de configuration à étais créer" + this.parent.getDataFolder() + "\\" + filename + ", editez le à vos gouts");
                globalConfig.save(global);
            } catch (IOException ex) {
                Log.logWarning(String.valueOf(this.parent.getDescription().getName()) + ": pas réussi à générer " + filename + ". Le fichier à les permissions ?");
            } catch (Exception e) {
                Log.logWarning(String.valueOf(this.parent.getDescription().getName()) + ": pas réussi à générer " + filename + ". Le fichier à les permissions ?");
            }
        globalConfig.load(global);
        verbosity = CommonPlugin.getConfigVerbosity(globalConfig);
        timeout = globalConfig.getInt("default_timeout", 5);
        gColorLogMessages = globalConfig.getBoolean("color_log_messages", true);
        permissions = globalConfig.getStringList("permissions");
        commands = globalConfig.getStringList("kick_commands");
        worlds = globalConfig.getStringList("check_worlds");
    }

    public static Verbosity getVerbosity() {
        return verbosity;
    }

    public static List<String> getCommands() {
        return commands;
    }

    public static void setVerbosity(Verbosity verbosity) {
        LeSpawnAFKConfig.verbosity = verbosity;
    }

    private void copy(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0)
                out.write(buf, 0, len);
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
