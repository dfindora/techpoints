package com.goldensands.bukkit.modules;

import com.goldensands.bukkit.main.Techpoints;
import com.goldensands.bukkit.util.Vector2d;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.util.HashMap;

public class DatabaseModule
{
    String dbFilename;
    File libDir;
    Techpoints plugin;

    public DatabaseModule(Techpoints plugin)
    {
        this.plugin = plugin;
        dbFilename = plugin.getDataFolder() + "/techlimit.db";
        libDir = new File(plugin.getDataFolder() + File.separator + "lib" + File.separator);
        setup();
    }

    private void setup()
    {
        if (libDir.exists() && libDir.isDirectory() && !new File(libDir.getPath() + File.separator
                                                                + "sqlite-jdbc-3.30.1.jar").exists())
        {
            plugin.getLogger().info("downloading JDBC..");
            getJDBCLib();
        }
        String sql = "CREATE TABLE IF NOT EXISTS chunks ("
                     + "x integer  NOT NULL, "
                     + "z integer NOT NULL, "
                     + "minTechPoints integer NOT NULL,"
                     + "maxTechPoints integer NOT NULL,"
                     + "PRIMARY KEY (x, z)"
                     + ");";
        try (Connection conn = connect();
             Statement statement = conn.createStatement())
        {
            plugin.getLogger().info("Database connection successful.");
            statement.execute(sql);
        }
        catch (SQLException e)
        {
            plugin.getLogger().warning("Database conection failed.");
            System.out.println(e.getMessage());
        }
    }

    private void getJDBCLib()
    {
        try
        {
            URL url = new URL("https://bitbucket.org/xerial/sqlite-jdbc/downloads/sqlite-jdbc-3.30.1.jar");
            if(!libDir.exists())
            {
                boolean success = libDir.mkdir();
                if(!success)
                {
                    plugin.getLogger().warning("unable to create the lib directory.");
                }
            }
            try(BufferedInputStream is = new BufferedInputStream(url.openStream());
                BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(libDir.getPath() + File.separator
                                                                                        + "sqlite-jdbc-3.30.1.jar")))
            {
                byte[] buffer = new byte[32];
                int read = is.read(buffer, 0, buffer.length);
                while(read > 0)
                {
                    os.write(buffer, 0, read);
                    read = is.read(buffer, 0, buffer.length);
                }
                os.flush();
            }
            catch (IOException e)
            {
                plugin.getLogger().warning("JDBC download failed.");
                e.printStackTrace();
            }
        }
        catch (MalformedURLException e)
        {
            plugin.getLogger().warning("JDBC download failed.");
            e.printStackTrace();
        }
    }


    private Connection connect()
    {
        String url = "jdbc:sqlite:" + dbFilename;
        Connection conn = null;
        try
        {
            conn = DriverManager.getConnection(url);
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public void addChunk(int x, int z, int minTechPoints, int maxTechPoints)
    {
        String sql = "INSERT OR REPLACE INTO chunks(x, z, minTechPoints, maxTechPoints) VALUES(?, ?, ?, ?);";
        try (Connection conn = connect();
             PreparedStatement statement = conn.prepareStatement(sql))
        {
            statement.setInt(1, x);
            statement.setInt(2, z);
            statement.setInt(3, minTechPoints);
            statement.setInt(4, maxTechPoints);
            statement.executeUpdate();
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }
    }

    public HashMap<Vector2d, Vector2d> getChunksOverLimit()
    {
        String sql = "SELECT x, z, minTechPoints, maxTechPoints FROM chunks WHERE maxTechPoints > 200";
        HashMap<Vector2d, Vector2d> chunks = new HashMap<>();
        try(Connection conn = connect();
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(sql))
        {
            while(rs.next())
            {
                chunks.put(new Vector2d(rs.getInt("x"),
                                        rs.getInt("z")),
                           new Vector2d(rs.getInt("minTechPoints"),
                                        rs.getInt("maxTechPoints")));
            }
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }
        return chunks;
    }
}
