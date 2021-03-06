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
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import n3tpd.Config;
import n3tpd.Debug;

/**
 * Represents a newsgroup article.
 * @author Christian Lins
 * @author Denis Schwerdel
 */
public class Article
{
  /**
   * Loads the Article identified by the given ID from the Database.
   * @param messageID
   * @return null if Article is not found or if an error occurred.
   */
  public static Article getByMessageID(String messageID)
  {
    try
    {
      return Database.getInstance().getArticle(messageID);
    }
    catch(SQLException ex)
    {
      ex.printStackTrace(Debug.getInstance().getStream());
      return null;
    }
  }
  
  public static Article getByNumberInGroup(Group group, int number)
  {
    return null;
  }
  
  private String                  body      = null;
  private String                  filePath  = null;
  private long                    groupID   = 0;
  private HashMap<String, String> header    = null;
  private int                     numberInGroup = -1;
  
  /**
   * Default constructor.
   */
  public Article()
  {
  }
  
  /**
   * Creates a new Article object using the date from the given
   * ResultSet. It is expected that ResultSet.next() was already
   * called by the Database class.
   * @param rs
   */
  public Article(ResultSet rs)
    throws SQLException
  {
    this.body = rs.getString("Body");
  }
  
  /**
   * Returnes the next Article in the group of this Article.
   * @return
   */
  public Article nextArticleInGroup()
  {
    return null;
  }

  /**
   * Returns the previous Article in the group of this Article.
   * @return
   */
  public Article prevArticleInGroup()
  {
    return null;
  }

  /**
   * Generates a message id for this article and sets it into
   * the header HashMap.
   */
  private String generateMessageID()
  {
    String msgID = "<" + UUID.randomUUID() + "@"
        + Config.getInstance().get("n3tpd.hostname", "localhost") + ">";
    
    this.header.put("Message-ID", msgID);
    
    return msgID;
  }

  /**
   * Tries to delete this article.
   * @return false if the article could not be deleted, otherwise true
   */
  public boolean delete()
  {
    return false;
  }
  
  /**
   * Checks if all necessary header fields are within this header.
   */
  private void validateHeader()
  {    
    // Forces a MessageID creation if not existing
    getMessageID();
    
    // Check if the references are correct...
    String rep = header.get("In-Reply-To");
    if(rep == null) // Some clients use only references instead of In-Reply-To
      return; //rep = header.get("References");
    
    String ref = getMessageID();
    
    if(rep != null && !rep.equals(""))
    {
      Article art = null; //TODO // getByMessageID(rep, articleDir);
      if(art != null)
      {
        ref = art.getHeader().get("References") + " " + rep;
      }
    }
    header.put("References", ref);
  }

  /**
   * Returns the body string.
   */
  public String getBody()
  {
    return body;
  }
  
  public long getGroupID()
  {
    if(groupID == 0)
    {
      // Determining GroupID
      String   newsgroups = this.header.get("Newsgroups");
      if(newsgroups != null)
      {
        String[] newsgroup  = newsgroups.split(",");
        // Crossposting is not supported
        try
        {
          Group group;
          if(newsgroup.length > 0)
            group = Database.getInstance().getGroup(newsgroup[0].trim());
          else
            group = Database.getInstance().getGroup(newsgroups.trim());
          // TODO: What to do if Group does not exist?
          this.groupID = group.getID();
        }
        catch(SQLException ex)
        {
          ex.printStackTrace(Debug.getInstance().getStream());
          System.err.println(ex.getLocalizedMessage());
        }
      }
      else
        System.err.println("Should never happen: Article::getGroupID");
    }
    return this.groupID;
  }

  public void setBody(String body)
  {
    this.body = body;
  }
  
  public void setGroupID(long groupID)
  {
    this.groupID = groupID;
  }

  public int getNumberInGroup()
  {
    return this.numberInGroup;
  }
  
  public void setHeader(HashMap<String, String> header)
  {
    this.header = header;
  }

  public void setNumberInGroup(int id)
  {
    this.numberInGroup = id;
  }

  public String getMessageID()
  {
    String msgID = header.get("Message-ID");
    if(msgID == null)
      msgID = generateMessageID();
    return msgID;
  }

  public HashMap<String, String> getHeader()
  {
    return header;
  }
  
  public Date getDate()
  {
    try
    {
      String date = this.header.get("Date");
      return new Date(Long.parseLong(date));
    }
    catch(IllegalArgumentException e)
    {
      e.printStackTrace(Debug.getInstance().getStream());
      return null;
    }
  }

  public void setDate(Date date)
  {
    if(this.header == null)
      Debug.getInstance().log("Article.class::setDate()\tHeader is null! Called before setHeader?");
    else
      this.header.put("Date", Long.toString(date.getTime()));
  }
  
  @Override
  public String toString()
  {
    return getMessageID();
  }
}
