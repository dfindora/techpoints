package com.goldensands.modules;

import com.goldensands.main.Techpoints;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Handles non-config file I/O
 */
class FileModule
{
    private Techpoints plugin;
    private File chunkDir;

    FileModule(Techpoints plugin)
    {
        this.plugin = plugin;
    }

    void setup()
    {
        chunkDir = new File(plugin.getDataFolder().getPath() + File.separator + "chunks" + File.separator);
        if(!chunkDir.exists())
        {
            boolean isCreated = chunkDir.mkdir();
            if (isCreated)
            {
               Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN
                                                                 + "Chunk directory has been created.");
            }
        }
    }

    /**
     * logs the specified message to the file that corresponds to the specified chunk coordinates.
     * @param message - the message to log.
     * @param chunkX - the X coordinate of the chunk.
     * @param chunkZ - the Z coordinate of the chunk.
     */
    private void logToChunkFile(String message, int chunkX, int chunkZ)
    {
        //file creation
        File file = new File(chunkDir.getPath() + File.separator + chunkX + ", " + chunkZ);
        if (!file.exists())
        {
            try
            {
                boolean isCreated = file.createNewFile();
                if (isCreated)
                {
                    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + file.getName()
                                                                      + " has been created.");
                }
            }
            catch (IOException e)
            {
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Unable to create "
                                                                  + file.getName() + ".");
            }
        }

        try
        {
            FileWriter fw = new FileWriter(file);
            PrintWriter pw = new PrintWriter(fw);

            pw.println(message);
            pw.close();
            fw.close();
        }
        catch (IOException e)
        {
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED
                                                              + "Error in writing to the log file.");
            e.printStackTrace();
        }
    }
}
