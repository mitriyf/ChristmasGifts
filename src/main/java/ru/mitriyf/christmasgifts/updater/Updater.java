package ru.mitriyf.christmasgifts.updater;

import org.bukkit.configuration.file.YamlConfiguration;
import ru.mitriyf.christmasgifts.ChristmasGifts;
import ru.mitriyf.christmasgifts.updater.config.ConfigUpdater;
import ru.mitriyf.christmasgifts.values.Values;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class Updater {
    private final ConfigUpdater configUpdater;
    private final ChristmasGifts plugin;
    private final Values values;
    private final Logger logger;
    private final File dataFolder;

    public Updater(ChristmasGifts plugin, Values values) {
        this.plugin = plugin;
        this.values = values;
        logger = plugin.getLogger();
        dataFolder = plugin.getDataFolder();
        configUpdater = new ConfigUpdater(plugin);
    }

    public void checkUpdates(boolean onlineUpdates) {
        if (values.isUpdaterEnabled() && onlineUpdates) {
            checkVersions();
        }
        checkLocales();
    }

    private void checkVersions() {
        try {
            URLConnection connection = new URL("https://github.com/mitriyf/ChristmasGifts/raw/refs/heads/main/updater/versions.yml").openConnection();
            connection.setConnectTimeout(5000);
            InputStream in = connection.getInputStream();
            File cfg = new File(dataFolder, "temp/versions.yml");
            File folder = cfg.getParentFile();
            if (folder.mkdirs()) {
                copyInputStreamToFile(in, cfg);
            }
            YamlConfiguration versions = YamlConfiguration.loadConfiguration(cfg);
            values.deleteDirectory(folder);
            List<String> versionsList = versions.getStringList("versions");
            String pluginVersion = plugin.getDescription().getVersion();
            String updaterMessage = null;
            boolean versionFound = false;
            for (String ver : versionsList) {
                String[] info = ver.split(";");
                if (pluginVersion.equals(info[0])) {
                    if (updaterMessage == null) {
                        logger.info("This is the correct version of the plugin, as reviewed by Updater.");
                        return;
                    }
                    versionFound = true;
                    break;
                } else if ((values.isRequired() && info[1].equals("REQUIRED")) || values.isRelease()) {
                    if (updaterMessage == null) {
                        updaterMessage = "Download the new plugin update: " + info[2];
                    }
                }
            }
            if (!versionFound) {
                logger.warning("No version was found. This is a developer version.");
            } else {
                logger.warning(updaterMessage);
            }
        } catch (Exception e) {
            logger.warning("An error occurred while checking for updates. Please check your network connection.");
            logger.warning("Error: " + e);
        }
    }

    private void checkLocales() {
        File folderLocales = new File(dataFolder, "locales");
        File cfg = new File(dataFolder, "config.yml");
        File[] lFiles = folderLocales.listFiles();
        String ver = plugin.getConfigVersion();
        Set<File> locales = new HashSet<>();
        locales.add(cfg);
        if (lFiles != null) {
            locales.addAll(Arrays.asList(lFiles));
        }
        for (File locale : locales) {
            String parentPath, updatePath;
            String localeName = locale.getName();
            parentPath = "locales/";
            String ignoreSection = "";
            if (locale.getParentFile().getName().equals(plugin.getName())) {
                parentPath = "";
                localeName = localeName.substring(0, localeName.indexOf(".")) + ".yml";
            }
            updatePath = parentPath + localeName;
            if (plugin.getResource(updatePath) == null) {
                updatePath = parentPath + "en_US.yml";
            }
            updateConfig(locale, ver, parentPath, updatePath, ignoreSection);
        }
    }

    private void updateConfig(File cfg, String ver, String parentPath, String updatePath, String ignoreSection) {
        if (cfg.getName().contains(".yml")) {
            try {
                YamlConfiguration yml = YamlConfiguration.loadConfiguration(cfg);
                String oldVersion = yml.getString("version") == null ? "1.0" : yml.getString("version");
                if (!oldVersion.equals(ver)) {
                    values.backupConfig(parentPath, cfg, oldVersion);
                    yml.set("version", ver);
                    if (yml.getConfigurationSection(ignoreSection) == null) {
                        yml.createSection(ignoreSection);
                    }
                    yml.save(cfg);
                    configUpdater.update(updatePath, cfg, ignoreSection);
                    plugin.getLogger().info("The " + cfg.getName() + " has been successfully updated to version " + ver);
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Update " + cfg.getName() + " is failed! Error: " + e);
            }
        }
    }

    private void copyInputStreamToFile(InputStream inputStream, File file) throws Exception {
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }
}
