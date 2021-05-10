package com.goldensands.bukkit.modules;

import com.goldensands.bukkit.main.Techpoints;
import com.goldensands.bukkit.util.Vector2d;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TechLimitModule
{
    private Techpoints plugin;
    private HashMap<CommandSender, Integer> pageIndexes;
    private HashMap<CommandSender,  ArrayList<ArrayList<Map.Entry<Vector2d, Vector2d>>>> queries;

    public TechLimitModule(Techpoints plugin)
    {
        this.plugin = plugin;
        pageIndexes = new HashMap<>();
        queries = new HashMap<>();

    }
    public void techLimit(CommandSender sender)
    {
        if(pageIndexes.containsKey(sender))
        {
            pageIndexes.replace(sender, 1);
        }
        else
        {
            pageIndexes.put(sender, 1);
        }
        ArrayList<ArrayList<Map.Entry<Vector2d, Vector2d>>> pages = new ArrayList<>();
        HashMap<Vector2d, Vector2d> chunks
                = plugin.getModuleHandler().getDatabaseModule().getChunksOverLimit();
        ArrayList<Map.Entry<Vector2d, Vector2d>> entries = new ArrayList<>(chunks.entrySet());
        int pageEntryIndex = 0;
        if(chunks.size() > 0)
        {
            pages.add(new ArrayList<>());
        }
        for(int i = 0; i < chunks.entrySet().size(); i++)
        {
            if(pageEntryIndex > 4)
            {
                pages.add(new ArrayList<>());
                pageEntryIndex = -1;
            }
            pages.get(pages.size() - 1).add(entries.get(i));
            pageEntryIndex++;
        }
        queries.put(sender, pages);
    }

    public void pageNext(CommandSender sender)
    {
        if(pageIndexes.containsKey(sender))
        {
            pageIndexes.replace(sender, pageIndexes.get(sender) + 1);
        }
        else
        {
            sender.sendMessage(ChatColor.RED + "You have not executed /techlimit yet.");
        }
    }

    public void pagePrevious(CommandSender sender)
    {
        if(pageIndexes.containsKey(sender))
        {
            pageIndexes.replace(sender, pageIndexes.get(sender) - 1);
        }
        else
        {
            sender.sendMessage(ChatColor.RED + "You have not executed /techlimit yet.");
        }
    }

    public void setPage(CommandSender sender, int page)
    {
        if(pageIndexes.containsKey(sender))
        {
            if(page > 0 && page <= queries.get(sender).size())
            {
                pageIndexes.replace(sender, page);
            }
            else
            {
                sender.sendMessage(ChatColor.RED + "Page does not exist.");
            }
        }
        else
        {
            sender.sendMessage(ChatColor.RED + "You have not executed /techlimit yet.");
        }
    }

    public int getPage(CommandSender sender)
    {
        return pageIndexes.get(sender);
    }

    public ArrayList<ArrayList<Map.Entry<Vector2d, Vector2d>>> getQuery(CommandSender sender)
    {
        return queries.get(sender);
    }
}
