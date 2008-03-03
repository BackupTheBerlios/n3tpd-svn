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
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import n3tpd.Config;
import n3tpd.io.Resource;

class Database
{
  private static Database instance = null;
  
  public static Database getInstance()
  {
    try
    {
      if(instance == null)
        instance = new Database();
      
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
      //ex.printStackTrace();
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
  
  public ResultSet getGroups()
    throws SQLException
  {
    Statement stmt = conn.createStatement();
    ResultSet rs = stmt.executeQuery("SELECT * FROM Groups");
    
    return rs;
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
  
  public void setArticle(Article article)
  {
    
  }
}
