package com.goldensands.config;

import com.goldensands.bukkit.main.Techpoints;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SpigotConfigManager
{
    private Techpoints spigotPlugin;

    private File techPointsFile;
    private ArrayList<BasicTechPointItem> basicTechPointItems;
    private ArrayList<VariedTechPointItem> variedTechPointItems;
    private ArrayList<MultiBlock> multiBlocks;

    public SpigotConfigManager(Techpoints plugin)
    {
        this.spigotPlugin = plugin;
    }

    public SpigotConfigManager(File techPointsFile)
    {
        this.techPointsFile = techPointsFile;
    }

    /**
     * techpoints.yml initialization
     */
    public void setup()
    {
        //file check
        directoryCheck();
        techPointsFile = new File(spigotPlugin.getDataFolder(), "techpoints.yml");
        boolean isFileCreated = fileCheck(techPointsFile);

        //defaults creation
        //TODO: hard-coded defaults. move to a seperate default config file.
        if (isFileCreated)
        {
            String[] btpi = new String[]{
                    "2:0:11:grass",
                    "5:*:11:Wood Planks"
            };
            String[] vtpi = new String[]{
                    "7:0:3:6:Bedrock:This is a template. Please change this."
            };
            String[] mb = new String[]{
                    "17:0:11:Oak Tree:6"
            };
            copyDefaults(btpi, vtpi, mb);
        }
        loadTechPoints();
    }

    /**
     * verifies that the plugin's data folder exists. If it does not, it is created.
     */
    private void directoryCheck()
    {
        if (!spigotPlugin.getDataFolder().exists())
        {
            boolean isCreated = spigotPlugin.getDataFolder().mkdir();
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
     * @param vtpi - VariedTechPointItem defaults
     * @param mb - MultiBlock defaults
     */
    private void copyDefaults(String[] btpi, String[] vtpi, String[] mb)
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
            bw.write("VariedTechPointItems:\n");
            for (String str : vtpi)
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
        if (spigotPlugin != null)
        {
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "techpoints.yml reloaded.");
        }
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
        variedTechPointItems = new ArrayList<>();
        multiBlocks = new ArrayList<>();
        //load lists from config
        List<String> basicTechPointItemString = (List<String>) techPointsCfg.getList("BasicTechPointItems");
        List<String> variedTechPointItemString = (List<String>) techPointsCfg.getList("VariedTechPointItems");
        List<String> multiBlockString = (List<String>) techPointsCfg.getList("MultiBlocks");
        //verify config is properly formatted before loading. If there is any errors in the config, it can't be loaded.
        //TODO:instead of not loading the config at all, maybe it should just remove invalid config entries?
        boolean isValid = verifyConfiguration(basicTechPointItemString, variedTechPointItemString, multiBlockString);
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
            //VariedTechPointItems
            for (String variedTechPointItem : variedTechPointItemString)
            {
                String[] split = variedTechPointItem.split(":");
                VariedTechPointItem utpi = (split[1].equals("*"))
                                           ? new VariedTechPointItem(Integer.parseInt(split[0]),
                                                                     -1, Integer.parseInt(split[2]),
                                                                     Integer.parseInt(split[3]),
                                                                     split[4], split[5])
                                           : new VariedTechPointItem(Integer.parseInt(split[0]),
                                                                     Integer.parseInt(split[1]),
                                                                     Integer.parseInt(split[2]),
                                                                     Integer.parseInt(split[3]),
                                                                     split[4], split[5]);
                variedTechPointItems.add(utpi);
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
            if (spigotPlugin != null)
            {
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Unable to load config.");
            }
        }
    }

    /**
     * verifies that the whole techpoints.yml configuration file is properly formatted.
     * @param basicTechPointItems - list of BasicTechPointItem config strings.
     * @param variedTechPointItems - list of VariedTechPointItem config strings.
     * @param multiBlocks - list of MultiBlock config strings.
     * @return if the whole configuration is properly formatted, it returns true. Otherwise, it returns false.
     */
    private boolean verifyConfiguration(List<String> basicTechPointItems, List<String> variedTechPointItems, List<String> multiBlocks)
    {
        boolean isVerified = true;
        for (String configString : basicTechPointItems)
        {
            if (verifyConfigString(configString, "BasicTechPointItem"))
            {
                isVerified = false;

                if (spigotPlugin != null)
                {
                    spigotPlugin.getLogger().warning(configString + " is incorrectly formatted.");
                }
            }
        }
        for (String configString : variedTechPointItems)
        {
            if (verifyConfigString(configString, "VariedTechPointItem"))
            {
                isVerified = false;
                if (spigotPlugin != null)
                {
                    spigotPlugin.getLogger().warning(configString + " is incorrectly formatted.");
                }
            }
        }
        for (String configString : multiBlocks)
        {
            if (verifyConfigString(configString, "MultiBlock"))
            {
                isVerified = false;
                if (spigotPlugin != null)
                {
                    spigotPlugin.getLogger().warning(configString + " is incorrectly formatted.");
                }
            }
        }
        return isVerified;
    }

    /**
     * verifies a specific string.
     * @param configString - the string to verify.
     * @param type - the name of the type of TechPointItem it is. current valid types are:
     *             BasicTechPointItem
     *             VariedTechPointItem
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
            case "VariedTechPointItem":
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
     * Matches a block or item to a config item if possible.
     * @param block - the block to match. If this is being used to match an item instead, leave this null.
     * @param hotbar - the item to match. If this is being used to match a block instead, leave this null.
     * @return If the block or item matched a config item, that item will be returned. If not, this returns null.
     */
    @SuppressWarnings({"deprecation"})
    public BasicTechPointItem configMatch(Block block, ItemStack hotbar)
    {
        BasicTechPointItem btpi = null;
        BasicTechPointItem compareTo = (block != null)
                                       ? new BasicTechPointItem(block.getTypeId(), block.getData(), 0,
                                                                block.getType().name())
                                       : new BasicTechPointItem(hotbar.getTypeId(), hotbar.getDurability(), 0,
                                                                hotbar.getType().name());
        boolean found = false;
        if ((block != null && block.getTypeId() != 0) || (hotbar != null && hotbar.getTypeId() != 0))
        {
            for (BasicTechPointItem basicTechPointItem : basicTechPointItems)
            {
                if (basicTechPointItem.compareTo(compareTo) == 0)
                {
                    btpi = basicTechPointItem;
                    found = true;
                    break;
                }
            }
            if (!found)
            {
                for (VariedTechPointItem variedTechPointItem : variedTechPointItems)
                {
                    if (variedTechPointItem.compareTo(compareTo) == 0)
                    {
                        btpi = variedTechPointItem;
                        found = true;
                        break;
                    }
                }
            }
            if (!found)
            {

                for (MultiBlock multiBlock : multiBlocks)
                {
                    if (multiBlock.compareTo(compareTo) == 0)
                    {
                        btpi = multiBlock;
                        break;
                    }
                }
            }
        }
        return btpi;
    }
}
