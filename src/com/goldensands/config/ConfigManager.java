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

public class ConfigManager
{
    private Techpoints plugin;

    private File techPointsFile;
    private ArrayList<BasicTechPointItem> basicTechPointItems;
    private ArrayList<UniqueTechPointItem> uniqueTechPointItems;
    private ArrayList<MultiBlock> multiBlocks;

    public ConfigManager(Techpoints plugin)
    {
        this.plugin = plugin;
    }

    /**
     * techpoints.yml initialization
     */
    public void setup()
    {
        //file check
        directoryCheck();
        techPointsFile = new File(plugin.getDataFolder(), "techpoints.yml");
        boolean isFileCreated = fileCheck(techPointsFile);

        //defaults creation
        //TODO: hard-coded defaults. move to a seperate default config file.
        if (isFileCreated)
        {
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

    /**
     * verifies that the plugin's data folder exists. If it does not, it is created.
     */
    private void directoryCheck()
    {
        if (!plugin.getDataFolder().exists())
        {
            boolean isCreated = plugin.getDataFolder().mkdir();
            if (isCreated)
            {
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN
                                                                  + "TechPoints directory has been created.");
            }
        }
    }

    /**
     * verifies if the specified config file is created. If it is not, it is created.
     * @param file - config file to check
     * @return if it is created, true. Otherwise, false.
     */
    private boolean fileCheck(File file)
    {
        boolean isFileCreated = false;
        if (!file.exists())
        {
            try
            {
                isFileCreated = file.createNewFile();
                if (isFileCreated)
                {
                    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN
                                                                      + "techpoints.yml has been created.");
                }
            }
            catch (IOException e)
            {
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "unable to create techpoints.yml");
            }
        }
        return isFileCreated;
    }

    /**
     * writes the defaults to the techpoints.yml file.
     * @param btpi - BasicTechPointItem defaults
     * @param utpi - UniqueTechPointItem defaults
     * @param mb - MultiBlock defaults
     */
    private void copyDefaults(String[] btpi, String[] utpi, String[] mb)
    {
        try
        {
            FileWriter fw = new FileWriter(techPointsFile);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("BasicTechPointItems:\n");
            for (String str : btpi)
            {
                bw.write("- " + str + "\n");
            }
            bw.write("UniqueTechPointItems:\n");
            for (String str : utpi)
            {
                bw.write("- " + str + "\n");
            }
            bw.write("MultiBlocks:\n");
            for (String str : mb)
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

    /**
     * reloads techpoints.yml.
     */
    public void reloadTechPoints()
    {
        loadTechPoints();
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "techpoints.yml reloaded.");
    }

    /**
     * pre-loads the techpoints.yml values into this ConfigManager.
     */
    @SuppressWarnings("unchecked")
    private void loadTechPoints()
    {
        FileConfiguration techPointsCfg = YamlConfiguration.loadConfiguration(techPointsFile);

        //array initialization
        basicTechPointItems = new ArrayList<>();
        uniqueTechPointItems = new ArrayList<>();
        multiBlocks = new ArrayList<>();
        //load lists from config
        List<String> basicTechPointItemString = (List<String>) techPointsCfg.getList("BasicTechPointItems");
        List<String> uniqueTechPointItemString = (List<String>) techPointsCfg.getList("UniqueTechPointItems");
        List<String> multiBlockString = (List<String>) techPointsCfg.getList("MultiBlocks");
        //verify config is properly formatted before loading. If there is any errors in the config, it can't be loaded.
        //TODO:instead of not loading the config at all, maybe it should just remove invalid config entries?
        boolean isValid = verifyConfiguration(basicTechPointItemString, uniqueTechPointItemString, multiBlockString);
        //populate arrays
        if (isValid)
        {
            //BasicTechPointItems
            for (String basicTechPointItem : basicTechPointItemString)
            {
                String[] split = basicTechPointItem.split(":");
                BasicTechPointItem btpi = (split[1].equals("*"))
                                          ? new BasicTechPointItem(Integer.parseInt(split[0]),
                                                                   -1, Integer.parseInt(split[2]), split[3])
                                          : new BasicTechPointItem(Integer.parseInt(split[0]),
                                                                   Integer.parseInt(split[1]), Integer.parseInt(split[2]), split[3]);
                basicTechPointItems.add(btpi);
            }
            //UniqueTechPointItems
            for (String uniqueTechPointItem : uniqueTechPointItemString)
            {
                String[] split = uniqueTechPointItem.split(":");
                UniqueTechPointItem utpi = (split[1].equals("*"))
                                           ? new UniqueTechPointItem(Integer.parseInt(split[0]),
                                                                     -1, Integer.parseInt(split[2]), split[3], split[4])
                                           : new UniqueTechPointItem(Integer.parseInt(split[0]),
                                                                     Integer.parseInt(split[1]), Integer.parseInt(split[2]), split[3], split[4]);
                uniqueTechPointItems.add(utpi);
            }
            //MultiBlocks
            for (String multiBlock : multiBlockString)
            {
                String[] split = multiBlock.split(":");
                MultiBlock mb = (split[1].equals("*"))
                                ? new MultiBlock(Integer.parseInt(split[0]),
                                                 -1, Integer.parseInt(split[2]), Integer.parseInt(split[4]), split[3])
                                : new MultiBlock(Integer.parseInt(split[0]),
                                                 Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[4]), split[3]);
                multiBlocks.add(mb);
            }
        }
        else
        {
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Unable to load config.");
        }
    }

    /**
     * verifies that the whole techpoints.yml configuration file is properly formatted.
     * @param basicTechPointItems - list of BasicTechPointItem config strings.
     * @param uniqueTechPointItems - list of UniqueTechPointItem config strings.
     * @param multiBlocks - list of MultiBlock config strings.
     * @return if the whole configuration is properly formatted, it returns true. Otherwise, it returns false.
     */
    private boolean verifyConfiguration(List<String> basicTechPointItems, List<String> uniqueTechPointItems, List<String> multiBlocks)
    {
        boolean isVerified = true;
        for (String configString : basicTechPointItems)
        {
            if (verifyConfigString(configString, "BasicTechPointItem"))
            {
                isVerified = false;
                plugin.getLogger().warning(configString + " is incorrectly formatted.");
            }
        }
        for (String configString : uniqueTechPointItems)
        {
            if (verifyConfigString(configString, "UniqueTechPointItem"))
            {
                isVerified = false;
                plugin.getLogger().warning(configString + " is incorrectly formatted.");
            }
        }
        for (String configString : multiBlocks)
        {
            if (verifyConfigString(configString, "MultiBlock"))
            {
                isVerified = false;
                plugin.getLogger().warning(configString + " is incorrectly formatted.");
            }
        }
        return isVerified;
    }

    /**
     * verifies a specific string.
     * @param configString - the string to verify.
     * @param type - the name of the type of TechPointItem it is. current valid types are:
     *             BasicTechPointItem
     *             UniqueTechPointItem
     *             MultiBlock
     * @return if the string is valid, it returns true. Otherwise, it returns false.
     */
    private boolean verifyConfigString(String configString, String type)
    {
        boolean isValid = false;
        switch (type)
        {
            case "BasicTechPointItem":
                isValid = configString.matches("\\d*:\\d*:\\d*:.*|\\d*:\\*:\\d*:.*");
                break;
            case "UniqueTechPointItem":
                isValid = configString.matches("\\d*:\\d*:\\d*:.*:.*|\\d*:\\*:\\d*:.*:.*");
                break;
            case "MultiBlock":
                isValid = configString.matches("\\d*:\\d*:\\d*:.*:\\d*|\\d*:\\*:\\d*:.*:\\d*");
                break;
            default:
                break;
        }
        return !isValid;
    }

    /**
     *
     * @return the list of BasicTechPointItems from techpoints.yml.
     */
    public ArrayList<BasicTechPointItem> getBasicTechPointItems()
    {
        return basicTechPointItems;
    }

    /**
     *
     * @return the list of UniqueTechPointItems from techpoints.yml
     */
    public ArrayList<UniqueTechPointItem> getUniqueTechPointItems()
    {
        return uniqueTechPointItems;
    }

    /**
     *
     * @return the list of MultiBlocks from techpoints.yml.
     */
    public ArrayList<MultiBlock> getMultiBlocks()
    {
        return multiBlocks;
    }
}
