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

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.LinkedList;
import n3tpd.Config;

/**
 * Represents a logical Group within this newsserver.
 * @author Christian Lins
 */
public class Group
{
  private int    id;
  private String name;
  private File   path;

  private int    firstArticle = Integer.MAX_VALUE;
  private int    lastArticle  = 0;

  public Group(File path)
  {
    this.name = path.getAbsolutePath().substring(
      new File(Config.getInstance().get("n3tpd.datadir", ".")).getAbsolutePath().length() + 1);
    this.name = this.name.replace(File.separatorChar, '.');
    this.path = path;
    
    if(this.path.exists())
    {
      File[] articles = path.listFiles(new FileFilter()
        {
          public boolean accept(File f)
          {
            return f.getName().endsWith(".news");
          }
        });
      for(File a : articles)
      {
        int n = Integer.parseInt(a.getName().replaceAll(".news", ""));
        if(n > lastArticle)
          lastArticle = n;
        if(n < firstArticle)
          firstArticle = n;
      }
    }
  }

  public static Group getById(int id)
  {
    return null;
  }

  public static Group getByName(String name)
  {
    name = name.replace('.', File.separatorChar);
    File dir = new File(Config.getInstance().get("n3tpd.datadir", ".")
        + File.separatorChar + name);

    if (dir.exists())
      return new Group(dir);

    System.err.println("Dir " + dir.getPath() + " does not exist!");
    return null;
  }

  /**
   * Recursive method that adds all directories to an ArrayList that either
   * have files or no subdirectories.
   * @param lst
   * @param g
   */
  private static void addSubGroups(ArrayList<Group> lst, File g)
  {
    File[] groups = g.listFiles(new FileFilter()
    {
      public boolean accept(File f)
      {
        return f.isDirectory();
      }
    });

    //  Check if this is only an empty dir
    if(groups.length == 0 || g.list().length - groups.length > 0)
      lst.add(new Group(g));
    
    for (File f : groups)
      addSubGroups(lst, f);
  }

  /**
   * Returns a list of all groups this server handles.
   * @return
   */
  public static ArrayList<Group> getAll()
  {
    ArrayList<Group> grpLst = new ArrayList<Group>();

    File dataDir = new File(Config.getInstance().get("n3tpd.datadir", "."));
    addSubGroups(grpLst, dataDir);

    return grpLst;
  }

  public LinkedList<Article> getAllArticles()
  {
    return getAllArticles(getFirstArticle(), getLastArticle());
  }

  public LinkedList<Article> getAllArticles(int first, int last)
  {
    return null;
  }

  public static LinkedList<Group> getAll(int date)
  {
    return null;
  }

  public void addArticle(Article article)
  {
  }

  public void create()
  {

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

  public int getId()
  {
    return id;
  }

  public void setId(int id)
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

  public String getPath()
  {
    return this.path.getAbsolutePath();
  }
}
