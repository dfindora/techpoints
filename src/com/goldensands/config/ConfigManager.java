package com.goldensands.config;

import com.goldensands.main.Techpoints;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {
    private Techpoints plugin;

    private FileConfiguration techPointsCfg;
    private File techPointsFile;
    private ArrayList<BasicTechPointItem> basicTechPointItems;
    private ArrayList<UniqueTechPointItem> uniqueTechPointItems;
    private ArrayList<MultiBlock> multiBlocks;

    public ConfigManager(Techpoints plugin) {
        this.plugin = plugin;
    }

    public void setup() {
        if (!plugin.getDataFolder().exists()) {
            boolean isCreated = plugin.getDataFolder().mkdir();
            if (isCreated) {
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN
                        + "TechPoints directory has been created.");
            }
        }
        techPointsFile = new File(plugin.getDataFolder(), "techpoints.yml");
        boolean isFileCreated = false;
        if (!techPointsFile.exists()) {
            try {
                isFileCreated = techPointsFile.createNewFile();
                if (isFileCreated) {
                    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN
                            + "techpoints.yml has been created.");
                }
            } catch (IOException e) {
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "unable to create techpoints.yml");
            }
        }

        if (isFileCreated) {
            String[] btpi = new String[]{
                    "2:0:11:grass",
                    "5:*:11:Wood Planks"
            };
            String[] utpi = new String[]{
                    "7:0:3:Bedrock:This is a template. Please change this."
            };
            String[] mb = new String[]{
                    "17:0:11:Oak Tree:6"
            };
            copyDefaults(btpi, utpi, mb);
        }
        loadTechPoints();


    }

    private void copyDefaults(String[] btpi, String[] utpi, String[] mb)
    {
        try
        {
            FileWriter fw = new FileWriter(techPointsFile);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("BasicTechPointItems:\n");
            for(String str : btpi)
            {
                bw.write("- " + str + "\n");
            }
            bw.write("UniqueTechPointItems:\n");
            for(String str : utpi)
            {
                bw.write("- " + str + "\n");
            }
            bw.write("MultiBlocks:\n");
            for(String str : mb)
            {
                bw.write("- " + str + "\n");
            }
            bw.close();
            fw.close();
        }
        catch (IOException e)
        {
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "could not save file.");
        }
    }

    public void reloadTechPoints() {
        loadTechPoints();
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "techpoints.yml reloaded.");
    }

    @SuppressWarnings("unchecked")
    private void loadTechPoints() {
        techPointsCfg = YamlConfiguration.loadConfiguration(techPointsFile);

        basicTechPointItems = new ArrayList<>();
        uniqueTechPointItems = new ArrayList<>();
        multiBlocks = new ArrayList<>();
        List<String> basicTechPointItemString = (List<String>) techPointsCfg.getList("BasicTechPointItems");
        List<String> uniqueTechPointItemString = (List<String>) techPointsCfg.getList("UniqueTechPointItems");
        List<String> multiBlockString = (List<String>) techPointsCfg.getList("MultiBlocks");
        boolean isValid = verifyConfiguration(basicTechPointItemString, uniqueTechPointItemString, multiBlockString);
        if (isValid) {
            for (String basicTechPointItem : basicTechPointItemString) {
                String[] split = basicTechPointItem.split(":");
                BasicTechPointItem btpi = (split[1].equals("*"))
                        ? new BasicTechPointItem(Integer.parseInt(split[0]),
                        -1, Integer.parseInt(split[2]), split[3])
                        : new BasicTechPointItem(Integer.parseInt(split[0]),
                        Integer.parseInt(split[1]), Integer.parseInt(split[2]), split[3]);
                basicTechPointItems.add(btpi);
            }
            for (String uniqueTechPointItem : uniqueTechPointItemString) {
                String[] split = uniqueTechPointItem.split(":");
                UniqueTechPointItem utpi = (split[1].equals("*"))
                        ? new UniqueTechPointItem(Integer.parseInt(split[0]),
                        -1, Integer.parseInt(split[2]), split[3], split[4])
                        : new UniqueTechPointItem(Integer.parseInt(split[0]),
                        Integer.parseInt(split[1]), Integer.parseInt(split[2]), split[3], split[4]);
                uniqueTechPointItems.add(utpi);
            }
            for (String multiBlock : multiBlockString) {
                String[] split = multiBlock.split(":");
                MultiBlock mb = (split[1].equals("*"))
                        ? new MultiBlock(Integer.parseInt(split[0]),
                        -1, Integer.parseInt(split[2]), Integer.parseInt(split[4]), split[3])
                        : new MultiBlock(Integer.parseInt(split[0]),
                        Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[4]), split[3]);
                multiBlocks.add(mb);
            }
        } else {
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Unable to load config.");
        }
    }

    private boolean verifyConfiguration(List<String> basicTechPointItems, List<String> uniqueTechPointItems, List<String> multiBlocks) {
        boolean isVerified = true;
        for (String configString : basicTechPointItems) {
            if (verifyConfigString(configString, "BasicTechPointItems")) {
                isVerified = false;
                plugin.getLogger().warning(configString + " is incorrectly formatted.");
            }
        }
        for (String configString : uniqueTechPointItems) {
            if (verifyConfigString(configString, "UniqueTechPointItems")) {
                isVerified = false;
                plugin.getLogger().warning(configString + " is incorrectly formatted.");
            }
        }
        for (String configString : multiBlocks) {
            if (verifyConfigString(configString, "MultiBlocks")) {
                isVerified = false;
                plugin.getLogger().warning(configString + " is incorrectly formatted.");
            }
        }
        return isVerified;
    }

    private boolean verifyConfigString(String configString, String type) {
        boolean isValid = false;
        switch (type) {
            case "BasicTechPointItems":
                isValid = configString.matches("\\d*:\\d*:\\d*:.*|\\d*:\\*:\\d*:.*");
                break;
            case "UniqueTechPointItems":
                isValid = configString.matches("\\d*:\\d*:\\d*:.*:.*|\\d*:\\*:\\d*:.*:.*");
                break;
            case "MultiBlocks":
                isValid = configString.matches("\\d*:\\d*:\\d*:.*:\\d*|\\d*:\\*:\\d*:.*:\\d*");
                break;
            default:
                break;
        }
        return !isValid;
    }

    public ArrayList<BasicTechPointItem> getBasicTechPointItems() {
        return basicTechPointItems;
    }

    public ArrayList<UniqueTechPointItem> getUniqueTechPointItems() {
        return uniqueTechPointItems;
    }

    public ArrayList<MultiBlock> getMultiBlocks() {
        return multiBlocks;
    }
}
