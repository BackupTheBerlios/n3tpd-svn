/*
 *   Netvader NNTP Daemon (n3tpd)
 *   Copyright (C) 2007 by Christian Lins <christian.lins@web.de>
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
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;

import n3tpd.Config;
import n3tpd.NNTPConnection;

/**
 * Represents a newsgroup article.
 * @author Christian Lins
 * @author Denis Schwerdel
 */
public class Article
{
  private String                  body;
  private String                  filePath  = null;
  private HashMap<String, String> header;
  private int                     id        = -1;
  private String                  messageID;
  
  public Article()
  {
  }
  
  public Article(String filePath)
  {
    this.filePath = filePath;
  }
  
  public static Article getByMessageID(String id, String path)
  {
    Stack<File> stack = new Stack<File>();
    stack.push(new File(path));
    
    while(!stack.empty())
    {
      File file = stack.pop();
      if(file.isDirectory())
      {
        for(File f : file.listFiles())
          stack.push(f);
      }
      else
      {
        try
        {
          Article art = new Article(file.getAbsolutePath());
          art.loadFromFile();
          if(art.getMessageID().equals(id))
            return art;
        }
        catch(IOException e)
        {
          e.printStackTrace();
        }
      }
    }
    
    return null;
  }
  
  public static Article getByMessageID(String id)
  {
    String path = Config.getInstance().getProperty("n3tpd.datadir");
    if(path == null)
      return null;
    
    return getByMessageID(id, path);
  }

  public static Article getByID(Group group, int id)
  {
    try
    {
      String path = group.getPath()
        + File.separatorChar + id + ".news";
      Article art = new Article(path);
      art.setID(id);
      art.loadFromFile();
      
      return art;
    }
    catch(Exception e)
    {
      e.printStackTrace();
      return null;
    }
  }

  private void generateNewFileID()
  {
    String path = Config.getInstance().getProperty("n3tpd.datadir")
      + File.separatorChar       
      + header.get("Newsgroups").replace('.', File.separatorChar) 
      + File.separatorChar;
    
    for(int n = 1;; n++) // Starting with 1, zero is not nice
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
    filePath = Config.getInstance().getProperty("n3tpd.datadir") 
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

  public void generateMessageID()
  {
    setMessageID("<" + UUID.randomUUID() + "@"
        + Config.getInstance().getProperty("n3tpd.hostname") + ">");
  }

  /**
   * Tries to delete this article.
   * @return
   */
  public boolean delete()
  {
    File f = new File(filePath);
    return f.delete();
  }
  
  private void validateHeader()
  {
    String articleDir = filePath.substring(0, filePath.lastIndexOf(File.separator));
    
    // Check if the references are correct...
    String rep = header.get("In-Reply-To");
    String ref = new String();
    
    if(rep != null && !rep.equals(""))
    {
      Article art = getByMessageID(rep, articleDir);
      if(art != null)
        ref = art.getHeader().get("References") + " " + rep;
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
    try
    {
      return header.get("Message-ID");
    }
    catch(NullPointerException e)
    {
      return messageID;
    }
  }

  public void setMessageID(String messageId)
  {
    this.messageID = messageId;
  }

  public HashMap<String, String> getHeader()
  {
    return header;
  }
  
  public void setDate(Timestamp date)
  {
    this.header.put("Date", date.toString());
  }
}
