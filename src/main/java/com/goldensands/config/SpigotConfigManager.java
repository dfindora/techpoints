package com.goldensands.config;

import com.goldensands.bukkit.main.Techpoints;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class SpigotConfigManager
{
    private final Techpoints spigotPlugin;

    private File techPointsFile;
    private ArrayList<BasicTechPointItem> basicTechPointItems;
    private ArrayList<VariedTechPointItem> variedTechPointItems;
    private ArrayList<MultiBlock> multiBlocks;

    public SpigotConfigManager(Techpoints plugin)
    {
        this.spigotPlugin = plugin;
    }

    /**
     * techpoints.yml initialization
     */
    public void setup()
    {
        //file check
        directoryCheck();
        //defaults creation
        spigotPlugin.saveResource("techpoints.yml", false);
        techPointsFile = new File(spigotPlugin.getDataFolder(), "techpoints.yml");
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
            assert basicTechPointItemString != null;
            for (String basicTechPointItem : basicTechPointItemString)
            {
                String[] split = basicTechPointItem.split(":");
                BasicTechPointItem btpi = (split[1].equals("*"))
                                          ? new BasicTechPointItem(split[0], -1,
                                            Integer.parseInt(split[2]), split[3])
                                          : new BasicTechPointItem(split[0], Integer.parseInt(split[1]),
                                            Integer.parseInt(split[2]), split[3]);
                basicTechPointItems.add(btpi);
            }
            //VariedTechPointItems
            assert variedTechPointItemString != null;
            for (String variedTechPointItem : variedTechPointItemString)
            {
                String[] split = variedTechPointItem.split(":");
                VariedTechPointItem utpi = (split[1].equals("*"))
                                           ? new VariedTechPointItem(split[0],
                                                                     -1, Integer.parseInt(split[2]),
                                                                     Integer.parseInt(split[3]),
                                                                     split[4], split[5])
                                           : new VariedTechPointItem(split[0],
                                                                     Integer.parseInt(split[1]),
                                                                     Integer.parseInt(split[2]),
                                                                     Integer.parseInt(split[3]),
                                                                     split[4], split[5]);
                variedTechPointItems.add(utpi);
            }
            //MultiBlocks
            assert multiBlockString != null;
            for (String multiBlock : multiBlockString)
            {
                String[] split = multiBlock.split(":");
                MultiBlock mb = (split[1].equals("*"))
                                ? new MultiBlock(split[0], -1, Integer.parseInt(split[2]),
                                Integer.parseInt(split[4]), split[3])
                                : new MultiBlock(split[0], Integer.parseInt(split[1]), Integer.parseInt(split[2]),
                                Integer.parseInt(split[4]), split[3]);
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
        Logger logger = spigotPlugin.getLogger();
        if (basicTechPointItems != null)
        {
            for (String configString : basicTechPointItems)
            {
                if (verifyConfigString(configString, "BasicTechPointItem"))
                {
                    isVerified = false;
                    logger.warning(configString + " is incorrectly formatted.");
                }
            }
        }
        else
        {
            logger.warning("Config missing BasicTechPointItems category.");
            isVerified = false;
        }
        if (variedTechPointItems != null)
        {
            for (String configString : variedTechPointItems)
            {
                if (verifyConfigString(configString, "VariedTechPointItem"))
                {
                    isVerified = false;
                    logger.warning(configString + " is incorrectly formatted.");
                }
            }
        }
        else
        {
            spigotPlugin.getLogger().warning("Config missing VariedTechPointItems category.");
            isVerified = false;
        }
        if (multiBlocks != null)
        {
            for (String configString : multiBlocks)
            {
                if (verifyConfigString(configString, "MultiBlock"))
                {
                    isVerified = false;
                    logger.warning(configString + " is incorrectly formatted.");
                }
            }
        }
        else
        {
            spigotPlugin.getLogger().warning("Config missing MultiBlocks category.");
            isVerified = false;
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
                isValid = configString.matches("\\w*:\\d*:\\d*:[^:]*|\\w*:\\*:\\d*:[^:]*");
                break;
            case "VariedTechPointItem":
                isValid = configString.matches("\\w*:\\d*:\\d*:.*:\\D.*|\\w*:\\*:\\d*:.*:\\D.*");
                break;
            case "MultiBlock":
                isValid = configString.matches("\\w*:\\d*:\\d*:.*:\\d*|\\w*:\\*:\\d*:.*:\\d*");
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
        BasicTechPointItem techPointItem = null;
        BasicTechPointItem compareTo = (block != null)
                                       ? new BasicTechPointItem(block.getType().name(), block.getData(), 0,
                                                                block.getType().name())
                                       : new BasicTechPointItem(hotbar.getType().name(), hotbar.getDurability(), 0,
                                                                hotbar.getType().name());
        boolean found = false;
        if ((block != null && block.getType() != Material.AIR)
                || (hotbar != null && hotbar.getType() != Material.AIR))
        {
            for (BasicTechPointItem basicTechPointItem : basicTechPointItems)
            {
                if (basicTechPointItem.compareTo(compareTo) == 0)
                {
                    techPointItem = basicTechPointItem;
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
                        techPointItem = variedTechPointItem;
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
                        techPointItem = multiBlock;
                        break;
                    }
                }
            }
        }
        return techPointItem;
    }
}
