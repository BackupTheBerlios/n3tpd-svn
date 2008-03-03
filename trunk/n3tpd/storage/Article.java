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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import n3tpd.Config;
import n3tpd.Debug;
import n3tpd.NNTPConnection;

/**
 * Represents a newsgroup article.
 * @author Christian Lins
 * @author Denis Schwerdel
 */
public class Article
{
  public static Article getByMessageID(String messageID)
  {
    try
    {
      return Database.getInstance().getArticle(messageID);
    }
    catch(SQLException ex)
    {
      ex.printStackTrace();
      return null;
    }
  }
  
  private String                  body;
  private String                  filePath  = null;
  private HashMap<String, String> header    = null;
  private int                     id        = -1;
  
  public Article()
  {
  }
  
  public Article(String filePath)
  {
    this.filePath = filePath;
  }

  public static Article getByID(Group group, int id)
  {
    return null;
  }

  private void generateNewFileID()
  {
    String path = Config.getInstance().get("n3tpd.datadir", ".")
      + File.separatorChar       
      + header.get("Newsgroups").replace('.', File.separatorChar) 
      + File.separatorChar;
    
    // Get one filename...
    int start = 1;
    File dir  = new File(path);
    File[] fs = dir.listFiles();
    if(fs.length > 0)
    {
      String number = fs[fs.length-1].getName().substring(0, fs[fs.length-1].getName().indexOf("."));
      if(number != null)
        start = Integer.parseInt(number) + 1;
    }

    for(int n = start;; n++)
    {
      String filepath = path + n + ".news";
      File file = new File(filepath);
      if(!file.exists())
      {
        id = n;
        return;
      }
    }
  }
  
  public void loadFromFile()
    throws IOException
  {
    BufferedReader in = new BufferedReader(
        new InputStreamReader(new FileInputStream(filePath)));
    
    // Read header
    this.header = new HashMap<String, String>();
    for(;;)
    {
      String line = in.readLine();
      if(line == null) // File is malformed...
      {
        // ... and must be deleted
        in.close();
        delete();
        throw new IOException("Malformed article file!");
      }
      
      if(line.trim().equals(""))
        break;
      
      String[] headElement = line.split(":", 2);
      this.header.put(headElement[0].trim(), headElement[1].trim());
    }
    
    // Read body
    StringBuilder buffer = new StringBuilder();
    for(;;)
    {
      String line = in.readLine();
      if(line == null)
        break;
      
      buffer.append(line);
      buffer.append(NNTPConnection.NEWLINE);
    }
    
    this.body = buffer.toString();
    
    in.close();
  }
  
  public void writeToFile()
    throws IOException
  {
    if(id == -1)
      generateNewFileID();
    
    String newsgroup = header.get("Newsgroups");
    filePath = Config.getInstance().get("n3tpd.datadir", ".") 
      + File.separatorChar 
      + newsgroup.replace('.', File.separatorChar) 
      + File.separatorChar
      + id + ".news";
    
    // Validate and write Header
    validateHeader();
    
    BufferedWriter out = new BufferedWriter(
        new OutputStreamWriter(new FileOutputStream(filePath)));
    
    for(Map.Entry<String, String> h : header.entrySet())
      out.write(h.getKey() + ": " + h.getValue() + "\n");
    
    // Write Body
    out.write("\n" + body);
    
    out.flush();
    out.close();
  }

  public Article nextArticleInGroup(Group group)
  {
    return null;
  }

  public Article prevArticleInGroup(Group group)
  {
    return null;
  }

  /**
   * Generates a message id for this article and sets it into
   * the header HashMap.
   */
  private String generateMessageID()
  {
    return this.header.put("Message-ID", "<" + UUID.randomUUID() + "@"
        + Config.getInstance().get("n3tpd.hostname", "localhost") + ">");
  }

  /**
   * Tries to delete this article.
   * @return false if the article could be deleted, otherwise true
   */
  public boolean delete()
  {
    File f = new File(filePath);
    return f.delete();
  }
  
  private void validateHeader()
  {
    String articleDir = filePath.substring(0, filePath.lastIndexOf(File.separator));
    
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

  /*****************************************************************************
   * Getters and Setters
   ****************************************************************************/

  /**
   * Returns the body string.
   */
  public String getBody()
  {
    return body;
  }

  public void setBody(String body)
  {
    this.body = body;
  }

  public int getID()
  {
    return id;
  }
  
  public void setHeader(HashMap<String, String> header)
  {
    this.header = header;
  }

  public void setID(int id)
  {
    this.id = id;
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
  
  public String toString()
  {
    return getMessageID();
  }
}
