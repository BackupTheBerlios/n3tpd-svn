/*
 *   Neat NNTP Daemon (n3tpd)
 *   Copyright (C) 2007, 2008 by Christian Lins <christian.lins@web.de>
 *   based on tnntpd (C) 2003 by Dennis Schwerdel
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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import n3tpd.Debug;

/**
 * Represents a logical Group within this newsserver.
 * @author Christian Lins
 */
public class Group
{
  private long   id;
  private String name;

  private int    firstArticle = 0;
  private int    lastArticle  = 0;

  /**
   * Private constructor.
   * @param name
   * @param id
   */
  Group(String name, long id)
  {
    this.id   = id;
    this.name = name;
  }
  
  /**
   * Returns a Group identified by its full name.
   * @param name
   * @return
   */
  public static Group getByName(String name)
  {
    try
    {
      return Database.getInstance().getGroup(name);
    }
    catch(SQLException ex)
    {
      System.err.println(ex.getLocalizedMessage());
      ex.printStackTrace(Debug.getInstance().getStream());
      return null;
    }
  }

  /**
   * Returns a list of all groups this server handles.
   * @return
   */
  public static ArrayList<Group> getAll()
  {
    ArrayList<Group> buffer = new ArrayList<Group>();
    
    try
    {
      ResultSet rs = Database.getInstance().getGroups();
      
      while(rs.next())
      {
        String name = rs.getString("Name");
        long   id   = rs.getLong("ID");
        
        // Determine the first and last message number in the Group
        Group group = new Group(name, id);
        int first = Database.getInstance().getFirstArticleNumber(group);
        int last  = Database.getInstance().getLastArticleNumber(group);
        group.setFirstArticle(first);
        group.setLastArticle(last);
        
        buffer.add(group);
      }
    }
    catch(SQLException ex)
    {
      ex.printStackTrace(Debug.getInstance().getStream());
      System.err.println(ex.getLocalizedMessage());
    }
    
    return buffer;
  }

  public LinkedList<Article> getAllArticles()
  {
    return getAllArticles(getFirstArticle(), getLastArticle());
  }

  public LinkedList<Article> getAllArticles(int first, int last)
  {
    return null;
  }

  /*****************************************************************************
   * Getters and Setters
   ****************************************************************************/

  public int getFirstArticle()
  {
    return firstArticle;
  }

  public void setFirstArticle(int firstArticle)
  {
    this.firstArticle = firstArticle;
  }

  public long getID()
  {
    return id;
  }

  public void setID(long id)
  {
    this.id = id;
  }

  public int getLastArticle()
  {
    return lastArticle;
  }

  public void setLastArticle(int lastArticle)
  {
    this.lastArticle = lastArticle;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public int getEstimatedArticleCount()
  {
    if (getLastArticle() < getFirstArticle())
      return 0;
    return getLastArticle() - getFirstArticle() + 1;
  }

}
