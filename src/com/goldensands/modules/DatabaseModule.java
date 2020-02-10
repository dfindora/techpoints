package com.goldensands.modules;

import com.goldensands.main.Techpoints;

import java.sql.*;
import java.util.HashMap;

public class DatabaseModule
{
    String dbFilename;
    Techpoints plugin;

    public DatabaseModule(Techpoints plugin)
    {
        this.plugin = plugin;
        dbFilename = plugin.getDataFolder() + "techlimit.db";
    }

    public void setup()
    {
        try (Connection conn = connect()) {
            String sql = "CREATE TABLE IF NOT EXISTS chunks ("
                         + "x integer  NOT NULL, "
                         + "z integer NOT NULL, "
                         + "techpoints integer NOT NULL,"
                         + "PRIMARY KEY (x, z)"
                         + ");";
            Statement statement = conn.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public Connection connect()
    {
        String url = "jdbc:sqlite:" + dbFilename;
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public void addChunk(int x, int z, int techpoints)
    {
        String url = "jdbc:sqlite:" + dbFilename;
        try (Connection conn = connect())
        {
            String sql = "INSERT OR REPLACE INTO chunks(x, z, techpoints) VALUES(?, ?, ?);";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1, x);
            statement.setInt(2, z);
            statement.setInt(3, techpoints);
            statement.executeUpdate();
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }
    }

    public HashMap<ChunkCoordinate, Integer> getChunksOverLimit()
    {
        String sql = "SELECT x, z, techpoints FROM chunks WHERE techpoints > 200";
        HashMap<ChunkCoordinate, Integer> chunks = new HashMap<>();
        try(Connection conn = connect())
        {
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while(rs.next())
            {
                chunks.put(new ChunkCoordinate(rs.getInt("x"), rs.getInt("z")), rs.getInt("techpoints"));
            }
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }
        return chunks;
    }
}
