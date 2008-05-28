/*
 *   Neat NNTP Daemon (n3tpd)
 *   Copyright (C) 2007, 2008 by Christian Lins <christian.lins@web.de>
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package n3tpd.storage;

import java.sql.Connection;
import java.util.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.zip.CRC32;
import n3tpd.Config;
import n3tpd.io.Resource;
import n3tpd.util.StringTemplate;

public class Database
{
  private static Database instance = null;
  
  public static void arise()
    throws Exception
  {
    // Tries to load the Database driver and establish a connection.
    if(instance == null)
      instance = new Database();
  }
  
  public static Database getInstance()
  {
    try
    {
      arise();
      return instance;
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
      return null;
    }
  }
  
  private Connection conn;
  
  private Database()
    throws Exception
  {
    Class.forName("org.hsqldb.jdbcDriver");
    
    String path = Config.getInstance().get("n3tpd.datadir", "data/");
    this.conn = DriverManager.getConnection("jdbc:hsqldb:file:" + path);
    
    checkTables();
  }
  
  public boolean addArticle(Article article)
    throws SQLException
  {
    Statement stmt = this.conn.createStatement();

    String sql = "INSERT INTO Articles (Body,Date,GroupID,MessageID,NumberInGroup,Subject)" +
            "VALUES('%body',%date,%gid,'%mid',%nig,'%subject')";
    
    StringTemplate tmpl = new StringTemplate(sql);
    tmpl.set("body", article.getBody());
    tmpl.set("date", new Date().getTime());
    tmpl.set("gid", article.getGroupID());
    tmpl.set("mid", article.getMessageID());
    tmpl.set("nig", article.getNumberInGroup());
    tmpl.set("subject", article.getHeader().get("Subject"));
    
    return 1 == stmt.executeUpdate(tmpl.toString());
  }
  
  /**
   * Adds a group to the Database.
   * @param name
   * @throws java.sql.SQLException
   */
  public boolean addGroup(String name)
    throws SQLException
  {
    CRC32 crc = new CRC32();
    crc.update(name.getBytes());
    
    long id = crc.getValue();
    
    Statement stmt = conn.createStatement();
    return 1 == stmt.executeUpdate("INSERT INTO Groups (ID, Name) VALUES (" + id + ", '" + name + "')");
  }
  
  /**
   * Check if all necessary tables are existing.
   */
  private void checkTables()
  {
    try
    {
      String sql = Resource.getAsString("helpers/database.sql", false);
      Statement stmt = conn.createStatement();
      stmt.execute(sql);
    }
    catch(Exception ex)
    {
      // Silently ignore the exception if the tables already exist.
      ex.printStackTrace();
    }
  }
  
  public void delete(Article article)
  {
    
  }
  
  public void delete(Group group)
  {
    
  }
  
  public Article getArticle(String messageID)
    throws SQLException
  {
    Statement stmt = conn.createStatement();
    ResultSet rs =
      stmt.executeQuery("SELECT * FROM Articles WHERE MessageID = '" + messageID + "'");
    
    return new Article(rs);
  }
  
  public ResultSet getArticles()
    throws SQLException
  {
    Statement stmt = conn.createStatement();
    return stmt.executeQuery("SELECT * FROM Articles");
  }
  
  /**
   * Reads all Groups from the Database.
   * @return
   * @throws java.sql.SQLException
   */
  public ResultSet getGroups()
    throws SQLException
  {
    Statement stmt = conn.createStatement();
    ResultSet rs = stmt.executeQuery("SELECT * FROM Groups");
    
    return rs;
  }
  
  public Group getGroup(String name)
    throws SQLException
  {
    Statement stmt = this.conn.createStatement();
    ResultSet rs = stmt.executeQuery("SELECT ID FROM Groups WHERE Name = '" + name + "'");
  
    if(!rs.next())
      return null;
    else
    {
      long id = rs.getLong("ID");
      return new Group(name, id);
    }
  }
  
  public int getLastArticleNumber(Group group)
    throws SQLException
  {
    Statement stmt = conn.createStatement();
    ResultSet rs = 
      stmt.executeQuery("SELECT Max(NumberInGroup) FROM Articles WHERE GroupID = " + group.getID());
  
    if(!rs.next())
      return 0;
    else
      return rs.getInt(1);
  }
  
  public int getFirstArticleNumber(Group group)
    throws SQLException
  {
    Statement stmt = conn.createStatement();
    ResultSet rs = 
      stmt.executeQuery("SELECT Min(NumberInGroup) FROM Articles WHERE GroupID = " + group.getID());
  
    if(!rs.next())
      return 0;
    else
      return rs.getInt(1);
  }
  
  /**
   * Returns a group name identified by the given id.
   * @param id
   * @return
   * @throws java.sql.SQLException
   */
  public String getGroup(int id)
    throws SQLException
  {
    Statement stmt = conn.createStatement();
    ResultSet rs   = stmt.executeQuery("SELECT Name FROM Groups WHERE ID = " + id);
    
    if(rs.next())
    {
      return rs.getString(1);
    }
    else
      return null;
  }
  
  public Article getOldestArticle()
    throws SQLException
  {
    Statement stmt = conn.createStatement();
    ResultSet rs = 
      stmt.executeQuery("SELECT * FROM Articles WHERE Date = (SELECT Min(Date) FROM Articles)");
    
    if(rs.next())
      return new Article(rs);
    else
      return null;
  }
  
  /**
   * Checks if there is a group with the given name in the Database.
   * @param name
   * @return
   * @throws java.sql.SQLException
   */
  public boolean isGroupExisting(String name)
    throws SQLException
  {
    Statement stmt = this.conn.createStatement();
    ResultSet rs   = stmt.executeQuery("SELECT * FROM Groups WHERE Name = '" + name + "'");
    
    return rs.next();
  }
  
  public void updateArticle(Article article)
  {
    
  }
}
